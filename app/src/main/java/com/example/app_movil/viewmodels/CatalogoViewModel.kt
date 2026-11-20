package com.example.app_movil.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.app_movil.utils.Validadores

class CatalogoViewModel : ViewModel() {

    // Estados observables del buscador y del chip de categoría seleccionado
    var queryBusqueda by mutableStateOf("")
        private set

    var busquedaError by mutableStateOf<String?>(null)
        private set

    var categoriaSeleccionada by mutableStateOf("Todos")
        private set

    // Actualiza el texto de búsqueda y valida que no tenga caracteres especiales
    fun onQueryBusquedaChange(nuevoTexto: String) {
        queryBusqueda = nuevoTexto
        busquedaError = if (!Validadores.esTextoBusquedaValido(nuevoTexto)) {
            "No se permiten caracteres especiales."
        } else {
            null
        }
    }

    // Limpia el texto del buscador y quita el estado de error
    fun limpiarBusqueda() {
        queryBusqueda = ""
        busquedaError = null
    }

    // Guarda la categoría seleccionada al tocar un FilterChip
    fun onCategoriaSeleccionada(categoria: String) {
        categoriaSeleccionada = categoria
    }
}