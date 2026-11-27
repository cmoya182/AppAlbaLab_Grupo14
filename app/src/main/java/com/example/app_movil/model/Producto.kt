package com.example.app_movil.model

import androidx.compose.runtime.Immutable

// Modelo inmutable de producto para evitar recomposiciones innecesarias en la grilla de tienda
@Immutable
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String = "General",
    val imagenRes: Int? = null,
    val imagenUrl: String? = null
)