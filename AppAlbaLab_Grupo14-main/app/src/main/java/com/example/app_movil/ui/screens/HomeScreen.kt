package com.example.app_movil.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import com.example.app_movil.ui.utils.obtenerWindowSizeClass

// "DIRECTOR": mide la pantalla y decide cuál de las 3 versiones mostrar.
// Se llama igual que antes (HomeScreen), así MainActivity NO necesita cambios.
@Composable
fun HomeScreen() {
    val windowSizeClass = obtenerWindowSizeClass()
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> HomeScreenCompacta()
        WindowWidthSizeClass.Medium -> HomeScreenMediana()
        WindowWidthSizeClass.Expanded -> HomeScreenExpandida()
        else -> HomeScreenCompacta()
    }
}
