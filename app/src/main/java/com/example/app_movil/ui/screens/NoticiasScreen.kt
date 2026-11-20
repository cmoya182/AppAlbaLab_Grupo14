package com.example.app_movil.ui.screens

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_movil.model.Noticia
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.AlbaTopBar
import com.example.app_movil.ui.components.AppMainScaffold
import com.example.app_movil.ui.theme.APP_MOVILTheme
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape
import com.example.app_movil.viewmodels.AuthViewModel
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.viewmodels.NoticiasUiState
import com.example.app_movil.viewmodels.NoticiasViewModel

// PRUÉBALO AQUÍ: Pantalla de Noticias reales de CORPALIV con estados de carga, error, vacío y apertura en navegador.
@Composable
fun NoticiasScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    noticiasViewModel: NoticiasViewModel
) {
    val context = LocalContext.current
    val estado = noticiasViewModel.uiState

    // Estructura principal con barra superior y acción de recarga manual
    AppMainScaffold(
        currentScreen = Screen.Noticias,
        rolActual = authViewModel.rolActual,
        onNavigate = { mainViewModel.navigateTo(it, authViewModel.rolActual) },
        topBar = {
            AlbaTopBar(
                title = "Noticias CORPALIV",
                subtitle = "Actualidad e inclusión sociolaboral",
                actions = {
                    IconButton(
                        onClick = { noticiasViewModel.cargarNoticias() },
                        modifier = Modifier.defaultMinSize(
                            minWidth = Dimens.MinTouchTarget,
                            minHeight = Dimens.MinTouchTarget
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Actualizar noticias",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Renderizado condicional según el estado de la consulta HTTP
        when (estado) {
            is NoticiasUiState.Cargando -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(Dimens.SpaceLG)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(Dimens.SpaceMD))
                        Text(
                            text = "Cargando noticias oficiales de CORPALIV...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is NoticiasUiState.Error -> {
                EstadoMensajeNoticias(
                    icono = Icons.Outlined.CloudOff,
                    titulo = "Sin conexión al portal de noticias",
                    descripcion = estado.mensaje,
                    textoBoton = "Reintentar",
                    onAccion = { noticiasViewModel.cargarNoticias() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            is NoticiasUiState.Vacio -> {
                EstadoMensajeNoticias(
                    icono = Icons.Outlined.Newspaper,
                    titulo = "No hay noticias disponibles",
                    descripcion = "Por el momento no se encontraron publicaciones recientes en corpaliv.cl.",
                    textoBoton = "Actualizar",
                    onAccion = { noticiasViewModel.cargarNoticias() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            is NoticiasUiState.Exito -> {
                // Lista desplazable con banner de origen verificado y tarjetas de noticias
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
                    item {
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                            ),
                            border = BorderStroke(
                                width = Dimens.BorderThin,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(Dimens.SpaceMD)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Newspaper,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(Dimens.IconLG)
                                )
                                Spacer(modifier = Modifier.width(Dimens.SpaceSM))
                                Text(
                                    text = "Publicaciones verificadas directamente desde el sitio oficial corpaliv.cl",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    items(estado.noticias, key = { it.id }) { noticia ->
                        NoticiaCard(
                            noticia = noticia,
                            onAbrirEnlace = {
                                if (noticia.enlace.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(noticia.enlace))
                                    context.startActivity(intent)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// Tarjeta individual de noticia con fecha, título, resumen y acceso al enlace oficial
@Composable
fun NoticiaCard(
    noticia: Noticia,
    onAbrirEnlace: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationLow),
        border = BorderStroke(
            width = Dimens.BorderThin,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Dimens.SpaceMD)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = PillShape,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = "CORPALIV",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(
                            horizontal = Dimens.SpaceSM,
                            vertical = Dimens.SpaceXXS
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpaceXXS))
                    Text(
                        text = noticia.fechaPublicacion,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceXS))

            Text(
                text = noticia.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceXS))

            Text(
                text = noticia.resumen,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSM))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(Dimens.SpaceXS))

            OutlinedButton(
                onClick = onAbrirEnlace,
                shape = PillShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = Dimens.MinTouchTarget)
            ) {
                Text(
                    text = "Leer noticia en corpaliv.cl",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Abrir noticia en navegador",
                    modifier = Modifier.size(Dimens.IconSM)
                )
            }
        }
    }
}

// Componente de estado vacío o error con botón de reintento
@Composable
private fun EstadoMensajeNoticias(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    textoBoton: String,
    onAccion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(Dimens.SpaceLG)
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
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
                            imageVector = icono,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.SpaceMD))
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(Dimens.SpaceXXS))
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(Dimens.SpaceLG))
                Button(
                    onClick = onAccion,
                    shape = PillShape,
                    modifier = Modifier.defaultMinSize(minHeight = Dimens.MinTouchTarget)
                ) {
                    Text(textoBoton, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// Vista previa de tarjeta de noticia en modo claro y oscuro
@Preview(name = "NoticiaCard Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "NoticiaCard Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun NoticiaCardPreview() {
    APP_MOVILTheme {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(Dimens.SpaceMD)) {
            NoticiaCard(
                noticia = Noticia(
                    id = 1,
                    titulo = "Talleres laborales CORPALIV impulsan inclusión sociolaboral",
                    resumen = "Jóvenes y adultos participan en procesos formativos de producción artesanal y desarrollo de autonomía.",
                    fechaPublicacion = "15/11/2026",
                    enlace = "https://corpaliv.cl"
                ),
                onAbrirEnlace = {}
            )
        }
    }
}