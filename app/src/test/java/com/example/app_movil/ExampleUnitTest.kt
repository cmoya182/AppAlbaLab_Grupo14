package com.example.app_movil

import com.example.app_movil.viewmodels.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ARCHIVO: ExampleUnitTest.kt
 * PROPÓSITO: Pruebas unitarias locales que se ejecutan en la máquina de desarrollo (JVM) sin requerir emulador.
 *
 * ¿CÓMO FUNCIONA?:
 * En lugar de volver a escribir fórmulas o expresiones regulares independientes, invoca directamente
 * las funciones y constantes centralizadas en MainViewModel.companion, garantizando que las pruebas
 * verifiquen exactamente el mismo código que utiliza la aplicación en producción.
 */
class ExampleUnitTest {

    @Test
    fun validarCorreo_utilizaReglaRealDelViewModel() {
        assertTrue(MainViewModel.esCorreoValido(MainViewModel.CORREO_ADMIN_DEMO))
        assertFalse(MainViewModel.esCorreoValido("correo_invalido@"))
    }

    @Test
    fun validarTelefono_utilizaReglaRealDelViewModel() {
        assertTrue(MainViewModel.esTelefonoValido("+56912345678"))
        assertFalse(MainViewModel.esTelefonoValido("12345"))
    }

    @Test
    fun simuladorImpactoSocial_utilizaCalculoReferencialDelViewModel() {
        val montoPrueba = 30000L
        assertEquals(6, MainViewModel.calcularKitsInsumos(montoPrueba))
        assertEquals(2, MainViewModel.calcularSesionesApoyo(montoPrueba))
    }
}
