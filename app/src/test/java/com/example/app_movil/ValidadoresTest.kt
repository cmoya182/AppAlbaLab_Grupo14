package com.example.app_movil

import com.example.app_movil.utils.Validadores
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Suite de pruebas unitarias para todas las reglas de validación de entradas del usuario
class ValidadoresTest {

    // Comprueba que correos institucionales y personales válidos sean aceptados
    @Test
    fun esCorreoValido_conFormatosCorrectos_retornaTrue() {
        assertTrue(Validadores.esCorreoValido("admin@corpaliv.cl"))
        assertTrue(Validadores.esCorreoValido("  contacto.empresa@gmail.com  "))
        assertTrue(Validadores.esCorreoValido("voluntario_2026@duocuc.cl"))
    }

    // Comprueba que correos incompletos, vacíos o sin dominio sean rechazados
    @Test
    fun esCorreoValido_conFormatosIncorrectos_retornaFalse() {
        assertFalse(Validadores.esCorreoValido(""))
        assertFalse(Validadores.esCorreoValido("usuario_sin_arroba.cl"))
        assertFalse(Validadores.esCorreoValido("usuario@dominio"))
        assertFalse(Validadores.esCorreoValido("@corpaliv.cl"))
    }

    // Comprueba aceptación de teléfonos válidos de 8 o más dígitos con o sin '+'
    @Test
    fun esTelefonoValido_conNumerosValidos_retornaTrue() {
        assertTrue(Validadores.esTelefonoValido("+56912345678"))
        assertTrue(Validadores.esTelefonoValido("98765432"))
        assertTrue(Validadores.esTelefonoValido("  +56223456789  "))
    }

    // Comprueba rechazo de teléfonos cortos, vacíos o con letras
    @Test
    fun esTelefonoValido_conNumerosInvalidos_retornaFalse() {
        assertFalse(Validadores.esTelefonoValido("1234567"))
        assertFalse(Validadores.esTelefonoValido("98765abc"))
        assertFalse(Validadores.esTelefonoValido(""))
    }

    // Comprueba que el buscador del catálogo bloquee caracteres especiales o inyecciones
    @Test
    fun esTextoBusquedaValido_filtraSimbolosEspeciales() {
        assertTrue(Validadores.esTextoBusquedaValido("Difusor Aromático 150"))
        assertTrue(Validadores.esTextoBusquedaValido("Tazón Ñandú"))
        assertFalse(Validadores.esTextoBusquedaValido("Bolsa @#$%"))
        assertFalse(Validadores.esTextoBusquedaValido("SELECT * FROM;"))
    }
}
