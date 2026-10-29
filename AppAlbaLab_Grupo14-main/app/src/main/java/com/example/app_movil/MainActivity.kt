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
import com.example.app_movil.ui.screens.*
import com.example.app_movil.ui.theme.APP_MOVILTheme
import com.example.app_movil.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Actividad principal y punto de entrada del ciclo de vida de la aplicación Alba Lab.
 *
 * Implementa la arquitectura Single-Activity en conjunto con Jetpack Compose.
 * Sus responsabilidades principales comprenden:
 * 1. Inicialización del contenedor raíz y aplicación del tema institucional (APP_MOVILTheme).
 * 2. Cálculo del factor de forma de la pantalla mediante WindowSizeClass para diseño adaptable.
 * 3. Configuración del grafo declarativo de navegación (NavHost) y recolección de eventos asíncronos.
 * 4. Determinación dinámica de la ruta inicial (startDestination) según el estado de sesión persistido.
 */
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            APP_MOVILTheme {
                // Instancia centralizada del ViewModel vinculada al ciclo de vida del NavHost
                val viewModel: MainViewModel = viewModel()

                // Controlador del historial de pantallas y transiciones declarativas
                val navController = rememberNavController()

                // Cálculo de las dimensiones de la ventana para habilitar UI responsiva
                val windowSizeClass = calculateWindowSizeClass(this)

                // Colector reactivo de eventos únicos de navegación emitidos por el ViewModel
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
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    // Grafo de rutas de navegación: redirige a Home si existe sesión activa previa en SharedPreferences
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
                        composable(route = Screen.Contacto.route) {
                            ContactoEmpresaScreen(viewModel = viewModel)
                        }
                        composable(route = Screen.Profile.route) {
                            ProfileScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(route = Screen.Settings.route) {
                            SettingsScreen(navController = navController, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}