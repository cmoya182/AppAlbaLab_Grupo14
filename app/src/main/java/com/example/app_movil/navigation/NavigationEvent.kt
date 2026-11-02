package com.example.app_movil.navigation

/**
 * ARCHIVO: NavigationEvent.kt
 * PROPÓSITO: Define los tipos de órdenes de viaje que el MainViewModel puede enviarle a MainActivity.
 *
 * ¿CÓMO FUNCIONA?:
 * En la arquitectura MVVM, el ViewModel no debe manipular directamente el navController de la interfaz.
 * Por eso, el ViewModel emite uno de estos 2 eventos a través de un SharedFlow y MainActivity lo ejecuta.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - MainViewModel.kt: Crea y emite estos eventos en navigateTo() y navigateBack().
 * - MainActivity.kt: Escucha estos eventos dentro de LaunchedEffect y ejecuta la acción en el navController.
 */
sealed class NavigationEvent {

    // Orden para ir hacia una pantalla específica (ej. de Login a Home, o de Catálogo a Compra)
    data class NavigateTo(
        val route: Screen,
        val popupToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    // Orden para regresar a la pantalla inmediatamente anterior (flecha Volver)
    object PopBackStack : NavigationEvent()
}
