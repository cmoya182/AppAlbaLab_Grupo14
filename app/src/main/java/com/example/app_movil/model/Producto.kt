package com.example.app_movil.model

/**
 * ARCHIVO: Producto.kt
 * PROPÓSITO: Define el modelo de datos (estructura) de cada producto solidario elaborado en los talleres de Alba Lab.
 *
 * ¿CÓMO FUNCIONA?:
 * Es una "data class" que agrupa las 5 propiedades de un producto: su identificador numérico (id),
 * nombre, descripción del taller, precio de aporte en CLP y categoría ("Textil", "Aromas" o "Cerámica").
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - CatalogoScreen.kt: Crea la lista de productos disponibles y los filtra por categoría o texto de búsqueda.
 * - ProductoItem.kt: Recibe un objeto Producto para dibujar su tarjeta expandible con precio y descripción.
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String = "General"
)
