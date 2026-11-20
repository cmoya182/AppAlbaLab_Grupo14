package com.example.app_movil.data.repository

import com.example.app_movil.model.Producto

// Fuente centralizada de productos y categorías fuera de la capa visual
object ProductoRepository {
    val categorias = listOf("Todos", "Aromas", "Textil", "Cerámica")

    fun obtenerProductos(): List<Producto> = listOf(
        Producto(1, "Bolsa Alba Lab", "Bolsa ecológica de tela reutilizable estampada en taller", 3500, "Textil"),
        Producto(2, "Difusor de Varillas 150 ml", "Aromatizador ambiental elaborado por jóvenes del taller", 6500, "Aromas"),
        Producto(3, "Difusor Colgante", "Aromatizador para auto o espacios pequeños hecho a mano", 3000, "Aromas"),
        Producto(4, "Tazón Institucional", "Tazón de cerámica personalizado en taller sociolaboral", 5000, "Cerámica")
    )
}