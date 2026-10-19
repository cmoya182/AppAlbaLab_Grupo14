package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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

// Pantalla principal que adapta su contenido al tamaño del dispositivo y contiene el menú lateral
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val windowSizeClass = obtenerWindowSizeClass()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Menú lateral desplegable con accesos a las distintas secciones de la aplicación
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Título del encabezado del menú lateral
                Text(
                    text = "Menú Alba Lab",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                HorizontalDivider()

                // Opción para abrir el catálogo de productos
                NavigationDrawerItem(
                    label = { Text(text = "Ver Catálogo") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Catalogo)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción para abrir el formulario de vinculación
                NavigationDrawerItem(
                    label = { Text(text = "Formulario de Vinculación") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Contacto)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción para ir al perfil
                NavigationDrawerItem(
                    label = { Text(text = "Ir a Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Profile)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción para ir a la configuración
                NavigationDrawerItem(
                    label = { Text(text = "Ir a Configuración") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Settings)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    ) {
        // Estructura con la barra superior de la pantalla de inicio
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "CORPALIV - Alba Lab") },
                    navigationIcon = {
                        // Botón que abre el menú lateral al presionarlo
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Abrir menú de navegación")
                        }
                    },
                    // Pinta la barra superior con el color morado institucional y letras blancas
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            // Contenedor que selecciona qué versión de pantalla mostrar según el ancho disponible
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