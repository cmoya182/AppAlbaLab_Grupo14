package com.example.app_movil.navigation

// Eventos de navegación desacoplados emitidos por MainViewModel hacia MainActivity
sealed class NavigationEvent {
    // Orden para navegar a una pantalla con opción de limpiar el historial (BackStack)
    data class NavigateTo(
        val route: Screen,
        val popupToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false,
        val clearBackStack: Boolean = false
    ) : NavigationEvent()

    // Orden para regresar a la pantalla anterior
    data object PopBackStack : NavigationEvent()
}