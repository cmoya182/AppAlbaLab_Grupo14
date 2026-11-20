package com.example.app_movil.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.viewmodels.UserRole

// Estructura para pantallas principales: ya no dibuja la barra inferior (vive fija en MainActivity)
// y entrega el padding inferior exacto para que el contenido nunca quede tapado por la barra flotante
@Composable
fun AppMainScaffold(
    currentScreen: Screen,
    rolActual: UserRole,
    onNavigate: (Screen) -> Unit,
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = topBar
    ) { innerPadding ->
        content(
            PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + Dimens.FloatingBarClearance
            )
        )
    }
}