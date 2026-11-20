package com.example.app_movil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_movil.data.repository.ProductoRepository
import com.example.app_movil.model.Producto
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.AlbaTopBar
import com.example.app_movil.ui.components.AppMainScaffold
import com.example.app_movil.ui.components.ProductoImagenHeader
import com.example.app_movil.ui.components.ProductoItem
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape
import com.example.app_movil.viewmodels.AuthViewModel
import com.example.app_movil.viewmodels.CatalogoViewModel
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.viewmodels.SolicitudesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    catalogoViewModel: CatalogoViewModel,
    solicitudesViewModel: SolicitudesViewModel
) {
    val todosLosProductos = remember { ProductoRepository.obtenerProductos() }
    val categorias = ProductoRepository.categorias

    // Conserva el estado del banner informativo y el ID del producto abierto en BottomSheet al girar
    var infoExpandida by rememberSaveable { mutableStateOf(false) }
    var productoDetalleId by rememberSaveable { mutableStateOf<Int?>(null) }

    // Recupera el objeto Producto completo a partir del ID guardado en rememberSaveable
    val productoDetalle = remember(productoDetalleId, todosLosProductos) {
        todosLosProductos.find { it.id == productoDetalleId }
    }

    // Filtrado combinado por categoría seleccionada y búsqueda válida
    val productosFiltrados = todosLosProductos.filter { producto ->
        val coincideCategoria = catalogoViewModel.categoriaSeleccionada == "Todos" ||
                producto.categoria == catalogoViewModel.categoriaSeleccionada
        val coincideTexto = if (catalogoViewModel.busquedaError == null && catalogoViewModel.queryBusqueda.isNotBlank()) {
            producto.nombre.contains(catalogoViewModel.queryBusqueda, ignoreCase = true) ||
                    producto.descripcion.contains(catalogoViewModel.queryBusqueda, ignoreCase = true)
        } else {
            true
        }
        coincideCategoria && coincideTexto
    }

    // Precarga el producto seleccionado en el formulario de compra y navega hacia él
    val cotizarProducto: (Producto) -> Unit = { producto ->
        solicitudesViewModel.prepararFormulario(
            tipo = "Compra Solidaria",
            mensajeInicial = "Cotización para: ${producto.nombre} ($${producto.precio})"
        )
        mainViewModel.navigateTo(Screen.ContactoCompra, authViewModel.rolActual)
    }

    AppMainScaffold(
        currentScreen = Screen.Catalogo,
        rolActual = authViewModel.rolActual,
        onNavigate = { mainViewModel.navigateTo(it, authViewModel.rolActual) },
        topBar = {
            AlbaTopBar(
                title = "Tienda Solidaria",
                subtitle = "Productos hechos en talleres CORPALIV"
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + Dimens.SpaceXS,
                bottom = innerPadding.calculateBottomPadding(),
                start = Dimens.SpaceMD,
                end = Dimens.SpaceMD
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSM),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSM),
            modifier = Modifier.fillMaxSize()
        ) {
            // Cabecera de ancho completo con banner colapsable, buscador y chips de categoría
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { infoExpandida = !infoExpandida }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.SpaceSM, vertical = Dimens.SpaceXS)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(Dimens.IconMD)
                                )
                                Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                                Text(
                                    text = "Conoce el impacto de cada compra",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (infoExpandida) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                    contentDescription = if (infoExpandida) "Contraer información" else "Expandir información",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            AnimatedVisibility(visible = infoExpandida) {
                                Text(
                                    text = "Cada producto es elaborado por jóvenes y adultos con discapacidad múltiple en nuestros talleres de inclusión sociolaboral.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(top = Dimens.SpaceXS)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                    OutlinedTextField(
                        value = catalogoViewModel.queryBusqueda,
                        onValueChange = { catalogoViewModel.onQueryBusquedaChange(it) },
                        placeholder = { Text("Buscar producto o taller...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Buscar"
                            )
                        },
                        trailingIcon = {
                            if (catalogoViewModel.busquedaError != null) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = "Error en búsqueda",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            } else if (catalogoViewModel.queryBusqueda.isNotEmpty()) {
                                IconButton(onClick = { catalogoViewModel.limpiarBusqueda() }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = "Limpiar búsqueda"
                                    )
                                }
                            }
                        },
                        isError = catalogoViewModel.busquedaError != null,
                        supportingText = catalogoViewModel.busquedaError?.let { errorMsg ->
                            { Text(text = errorMsg, color = MaterialTheme.colorScheme.error) }
                        },
                        singleLine = true,
                        shape = PillShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceXS))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceXS)
                    ) {
                        categorias.forEach { cat ->
                            FilterChip(
                                selected = catalogoViewModel.categoriaSeleccionada == cat,
                                onClick = { catalogoViewModel.onCategoriaSeleccionada(cat) },
                                shape = PillShape,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                label = {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Estado vacío o cuadrícula de productos
            if (productosFiltrados.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EstadoVacioTienda(
                        onLimpiarFiltros = {
                            catalogoViewModel.limpiarBusqueda()
                            catalogoViewModel.onCategoriaSeleccionada("Todos")
                        }
                    )
                }
            } else {
                items(productosFiltrados, key = { it.id }) { producto ->
                    ProductoItem(
                        producto = producto,
                        onCardClick = { productoDetalleId = producto.id },
                        onCotizarClick = { cotizarProducto(producto) }
                    )
                }
            }
        }
    }

    // Hoja inferior modal de detalle que permanece abierta tras girar el dispositivo
    productoDetalle?.let { producto ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { productoDetalleId = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.SpaceLG, vertical = Dimens.SpaceSM)
            ) {
                ProductoImagenHeader(
                    producto = producto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(MaterialTheme.shapes.large)
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceMD))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Taller ${producto.categoria}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(
                                horizontal = Dimens.SpaceSM,
                                vertical = Dimens.SpaceXXS
                            )
                        )
                    }
                    Text(
                        text = "Aporte: $${producto.precio} CLP",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceXS))

                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceXS))

                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceSM))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                Text(
                    text = "Elaborado artesanalmente en los talleres laborales de CORPALIV por jóvenes con discapacidad múltiple y sordoceguera.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceLG))

                Button(
                    onClick = {
                        val seleccionado = producto
                        productoDetalleId = null
                        cotizarProducto(seleccionado)
                    },
                    shape = PillShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconMD)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                    Text(
                        text = "Solicitar o Consultar por este Producto",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceLG))
            }
        }
    }
}

// Estado vacío con botón para restablecer filtros
@Composable
private fun EstadoVacioTienda(onLimpiarFiltros: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(
            width = Dimens.BorderThin,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpaceMD)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpaceXL)
        ) {
            Surface(
                shape = PillShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(Dimens.IconHero)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.SpaceMD))
            Text(
                text = "Sin coincidencias en el catálogo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceXXS))
            Text(
                text = "Prueba con otro término o revisa todas las categorías de taller.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceMD))
            OutlinedButton(
                onClick = onLimpiarFiltros,
                shape = PillShape,
                modifier = Modifier.defaultMinSize(minHeight = Dimens.MinTouchTarget)
            ) {
                Text("Mostrar todos los productos")
            }
        }
    }
}