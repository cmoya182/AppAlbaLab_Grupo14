package com.example.app_movil.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_movil.model.Producto
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.ProductoItem
import com.example.app_movil.viewmodels.MainViewModel

/**
 * ARCHIVO: CatalogoScreen.kt
 * PROPÓSITO: Construye la pantalla de la "Tienda Solidaria Alba Lab", mostrando el catálogo de productos
 * elaborados en los talleres laborales de CORPALIV.
 *
 * ¿CÓMO FUNCIONA?:
 * 1. Define la lista de 4 productos institucionales y las categorías ("Todos", "Aromas", "Textil", "Cerámica").
 * 2. Incluye un buscador conectado a viewModel.queryBusqueda que valida en tiempo real que no se ingresen
 *    caracteres especiales (mostrando un ícono de error rojo si ocurre).
 * 3. Filtra dinámicamente la lista combinando la categoría elegida en los FilterChip y el texto escrito.
 * 4. Cuando el usuario expande un ProductoItem y presiona "Solicitar o Consultar por este Producto",
 *    ejecuta viewModel.prepararFormulario("Compra Solidaria", ...) precargando el nombre y precio del
 *    producto y navega directamente hacia Screen.ContactoCompra.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - Producto.kt y ProductoItem.kt: Representan y dibujan cada producto de la lista.
 * - MainViewModel.kt: Controla el texto de búsqueda, el error del buscador, la categoría activa y la navegación.
 * - ContactoScreen.kt (ContactoCompraScreen): Es la pantalla a la que viaja el usuario al cotizar un producto.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(viewModel: MainViewModel) {

    // Catálogo de productos artesanales confeccionados en los talleres de Alba Lab
    val todosLosProductos = listOf(
        Producto(1, "Bolsa Alba Lab", "Bolsa ecológica de tela reutilizable estampada en taller", 3500, "Textil"),
        Producto(2, "Difusor de Varillas 150 ml", "Aromatizador ambiental elaborado por jóvenes del taller", 6500, "Aromas"),
        Producto(3, "Difusor Colgante", "Aromatizador para auto o espacios pequeños hecho a mano", 3000, "Aromas"),
        Producto(4, "Tazón Institucional", "Tazón de cerámica personalizado en taller sociolaboral", 5000, "Cerámica")
    )

    val categorias = listOf("Todos", "Aromas", "Textil", "Cerámica")

    // Filtrado reactivo: combina el chip de categoría seleccionado y el texto válido del buscador
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

                // Barra de búsqueda con validación de caracteres especiales en tiempo real
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

                // Chips horizontales para filtrar por taller ("Todos", "Aromas", "Textil", "Cerámica")
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
                items(productosFiltrados, key = { it.id }) { producto ->
                    ProductoItem(
                        producto = producto,
                        onCotizarClick = {
                            // Precarga el producto seleccionado en el ViewModel y abre el formulario de Compra Solidaria
                            viewModel.prepararFormulario(
                                tipo = "Compra Solidaria",
                                mensajeInicial = "Cotización para: ${producto.nombre} ($${producto.precio})"
                            )
                            viewModel.navigateTo(Screen.ContactoCompra)
                        }
                    )
                }
            }
        }
    }
}
