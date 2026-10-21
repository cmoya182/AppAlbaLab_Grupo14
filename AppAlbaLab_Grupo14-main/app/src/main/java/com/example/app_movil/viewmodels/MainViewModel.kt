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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ViewModel que centraliza la navegación, validación de formularios, persistencia en Room y recursos multimedia
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val solicitudDao = database.solicitudDao()

    // Flujo de datos reactivo con la lista de solicitudes desde Room
    val listaSolicitudes: StateFlow<List<SolicitudEntity>> = solicitudDao
        .obtenerTodasLasSolicitudes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    fun navigateTo(screen: Screen) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateTo(route = screen))
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    fun navigateUp() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }

    // --- ESTADO DEL FORMULARIO DE VINCULACIÓN ---

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

    fun enviarFormulario() {
        var esValido = true

        if (nombre.trim().length < 3) {
            nombreError = "El nombre o empresa debe tener al menos 3 caracteres."
            esValido = false
        }

        val regexCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        if (!correo.trim().matches(regexCorreo)) {
            correoError = "Ingrese un correo electrónico válido (ejemplo@empresa.cl)."
            esValido = false
        }

        if (telefono.trim().length < 8 || !telefono.all { it.isDigit() || it == '+' }) {
            telefonoError = "Ingrese un número telefónico válido de al menos 8 dígitos."
            esValido = false
        }

        if (mensaje.trim().length < 10) {
            mensajeError = "Por favor detalle su propuesta o consulta (mínimo 10 caracteres)."
            esValido = false
        }

        if (esValido) {
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fechaActual = formatoFecha.format(Date())

            val nuevaSolicitud = SolicitudEntity(
                nombre = nombre.trim(),
                correo = correo.trim(),
                telefono = telefono.trim(),
                mensaje = mensaje.trim(),
                fecha = fechaActual
            )

            viewModelScope.launch(Dispatchers.IO) {
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

    // Elimina una solicitud puntual por ID
    fun eliminarSolicitudPorId(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            solicitudDao.eliminarSolicitudPorId(id)
        }
    }

    // Vacía toda la tabla en Room
    fun limpiarHistorialSolicitudes() {
        viewModelScope.launch(Dispatchers.IO) {
            solicitudDao.eliminarTodasLasSolicitudes()
        }
    }

    // --- ESTADO DE BÚSQUEDA Y FILTRADO DEL CATÁLOGO ---

    var queryBusqueda by mutableStateOf("")
        private set

    var busquedaError by mutableStateOf<String?>(null)
        private set

    var categoriaSeleccionada by mutableStateOf("Todos")
        private set

    // Actualiza la búsqueda y valida que no incluya caracteres inválidos
    fun onQueryBusquedaChange(nuevoTexto: String) {
        queryBusqueda = nuevoTexto
        val regexValido = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]*$".toRegex()
        busquedaError = if (!nuevoTexto.matches(regexValido)) {
            "No se permiten caracteres especiales en la búsqueda."
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
// --- SIMULADOR DE IMPACTO SOCIAL (APORTE CORPORATIVO) ---

    var montoSimulado by mutableStateOf("")
        private set

    var montoSimuladoError by mutableStateOf<String?>(null)
        private set

    var resultadoImpacto by mutableStateOf<String?>(null)
        private set

    // Valida el monto ingresado y calcula cuántos kits de taller financia el aporte
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
            if (valor < 5000) {
                montoSimuladoError = "El aporte mínimo para simulación es de $5.000 CLP."
            } else {
                montoSimuladoError = null
            }
        }
    }

    // Ejecuta el cálculo de impacto social para mostrarlo en la interfaz
    fun calcularImpactoSocial() {
        val valor = montoSimulado.toLongOrNull()
        if (valor == null || valor < 5000) {
            montoSimuladoError = "Ingrese un monto válido igual o superior a $5.000 CLP."
            resultadoImpacto = null
        } else {
            val insumosTaller = (valor / 5000).toInt()
            val horasTerapia = (valor / 15000).toInt()
            resultadoImpacto = "Con un aporte de $$valor CLP financias aproximadamente $insumosTaller kits de insumos para el taller laboral" +
                    if (horasTerapia > 0) " y $horasTerapia sesiones de apoyo sociolaboral." else "."
        }
    }
}