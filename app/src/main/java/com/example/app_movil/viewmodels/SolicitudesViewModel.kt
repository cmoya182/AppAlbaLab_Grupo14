package com.example.app_movil.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_movil.data.local.AppDatabase
import com.example.app_movil.data.local.SolicitudEntity
import com.example.app_movil.data.repository.SolicitudesRepository
import com.example.app_movil.utils.BitmapUtils
import com.example.app_movil.utils.Validadores
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SolicitudesViewModel(application: Application) : AndroidViewModel(application) {

    // Conexión al repositorio que une Room SQLite con el microservicio Spring Boot (Retrofit)
    private val repository = SolicitudesRepository(
        solicitudDao = AppDatabase.getDatabase(application).solicitudDao()
    )

    // Flujo en tiempo real con todas las solicitudes visibles en el Panel Institucional
    val listaSolicitudes: StateFlow<List<SolicitudEntity>> = repository
        .observarSolicitudes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Indicadores de sincronización con el microservicio Spring Boot
    var sincronizandoServidor by mutableStateOf(false)
        private set

    var estadoSincronizacion by mutableStateOf("Sincronizado con alba-lab-api")
        private set

    // Marca temporal para evitar llamadas repetitivas de red al cambiar rápido entre pestañas
    private var ultimoSyncExitosoMs: Long = 0L

    // Filtro activo en el Panel Institucional
    var filtroSolicitudes by mutableStateOf("Todas")
        private set

    fun onFiltroSolicitudesChange(nuevoFiltro: String) {
        filtroSolicitudes = nuevoFiltro
    }

    // Variables de estado de los 4 campos del formulario, foto adjunta y diálogo de éxito
    var tipoContacto by mutableStateOf("")
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

    // Evita saturar la red y Room al alternar pestañas en menos de 15 segundos
    fun sincronizarSiNecesario(intervaloMinimoMs: Long = 15_000L) {
        val ahora = System.currentTimeMillis()
        if (ultimoSyncExitosoMs > 0L && (ahora - ultimoSyncExitosoMs) < intervaloMinimoMs) {
            return
        }
        sincronizarDesdeServidor()
    }

    // GET /api/solicitudes -> Sincroniza el Panel Institucional con el microservicio Spring Boot
    fun sincronizarDesdeServidor() {
        if (sincronizandoServidor) return
        sincronizandoServidor = true
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                repository.sincronizarConServidor()
            }
            sincronizandoServidor = false
            estadoSincronizacion = resultado.fold(
                onSuccess = { total ->
                    ultimoSyncExitosoMs = System.currentTimeMillis()
                    "Conectado a alba-lab-api ($total en servidor)"
                },
                onFailure = { e -> "Modo local (GET) · ${e::class.simpleName}: ${e.message}" }
            )
        }
    }

    // Reinicia el formulario fijando el canal ("Empresa", "Compra Solidaria" o "Voluntariado")
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

    // Guarda la foto físicamente como archivo .jpg en filesDir y devuelve su ruta
    private fun guardarImagenEnAlmacenamientoInterno(bitmap: Bitmap?, uri: Uri?): String? {
        val context = getApplication<Application>().applicationContext
        val bitmapAGuardar: Bitmap = when {
            bitmap != null -> bitmap
            uri != null -> BitmapUtils.decodificarUriMuestreada(
                context = context,
                uri = uri,
                reqWidth = 1280,
                reqHeight = 1280
            ) ?: return null
            else -> return null
        }

        return try {
            val directorioImagenes = File(context.filesDir, "imagenes_solicitudes")
            if (!directorioImagenes.exists()) directorioImagenes.mkdirs()

            val archivoDestino = File(directorioImagenes, "adjunto_${System.currentTimeMillis()}.jpg")
            FileOutputStream(archivoDestino).use { outputStream ->
                bitmapAGuardar.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                outputStream.flush()
            }
            archivoDestino.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    // POST /api/solicitudes -> Valida el formulario, envía al microservicio Spring Boot y respalda en Room
    fun enviarFormulario() {
        var esValido = true

        val minNombre = if (tipoContacto == "Empresa") 4 else 3
        if (nombre.trim().length < minNombre) {
            nombreError = if (tipoContacto == "Empresa") {
                "Ingrese razón social o empresa válida (mínimo 4 caracteres)."
            } else {
                "El nombre completo debe tener al menos 3 caracteres."
            }
            esValido = false
        }

        if (!Validadores.esCorreoValido(correo)) {
            correoError = "Ingrese un correo electrónico válido."
            esValido = false
        }

        if (!Validadores.esTelefonoValido(telefono)) {
            telefonoError = "Ingrese un número de teléfono válido (mínimo 8 dígitos)."
            esValido = false
        }

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
            val nombreLimpio = nombre.trim()
            val correoLimpio = correo.trim()
            val telefonoLimpio = telefono.trim()
            val mensajeLimpio = mensaje.trim()
            val tipoActual = tipoContacto
            val bitmapCapturado = fotoBitmap
            val uriSeleccionada = imagenUri

            viewModelScope.launch {
                val resultado = withContext(Dispatchers.IO) {
                    val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                    val rutaImagenGuardada = guardarImagenEnAlmacenamientoInterno(bitmapCapturado, uriSeleccionada)
                    val nuevaSolicitud = SolicitudEntity(
                        nombre = nombreLimpio,
                        correo = correoLimpio,
                        telefono = telefonoLimpio,
                        mensaje = mensajeLimpio,
                        fecha = fechaActual,
                        tipoContacto = tipoActual,
                        imagenPath = rutaImagenGuardada,
                        estado = "PENDIENTE"
                    )
                    repository.crearSolicitud(nuevaSolicitud)
                }
                estadoSincronizacion = resultado.fold(
                    onSuccess = {
                        ultimoSyncExitosoMs = System.currentTimeMillis()
                        "Solicitud #${it.remoteId} enviada a alba-lab-api"
                    },
                    onFailure = { e -> "Guardada local (POST) · ${e::class.simpleName}: ${e.message}" }
                )
            }
            mostrarConfirmacion = true
        }
    }

    // PUT /api/solicitudes/{id} -> Alterna el estado entre PENDIENTE y ATENDIDA en servidor y Room
    fun alternarEstadoSolicitud(solicitud: SolicitudEntity) {
        val nuevoEstado = if (solicitud.estado.equals("ATENDIDA", ignoreCase = true)) {
            "PENDIENTE"
        } else {
            "ATENDIDA"
        }
        actualizarSolicitud(solicitud.copy(estado = nuevoEstado))
    }

    // PUT /api/solicitudes/{id} -> Edita mensaje y/o estado de una solicitud en Spring Boot y Room
    fun actualizarSolicitud(solicitudActualizada: SolicitudEntity) {
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                repository.actualizarSolicitud(solicitudActualizada)
            }
            estadoSincronizacion = resultado.fold(
                onSuccess = { "Solicitud #${it.remoteId ?: it.id} actualizada (${it.estado})" },
                onFailure = { e -> "Actualizada local (PUT) · ${e::class.simpleName}: ${e.message}" }
            )
        }
    }

    // Cierra el diálogo de confirmación y limpia el formulario
    fun cerrarConfirmacion() {
        mostrarConfirmacion = false
        tipoContacto = ""
        nombre = ""
        correo = ""
        telefono = ""
        mensaje = ""
        eliminarImagen()
    }

    // DELETE /api/solicitudes/{id} -> Elimina la solicitud en Spring Boot, en Room y borra su foto local
    fun eliminarSolicitudPorId(id: Int) {
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                val res = repository.eliminarSolicitud(id)
                res.getOrNull()?.let { ruta ->
                    BitmapUtils.invalidarCache(ruta)
                    try {
                        val archivo = File(ruta)
                        if (archivo.exists()) archivo.delete()
                    } catch (_: Exception) { }
                }
                res
            }
            estadoSincronizacion = resultado.fold(
                onSuccess = { "Solicitud eliminada en alba-lab-api y local" },
                onFailure = { e -> "Eliminada local (DELETE) · ${e::class.simpleName}: ${e.message}" }
            )
        }
    }

    // Vacía las solicitudes remotas y locales junto con las fotos internas (solo administrador)
    fun limpiarHistorialSolicitudes(estaAutenticado: Boolean) {
        if (!estaAutenticado) return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val context = getApplication<Application>().applicationContext
                    val directorioImagenes = File(context.filesDir, "imagenes_solicitudes")
                    if (directorioImagenes.exists()) {
                        directorioImagenes.listFiles()?.forEach { it.delete() }
                    }
                } catch (_: Exception) { }
                BitmapUtils.limpiarCache()
                repository.eliminarTodasLasSolicitudes()
            }
            estadoSincronizacion = "Historial vaciado en servidor y dispositivo"
        }
    }
}