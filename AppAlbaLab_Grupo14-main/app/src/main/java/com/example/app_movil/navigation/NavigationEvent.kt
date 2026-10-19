package com.example.app_movil.navigation

// Representa los distintos tipos de acciones de navegación que podemos hacer
sealed class NavigationEvent {

    // Evento para viajar a una pantalla específica
    data class NavigateTo(
        val route: Screen,
        val popupToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    // Evento para volver a la pantalla anterior (botón atrás del celular)
    object PopBackStack : NavigationEvent()

    // Evento para navegar hacia arriba en la jerarquía
    object NavigateUp : NavigationEvent()
}