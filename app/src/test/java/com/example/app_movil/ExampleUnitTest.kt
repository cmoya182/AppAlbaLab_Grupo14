package com.example.app_movil

import com.example.app_movil.data.remote.RenderedTextDto
import com.example.app_movil.data.remote.WordPressPostDto
import com.example.app_movil.data.repository.NoticiasRepository
import com.example.app_movil.navigation.Screen
import com.example.app_movil.utils.Validadores
import com.example.app_movil.viewmodels.AuthViewModel
import com.example.app_movil.viewmodels.SimuladorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    // Verifica que la validación de correo electrónico acepte formatos válidos y rechace incompletos
    @Test
    fun validarCorreo_utilizaReglaRealDeValidadores() {
        assertTrue(Validadores.esCorreoValido(AuthViewModel.CORREO_ADMIN_DEMO))
        assertFalse(Validadores.esCorreoValido("correo_invalido@"))
    }

    // Verifica que la validación de teléfono exija mínimo 8 caracteres numéricos
    @Test
    fun validarTelefono_utilizaReglaRealDeValidadores() {
        assertTrue(Validadores.esTelefonoValido("+56912345678"))
        assertFalse(Validadores.esTelefonoValido("12345"))
    }

    // Verifica el cálculo de kits de insumos y sesiones del simulador de impacto social
    @Test
    fun simuladorImpactoSocial_utilizaCalculoReferencialDelViewModel() {
        val montoPrueba = 30000L
        assertEquals(6, SimuladorViewModel.calcularKitsInsumos(montoPrueba))
        assertEquals(2, SimuladorViewModel.calcularSesionesApoyo(montoPrueba))
    }

    // Comprueba que solo el Panel Institucional esté marcado como exclusivo de administrador
    @Test
    fun controlDeAcceso_rutasProtegidasPorRol() {
        assertTrue(Screen.Profile.requiereAdministrador)
        assertFalse(Screen.Noticias.requiereAdministrador)
        assertFalse(Screen.Catalogo.requiereAdministrador)
    }

    // Comprueba la transformación de fecha ISO y mapeo de DTO a modelo Noticia
    @Test
    fun noticiasRepository_formateoFechaYLimpiezaDto() {
        assertEquals("14/08/2026", NoticiasRepository.formatearFechaIso("2026-08-14T15:30:00"))
        val dto = WordPressPostDto(
            id = 10,
            date = "2026-10-05T10:00:00",
            link = "https://corpaliv.cl/noticia-test",
            title = RenderedTextDto("Taller Alba Lab"),
            excerpt = RenderedTextDto("Resumen oficial")
        )
        val noticia = NoticiasRepository.mapearDtoANoticia(dto)
        assertEquals(10, noticia.id)
        assertEquals("05/10/2026", noticia.fechaPublicacion)
        assertEquals("https://corpaliv.cl/noticia-test", noticia.enlace)
    }
}