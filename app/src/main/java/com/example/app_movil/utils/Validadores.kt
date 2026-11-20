package com.example.app_movil.utils

object Validadores {
    // Expresiones regulares para formato de correo y búsqueda sin símbolos especiales
    private val REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    private val REGEX_BUSQUEDA = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]*\$".toRegex()

    // Comprueba que el correo tenga formato válido (limpiando espacios en los bordes)
    fun esCorreoValido(correo: String): Boolean {
        return correo.trim().matches(REGEX_EMAIL)
    }

    // Verifica que el teléfono tenga al menos 8 caracteres y solo contenga números o '+'
    fun esTelefonoValido(telefono: String): Boolean {
        val limpio = telefono.trim()
        return limpio.length >= 8 && limpio.all { it.isDigit() || it == '+' }
    }

    // Revisa que el texto del buscador no incluya caracteres especiales
    fun esTextoBusquedaValido(texto: String): Boolean {
        return texto.matches(REGEX_BUSQUEDA)
    }
}