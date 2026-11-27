package com.example.app_movil

import com.example.app_movil.data.remote.MindicadorApiService
import com.example.app_movil.data.remote.MindicadorResponseDto
import com.example.app_movil.data.remote.SerieIndicadorDto
import com.example.app_movil.viewmodels.IndicadorViewModel
import com.example.app_movil.viewmodels.UfUiState
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

// Pruebas unitarias de IndicadorViewModel: carga de UF diaria y formateo chileno de moneda y fecha
@OptIn(ExperimentalCoroutinesApi::class)
class IndicadorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Verifica transición a estado Exito con valor CLP y fecha corta cuando la API responde correctamente
    @Test
    fun cargarUfDiaria_conRespuestaExitosa_actualizaEstadoAExito() = runTest {
        val apiMock = mockk<MindicadorApiService>()
        coEvery { apiMock.obtenerUfDiaria() } returns MindicadorResponseDto(
            codigo = "uf",
            nombre = "Unidad de fomento (UF)",
            serie = listOf(
                SerieIndicadorDto(fecha = "2026-11-24T03:00:00.000Z", valor = 38450.12)
            )
        )

        val viewModel = IndicadorViewModel(apiService = apiMock)
        advanceUntilIdle()

        assertTrue(viewModel.uiState is UfUiState.Exito)
        val estado = viewModel.uiState as UfUiState.Exito
        assertEquals("$38.450,12", estado.valorFormateado)
        assertEquals("24/11/2026", estado.fecha)
    }

    // Verifica que una serie vacía desde mindicador.cl derive en estado Error controlado
    @Test
    fun cargarUfDiaria_conSerieVacia_actualizaEstadoAError() = runTest {
        val apiMock = mockk<MindicadorApiService>()
        coEvery { apiMock.obtenerUfDiaria() } returns MindicadorResponseDto(
            codigo = "uf",
            nombre = "Unidad de fomento (UF)",
            serie = emptyList()
        )

        val viewModel = IndicadorViewModel(apiService = apiMock)
        advanceUntilIdle()

        assertTrue(viewModel.uiState is UfUiState.Error)
    }

    // Verifica que una excepción de red sea capturada y muestre estado Error
    @Test
    fun cargarUfDiaria_conErrorDeRed_actualizaEstadoAError() = runTest {
        val apiMock = mockk<MindicadorApiService>()
        coEvery { apiMock.obtenerUfDiaria() } throws IOException("Sin conexión a mindicador.cl")

        val viewModel = IndicadorViewModel(apiService = apiMock)
        advanceUntilIdle()

        assertTrue(viewModel.uiState is UfUiState.Error)
    }

    // Valida el formato de moneda chilena con punto de miles y coma decimal
    @Test
    fun formatearMonedaClp_aplicaSeparadoresChilenosCorrectamente() {
        assertEquals("$38.450,12", IndicadorViewModel.formatearMonedaClp(38450.12))
        assertEquals("$1.000,50", IndicadorViewModel.formatearMonedaClp(1000.5))
        assertEquals("$0,00", IndicadorViewModel.formatearMonedaClp(0.0))
    }

    // Valida la conversión de fecha ISO a dd/MM/yyyy y el respaldo ante formatos inesperados
    @Test
    fun formatearFechaCorta_convierteIsoAFormatoChileno() {
        assertEquals("24/11/2026", IndicadorViewModel.formatearFechaCorta("2026-11-24T03:00:00.000Z"))
        assertEquals("05/01/2026", IndicadorViewModel.formatearFechaCorta("2026-01-05"))
        assertEquals("sin-fecha", IndicadorViewModel.formatearFechaCorta("sin-fecha"))
    }
}
