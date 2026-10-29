package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.AppBottomBar
import com.example.app_movil.viewmodels.MainViewModel

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
            val irACatalogo = { viewModel.navigateTo(Screen.Catalogo) }
            val irAEmpresa = { viewModel.navigateTo(Screen.ContactoEmpresa) }
            val irAVoluntariado = { viewModel.navigateTo(Screen.ContactoVoluntariado) }

            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> HomeScreenCompacta(
                    onCatalogoClick = irACatalogo,
                    onEmpresaClick = irAEmpresa,
                    onVoluntariadoClick = irAVoluntariado
                )
                WindowWidthSizeClass.Medium -> HomeScreenMediana(
                    onCatalogoClick = irACatalogo,
                    onContactoClick = irAEmpresa
                )
                WindowWidthSizeClass.Expanded -> HomeScreenExpandida(
                    onCatalogoClick = irACatalogo,
                    onContactoClick = irAEmpresa
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