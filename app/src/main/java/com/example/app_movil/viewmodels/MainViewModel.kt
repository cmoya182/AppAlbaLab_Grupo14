package com.example.app_movil.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_movil.data.local.AppDatabase
import com.example.app_movil.data.local.SolicitudEntity
import com.example.app_movil.navigation.NavigationEvent
import com.example.app_movil.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ARCHIVO: MainViewModel.kt
 * PROPÓSITO: Es el "cerebro" central de la aplicación dentro del patrón de arquitectura MVVM
 * (Model - View - ViewModel). Separa toda la lógica de negocio de la interfaz visual.
 *
 * ¿CÓMO FUNCIONA?:
 * Hereda de AndroidViewModel(application) para tener acceso al contexto global de la app y poder
 * inicializar tanto SharedPreferences (sesión) como Room SQLite (base de datos) y el directorio interno
 * de archivos (filesDir) donde se guardan las fotografías adjuntas.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - AppDatabase.kt / SolicitudDao.kt / SolicitudEntity.kt: Para guardar, leer y borrar solicitudes en SQLite.
 * - MainActivity.kt: Escucha los eventos de navegación (navigationEvents) y consulta estaAutenticado.
 * - Todas las pantallas en ui/screens/: Leen las variables de estado de este ViewModel y llaman a sus funciones.
 * - ExampleUnitTest.kt: Ejecuta pruebas unitarias directamente sobre las funciones de validación y cálculo
 *   definidas en el companion object de esta clase, evitando duplicar fórmulas en el proyecto.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * CONSTANTES Y REGLAS DE NEGOCIO CENTRALIZADAS (ÚNICA FUENTE DE VERDAD):
     * Se ubican en el companion object para que tanto las funciones internas del ViewModel como
     * las pruebas unitarias (ExampleUnitTest.kt) utilicen exactamente el mismo código y los mismos valores,
     * sin repetir fórmulas ni expresiones regulares en distintos archivos.
     *
     * DOCUMENTACIÓN SOBRE LOS VALORES DEL SIMULADOR DE IMPACTO SOCIAL:
     * Los montos de $5.000 CLP (por kit de insumos) y $15.000 CLP (por sesión de apoyo sociolaboral)
     * son VALORES REFERENCIALES definidos para el proyecto con el fin de cumplir el objetivo del caso
     * de "visibilizar el destino de los aportes" (Secciones 2.4 y 4.1 del Caso CORPALIV) respetando
     * la restricción oficial (Sección 5.1) de utilizar datos sintéticos/ficticios en el MVP.
     */
    companion object {
        const val CORREO_ADMIN_DEMO = "admin@corpaliv.cl"
        const val CLAVE_ADMIN_DEMO = "albalab2026"

        // Valores referenciales de simulación para el MVP (no corresponden a tarifas oficiales de CORPALIV)
        const val MONTO_MINIMO_SIMULACION = 5000L
        const val COSTO_REFERENCIAL_KIT_INSUMOS = 5000L
        const val COSTO_REFERENCIAL_SESION_APOYO = 15000L

        private val REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        private val REGEX_BUSQUEDA = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]*\$".toRegex()

        /**
         * Valida si una cadena de texto cumple con el formato estándar de correo electrónico.
         */
        fun esCorreoValido(correo: String): Boolean {
            return correo.trim().matches(REGEX_EMAIL)
        }

        /**
         * Valida si un número telefónico tiene al menos 8 caracteres y contiene solo dígitos o el signo '+'.
         */
        fun esTelefonoValido(telefono: String): Boolean {
            val limpio = telefono.trim()
            return limpio.length >= 8 && limpio.all { it.isDigit() || it == '+' }
        }

        /**
         * Valida que el texto ingresado en el buscador del catálogo no contenga caracteres especiales.
         */
        fun esTextoBusquedaValido(texto: String): Boolean {
            return texto.matches(REGEX_BUSQUEDA)
        }

        /**
         * Calcula la cantidad referencial de kits de insumos financiados según el monto aportado.
         */
        fun calcularKitsInsumos(montoClp: Long): Int {
            return if (montoClp >= MONTO_MINIMO_SIMULACION) {
                (montoClp / COSTO_REFERENCIAL_KIT_INSUMOS).toInt()
            } else 0
        }

        /**
         * Calcula la cantidad referencial de sesiones de apoyo sociolaboral financiadas según el monto aportado.
         */
        fun calcularSesionesApoyo(montoClp: Long): Int {
            return if (montoClp >= COSTO_REFERENCIAL_SESION_APOYO) {
                (montoClp / COSTO_REFERENCIAL_SESION_APOYO).toInt()
            } else 0
        }

        /**
         * Genera el mensaje descriptivo del Simulador de Impacto Social a partir del monto en CLP.
         */
        fun generarTextoImpactoSocial(montoClp: Long): String {
            val kitsInsumos = calcularKitsInsumos(montoClp)
            val sesionesApoyo = calcularSesionesApoyo(montoClp)
            return "Con un aporte de \$$montoClp CLP financias aproximadamente $kitsInsumos kits de insumos para el taller laboral" +
                    if (sesionesApoyo > 0) " y $sesionesApoyo sesiones de apoyo sociolaboral." else "."
        }
    }

    // 1. Conexión con SharedPreferences: archivo ligero clave-valor en disco para recordar si hay sesión iniciada.
    private val prefs = application.getSharedPreferences("alba_lab_session_prefs", Context.MODE_PRIVATE)

    // 2. Conexión con Room SQLite: obtiene la instancia única (Singleton) de la base de datos y su DAO.
    private val database = AppDatabase.getDatabase(application)
    private val solicitudDao = database.solicitudDao()

    /**
     * Lista reactiva de solicitudes (StateFlow).
     * Convierte el Flow continuo que viene de SolicitudDao en un estado observable por Jetpack Compose.
     */
    val listaSolicitudes: StateFlow<List<SolicitudEntity>> = solicitudDao
        .obtenerTodasLasSolicitudes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Canal de eventos de navegación (SharedFlow) que MainActivity escucha para cambiar de pantalla.
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    /**
     * Envía una orden a MainActivity para navegar hacia una pantalla específica (definida en Screen.kt).
     */
    fun navigateTo(screen: Screen) {
        viewModelScope.launch { _navigationEvents.emit(NavigationEvent.NavigateTo(route = screen)) }
    }

    /**
     * Envía una orden a MainActivity para regresar a la pantalla anterior del historial (PopBackStack).
     */
    fun navigateBack() {
        viewModelScope.launch { _navigationEvents.emit(NavigationEvent.PopBackStack) }
    }

    // =========================================================================
    // BLOQUE 1: AUTENTICACIÓN INSTITUCIONAL Y SESIÓN PERMANENTE (LoginScreen.kt)
    // =========================================================================
    var estaAutenticado by mutableStateOf(prefs.getBoolean("sesion_activa", false))
        private set

    var usuarioLogueado by mutableStateOf(prefs.getString("usuario_logueado", null))
        private set

    var loginEmail by mutableStateOf("")
        private set

    var loginEmailError by mutableStateOf<String?>(null)
        private set

    var loginPassword by mutableStateOf("")
        private set

    var loginPasswordError by mutableStateOf<String?>(null)
        private set

    var passwordVisible by mutableStateOf(false)
        private set

    fun onLoginEmailChange(nuevoEmail: String) {
        loginEmail = nuevoEmail
        if (loginEmailError != null) loginEmailError = null
    }

    fun onLoginPasswordChange(nuevaClave: String) {
        loginPassword = nuevaClave
        if (loginPasswordError != null) loginPasswordError = null
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    /**
     * Valida el formulario de Login y verifica las credenciales institucionales de prueba:
     * - Correo: admin@corpaliv.cl
     * - Contraseña: albalab2026
     * Si son correctas, guarda "sesion_activa = true" en SharedPreferences y navega a HomeScreen.
     */
    fun iniciarSesion() {
        var esValido = true

        if (!esCorreoValido(loginEmail)) {
            loginEmailError = "Ingrese un correo institucional válido (@corpaliv.cl)."
            esValido = false
        }

        if (loginPassword.length < 6) {
            loginPasswordError = "La contraseña debe contener al menos 6 caracteres."
            esValido = false
        }

        if (esValido) {
            if (loginEmail.trim().equals(CORREO_ADMIN_DEMO, ignoreCase = true) && loginPassword == CLAVE_ADMIN_DEMO) {
                estaAutenticado = true
                usuarioLogueado = "Administrador Alba Lab"
                loginEmail = ""
                loginPassword = ""
                loginEmailError = null
                loginPasswordError = null

                // Persiste la sesión en el disco del teléfono para no pedir login al cerrar y reabrir la app
                prefs.edit()
                    .putBoolean("sesion_activa", true)
                    .putString("usuario_logueado", "Administrador Alba Lab")
                    .apply()

                navigateTo(Screen.Home)
            } else {
                loginPasswordError = "Credenciales incorrectas (Demo: $CORREO_ADMIN_DEMO / $CLAVE_ADMIN_DEMO)."
            }
        }
    }

    /**
     * Permite a donantes o empresas explorar la aplicación sin iniciar sesión administrativa.
     */
    fun ingresarComoInvitado() {
        loginEmailError = null
        loginPasswordError = null
        navigateTo(Screen.Home)
    }

    /**
     * Cierra la sesión administrativa borrando los datos de SharedPreferences y volviendo al Login.
     */
    fun cerrarSesion() {
        estaAutenticado = false
        usuarioLogueado = null

        prefs.edit()
            .remove("sesion_activa")
            .remove("usuario_logueado")
            .apply()

        navigateTo(Screen.Login)
    }

    // =========================================================================
    // BLOQUE 2: FILTRADO DEL PANEL INSTITUCIONAL (ProfileScreen.kt)
    // =========================================================================
    var filtroSolicitudes by mutableStateOf("Todas")
        private set

    fun onFiltroSolicitudesChange(nuevoFiltro: String) {
        filtroSolicitudes = nuevoFiltro
    }

    // =========================================================================
    // BLOQUE 3: FORMULARIOS, VALIDACIONES, IMÁGENES Y ROOM (ContactoScreen.kt)
    // =========================================================================
    var tipoContacto by mutableStateOf("Empresa")
        private set

    var nombre by mutableStateOf("")
        private set

    var nombreError by mutableStateOf<String?>(null)
        private set

    var correo by mutableStateOf("")
        private set

    var correoError by mutableStateOf<String?>(null)
        private set

    var telefono by mutableStateOf("")
        private set

    var telefonoError by mutableStateOf<String?>(null)
        private set

    var mensaje by mutableStateOf("")
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    var imagenUri by mutableStateOf<Uri?>(null)
        private set

    var fotoBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var mostrarConfirmacion by mutableStateOf(false)
        private set

    /**
     * Prepara y limpia las variables antes de abrir cualquiera de los 3 formularios ("Empresa",
     * "Compra Solidaria" o "Voluntariado"). Si el usuario viene desde el catálogo, precarga el producto.
     */
    fun prepararFormulario(tipo: String, mensajeInicial: String = "") {
        tipoContacto = tipo
        nombre = ""
        correo = ""
        telefono = ""
        mensaje = mensajeInicial
        nombreError = null
        correoError = null
        telefonoError = null
        mensajeError = null
        eliminarImagen()
    }

    fun onNombreChange(nuevoValor: String) {
        nombre = nuevoValor
        if (nombreError != null) nombreError = null
    }

    fun onCorreoChange(nuevoValor: String) {
        correo = nuevoValor
        if (correoError != null) correoError = null
    }

    fun onTelefonoChange(nuevoValor: String) {
        telefono = nuevoValor
        if (telefonoError != null) telefonoError = null
    }

    fun onMensajeChange(nuevoValor: String) {
        mensaje = nuevoValor
        if (mensajeError != null) mensajeError = null
    }

    fun onImagenSeleccionada(uri: Uri?) {
        imagenUri = uri
        fotoBitmap = null
    }

    fun onFotoCapturada(bitmap: Bitmap?) {
        fotoBitmap = bitmap
        imagenUri = null
    }

    fun eliminarImagen() {
        imagenUri = null
        fotoBitmap = null
    }

    /**
     * Guarda físicamente la fotografía (capturada con Cámara o elegida de Galería) como un archivo .jpg
     * dentro de la carpeta privada de la aplicación (filesDir/imagenes_solicitudes).
     */
    private fun guardarImagenEnAlmacenamientoInterno(bitmap: Bitmap?, uri: Uri?): String? {
        val context = getApplication<Application>().applicationContext
        val bitmapAGuardar: Bitmap = when {
            bitmap != null -> bitmap
            uri != null -> {
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                } catch (e: Exception) {
                    null
                } ?: return null
            }
            else -> return null
        }

        return try {
            val directorioImagenes = File(context.filesDir, "imagenes_solicitudes")
            if (!directorioImagenes.exists()) {
                directorioImagenes.mkdirs()
            }
            val archivoDestino = File(directorioImagenes, "adjunto_${System.currentTimeMillis()}.jpg")
            FileOutputStream(archivoDestino).use { outputStream ->
                bitmapAGuardar.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                outputStream.flush()
            }
            archivoDestino.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Valida de forma centralizada todos los campos del formulario activo.
     * Si todos cumplen las reglas, guarda la imagen en almacenamiento interno y registra la solicitud
     * en SQLite usando una corrutina en segundo plano (Dispatchers.IO).
     */
    fun enviarFormulario() {
        var esValido = true

        // 1. Validación de nombre o razón social (mínimo 4 letras para Empresa, 3 para personas)
        val minNombre = if (tipoContacto == "Empresa") 4 else 3
        if (nombre.trim().length < minNombre) {
            nombreError = if (tipoContacto == "Empresa") {
                "Ingrese razón social o empresa válida (mínimo 4 caracteres)."
            } else {
                "El nombre completo debe tener al menos 3 caracteres."
            }
            esValido = false
        }

        // 2. Validación de formato de correo electrónico usando la función centralizada
        if (!esCorreoValido(correo)) {
            correoError = "Ingrese un correo electrónico válido."
            esValido = false
        }

        // 3. Validación de teléfono usando la función centralizada
        if (!esTelefonoValido(telefono)) {
            telefonoError = "Ingrese un número de teléfono válido (mínimo 8 dígitos)."
            esValido = false
        }

        // 4. Validación de mensaje o requerimiento según el tipo de formulario
        val minMensaje = if (tipoContacto == "Compra Solidaria") 5 else 10
        if (mensaje.trim().length < minMensaje) {
            mensajeError = when (tipoContacto) {
                "Empresa" -> "Detalle de propuesta corporativa o requerimiento (mínimo 10 caracteres)."
                "Compra Solidaria" -> "Indique los productos o cantidades a cotizar (mínimo 5 caracteres)."
                else -> "Disponibilidad horaria y motivación para voluntariado (mínimo 10 caracteres)."
            }
            esValido = false
        }

        if (esValido) {
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fechaActual = formatoFecha.format(Date())

            val nombreLimpio = nombre.trim()
            val correoLimpio = correo.trim()
            val telefonoLimpio = telefono.trim()
            val mensajeLimpio = mensaje.trim()
            val tipoActual = tipoContacto
            val bitmapCapturado = fotoBitmap
            val uriSeleccionada = imagenUri

            viewModelScope.launch(Dispatchers.IO) {
                val rutaImagenGuardada = guardarImagenEnAlmacenamientoInterno(bitmapCapturado, uriSeleccionada)

                val nuevaSolicitud = SolicitudEntity(
                    nombre = nombreLimpio,
                    correo = correoLimpio,
                    telefono = telefonoLimpio,
                    mensaje = mensajeLimpio,
                    fecha = fechaActual,
                    tipoContacto = tipoActual,
                    imagenPath = rutaImagenGuardada
                )

                solicitudDao.insertarSolicitud(nuevaSolicitud)
            }
            mostrarConfirmacion = true
        }
    }

    fun cerrarConfirmacion() {
        mostrarConfirmacion = false
        nombre = ""
        correo = ""
        telefono = ""
        mensaje = ""
        eliminarImagen()
    }

    /**
     * Elimina una solicitud específica por su ID en SQLite y borra también su fotografía asociada
     * del almacenamiento interno para no dejar archivos huérfanos en el teléfono.
     */
    fun eliminarSolicitudPorId(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val solicitud = solicitudDao.obtenerSolicitudPorId(id)
            solicitud?.imagenPath?.let { ruta ->
                try {
                    val archivo = File(ruta)
                    if (archivo.exists()) archivo.delete()
                } catch (_: Exception) { }
            }
            solicitudDao.eliminarSolicitudPorId(id)
        }
    }

    /**
     * Vacía todas las solicitudes de la tabla SQLite y limpia la carpeta de fotografías adjuntas.
     * SEGURIDAD Y CONTROL DE ROLES:
     * Verifica estrictamente que exista una sesión administrativa activa (estaAutenticado == true)
     * antes de despachar la corrutina de borrado. Si un usuario en modo invitado intenta ejecutar
     * esta acción, el método se detiene de inmediato protegiendo la base de datos local y el almacenamiento físico.
     */
    fun limpiarHistorialSolicitudes() {
        if (!estaAutenticado) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                val directorioImagenes = File(context.filesDir, "imagenes_solicitudes")
                if (directorioImagenes.exists()) {
                    directorioImagenes.listFiles()?.forEach { it.delete() }
                }
            } catch (_: Exception) { }
            solicitudDao.eliminarTodasLasSolicitudes()
        }
    }

    // =========================================================================
    // BLOQUE 4: BÚSQUEDA Y FILTRADO DEL CATÁLOGO (CatalogoScreen.kt)
    // =========================================================================
    var queryBusqueda by mutableStateOf("")
        private set

    var busquedaError by mutableStateOf<String?>(null)
        private set

    var categoriaSeleccionada by mutableStateOf("Todos")
        private set

    fun onQueryBusquedaChange(nuevoTexto: String) {
        queryBusqueda = nuevoTexto
        busquedaError = if (!esTextoBusquedaValido(nuevoTexto)) {
            "No se permiten caracteres especiales."
        } else {
            null
        }
    }

    fun limpiarBusqueda() {
        queryBusqueda = ""
        busquedaError = null
    }

    fun onCategoriaSeleccionada(categoria: String) {
        categoriaSeleccionada = categoria
    }

    // =========================================================================
    // BLOQUE 5: SIMULADOR DE IMPACTO SOCIAL (SettingsScreen.kt)
    // =========================================================================
    var montoSimulado by mutableStateOf("")
        private set

    var montoSimuladoError by mutableStateOf<String?>(null)
        private set

    var resultadoImpacto by mutableStateOf<String?>(null)
        private set

    fun onMontoSimuladoChange(nuevoMonto: String) {
        montoSimulado = nuevoMonto
        resultadoImpacto = null
        if (nuevoMonto.isEmpty()) {
            montoSimuladoError = null
            return
        }
        if (!nuevoMonto.all { it.isDigit() }) {
            montoSimuladoError = "Ingrese únicamente números sin puntos ni símbolos."
        } else {
            val valor = nuevoMonto.toLongOrNull() ?: 0L
            if (valor < MONTO_MINIMO_SIMULACION) {
                montoSimuladoError = "El aporte mínimo para simulación es de $5.000 CLP."
            } else {
                montoSimuladoError = null
            }
        }
    }

    /**
     * Calcula la equivalencia social de un aporte monetario en CLP delegando el cálculo
     * a las funciones centralizadas del companion object (COSTO_REFERENCIAL_KIT_INSUMOS = $5.000 CLP
     * y COSTO_REFERENCIAL_SESION_APOYO = $15.000 CLP).
     */
    fun calcularImpactoSocial() {
        val valor = montoSimulado.toLongOrNull()
        if (valor == null || valor < MONTO_MINIMO_SIMULACION) {
            montoSimuladoError = "Ingrese un monto válido igual o superior a $5.000 CLP."
            resultadoImpacto = null
        } else {
            resultadoImpacto = generarTextoImpactoSocial(valor)
        }
    }
}