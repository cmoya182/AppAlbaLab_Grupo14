package com.example.app_movil.navigation

// Define la lista cerrada de pantallas permitidas en la aplicación
sealed class Screen(val route: String) {
    data object Home : Screen(route = "home_page")
    data object Catalogo : Screen(route = "catalogo_page")
    data object Contacto : Screen(route = "contacto_page")
    data object Profile : Screen(route = "profile_page")
    data object Settings : Screen(route = "settings_page")
    data object Login : Screen(route = "login_page") // Pantalla de acceso exclusivo del equipo
}