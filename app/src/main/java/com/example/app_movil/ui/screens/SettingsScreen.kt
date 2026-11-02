package com.example.app_movil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.AppBottomBar
import com.example.app_movil.viewmodels.MainViewModel

/**
 * ARCHIVO: SettingsScreen.kt
 * PROPÓSITO: Construye la pestaña de "Impacto y Configuración" (Ajustes), que incluye tres herramientas:
 * 1. El Simulador de Impacto Social (para visibilizar el destino de las donaciones según el Caso CORPALIV).
 * 2. El control de sesión (permite iniciar o cerrar sesión administrativa).
 * 3. El botón para vaciar el historial local de SQLite con un cuadro de confirmación (AlertDialog).
 *
 * ¿CÓMO FUNCIONA EL SIMULADOR DE IMPACTO SOCIAL?:
 * El usuario escribe un monto en pesos chilenos (ej. 25000). El ViewModel valida en tiempo real que
 * solo sean números y que sea al menos $5.000 CLP. Al tocar "Calcular Impacto del Aporte", el ViewModel
 * calcula cuántos kits de taller ($5.000 c/u) y sesiones de apoyo ($15.000 c/u) equivale ese aporte,
 * y esta pantalla despliega el resultado con una animación suave (AnimatedVisibility).
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - MainViewModel.kt: Ejecuta onMontoSimuladoChange(), calcularImpactoSocial(), cerrarSesion() y limpiarHistorialSolicitudes().
 * - AppBottomBar.kt: Muestra la barra inferior con la pestaña "Ajustes" seleccionada.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    var mostrarDialogoBorrado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impacto y Configuración", fontWeight = FontWeight.Bold) },
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
                currentScreen = Screen.Settings,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Bloque 1: Información institucional de CORPALIV
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Acerca de Alba Lab - CORPALIV",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Iniciativa oficial orientada a la inclusión social y laboral de jóvenes y adultos con discapacidad múltiple y retos múltiples.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Santiago, Chile", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bloque 2: Simulador de Impacto Social (Valores referenciales de simulación para el MVP)
            Text(
                text = "Simulador de Impacto Social",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Descubre cómo tu aporte o compra solidaria financia insumos y talleres de inclusión:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.montoSimulado,
                        onValueChange = { viewModel.onMontoSimuladoChange(it) },
                        label = { Text("Monto en CLP (Ej: 25000)") },
                        leadingIcon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                        trailingIcon = {
                            if (viewModel.montoSimuladoError != null) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        isError = viewModel.montoSimuladoError != null,
                        supportingText = {
                            if (viewModel.montoSimuladoError != null) {
                                Text(text = viewModel.montoSimuladoError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.calcularImpactoSocial() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calcular Impacto del Aporte")
                    }

                    // Animación funcional (IE 2.2.2): hace aparecer suavemente la tarjeta verde con el resultado
                    AnimatedVisibility(
                        visible = viewModel.resultadoImpacto != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Text(
                                text = viewModel.resultadoImpacto ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bloque 3: Gestión de Cuenta y Sesión (SharedPreferences)
            Text(
                text = "Cuenta y Sesión",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (viewModel.estaAutenticado) {
                        Text(
                            text = "Sesión activa: ${viewModel.usuarioLogueado ?: "Administrador Alba Lab"}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.cerrarSesion() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cerrar Sesión")
                        }
                    } else {
                        Text(
                            text = "Modo Invitado / Donante",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.navigateTo(Screen.Login) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar Sesión como Equipo")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bloque 4: Vaciado completo de la tabla SQLite y archivos de imágenes
            Text(
                text = "Administración del Historial Local",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Permite vaciar las solicitudes guardadas localmente en este dispositivo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { mostrarDialogoBorrado = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Vaciar Historial de Solicitudes")
                    }
                }
            }
        }
    }

    // Diálogo de seguridad antes de eliminar todos los registros de Room SQLite
    if (mostrarDialogoBorrado) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrado = false },
            title = { Text("¿Vaciar historial?") },
            text = { Text("Esta acción eliminará todas las solicitudes almacenadas localmente en este dispositivo.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.limpiarHistorialSolicitudes()
                        mostrarDialogoBorrado = false
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
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
