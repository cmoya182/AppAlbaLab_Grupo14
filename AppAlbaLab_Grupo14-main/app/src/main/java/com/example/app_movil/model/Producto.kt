package com.example.app_movil.model

// Modelo de datos que representa los productos elaborados en los talleres de Alba Lab
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String = "General"
)