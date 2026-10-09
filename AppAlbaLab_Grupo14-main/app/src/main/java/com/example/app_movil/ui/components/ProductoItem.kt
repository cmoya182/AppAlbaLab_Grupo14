package com.example.app_movil.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_movil.model.Producto

// 1. @Composable: Es una etiqueta obligatoria. Le dice a Android que esto no es
// código matemático normal, sino que sirve para "dibujar" interfaz visual.
@Composable
fun ProductoItem(producto: Producto) {

    // 2. Card: Dibuja un rectángulo con una leve sombra, como una "tarjeta" de presentación.
    Card(
        // Modifier: Es tu herramienta para darle estilo (márgenes, color, tamaño).
        // padding(8.dp) le da una pequeña separación por fuera a la tarjeta para que no choque.
        modifier = Modifier.padding(8.dp)
    ) {
        // 3. Column: Es un organizador Todo lo que pongas dentro se apilará verticalmente (hacia abajo).
        Column(
            modifier = Modifier.padding(16.dp) // Le da espacio por dentro a la tarjeta para que el texto respire.
        ) {
            // 4. Text: Simplemente toma el texto que le pasamos y lo dibuja en pantalla.
            Text(text = producto.nombre)
            Text(text = producto.descripcion)
            Text(text = "Precio: $${producto.precio}")
        }
    }
}