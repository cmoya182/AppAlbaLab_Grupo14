package com.example.app_movil.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.app_movil.navigation.Screen

/**
 * ARCHIVO: AppBottonBar.kt
 * PROPÓSITO: Construye la barra de navegación inferior reutilizable que aparece en las vistas principales.
 *
 * ¿CÓMO FUNCIONA?:
 * Define una lista de 4 pestañas principales: Inicio (Screen.Home), Tienda (Screen.Catalogo),
 * Panel (Screen.Profile) y Ajustes (Screen.Settings). Compara currentScreen == screen para resaltar
 * el ícono de la pantalla activa y evitar recargar la misma pantalla si el usuario toca la pestaña actual.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - Screen.kt: Obtiene las rutas de destino de cada botón.
 * - HomeScreen.kt, ProfileScreen.kt y SettingsScreen.kt: Colocan este componente en el parámetro bottomBar de su Scaffold.
 */
@Composable
fun AppBottomBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        Screen.Home to ("Inicio" to Icons.Default.Home),
        Screen.Catalogo to ("Tienda" to Icons.Default.ShoppingBag),
        Screen.Profile to ("Panel" to Icons.Default.Person),
        Screen.Settings to ("Ajustes" to Icons.Default.Settings)
    )

    NavigationBar {
        items.forEach { (screen, info) ->
            val (label, icon) = info
            NavigationBarItem(
                selected = currentScreen == screen,
                onClick = {
                    if (currentScreen != screen) {
                        onNavigate(screen)
                    }
                },
                label = { Text(label) },
                icon = { Icon(imageVector = icon, contentDescription = label) }
            )
        }
    }
}
