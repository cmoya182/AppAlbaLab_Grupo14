package com.example.app_movil.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_movil.data.repository.NoticiasRepository
import com.example.app_movil.model.Noticia
import kotlinx.coroutines.launch

// Estados inmutables de la pantalla de noticias (evita inestabilidad por List<Noticia> en Compose)
@Immutable
sealed interface NoticiasUiState {
    data object Cargando : NoticiasUiState
    data class Exito(val noticias: List<Noticia>) : NoticiasUiState
    data object Vacio : NoticiasUiState
    data class Error(val mensaje: String) : NoticiasUiState
}

class NoticiasViewModel(
    private val repository: NoticiasRepository = NoticiasRepository()
) : ViewModel() {

    // Estado observable que controla lo que dibuja NoticiasScreen
    var uiState by mutableStateOf<NoticiasUiState>(NoticiasUiState.Cargando)
        private set

    // Carga automática de noticias al crear el ViewModel
    init {
        cargarNoticias()
    }

    // Consulta la API de CORPALIV en corrutina y actualiza el estado (Éxito, Vacío o Error)
    fun cargarNoticias() {
        uiState = NoticiasUiState.Cargando
        viewModelScope.launch {
            repository.obtenerNoticiasCorpaliv()
                .onSuccess { lista ->
                    uiState = if (lista.isEmpty()) {
                        NoticiasUiState.Vacio
                    } else {
                        NoticiasUiState.Exito(lista)
                    }
                }
                .onFailure {
                    uiState = NoticiasUiState.Error(
                        "No fue posible conectar con corpaliv.cl. Verifica tu conexión a internet e inténtalo nuevamente."
                    )
                }
        }
    }
}