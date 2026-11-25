package com.example.app_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app_movil.navigation.NavigationEvent
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.FloatingExpandingNavBar
import com.example.app_movil.ui.screens.CatalogoScreen
import com.example.app_movil.ui.screens.ContactoCompraScreen
import com.example.app_movil.ui.screens.ContactoEmpresaScreen
import com.example.app_movil.ui.screens.ContactoVoluntariadoScreen
import com.example.app_movil.ui.screens.HomeScreen
import com.example.app_movil.ui.screens.LoginScreen
import com.example.app_movil.ui.screens.NoticiasScreen
import com.example.app_movil.ui.screens.ProfileScreen
import com.example.app_movil.ui.screens.SettingsScreen
import com.example.app_movil.ui.theme.APP_MOVILTheme
import com.example.app_movil.viewmodels.AuthViewModel
import com.example.app_movil.viewmodels.CatalogoViewModel
import com.example.app_movil.viewmodels.IndicadorViewModel
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.viewmodels.NoticiasViewModel
import com.example.app_movil.viewmodels.SimuladorViewModel
import com.example.app_movil.viewmodels.SolicitudesViewModel
import com.example.app_movil.viewmodels.UserRole
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    // Devuelve el índice de cada pestaña para animar el deslizamiento hacia izquierda o derecha
    private fun indiceDeTab(ruta: String?): Int? = when (ruta) {
        Screen.Home.route -> 0
        Screen.Catalogo.route -> 1
        Screen.Noticias.route, Screen.Profile.route -> 2
        Screen.Settings.route -> 3
        else -> null
    }

    // Convierte la ruta guardada en String (compatible con rememberSaveable) a su objeto Screen
    private fun rutaATabScreen(ruta: String): Screen = when (ruta) {
        Screen.Catalogo.route -> Screen.Catalogo
        Screen.Noticias.route -> Screen.Noticias
        Screen.Profile.route -> Screen.Profile
        Screen.Settings.route -> Screen.Settings
        else -> Screen.Home
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            APP_MOVILTheme {
                // Instancias de los ViewModels separados por módulo (incluyendo indicador de UF diaria)
                val mainViewModel: MainViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                val catalogoViewModel: CatalogoViewModel = viewModel()
                val noticiasViewModel: NoticiasViewModel = viewModel()
                val indicadorViewModel: IndicadorViewModel = viewModel()
                val solicitudesViewModel: SolicitudesViewModel = viewModel()
                val simuladorViewModel: SimuladorViewModel = viewModel()

                // Controlador de navegación y detector de tamaño de pantalla
                val navController = rememberNavController()
                val windowSizeClass = calculateWindowSizeClass(this)

                // Conserva la ruta inicial ante rotaciones y recreación de la actividad
                val destinoInicial = rememberSaveable {
                    if (authViewModel.estaAutenticado) Screen.Home.route else Screen.Login.route
                }

                // Observa la ruta activa para mostrar u ocultar la barra inferior flotante
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val rutaActual = navBackStackEntry?.destination?.route

                val pantallaTabActual = when (rutaActual) {
                    Screen.Home.route -> Screen.Home
                    Screen.Catalogo.route -> Screen.Catalogo
                    Screen.Noticias.route -> Screen.Noticias
                    Screen.Profile.route -> Screen.Profile
                    Screen.Settings.route -> Screen.Settings
                    null -> if (destinoInicial == Screen.Home.route) Screen.Home else null
                    else -> null
                }

                // Guarda la última pestaña activa como String para sobrevivir rotaciones
                var ultimaTabRuta by rememberSaveable { mutableStateOf(Screen.Home.route) }
                if (pantallaTabActual != null) {
                    ultimaTabRuta = pantallaTabActual.route
                }

                // Escuchador de eventos de navegación con control de permisos y preservación de estado
                LaunchedEffect(key1 = Unit) {
                    mainViewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                if (event.route.requiereAdministrador && authViewModel.rolActual != UserRole.ADMINISTRADOR) {
                                    return@collectLatest
                                }
                                val esTab = indiceDeTab(event.route.route) != null
                                navController.navigate(event.route.route) {
                                    if (event.clearBackStack) {
                                        popUpTo(0) { inclusive = true }
                                    } else if (esTab) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                    } else {
                                        event.popupToRoute?.let {
                                            popUpTo(it.route) { inclusive = event.inclusive }
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = !event.clearBackStack
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                        }
                    }
                }

                // Contenedor principal con el NavHost animado debajo y la barra flotante fija encima
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Mapa de pantallas con transiciones direccionales suaves (slide + fade)
                        NavHost(
                            navController = navController,
                            startDestination = destinoInicial,
                            modifier = Modifier.fillMaxSize(),
                            enterTransition = {
                                val origen = indiceDeTab(initialState.destination.route)
                                val destino = indiceDeTab(targetState.destination.route)
                                when {
                                    origen != null && destino != null -> {
                                        val direccion = if (destino >= origen) 1 else -1
                                        slideInHorizontally(
                                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                                        ) { ancho -> (ancho * 0.25f * direccion).toInt() } +
                                                fadeIn(animationSpec = tween(240, easing = FastOutSlowInEasing))
                                    }
                                    else -> {
                                        slideInHorizontally(
                                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                                        ) { ancho -> ancho } +
                                                fadeIn(animationSpec = tween(220))
                                    }
                                }
                            },
                            exitTransition = {
                                val origen = indiceDeTab(initialState.destination.route)
                                val destino = indiceDeTab(targetState.destination.route)
                                when {
                                    origen != null && destino != null -> {
                                        val direccion = if (destino >= origen) -1 else 1
                                        slideOutHorizontally(
                                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                                        ) { ancho -> (ancho * 0.20f * direccion).toInt() } +
                                                fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing))
                                    }
                                    else -> {
                                        slideOutHorizontally(
                                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                                        ) { ancho -> -(ancho * 0.20f).toInt() } +
                                                fadeOut(animationSpec = tween(200))
                                    }
                                }
                            },
                            popEnterTransition = {
                                slideInHorizontally(
                                    animationSpec = tween(280, easing = FastOutSlowInEasing)
                                ) { ancho -> -(ancho * 0.22f).toInt() } +
                                        fadeIn(animationSpec = tween(220))
                            },
                            popExitTransition = {
                                slideOutHorizontally(
                                    animationSpec = tween(280, easing = FastOutSlowInEasing)
                                ) { ancho -> ancho } +
                                        fadeOut(animationSpec = tween(200))
                            }
                        ) {
                            // Pantalla de inicio de sesión
                            composable(route = Screen.Login.route) {
                                LoginScreen(
                                    mainViewModel = mainViewModel,
                                    authViewModel = authViewModel
                                )
                            }

                            // Pantalla de inicio con el banner de UF diaria conectado
                            composable(route = Screen.Home.route) {
                                HomeScreen(
                                    mainViewModel = mainViewModel,
                                    authViewModel = authViewModel,
                                    indicadorViewModel = indicadorViewModel,
                                    windowSizeClass = windowSizeClass
                                )
                            }

                            // Pantalla del catálogo de la Tienda Solidaria
                            composable(route = Screen.Catalogo.route) {
                                CatalogoScreen(
                                    mainViewModel = mainViewModel,
                                    authViewModel = authViewModel,
                                    catalogoViewModel = catalogoViewModel,
                                    solicitudesViewModel = solicitudesViewModel
                                )
                            }

                            // Pantalla de noticias oficiales de CORPALIV (para invitados)
                            composable(route = Screen.Noticias.route) {
                                NoticiasScreen(
                                    mainViewModel = mainViewModel,
                                    authViewModel = authViewModel,
                                    noticiasViewModel = noticiasViewModel
                                )
                            }

                            // Formularios de vinculación (Empresa, Compra Solidaria y Voluntariado)
                            composable(route = Screen.ContactoEmpresa.route) {
                                ContactoEmpresaScreen(
                                    mainViewModel = mainViewModel,
                                    solicitudesViewModel = solicitudesViewModel
                                )
                            }
                            composable(route = Screen.ContactoCompra.route) {
                                ContactoCompraScreen(
                                    mainViewModel = mainViewModel,
                                    solicitudesViewModel = solicitudesViewModel
                                )
                            }
                            composable(route = Screen.ContactoVoluntariado.route) {
                                ContactoVoluntariadoScreen(
                                    mainViewModel = mainViewModel,
                                    solicitudesViewModel = solicitudesViewModel
                                )
                            }

                            // Panel administrativo protegido por rol con redirección si entra un invitado
                            composable(route = Screen.Profile.route) {
                                if (authViewModel.rolActual == UserRole.ADMINISTRADOR) {
                                    ProfileScreen(
                                        mainViewModel = mainViewModel,
                                        authViewModel = authViewModel,
                                        solicitudesViewModel = solicitudesViewModel
                                    )
                                } else {
                                    LaunchedEffect(Unit) {
                                        navController.navigate(Screen.Noticias.route) {
                                            popUpTo(Screen.Profile.route) { inclusive = true }
                                        }
                                    }
                                }
                            }

                            // Pantalla de ajustes y simulador de impacto social
                            composable(route = Screen.Settings.route) {
                                SettingsScreen(
                                    mainViewModel = mainViewModel,
                                    authViewModel = authViewModel,
                                    solicitudesViewModel = solicitudesViewModel,
                                    simuladorViewModel = simuladorViewModel
                                )
                            }
                        }

                        // Barra flotante persistente sobre las 4 pestañas principales
                        AnimatedVisibility(
                            visible = pantallaTabActual != null,
                            enter = slideInVertically(tween(240)) { it } + fadeIn(tween(200)),
                            exit = slideOutVertically(tween(200)) { it } + fadeOut(tween(160)),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            FloatingExpandingNavBar(
                                currentScreen = rutaATabScreen(ultimaTabRuta),
                                rolActual = authViewModel.rolActual,
                                onNavigate = { mainViewModel.navigateTo(it, authViewModel.rolActual) }
                            )
                        }
                    }
                }
            }
        }
    }
}