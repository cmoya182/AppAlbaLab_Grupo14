package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.app_movil.navigation.Screen
import com.example.app_movil.viewmodels.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    windowSizeClass: WindowSizeClass
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val irACatalogo = { viewModel.navigateTo(Screen.Catalogo) }
            val irAContacto = { viewModel.navigateTo(Screen.Contacto) }

            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> HomeScreenCompacta(
                    onCatalogoClick = irACatalogo,
                    onContactoClick = irAContacto
                )
                WindowWidthSizeClass.Medium -> HomeScreenMediana(
                    onCatalogoClick = irACatalogo,
                    onContactoClick = irAContacto
                )
                WindowWidthSizeClass.Expanded -> HomeScreenExpandida(
                    onCatalogoClick = irACatalogo,
                    onContactoClick = irAContacto
                )
                else -> HomeScreenCompacta(
                    onCatalogoClick = irACatalogo,
                    onContactoClick = irAContacto
                )
            }
        }
    }
}