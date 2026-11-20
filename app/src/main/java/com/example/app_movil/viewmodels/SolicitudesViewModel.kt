package com.example.app_movil.viewmodels

import android.app.Application
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
import com.example.app_movil.utils.Validadores
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SolicitudesViewModel(application: Application) : AndroidViewModel(application) {

    // Conexión con Room SQLite y acceso al DAO
    private val database = AppDatabase.getDatabase(application)
    private val solicitudDao = database.solicitudDao()

    // Flujo en tiempo real con todas las solicitudes guardadas en SQLite
    val listaSolicitudes: StateFlow<List<SolicitudEntity>> = solicitudDao
        .obtenerTodasLasSolicitudes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    // Manejadores de cambio de texto que limpian errores al escribir
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

    // Manejadores para guardar o quitar la imagen elegida de Galería o Cámara
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
            if (!directorioImagenes.exists()) directorioImagenes.mkdirs()

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

    // Valida todos los campos e inserta la solicitud en SQLite en segundo plano (Dispatchers.IO)
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
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
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

    // Elimina una solicitud de SQLite y borra su foto asociada del almacenamiento interno
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

    // Vacía toda la tabla SQLite y limpia la carpeta de fotos (solo si hay sesión de administrador)
    fun limpiarHistorialSolicitudes(estaAutenticado: Boolean) {
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
}