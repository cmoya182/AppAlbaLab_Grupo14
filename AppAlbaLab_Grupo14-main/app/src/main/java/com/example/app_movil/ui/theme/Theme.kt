package com.example.app_movil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Configuración de colores para el modo oscuro
private val DarkColorScheme = darkColorScheme(
    primary = CorpalivPurple,
    secondary = CorpalivPink,
    tertiary = CorpalivGreen
)

// Configuración de colores institucionales de CORPALIV para el modo claro
private val LightColorScheme = lightColorScheme(
    primary = CorpalivPurple,
    onPrimary = Color.White,
    secondary = CorpalivPink,
    onSecondary = Color.White,
    tertiary = CorpalivGreen,
    background = CorpalivBackground,
    surface = CorpalivSurface,
    secondaryContainer = CorpalivPurpleLight,
    onSecondaryContainer = CorpalivPurple
)

// Función que aplica la paleta de colores y tipografía a todas las pantallas de la app
@Composable
fun APP_MOVILTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Se desactiva dynamicColor (false) para que Android respete los colores de CORPALIV
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}