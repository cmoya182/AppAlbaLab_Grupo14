package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.app_movil.model.Producto
import com.example.app_movil.ui.components.ProductoItem

@OptIn(ExperimentalMaterial3Api::class) // Esto se pone porque TopAppBar es una herramienta moderna en evolución
@Composable
fun CatalogoScreen() {

    // 1. Simulamos un par de productos de Alba Lab como si los hubiéramos descargado de internet
    val producto1 = Producto(1, "Tazón de Cerámica", "Tazón hecho a mano por estudiantes", 5000)
    val producto2 = Producto(2, "Bolsa Ecológica", "Bolsa de tela reutilizable", 3000)

    // 2. Scaffold: El esqueleto de nuestra pantalla
    Scaffold(
        topBar = {
            // TopAppBar: La barra superior que lleva el título de la pantalla
            TopAppBar(
                title = { Text("Catálogo Alba Lab") }
            )
        }
    ) { innerPadding -> // innerPadding es el espacio que ocupa la TopAppBar, para no tapar nuestro contenido

        // 3. Column: Apilamos nuestros productos verticalmente
        Column(
            modifier = Modifier.padding(innerPadding) // Le decimos a la columna que respete el espacio de la barra
        ) {
            // 4. ¡Llamamos a nuestro componente ProductoItem que creaste en el paso anterior!
            ProductoItem(producto = producto1)
            ProductoItem(producto = producto2)
        }
    }
}