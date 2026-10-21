package com.example.app_movil.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_movil.model.Producto
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.ProductoItem
import com.example.app_movil.viewmodels.MainViewModel

// Dibuja la pantalla de la Tienda Alba Lab con búsqueda validada y filtro de categorías
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(viewModel: MainViewModel) {

    // Lista de productos reales clasificados por su respectivo taller laboral
    val todosLosProductos = listOf(
        Producto(1, "Bolsa Alba Lab", "Bolsa ecológica de tela reutilizable estampada en taller", 3500, "Textil"),
        Producto(2, "Difusor de Varillas 150 ml", "Aromatizador ambiental elaborado por jóvenes del taller", 6500, "Aromas"),
        Producto(3, "Difusor Colgante", "Aromatizador para auto o espacios pequeños hecho a mano", 3000, "Aromas"),
        Producto(4, "Tazón Institucional", "Tazón de cerámica personalizado en taller sociolaboral", 5000, "Cerámica")
    )

    val categorias = listOf("Todos", "Aromas", "Textil", "Cerámica")

    // Aplica el filtro combinado de categoría y texto de búsqueda
    val productosFiltrados = todosLosProductos.filter { producto ->
        val coincideCategoria = viewModel.categoriaSeleccionada == "Todos" || producto.categoria == viewModel.categoriaSeleccionada
        val coincideTexto = if (viewModel.busquedaError == null && viewModel.queryBusqueda.isNotBlank()) {
            producto.nombre.contains(viewModel.queryBusqueda, ignoreCase = true) ||
                    producto.descripcion.contains(viewModel.queryBusqueda, ignoreCase = true)
        } else {
            true
        }
        coincideCategoria && coincideTexto
    }

    Scaffold(
        topBar = {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
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

                // Formulario secundario: Campo de búsqueda con validación reactiva
                OutlinedTextField(
                    value = viewModel.queryBusqueda,
                    onValueChange = { viewModel.onQueryBusquedaChange(it) },
                    label = { Text("Buscar producto o descripción...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (viewModel.busquedaError != null) {
                            Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        } else if (viewModel.queryBusqueda.isNotEmpty()) {
                            IconButton(onClick = { viewModel.limpiarBusqueda() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                            }
                        }
                    },
                    isError = viewModel.busquedaError != null,
                    supportingText = {
                        if (viewModel.busquedaError != null) {
                            Text(text = viewModel.busquedaError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Fila de chips de filtro por categoría
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorias.forEach { cat ->
                        FilterChip(
                            selected = viewModel.categoriaSeleccionada == cat,
                            onClick = { viewModel.onCategoriaSeleccionada(cat) },
                            label = { Text(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Listado de productos filtrados o mensaje vacío
            if (productosFiltrados.isEmpty()) {
                item {
                    Text(
                        text = "No se encontraron productos coincidentes con el criterio de búsqueda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                items(productosFiltrados) { producto ->
                    ProductoItem(
                        producto = producto,
                        onCotizarClick = { viewModel.navigateTo(Screen.Contacto) }
                    )
                }
            }
        }
    }
}