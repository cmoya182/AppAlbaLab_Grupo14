package com.example.app_movil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * ARCHIVO: Theme.kt
 * PROPÓSITO: Configura el tema global Material Design 3 de la aplicación (APP_MOVILTheme).
 *
 * ¿CÓMO FUNCIONA?:
 * Asigna los colores de Color.kt y las fuentes de Type.kt al contenedor MaterialTheme.
 * El parámetro dynamicColor está fijado en 'false' a propósito: en Android 12+, dynamicColor intenta
 * usar los colores del fondo de pantalla del usuario; al desactivarlo, garantizamos que la app
 * mantenga siempre los colores institucionales de CORPALIV en cualquier teléfono.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - Color.kt y Type.kt: Toma sus definiciones visuales.
 * - MainActivity.kt: Envuelve toda la interfaz dentro de APP_MOVILTheme { ... }.
 */
private val DarkColorScheme = darkColorScheme(
    primary = CorpalivPurple,
    secondary = CorpalivPink,
    tertiary = CorpalivGreen
)

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

@Composable
fun APP_MOVILTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
