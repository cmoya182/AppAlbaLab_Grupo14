package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.app_movil.navigation.Screen
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.ui.utils.obtenerWindowSizeClass
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val windowSizeClass = obtenerWindowSizeClass()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú Alba Lab",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text(text = "Ver Catálogo") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Catalogo)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(text = "Formulario de Vinculación") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Contacto)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(text = "Panel Institucional") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Profile)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(text = "Ir a Configuración") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Settings)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = {
                        Text(text = if (viewModel.estaAutenticado) "Cerrar Sesión" else "Acceso Equipo")
                    },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.estaAutenticado) Icons.AutoMirrored.Filled.ExitToApp else Icons.Default.Login,
                            contentDescription = null
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (viewModel.estaAutenticado) {
                            viewModel.cerrarSesion()
                        } else {
                            viewModel.navigateTo(Screen.Login)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "CORPALIV - Alba Lab") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Abrir menú de navegación")
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
            Box(modifier = Modifier.padding(innerPadding)) {
                val irACatalogo = { viewModel.navigateTo(Screen.Catalogo) }
                val irAContacto = { viewModel.navigateTo(Screen.Contacto) }

                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> HomeScreenCompacta(
                        onCatalogoClick = irACatalogo,
                        onContactoClick = irAContacto
                    )
                    WindowWidthSizeClass.Medium -> HomeScreenMediana(onButtonClick = irAContacto)
                    WindowWidthSizeClass.Expanded -> HomeScreenExpandida(onButtonClick = irAContacto)
                    else -> HomeScreenCompacta(
                        onCatalogoClick = irACatalogo,
                        onContactoClick = irAContacto
                    )
                }
            }
        }
    }
}