package com.example.app_movil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.AlbaTopBar
import com.example.app_movil.ui.components.AppMainScaffold
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape
import com.example.app_movil.viewmodels.AuthViewModel
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.viewmodels.SimuladorViewModel
import com.example.app_movil.viewmodels.SolicitudesViewModel
import com.example.app_movil.viewmodels.UserRole

@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    solicitudesViewModel: SolicitudesViewModel,
    simuladorViewModel: SimuladorViewModel
) {
    // Conserva abierto el diálogo de confirmación de borrado si se rota la pantalla
    var mostrarDialogoBorrado by rememberSaveable { mutableStateOf(false) }
    val esAdmin = authViewModel.rolActual == UserRole.ADMINISTRADOR

    AppMainScaffold(
        currentScreen = Screen.Settings,
        rolActual = authViewModel.rolActual,
        onNavigate = { mainViewModel.navigateTo(it, authViewModel.rolActual) },
        topBar = {
            AlbaTopBar(
                title = "Ajustes e Impacto",
                subtitle = "Cuenta, simulación de aportes e información"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + Dimens.SpaceXS,
                    bottom = innerPadding.calculateBottomPadding(),
                    start = Dimens.SpaceMD,
                    end = Dimens.SpaceMD
                )
        ) {
            // Sección 1: Cuenta y control de sesión
            SettingsSectionTitle("CUENTA")
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Dimens.SpaceMD)) {
                    SettingsRowItem(
                        icon = if (esAdmin) Icons.Outlined.VerifiedUser else Icons.Outlined.PersonOutline,
                        iconContainer = if (esAdmin) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        iconTint = if (esAdmin) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        title = if (esAdmin) {
                            authViewModel.usuarioLogueado ?: "Administrador Alba Lab"
                        } else {
                            "Sesión de Invitado"
                        },
                        subtitle = if (esAdmin) {
                            "Cuenta institucional activa"
                        } else {
                            "Acceso público a tienda solidaria, noticias y canales de contacto"
                        }
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                    if (esAdmin) {
                        OutlinedButton(
                            onClick = {
                                authViewModel.cerrarSesion {
                                    mainViewModel.navigateTo(
                                        screen = Screen.Login,
                                        rolActual = UserRole.INVITADO,
                                        clearBackStack = true
                                    )
                                }
                            },
                            shape = PillShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                modifier = Modifier.size(Dimens.IconMD)
                            )
                            Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                            Text("Cerrar sesión", style = MaterialTheme.typography.labelLarge)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { mainViewModel.navigateTo(Screen.Login, authViewModel.rolActual) },
                            shape = PillShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Login,
                                contentDescription = null,
                                modifier = Modifier.size(Dimens.IconMD)
                            )
                            Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                            Text("Acceso colaboradores CORPALIV", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            // Sección 2: Simulador de impacto social aislado para no recomponer toda la pantalla al escribir
            SettingsSectionTitle("IMPACTO SOCIAL")
            SimuladorImpactoCard(simuladorViewModel = simuladorViewModel)

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            // Sección 3: Información institucional estática
            SettingsSectionTitle("ACERCA DE")
            AcercaDeCard()

            // Sección 4: Administración local visible solo para administradores autenticados
            if (esAdmin) {
                Spacer(modifier = Modifier.height(Dimens.SpaceLG))

                SettingsSectionTitle("ADMINISTRACIÓN")
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                    ),
                    border = BorderStroke(
                        width = Dimens.BorderThin,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(Dimens.SpaceMD)) {
                        SettingsRowItem(
                            icon = Icons.Outlined.DeleteSweep,
                            iconContainer = MaterialTheme.colorScheme.errorContainer,
                            iconTint = MaterialTheme.colorScheme.error,
                            title = "Vaciar historial local",
                            subtitle = "Elimina todas las solicitudes y fotos guardadas en este dispositivo",
                            onClick = { mostrarDialogoBorrado = true }
                        )

                        Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                        OutlinedButton(
                            onClick = { mostrarDialogoBorrado = true },
                            shape = PillShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteSweep,
                                contentDescription = null,
                                modifier = Modifier.size(Dimens.IconMD)
                            )
                            Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                            Text("Eliminar todo el historial", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación antes de vaciar la base de datos SQLite
    if (mostrarDialogoBorrado && esAdmin) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrado = false },
            title = { Text("¿Vaciar historial completo?") },
            text = {
                Text("Esta acción eliminará permanentemente todas las solicitudes y fotografías almacenadas en este teléfono.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        solicitudesViewModel.limpiarHistorialSolicitudes(authViewModel.estaAutenticado)
                        mostrarDialogoBorrado = false
                    }
                ) {
                    Text("Eliminar todo", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoBorrado = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Tarjeta del simulador aislada para que escribir el monto no recomponga las demás secciones de Ajustes
@Composable
private fun SimuladorImpactoCard(simuladorViewModel: SimuladorViewModel) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Dimens.SpaceMD)) {
            SettingsRowItem(
                icon = Icons.Outlined.VolunteerActivism,
                iconContainer = MaterialTheme.colorScheme.tertiaryContainer,
                iconTint = MaterialTheme.colorScheme.tertiary,
                title = "Simulador de aporte",
                subtitle = "Calcula cuántos insumos y sesiones financia tu contribución"
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMD))

            OutlinedTextField(
                value = simuladorViewModel.montoSimulado,
                onValueChange = { simuladorViewModel.onMontoSimuladoChange(it) },
                label = { Text("Monto a simular") },
                placeholder = { Text("25000") },
                prefix = { Text("$") },
                suffix = { Text("CLP") },
                leadingIcon = { Icon(Icons.Outlined.Calculate, contentDescription = null) },
                trailingIcon = {
                    if (simuladorViewModel.montoSimuladoError != null) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = "Error en monto",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = simuladorViewModel.montoSimuladoError != null,
                supportingText = simuladorViewModel.montoSimuladoError?.let { errorMsg ->
                    { Text(text = errorMsg, color = MaterialTheme.colorScheme.error) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceXS))

            Button(
                onClick = { simuladorViewModel.calcularImpactoSocial() },
                shape = PillShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = Dimens.MinTouchTarget)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.IconMD)
                )
                Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                Text("Calcular impacto del aporte", style = MaterialTheme.typography.labelLarge)
            }

            AnimatedVisibility(
                visible = simuladorViewModel.resultadoImpacto != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val montoNumerico = simuladorViewModel.montoSimulado.toLongOrNull() ?: 0L
                val kits = SimuladorViewModel.calcularKitsInsumos(montoNumerico)
                val sesiones = SimuladorViewModel.calcularSesionesApoyo(montoNumerico)

                ImpactoResultadoCard(
                    cifraPrincipal = if (sesiones > 0) {
                        "$kits kits · $sesiones sesiones"
                    } else {
                        "$kits kits de taller"
                    },
                    descripcion = simuladorViewModel.resultadoImpacto ?: "",
                    modifier = Modifier.padding(top = Dimens.SpaceMD)
                )
            }
        }
    }
}

// Tarjeta estática de información institucional para evitar recomposiciones innecesarias
@Composable
private fun AcercaDeCard() {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Dimens.SpaceMD)) {
            SettingsRowItem(
                icon = Icons.Outlined.Info,
                iconContainer = MaterialTheme.colorScheme.secondaryContainer,
                iconTint = MaterialTheme.colorScheme.secondary,
                title = "Alba Lab - CORPALIV",
                subtitle = "Inclusión social y laboral para jóvenes y adultos con discapacidad múltiple y sordoceguera."
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = Dimens.SpaceSM)
            )

            SettingsRowItem(
                icon = Icons.Outlined.LocationOn,
                iconContainer = MaterialTheme.colorScheme.primaryContainer,
                iconTint = MaterialTheme.colorScheme.primary,
                title = "Ubicación institucional",
                subtitle = "Santiago, Región Metropolitana, Chile"
            )
        }
    }
}

// Encabezado de sección en Ajustes
@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            start = Dimens.SpaceXXS,
            bottom = Dimens.SpaceXS
        )
    )
}

// Fila estándar con ícono contenedor, título y subtítulo
@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    iconContainer: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Dimens.MinTouchTarget)
            .clickable(onClick = onClick)
    } else {
        Modifier.fillMaxWidth()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = rowModifier
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = iconContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(Dimens.IconMD)
                )
            }
        }
        Spacer(modifier = Modifier.width(Dimens.SpaceSM))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Tarjeta de resultado destacado para el simulador de aportes
@Composable
fun ImpactoResultadoCard(
    cifraPrincipal: String,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
        border = BorderStroke(
            width = Dimens.BorderThin,
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Dimens.SpaceMD)
        ) {
            Surface(
                shape = PillShape,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(Dimens.IconXL)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.VolunteerActivism,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(Dimens.IconLG)
                    )
                }
            }
            Spacer(modifier = Modifier.width(Dimens.SpaceSM))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cifraPrincipal,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(Dimens.SpaceXXS))
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}