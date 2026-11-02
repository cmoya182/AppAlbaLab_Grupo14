package com.example.app_movil.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.AppBottomBar
import com.example.app_movil.viewmodels.MainViewModel
import java.io.File

/**
 * ARCHIVO: ProfileScreen.kt
 * PROPÓSITO: Construye la pantalla del "Panel Institucional", donde el equipo de CORPALIV / Alba Lab
 * revisa y administra las solicitudes enviadas desde los formularios de la aplicación.
 *
 * ¿CÓMO FUNCIONA?:
 * 1. Verifica si el usuario inició sesión (viewModel.estaAutenticado). Si no ha iniciado sesión,
 *    muestra un aviso con candado y un botón que lleva a LoginScreen.
 * 2. Si está autenticado, se suscribe al StateFlow de solicitudes (viewModel.listaSolicitudes.collectAsState()).
 *    Cada vez que se guarda o elimina una solicitud en SQLite, esta pantalla se actualiza sola en tiempo real.
 * 3. Si una solicitud incluye una fotografía guardada en el almacenamiento interno (solicitud.imagenPath),
 *    la decodifica con BitmapFactory.decodeFile() y la muestra dentro de la tarjeta.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - MainViewModel.kt: Obtiene la lista de solicitudes, el filtro activo y llama a eliminarSolicitudPorId().
 * - SolicitudEntity.kt: Es la estructura de cada solicitud que se dibuja en la lista (LazyColumn).
 * - AppBottomBar.kt: Muestra la barra de navegación inferior con el ícono "Panel" seleccionado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    // collectAsState() convierte el StateFlow de Room en un estado que Compose redibuja automáticamente
    val solicitudes by viewModel.listaSolicitudes.collectAsState()
    val categoriasFiltro = listOf("Todas", "Empresa", "Compra Solidaria", "Voluntariado")

    // Filtra la lista según el chip seleccionado arriba ("Todas", "Empresa", "Compra Solidaria" o "Voluntariado")
    val solicitudesFiltradas = if (viewModel.filtroSolicitudes == "Todas") {
        solicitudes
    } else {
        solicitudes.filter { it.tipoContacto.equals(viewModel.filtroSolicitudes, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Institucional", fontWeight = FontWeight.Bold) },
                actions = {
                    if (viewModel.estaAutenticado) {
                        IconButton(onClick = { viewModel.cerrarSesion() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            AppBottomBar(
                currentScreen = Screen.Profile,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        // --- CASO 1: USUARIO NO AUTENTICADO (MODO INVITADO) ---
        if (!viewModel.estaAutenticado) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Acceso restringido",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Área Exclusiva de Equipo Alba Lab",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Para visualizar y gestionar las solicitudes de vinculación recibidas, debe iniciar sesión con sus credenciales de colaborador.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.navigateTo(Screen.Login) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Login, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Iniciar Sesión")
                }
            }
        } else {
            // --- CASO 2: USUARIO AUTENTICADO (COLABORADOR ALBA LAB) ---
            // LazyColumn dibuja de manera eficiente solo las tarjetas que caben en la pantalla al hacer scroll
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // Tarjeta superior que indica qué usuario tiene la sesión iniciada
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = viewModel.usuarioLogueado ?: "Equipo Alba Lab",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Sesión Activa Permanente - Gestión de Solicitudes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Filtrar Solicitudes por Tipo:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fila de chips interactivos para filtrar solicitudes por categoría
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categoriasFiltro.forEach { cat ->
                            FilterChip(
                                selected = viewModel.filtroSolicitudes == cat,
                                onClick = { viewModel.onFiltroSolicitudesChange(cat) },
                                label = { Text(cat) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Solicitudes (${solicitudesFiltradas.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (solicitudesFiltradas.isEmpty()) {
                        Text(
                            text = if (solicitudes.isEmpty())
                                "No hay solicitudes registradas por el momento."
                            else
                                "No hay solicitudes en la categoría '${viewModel.filtroSolicitudes}'.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }

                // Recorre cada solicitud filtrada y dibuja su tarjeta con datos, fotografía (si tiene) y botones
                items(solicitudesFiltradas, key = { it.id }) { solicitud ->
                    // Si la solicitud tiene una ruta de imagen guardada en SQLite, lee el archivo físico desde disco
                    val bitmapAdjunto = remember(solicitud.imagenPath) {
                        solicitud.imagenPath?.let { ruta ->
                            try {
                                val archivo = File(ruta)
                                if (archivo.exists()) {
                                    BitmapFactory.decodeFile(archivo.absolutePath)
                                } else null
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Etiqueta que muestra si es "Empresa", "Compra Solidaria" o "Voluntariado"
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = solicitud.tipoContacto,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Row {
                                    // Botón Compartir: usa un Intent nativo de Android (ACTION_SEND) para enviar por WhatsApp/Correo
                                    IconButton(
                                        onClick = {
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "Solicitud Alba Lab [${solicitud.tipoContacto}]\nDe: ${solicitud.nombre}\nCorreo: ${solicitud.correo}\nTeléfono: ${solicitud.telefono}\nMensaje: ${solicitud.mensaje}\nFecha: ${solicitud.fecha}"
                                                )
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(sendIntent, "Compartir Solicitud"))
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Compartir solicitud",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Botón Eliminar: borra la solicitud de SQLite y su archivo de imagen del teléfono
                                    IconButton(
                                        onClick = { viewModel.eliminarSolicitudPorId(solicitud.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar solicitud",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = solicitud.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = solicitud.correo,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = solicitud.telefono,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Propuesta: ${solicitud.mensaje}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Si la solicitud tiene una fotografía adjunta válida, la muestra con bordes redondeados
                            if (bitmapAdjunto != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Fotografía / Documento adjunto:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Image(
                                    bitmap = bitmapAdjunto.asImageBitmap(),
                                    contentDescription = "Imagen adjunta de la solicitud",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = solicitud.fecha,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
