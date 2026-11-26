package com.example.app_movil

import com.example.app_movil.data.remote.CorpalivApiService
import com.example.app_movil.data.remote.RenderedTextDto
import com.example.app_movil.data.remote.WordPressPostDto
import com.example.app_movil.data.repository.NoticiasRepository
import com.example.app_movil.data.repository.ProductoRepository
import com.example.app_movil.navigation.Screen
import com.example.app_movil.viewmodels.CatalogoViewModel
import com.example.app_movil.viewmodels.NoticiasUiState
import com.example.app_movil.viewmodels.NoticiasViewModel
import com.example.app_movil.viewmodels.SimuladorViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Suite de pruebas unitarias para ViewModels, Repositorios y reglas de autorización usando JUnit y MockK
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsYRepositoriosTest {

    private val testDispatcher = StandardTestDispatcher()

    // Configura el despachador de pruebas antes de cada test con corrutinas
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    // Restaura el despachador principal al finalizar cada prueba
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Valida errores por monto bajo y cálculo exacto de kits y sesiones en SimuladorViewModel
    @Test
    fun simuladorViewModel_calculaImpactoYValidaMontoMinimo() {
        val vm = SimuladorViewModel()

        // Monto inferior al mínimo de $5.000 CLP debe generar error
        vm.onMontoSimuladoChange("3000")
        assertNotNull(vm.montoSimuladoError)
        vm.calcularImpactoSocial()
        assertNull(vm.resultadoImpacto)

        // Monto con letras debe generar error de formato
        vm.onMontoSimuladoChange("25mil")
        assertNotNull(vm.montoSimuladoError)

        // Monto válido de $30.000 CLP debe generar 6 kits y 2 sesiones
        vm.onMontoSimuladoChange("30000")
        assertNull(vm.montoSimuladoError)
        vm.calcularImpactoSocial()
        assertNotNull(vm.resultadoImpacto)
        assertTrue(vm.resultadoImpacto!!.contains("6 kits"))
        assertTrue(vm.resultadoImpacto!!.contains("2 sesiones"))
    }

    // Valida cambios de categoría, búsqueda válida, búsqueda con símbolos y limpieza en CatalogoViewModel
    @Test
    fun catalogoViewModel_actualizaBusquedaYCategoria() {
        val vm = CatalogoViewModel()
        assertEquals("Todos", vm.categoriaSeleccionada)

        vm.onCategoriaSeleccionada("Aromas")
        assertEquals("Aromas", vm.categoriaSeleccionada)

        vm.onQueryBusquedaChange("Difusor")
        assertNull(vm.busquedaError)

        vm.onQueryBusquedaChange("Difusor$$")
        assertNotNull(vm.busquedaError)

        vm.limpiarBusqueda()
        assertEquals("", vm.queryBusqueda)
        assertNull(vm.busquedaError)
    }

    // Verifica integridad del catálogo institucional entregado por ProductoRepository
    @Test
    fun productoRepository_retornaCatalogoConDatosValidos() {
        val productos = ProductoRepository.obtenerProductos()
        assertEquals(4, productos.size)
        assertTrue(productos.all { it.precio > 0 && it.nombre.isNotBlank() })
    }

    // Simula respuesta exitosa de Retrofit en NoticiasViewModel verificando estado Exito y fecha formateada
    @Test
    fun noticiasViewModel_conRespuestaExitosa_actualizaEstadoAExito() = runTest {
        val apiMock = mockk<CorpalivApiService>()
        coEvery { apiMock.obtenerNoticias(any(), any()) } returns listOf(
            WordPressPostDto(
                id = 101,
                date = "2026-11-10T09:00:00",
                link = "https://corpaliv.cl/noticia-101",
                title = RenderedTextDto("Inclusión Sociolaboral Alba Lab"),
                excerpt = RenderedTextDto("Nuevos talleres laborales activos.")
            )
        )

        val repo = NoticiasRepository(apiMock)
        val vm = NoticiasViewModel(repo)
        advanceUntilIdle()

        assertTrue(vm.uiState is NoticiasUiState.Exito)
        val exito = vm.uiState as NoticiasUiState.Exito
        assertEquals(1, exito.noticias.size)
        assertEquals("10/11/2026", exito.noticias.first().fechaPublicacion)
    }

    // Simula respuesta vacía desde la API para verificar estado Vacio
    @Test
    fun noticiasViewModel_conListaVacia_actualizaEstadoAVacio() = runTest {
        val apiMock = mockk<CorpalivApiService>()
        coEvery { apiMock.obtenerNoticias(any(), any()) } returns emptyList()

        val repo = NoticiasRepository(apiMock)
        val vm = NoticiasViewModel(repo)
        advanceUntilIdle()

        assertTrue(vm.uiState is NoticiasUiState.Vacio)
    }

    // Simula caída de red en NoticiasViewModel verificando transición controlada al estado Error
    @Test
    fun noticiasViewModel_conFallaDeRed_actualizaEstadoAError() = runTest {
        val apiMock = mockk<CorpalivApiService>()
        coEvery { apiMock.obtenerNoticias(any(), any()) } throws RuntimeException("Sin conexión")

        val repo = NoticiasRepository(apiMock)
        val vm = NoticiasViewModel(repo)
        advanceUntilIdle()

        assertTrue(vm.uiState is NoticiasUiState.Error)
    }

    // Verifica que las rutas administrativas estén protegidas por rol
    @Test
    fun screen_verificaRutasProtegidasPorRol() {
        assertTrue(Screen.Profile.requiereAdministrador)
        assertFalse(Screen.Noticias.requiereAdministrador)
        assertFalse(Screen.Home.requiereAdministrador)
        assertFalse(Screen.Catalogo.requiereAdministrador)
    }
}
