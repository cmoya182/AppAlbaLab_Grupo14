package com.example.app_movil.navigation

// 'sealed class' define la lista cerrada de pantallas permitidas en la aplicación.
// Recibe 'route', que es la dirección en texto que utiliza Android para abrir cada vista.
sealed class Screen(val route: String) {

    // 'data object' define cada pantalla como una opción fija y única en memoria.
    data object Home : Screen(route = "home_page")            // Pantalla principal de inicio
    data object Catalogo : Screen(route = "catalogo_page")    // Pantalla del catálogo de productos
    data object Contacto : Screen(route = "contacto_page")    // Pantalla del formulario de vinculación CORPALIV
    data object Profile : Screen(route = "profile_page")      // Pantalla de perfil de usuario
    data object Settings : Screen(route = "settings_page")    // Pantalla de configuración
}