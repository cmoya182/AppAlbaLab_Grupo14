package com.example.app_movil.navigation

// Definición de rutas de navegación y marca de restricción para pantallas administrativas
sealed class Screen(
    val route: String,
    val requiereAdministrador: Boolean = false
) {
    // Rutas públicas accesibles tanto para invitados como para administradores
    data object Login : Screen(route = "login_page")
    data object Home : Screen(route = "home_page")
    data object Catalogo : Screen(route = "catalogo_page")
    data object Noticias : Screen(route = "noticias_page")
    data object ContactoEmpresa : Screen(route = "contacto_empresa_page")
    data object ContactoCompra : Screen(route = "contacto_compra_page")
    data object ContactoVoluntariado : Screen(route = "contacto_voluntariado_page")
    data object Settings : Screen(route = "settings_page")

    // Ruta protegida exclusiva para el equipo administrador de CORPALIV
    data object Profile : Screen(route = "profile_page", requiereAdministrador = true)
}