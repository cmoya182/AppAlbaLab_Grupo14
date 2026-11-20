package com.example.app_movil.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.app_movil.data.local.SolicitudEntity
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.AlbaTopBar
import com.example.app_movil.ui.components.AppMainScaffold
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape
import com.example.app_movil.viewmodels.AuthViewModel
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.viewmodels.SolicitudesViewModel
import com.example.app_movil.viewmodels.UserRole
import java.io.File

@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    solicitudesViewModel: SolicitudesViewModel
) {
    // Guardia de autorización: redirige fuera del panel si no es administrador
    if (authViewModel.rolActual != UserRole.ADMINISTRADOR) {
        LaunchedEffect(Unit) {
            mainViewModel.navigateTo(Screen.Noticias, authViewModel.rolActual, clearBackStack = true)
        }
        return
    }

    val context = LocalContext.current
    val solicitudes by solicitudesViewModel.listaSolicitudes.collectAsState()
    val categoriasFiltro = listOf("Todas", "Empresa", "Compra Solidaria", "Voluntariado")

    // Guarda el ID de la solicitud pendiente de borrado para mantener el diálogo abierto tras rotar
    var solicitudIdAEliminar by rememberSaveable { mutableStateOf<Int?>(null) }
    val solicitudAEliminar = remember(solicitudIdAEliminar, solicitudes) {
        solicitudes.find { it.id == solicitudIdAEliminar }
    }

    // Filtra la lista según la categoría activa
    val solicitudesFiltradas = if (solicitudesViewModel.filtroSolicitudes == "Todas") {
        solicitudes
    } else {
        solicitudes.filter { it.tipoContacto.equals(solicitudesViewModel.filtroSolicitudes, ignoreCase = true) }
    }

    // Conteos estadísticos por canal
    val totalEmpresa = remember(solicitudes) {
        solicitudes.count { it.tipoContacto.equals("Empresa", ignoreCase = true) }
    }
    val totalCompra = remember(solicitudes) {
        solicitudes.count { it.tipoContacto.equals("Compra Solidaria", ignoreCase = true) }
    }
    val totalVoluntariado = remember(solicitudes) {
        solicitudes.count { it.tipoContacto.equals("Voluntariado", ignoreCase = true) }
    }

    AppMainScaffold(
        currentScreen = Screen.Profile,
        rolActual = authViewModel.rolActual,
        onNavigate = { mainViewModel.navigateTo(it, authViewModel.rolActual) },
        topBar = {
            AlbaTopBar(
                title = "Panel Institucional",
                subtitle = "Gestiona las solicitudes recibidas"
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + Dimens.SpaceXS,
                bottom = innerPadding.calculateBottomPadding(),
                start = Dimens.SpaceMD,
                end = Dimens.SpaceMD
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSM),
            modifier = Modifier.fillMaxSize()
        ) {
            // Cabecera con conteos por tipo y filtros
            item {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.SpaceMD)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = PillShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.VerifiedUser,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(Dimens.IconMD)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(Dimens.SpaceSM))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = authViewModel.usuarioLogueado ?: "Equipo Alba Lab",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${solicitudes.size} solicitudes registradas en total",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceXS),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StatMiniCard(
                                etiqueta = "Empresas",
                                cantidad = totalEmpresa,
                                icono = Icons.Outlined.Business,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniCard(
                                etiqueta = "Compras",
                                cantidad = totalCompra,
                                icono = Icons.Outlined.ShoppingBag,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniCard(
                                etiqueta = "Voluntarios",
                                cantidad = totalVoluntariado,
                                icono = Icons.Outlined.FavoriteBorder,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceXS)
                ) {
                    categoriasFiltro.forEach { cat ->
                        FilterChip(
                            selected = solicitudesViewModel.filtroSolicitudes == cat,
                            onClick = { solicitudesViewModel.onFiltroSolicitudesChange(cat) },
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

            // Lista de solicitudes o vista vacía
            if (solicitudesFiltradas.isEmpty()) {
                item {
                    EstadoVacioPanel(
                        hayRegistrosTotales = solicitudes.isNotEmpty(),
                        filtroActual = solicitudesViewModel.filtroSolicitudes
                    )
                }
            } else {
                items(solicitudesFiltradas, key = { it.id }) { solicitud ->
                    SolicitudCard(
                        solicitud = solicitud,
                        onEnviarCorreo = {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${solicitud.correo}")
                                putExtra(Intent.EXTRA_SUBJECT, "Contacto Alba Lab - CORPALIV")
                            }
                            context.startActivity(Intent.createChooser(emailIntent, "Enviar correo"))
                        },
                        onLlamar = {
                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${solicitud.telefono}")
                            }
                            context.startActivity(dialIntent)
                        },
                        onCompartir = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Solicitud Alba Lab [${solicitud.tipoContacto}]\nDe: ${solicitud.nombre}\nCorreo: ${solicitud.correo}\nTeléfono: ${solicitud.telefono}\nMensaje: ${solicitud.mensaje}\nFecha: ${solicitud.fecha}"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Compartir solicitud"))
                        },
                        onSolicitarEliminar = { solicitudIdAEliminar = solicitud.id }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación de borrado individual protegido tras rotación
    solicitudAEliminar?.let { pendiente ->
        AlertDialog(
            onDismissRequest = { solicitudIdAEliminar = null },
            title = { Text("¿Eliminar solicitud?") },
            text = {
                Text("Se eliminará el registro de ${pendiente.nombre} y su imagen adjunta de este dispositivo.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        solicitudesViewModel.eliminarSolicitudPorId(pendiente.id)
                        solicitudIdAEliminar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { solicitudIdAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Tarjeta compacta de estadística por categoría
@Composable
private fun StatMiniCard(
    etiqueta: String,
    cantidad: Int,
    icono: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = containerColor.copy(alpha = 0.65f),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(Dimens.SpaceSM)) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(Dimens.IconMD)
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceXS))
            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = contentColor
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Estado vacío cuando no hay registros visibles
@Composable
private fun EstadoVacioPanel(
    hayRegistrosTotales: Boolean,
    filtroActual: String
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpaceSM)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpaceXL)
        ) {
            Surface(
                shape = PillShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(Dimens.IconHero)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Outlined.Inbox,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.SpaceMD))
            Text(
                text = if (hayRegistrosTotales) {
                    "Sin solicitudes en \"$filtroActual\""
                } else {
                    "Aún no hay solicitudes recibidas"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceXXS))
            Text(
                text = "Cuando empresas, compradores o voluntarios envíen un formulario, aparecerán aquí al instante.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Tarjeta de solicitud con estado de expansión persistente por ID tras rotación
@Composable
fun SolicitudCard(
    solicitud: SolicitudEntity,
    onEnviarCorreo: () -> Unit,
    onLlamar: () -> Unit,
    onCompartir: () -> Unit,
    onSolicitarEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mantiene abierto o cerrado el bloque "Ver más" de cada tarjeta al rotar el teléfono
    var mensajeExpandido by rememberSaveable(solicitud.id) { mutableStateOf(false) }

    // Decodifica la imagen adjunta desde almacenamiento interno si existe
    val bitmapAdjunto = remember(solicitud.imagenPath) {
        solicitud.imagenPath?.let { ruta ->
            try {
                val archivo = File(ruta)
                if (archivo.exists()) BitmapFactory.decodeFile(archivo.absolutePath) else null
            } catch (_: Exception) {
                null
            }
        }
    }

    // Colores diferenciados según el tipo de solicitud
    val (chipBg, chipFg) = when {
        solicitud.tipoContacto.equals("Compra Solidaria", ignoreCase = true) ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        solicitud.tipoContacto.equals("Voluntariado", ignoreCase = true) ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        else ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationLow),
        border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(Dimens.SpaceMD)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(shape = PillShape, color = chipBg) {
                    Text(
                        text = solicitud.tipoContacto,
                        style = MaterialTheme.typography.labelMedium,
                        color = chipFg,
                        modifier = Modifier.padding(horizontal = Dimens.SpaceSM, vertical = Dimens.SpaceXXS)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onCompartir,
                        modifier = Modifier.defaultMinSize(
                            minWidth = Dimens.MinTouchTarget,
                            minHeight = Dimens.MinTouchTarget
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Compartir solicitud",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.IconMD)
                        )
                    }
                    IconButton(
                        onClick = onSolicitarEliminar,
                        modifier = Modifier.defaultMinSize(
                            minWidth = Dimens.MinTouchTarget,
                            minHeight = Dimens.MinTouchTarget
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Eliminar solicitud",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(Dimens.IconMD)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceXXS))

            Text(
                text = solicitud.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceXS))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceXS),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = PillShape,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                        .clip(PillShape)
                        .clickable(onClick = onEnviarCorreo)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = Dimens.SpaceSM, vertical = Dimens.SpaceXS)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = "Enviar correo a ${solicitud.correo}",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.IconSM)
                        )
                        Spacer(modifier = Modifier.width(Dimens.SpaceXXS))
                        Text(
                            text = solicitud.correo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = PillShape,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                        .clip(PillShape)
                        .clickable(onClick = onLlamar)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = Dimens.SpaceSM, vertical = Dimens.SpaceXS)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = "Llamar a ${solicitud.telefono}",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(Dimens.IconSM)
                        )
                        Spacer(modifier = Modifier.width(Dimens.SpaceXXS))
                        Text(
                            text = solicitud.telefono,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceSM))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            Spacer(modifier = Modifier.height(Dimens.SpaceSM))

            Text(
                text = solicitud.mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = if (mensajeExpandido) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            if (solicitud.mensaje.length > 110) {
                TextButton(
                    onClick = { mensajeExpandido = !mensajeExpandido },
                    contentPadding = PaddingValues(horizontal = Dimens.SpaceXS),
                    modifier = Modifier.defaultMinSize(minHeight = Dimens.MinTouchTarget)
                ) {
                    Text(
                        text = if (mensajeExpandido) "Ver menos" else "Ver más",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            if (bitmapAdjunto != null) {
                Spacer(modifier = Modifier.height(Dimens.SpaceSM))
                Image(
                    bitmap = bitmapAdjunto.asImageBitmap(),
                    contentDescription = "Archivo adjunto de ${solicitud.nombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(176.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceSM))

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.SpaceXXS))
                Text(
                    text = solicitud.fecha,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}