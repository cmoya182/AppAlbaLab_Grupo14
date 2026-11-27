package com.example.app_movil

import com.example.app_movil.navigation.NavigationEvent
import com.example.app_movil.navigation.Screen
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.viewmodels.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Pruebas unitarias de MainViewModel: control de acceso por rol y eventos de navegación
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Un usuario invitado que intenta abrir el Panel Institucional debe ser redirigido a Noticias
    @Test
    fun navigateTo_invitadoEnRutaProtegida_redirigeANoticias() = runTest {
        val viewModel = MainViewModel()
        val eventos = mutableListOf<NavigationEvent>()
        val colector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.navigateTo(screen = Screen.Profile, rolActual = UserRole.INVITADO)
        advanceUntilIdle()

        assertEquals(1, eventos.size)
        val evento = eventos.first() as NavigationEvent.NavigateTo
        assertEquals(Screen.Noticias, evento.route)
        assertFalse(evento.clearBackStack)
        colector.cancel()
    }

    // Un administrador autorizado puede ingresar sin restricciones al Panel Institucional
    @Test
    fun navigateTo_administradorEnRutaProtegida_permiteAccesoAPanel() = runTest {
        val viewModel = MainViewModel()
        val eventos = mutableListOf<NavigationEvent>()
        val colector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.navigateTo(
            screen = Screen.Profile,
            rolActual = UserRole.ADMINISTRADOR,
            clearBackStack = true
        )
        advanceUntilIdle()

        assertEquals(1, eventos.size)
        val evento = eventos.first() as NavigationEvent.NavigateTo
        assertEquals(Screen.Profile, evento.route)
        assertTrue(evento.clearBackStack)
        colector.cancel()
    }

    // Un invitado puede navegar libremente a rutas públicas como la Tienda Solidaria
    @Test
    fun navigateTo_invitadoEnRutaPublica_navegaADestinoSolicitado() = runTest {
        val viewModel = MainViewModel()
        val eventos = mutableListOf<NavigationEvent>()
        val colector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.navigateTo(screen = Screen.Catalogo, rolActual = UserRole.INVITADO)
        advanceUntilIdle()

        assertEquals(1, eventos.size)
        val evento = eventos.first() as NavigationEvent.NavigateTo
        assertEquals(Screen.Catalogo, evento.route)
        colector.cancel()
    }

    // Valida que navigateBack emita el evento PopBackStack hacia MainActivity
    @Test
    fun navigateBack_emiteEventoPopBackStack() = runTest {
        val viewModel = MainViewModel()
        val eventos = mutableListOf<NavigationEvent>()
        val colector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.navigateBack()
        advanceUntilIdle()

        assertEquals(1, eventos.size)
        assertEquals(NavigationEvent.PopBackStack, eventos.first())
        colector.cancel()
    }
}
