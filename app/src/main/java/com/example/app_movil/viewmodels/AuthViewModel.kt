package com.example.app_movil.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.app_movil.utils.Validadores

// Roles disponibles en la aplicación para separar permisos y experiencia de usuario
enum class UserRole {
    INVITADO,
    ADMINISTRADOR
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Credenciales institucionales de demostración
    companion object {
        const val CORREO_ADMIN_DEMO = "admin@corpaliv.cl"
        const val CLAVE_ADMIN_DEMO = "albalab2026"
    }

    // Preferencias locales para persistir la sesión administrativa en disco
    private val prefs = application.getSharedPreferences("alba_lab_session_prefs", Context.MODE_PRIVATE)

    // Estado de autenticación y rol activo derivado de la sesión
    var estaAutenticado by mutableStateOf(prefs.getBoolean("sesion_activa", false))
        private set

    val rolActual: UserRole
        get() = if (estaAutenticado) UserRole.ADMINISTRADOR else UserRole.INVITADO

    var usuarioLogueado by mutableStateOf(prefs.getString("usuario_logueado", null))
        private set

    // Estados de los campos y errores del formulario de inicio de sesión
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

    // Manejadores de entrada de texto y visibilidad de contraseña
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

    // Valida credenciales, activa el rol de administrador y guarda la sesión
    fun iniciarSesion(onExito: () -> Unit) {
        var esValido = true

        if (!Validadores.esCorreoValido(loginEmail)) {
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

                prefs.edit()
                    .putBoolean("sesion_activa", true)
                    .putString("usuario_logueado", "Administrador Alba Lab")
                    .apply()

                onExito()
            } else {
                loginPasswordError = "Credenciales incorrectas (Demo: $CORREO_ADMIN_DEMO / $CLAVE_ADMIN_DEMO)."
            }
        }
    }

    // Limpia errores visuales al continuar como invitado
    fun limpiarErrores() {
        loginEmailError = null
        loginPasswordError = null
    }

    // Revoca el rol de administrador y borra la sesión de SharedPreferences
    fun cerrarSesion(onCerrar: () -> Unit) {
        estaAutenticado = false
        usuarioLogueado = null

        prefs.edit()
            .remove("sesion_activa")
            .remove("usuario_logueado")
            .apply()

        onCerrar()
    }
}