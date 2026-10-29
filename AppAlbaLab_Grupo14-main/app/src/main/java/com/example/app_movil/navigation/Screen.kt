package com.example.app_movil.navigation

sealed class Screen(val route: String) {
    data object Home : Screen(route = "home_page")
    data object Catalogo : Screen(route = "catalogo_page")
    data object Contacto : Screen(route = "contacto_page")
    data object ContactoEmpresa : Screen(route = "contacto_empresa_page")
    data object ContactoCompra : Screen(route = "contacto_compra_page")
    data object ContactoVoluntariado : Screen(route = "contacto_voluntariado_page")
    data object Profile : Screen(route = "profile_page")
    data object Settings : Screen(route = "settings_page")
    data object Login : Screen(route = "login_page")
}