package com.example.app_movil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * ARCHIVO: Type.kt
 * PROPÓSITO: Define los estilos tipográficos base de Material Design 3 (tamaños de letra, interlineado y grosor)
 * para asegurar una lectura clara y accesible en toda la aplicación.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - Theme.kt: Pasa esta variable Typography al componente MaterialTheme.
 */
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
