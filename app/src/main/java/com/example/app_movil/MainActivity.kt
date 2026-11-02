package com.example.app_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app_movil.navigation.NavigationEvent
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.screens.CatalogoScreen
import com.example.app_movil.ui.screens.ContactoCompraScreen
import com.example.app_movil.ui.screens.ContactoEmpresaScreen
import com.example.app_movil.ui.screens.ContactoVoluntariadoScreen
import com.example.app_movil.ui.screens.HomeScreen
import com.example.app_movil.ui.screens.LoginScreen
import com.example.app_movil.ui.screens.ProfileScreen
import com.example.app_movil.ui.screens.SettingsScreen
import com.example.app_movil.ui.theme.APP_MOVILTheme
import com.example.app_movil.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ARCHIVO: MainActivity.kt
 * PROPÓSITO: Es el punto de entrada principal de la aplicación Android (declarado en AndroidManifest.xml).
 *
 * ¿CÓMO FUNCIONA?:
 * 1. Al tocar el ícono de Alba Lab en el teléfono, Android ejecuta la función onCreate().
 * 2. Aplica el tema visual institucional (APP_MOVILTheme) y crea una instancia única de MainViewModel.
 * 3. Mide el ancho de la pantalla con calculateWindowSizeClass(this) para saber si es un teléfono o una tablet.
 * 4. Configura el NavHost (mapa de navegación) con las 8 pantallas del proyecto. Si el usuario ya había
 *    iniciado sesión antes (viewModel.estaAutenticado == true), entra directo a Home; si no, abre Login.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - MainViewModel.kt: Escucha su flujo navigationEvents para cambiar de pantalla cuando el usuario interactúa.
 * - Screen.kt y NavigationEvent.kt: Define las rutas ("login_page", "home_page", etc.) y acciones de viaje.
 * - Todas las pantallas en ui/screens/: Las registra dentro de cada bloque composable(route = ...).
 */
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            APP_MOVILTheme {
                // Instancia compartida del ViewModel que sobrevive a giros de pantalla y cambios de vista
                val viewModel: MainViewModel = viewModel()

                // Controlador de navegación que recuerda el historial de pantallas visitadas (BackStack)
                val navController = rememberNavController()

                // Detecta el tamaño físico de la ventana (Compact, Medium o Expanded)
                val windowSizeClass = calculateWindowSizeClass(this)

                /**
                 * Escuchador reactivo de navegación:
                 * LaunchedEffect abre una corrutina que permanece atenta a los eventos que emite el ViewModel.
                 * Cuando el ViewModel ejecuta navigateTo() o navigateBack(), este bloque mueve al usuario de pantalla.
                 */
                LaunchedEffect(key1 = Unit) {
                    viewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.route.route) {
                                    event.popupToRoute?.let {
                                        popUpTo(it.route) { inclusive = event.inclusive }
                                    }
                                    launchSingleTop = event.singleTop
                                    restoreState = true
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    /**
                     * Mapa central de navegación (NavHost):
                     * Evalúa viewModel.estaAutenticado (leído desde SharedPreferences) para decidir la primera pantalla.
                     */
                    NavHost(
                        navController = navController,
                        startDestination = if (viewModel.estaAutenticado) Screen.Home.route else Screen.Login.route
                    ) {
                        composable(route = Screen.Login.route) {
                            LoginScreen(viewModel = viewModel)
                        }
                        composable(route = Screen.Home.route) {
                            HomeScreen(viewModel = viewModel, windowSizeClass = windowSizeClass)
                        }
                        composable(route = Screen.Catalogo.route) {
                            CatalogoScreen(viewModel = viewModel)
                        }
                        composable(route = Screen.ContactoEmpresa.route) {
                            ContactoEmpresaScreen(viewModel = viewModel)
                        }
                        composable(route = Screen.ContactoCompra.route) {
                            ContactoCompraScreen(viewModel = viewModel)
                        }
                        composable(route = Screen.ContactoVoluntariado.route) {
                            ContactoVoluntariadoScreen(viewModel = viewModel)
                        }
                        composable(route = Screen.Profile.route) {
                            ProfileScreen(viewModel = viewModel)
                        }
                        composable(route = Screen.Settings.route) {
                            SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
