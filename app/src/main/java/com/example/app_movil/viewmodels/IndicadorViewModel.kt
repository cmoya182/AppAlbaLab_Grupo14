package com.example.app_movil.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_movil.data.remote.MindicadorApiService
import com.example.app_movil.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

// Estados posibles del banner de UF diaria en el Inicio
sealed interface UfUiState {
    data object Cargando : UfUiState
    data class Exito(val valorFormateado: String, val fecha: String) : UfUiState
    data object Error : UfUiState
}

class IndicadorViewModel(
    private val apiService: MindicadorApiService = RetrofitClient.mindicadorService
) : ViewModel() {

    // Estado observable consumido por HomeScreen
    var uiState by mutableStateOf<UfUiState>(UfUiState.Cargando)
        private set

    // Consulta la UF automáticamente al iniciar la aplicación
    init {
        cargarUfDiaria()
    }

    // Ejecuta la llamada HTTP en corrutina y formatea valor y fecha al estándar chileno
    fun cargarUfDiaria() {
        uiState = UfUiState.Cargando
        viewModelScope.launch {
            try {
                val respuesta = apiService.obtenerUfDiaria()
                val registroHoy = respuesta.serie.firstOrNull()
                if (registroHoy != null) {
                    uiState = UfUiState.Exito(
                        valorFormateado = formatearMonedaClp(registroHoy.valor),
                        fecha = formatearFechaCorta(registroHoy.fecha)
                    )
                } else {
                    uiState = UfUiState.Error
                }
            } catch (_: Exception) {
                uiState = UfUiState.Error
            }
        }
    }

    companion object {
        // Formatea el número con separador de miles chileno (ej: $38.450,12)
        fun formatearMonedaClp(valor: Double): String {
            val simbolos = DecimalFormatSymbols(Locale("es", "CL")).apply {
                groupingSeparator = '.'
                decimalSeparator = ','
            }
            val formato = DecimalFormat("#,##0.00", simbolos)
            return "$${formato.format(valor)}"
        }

        // Convierte fecha ISO ("2026-11-24T03:00:00.000Z") a "24/11/2026"
        fun formatearFechaCorta(fechaIso: String): String {
            val partes = fechaIso.take(10).split("-")
            return if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fechaIso.take(10)
        }
    }
}