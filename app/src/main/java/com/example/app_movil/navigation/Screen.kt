package com.example.app_movil.navigation

/**
 * ARCHIVO: Screen.kt
 * PROPÓSITO: Contiene el listado oficial de rutas de navegación de todas las pantallas de la app.
 *
 * ¿CÓMO FUNCIONA?:
 * Utiliza una "sealed class" (clase sellada), que funciona como un catálogo cerrado de pantallas.
 * En vez de escribir textos sueltos como "home_page" en distintos archivos (con riesgo de equivocarse en una letra),
 * llamamos a objetos seguros como Screen.Home o Screen.Catalogo.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - MainActivity.kt: Registra cada una de estas 8 rutas dentro del NavHost.
 * - MainViewModel.kt: Recibe un objeto Screen en su función navigateTo(screen: Screen).
 * - AppBottonBar.kt: Usa Screen.Home, Screen.Catalogo, Screen.Profile y Screen.Settings para los 4 botones inferiores.
 */
sealed class Screen(val route: String) {
    data object Login : Screen(route = "login_page")
    data object Home : Screen(route = "home_page")
    data object Catalogo : Screen(route = "catalogo_page")
    data object ContactoEmpresa : Screen(route = "contacto_empresa_page")
    data object ContactoCompra : Screen(route = "contacto_compra_page")
    data object ContactoVoluntariado : Screen(route = "contacto_voluntariado_page")
    data object Profile : Screen(route = "profile_page")
    data object Settings : Screen(route = "settings_page")
}
