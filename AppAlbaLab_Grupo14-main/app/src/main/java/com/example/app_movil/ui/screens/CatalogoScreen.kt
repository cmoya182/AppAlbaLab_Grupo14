package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_movil.model.Producto
import com.example.app_movil.ui.components.ProductoItem
import com.example.app_movil.viewmodels.MainViewModel

// Dibuja la pantalla de la Tienda Alba Lab con barra superior institucional y listado de productos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(viewModel: MainViewModel) {

    // Lista de productos reales comercializados en la tienda Alba Lab de CORPALIV
    val productos = listOf(
        Producto(1, "Bolsa Alba Lab", "Bolsa ecológica de tela reutilizable estampada en taller", 3500),
        Producto(2, "Difusor de Varillas 150 ml", "Aromatizador ambiental elaborado por jóvenes del taller", 6500),
        Producto(3, "Difusor Colgante", "Aromatizador para auto o espacios pequeños hecho a mano", 3000),
        Producto(4, "Tazón Institucional", "Tazón de cerámica personalizado en taller sociolaboral", 5000)
    )

    Scaffold(
        topBar = {
            // Barra superior pintada con el color primario (Morado CORPALIV) y texto blanco
            TopAppBar(
                title = { Text("Tienda Solidaria Alba Lab", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al inicio"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Encabezado explicativo sobre el impacto social de la compra
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "Cada producto es elaborado por jóvenes y adultos con discapacidad múltiple en nuestros talleres de inclusión sociolaboral.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Recorre la lista y dibuja cada tarjeta de producto
            productos.forEach { producto ->
                ProductoItem(producto = producto)
            }
        }
    }
}