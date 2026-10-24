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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val solicitudDao = database.solicitudDao()

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

    // --- MÓDULO DE AUTENTICACIÓN / LOGIN ---

    var estaAutenticado by mutableStateOf(false)
        private set

    var usuarioLogueado by mutableStateOf<String?>(null)
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

    fun iniciarSesion() {
        var esValido = true

        val regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        if (!loginEmail.trim().matches(regexEmail)) {
            loginEmailError = "Ingrese un correo institucional válido (@corpaliv.cl)."
            esValido = false
        }

        if (loginPassword.length < 6) {
            loginPasswordError = "La contraseña debe contener al menos 6 caracteres."
            esValido = false
        }

        if (esValido) {
            if (loginEmail.trim().equals("admin@corpaliv.cl", ignoreCase = true) && loginPassword == "albalab2026") {
                estaAutenticado = true
                usuarioLogueado = "Equipo Vinculación CORPALIV"
                loginEmail = ""
                loginPassword = ""
                // Entra a la pantalla de inicio con sesión iniciada
                navigateTo(Screen.Home)
            } else {
                loginPasswordError = "Credenciales incorrectas (Demo: admin@corpaliv.cl / albalab2026)."
            }
        }
    }

    fun ingresarComoInvitado() {
        estaAutenticado = false
        usuarioLogueado = null
        navigateTo(Screen.Home)
    }

    fun cerrarSesion() {
        estaAutenticado = false
        usuarioLogueado = null
        // Vuelve a la pantalla de login
        navigateTo(Screen.Login)
    }

    // --- FORMULARIO DE VINCULACIÓN ADAPTATIVO ---

    var tipoContactoSeleccionado by mutableStateOf("Empresa")
        private set

    fun onTipoContactoSeleccionado(nuevoTipo: String) {
        tipoContactoSeleccionado = nuevoTipo
        nombreError = null
        correoError = null
        telefonoError = null
        mensajeError = null
    }

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

        val minNombre = if (tipoContactoSeleccionado == "Empresa") 4 else 3
        if (nombre.trim().length < minNombre) {
            nombreError = if (tipoContactoSeleccionado == "Empresa") {
                "Ingrese razón social o empresa válida (mínimo 4 caracteres)."
            } else {
                "El nombre completo debe tener al menos 3 caracteres."
            }
            esValido = false
        }

        val regexCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        if (!correo.trim().matches(regexCorreo)) {
            correoError = "Ingrese un correo electrónico válido (ejemplo@empresa.cl)."
            esValido = false
        }

        if (telefono.trim().length < 8 || !telefono.all { it.isDigit() || it == '+' }) {
            telefonoError = "Ingrese un número telefónico válido (mínimo 8 dígitos)."
            esValido = false
        }

        val minMensaje = if (tipoContactoSeleccionado == "Compra Solidaria") 5 else 10
        if (mensaje.trim().length < minMensaje) {
            mensajeError = when (tipoContactoSeleccionado) {
                "Empresa" -> "Detalle de propuesta corporativa o requerimiento (mínimo 10 caracteres)."
                "Compra Solidaria" -> "Indique los productos o cantidades a cotizar (mínimo 5 caracteres)."
                else -> "Disponibilidad horaria y motivación para voluntariado (mínimo 10 caracteres)."
            }
            esValido = false
        }

        if (esValido) {
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fechaActual = formatoFecha.format(Date())

            val nuevaSolicitud = SolicitudEntity(
                tipoContacto = tipoContactoSeleccionado,
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

    fun eliminarSolicitudPorId(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            solicitudDao.eliminarSolicitudPorId(id)
        }
    }

    fun limpiarHistorialSolicitudes() {
        viewModelScope.launch(Dispatchers.IO) {
            solicitudDao.eliminarTodasLasSolicitudes()
        }
    }

    // --- CATÁLOGO: BÚSQUEDA Y FILTRADO ---

    var queryBusqueda by mutableStateOf("")
        private set

    var busquedaError by mutableStateOf<String?>(null)
        private set

    var categoriaSeleccionada by mutableStateOf("Todos")
        private set

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

    // --- SIMULADOR DE IMPACTO SOCIAL ---

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
            if (valor < 5000) {
                montoSimuladoError = "El aporte mínimo para simulación es de $5.000 CLP."
            } else {
                montoSimuladoError = null
            }
        }
    }

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