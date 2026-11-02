package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.AppBottomBar
import com.example.app_movil.viewmodels.MainViewModel

/**
 * ARCHIVO: HomeScreen.kt
 * PROPÓSITO: Funciona como el contenedor principal de la pantalla de Inicio y selector de diseño adaptable (Responsive).
 *
 * ¿CÓMO FUNCIONA?:
 * 1. Dibuja el marco general (Scaffold) con la barra superior (TopAppBar) y la barra inferior (AppBottomBar).
 * 2. En la esquina superior derecha revisa viewModel.estaAutenticado: si hay sesión iniciada muestra el ícono
 *    de cerrar sesión; si es un invitado, muestra el botón "Acceder" para ir al Login.
 * 3. En el centro evalúa el ancho físico de la pantalla (windowSizeClass.widthSizeClass) mediante un bloque 'when':
 *    - Compact (teléfonos en vertical): Carga HomeScreenCompacta.kt
 *    - Medium (tablets pequeñas o teléfonos grandes): Carga HomeScreenMediana.kt
 *    - Expanded (tablets grandes o modo horizontal): Carga HomeScreenExpandida.kt
 *    En las tres variantes entrega las 3 acciones de navegación (Tienda, Convenio Empresa y Voluntariado).
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - MainActivity.kt: Recibe el viewModel y el windowSizeClass calculado al iniciar la actividad.
 * - HomeScreenCompacta.kt, HomeScreenMediana.kt y HomeScreenExpandida.kt: Carga una de las tres según la pantalla.
 * - AppBottomBar.kt: Muestra la barra de navegación inferior con la pestaña "Inicio" activa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    windowSizeClass: WindowSizeClass
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alba Lab - CORPALIV", fontWeight = FontWeight.Bold) },
                actions = {
                    if (viewModel.estaAutenticado) {
                        IconButton(onClick = { viewModel.cerrarSesion() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        TextButton(onClick = { viewModel.navigateTo(Screen.Login) }) {
                            Text(
                                "Acceder",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
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
                currentScreen = Screen.Home,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Acciones de navegación para los 3 canales de vinculación de Alba Lab
            val irACatalogo = { viewModel.navigateTo(Screen.Catalogo) }
            val irAEmpresa = { viewModel.navigateTo(Screen.ContactoEmpresa) }
            val irAVoluntariado = { viewModel.navigateTo(Screen.ContactoVoluntariado) }

            // Selección automática de interfaz según el ancho del dispositivo (IE 2.1.1 Adaptabilidad)
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> HomeScreenCompacta(
                    onCatalogoClick = irACatalogo,
                    onEmpresaClick = irAEmpresa,
                    onVoluntariadoClick = irAVoluntariado
                )
                WindowWidthSizeClass.Medium -> HomeScreenMediana(
                    onCatalogoClick = irACatalogo,
                    onEmpresaClick = irAEmpresa,
                    onVoluntariadoClick = irAVoluntariado
                )
                WindowWidthSizeClass.Expanded -> HomeScreenExpandida(
                    onCatalogoClick = irACatalogo,
                    onEmpresaClick = irAEmpresa,
                    onVoluntariadoClick = irAVoluntariado
                )
                else -> HomeScreenCompacta(
                    onCatalogoClick = irACatalogo,
                    onEmpresaClick = irAEmpresa,
                    onVoluntariadoClick = irAVoluntariado
                )
            }
        }
    }
}
