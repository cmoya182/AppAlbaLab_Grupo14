package com.example.app_movil.model

// Modelo de producto con campos opcionales listos para futuras imágenes locales o desde API
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String = "General",
    val imagenRes: Int? = null,
    val imagenUrl: String? = null
)