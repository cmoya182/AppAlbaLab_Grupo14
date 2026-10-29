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