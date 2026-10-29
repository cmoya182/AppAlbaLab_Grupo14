package com.example.app_movil.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_movil.R

// =========================================================================================
// TORPEDO PARA LA DEFENSA - ANIMACIÓN DE ENTRADA PROGRESIVA Y SUAVE (HOME)
// =========================================================================================
// Calibración fina:
// - Duración de transición: 600ms con FastOutSlowInEasing (desaceleración suave y elegante).
// - Escalonamiento continuo: 80ms, 200ms y 320ms para una cascada pausada.
// - Desplazamiento flotante: 22dp sutiles sin tirones ni rebotes.
// =========================================================================================

@Composable
fun HomeScreenCompacta(
    onCatalogoClick: () -> Unit = {},
    onEmpresaClick: () -> Unit = {},
    onVoluntariadoClick: () -> Unit = {}
) {
    var animacionIniciada by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animacionIniciada = true
    }

    // Configuración compartida de duración y curva suave
    val duracion = 600
    val curvaSuave = FastOutSlowInEasing

    // Tarjeta 1: Tienda Solidaria
    val alpha1 by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = tween(durationMillis = duracion, delayMillis = 80, easing = curvaSuave),
        label = "Alpha1"
    )
    val offsetY1 by animateFloatAsState(
        targetValue = if (animacionIniciada) 0f else 22f,
        animationSpec = tween(durationMillis = duracion, delayMillis = 80, easing = curvaSuave),
        label = "OffsetY1"
    )

    // Tarjeta 2: Convenio Empresa / RSE
    val alpha2 by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = tween(durationMillis = duracion, delayMillis = 200, easing = curvaSuave),
        label = "Alpha2"
    )
    val offsetY2 by animateFloatAsState(
        targetValue = if (animacionIniciada) 0f else 22f,
        animationSpec = tween(durationMillis = duracion, delayMillis = 200, easing = curvaSuave),
        label = "OffsetY2"
    )

    // Tarjeta 3: Postular a Voluntariado
    val alpha3 by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = tween(durationMillis = duracion, delayMillis = 320, easing = curvaSuave),
        label = "Alpha3"
    )
    val offsetY3 by animateFloatAsState(
        targetValue = if (animacionIniciada) 0f else 22f,
        animationSpec = tween(durationMillis = duracion, delayMillis = 320, easing = curvaSuave),
        label = "OffsetY3"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo oficial institucional (Fijo y estable)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo CORPALIV Alba Lab",
                modifier = Modifier
                    .fillMaxWidth(0.52f)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            )
        }

        Text(
            text = "Alba Lab",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
            )
        ) {
            Text(
                text = "Promovemos la autonomía y formación sociolaboral de personas con discapacidad múltiple y sordoceguera.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- TARJETA 1: TIENDA SOLIDARIA ---
        TarjetaAccionInteractiva(
            titulo = "Explorar Tienda Solidaria",
            subtitulo = "Catálogo de productos hechos en talleres",
            icono = Icons.Default.ShoppingBag,
            colorContenedor = MaterialTheme.colorScheme.surface,
            colorIconoFondo = MaterialTheme.colorScheme.primary,
            colorIcono = MaterialTheme.colorScheme.onPrimary,
            colorTexto = MaterialTheme.colorScheme.onSurface,
            alpha = alpha1,
            offsetY = offsetY1,
            onClick = onCatalogoClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- TARJETA 2: CONVENIO EMPRESA / RSE ---
        TarjetaAccionInteractiva(
            titulo = "Convenio Empresa / RSE",
            subtitulo = "Alianzas corporativas y compras por volumen",
            icono = Icons.Default.Business,
            colorContenedor = MaterialTheme.colorScheme.surface,
            colorIconoFondo = MaterialTheme.colorScheme.secondary,
            colorIcono = MaterialTheme.colorScheme.onSecondary,
            colorTexto = MaterialTheme.colorScheme.onSurface,
            alpha = alpha2,
            offsetY = offsetY2,
            onClick = onEmpresaClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- TARJETA 3: POSTULAR A VOLUNTARIADO ---
        TarjetaAccionInteractiva(
            titulo = "Postular a Voluntariado",
            subtitulo = "Súmate y apoya a los jóvenes en talleres",
            icono = Icons.Default.Favorite,
            colorContenedor = MaterialTheme.colorScheme.surface,
            colorIconoFondo = MaterialTheme.colorScheme.tertiary,
            colorIcono = MaterialTheme.colorScheme.onTertiary,
            colorTexto = MaterialTheme.colorScheme.onSurface,
            alpha = alpha3,
            offsetY = offsetY3,
            onClick = onVoluntariadoClick
        )
    }
}

// =========================================================================================
// TARJETA INTERACTIVA DE ACCIÓN (MATERIAL DESIGN 3)
// =========================================================================================
@Composable
fun TarjetaAccionInteractiva(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    colorContenedor: Color,
    colorIconoFondo: Color,
    colorIcono: Color,
    colorTexto: Color,
    alpha: Float,
    offsetY: Float,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val estaPresionado by interactionSource.collectIsPressedAsState()

    // Respuesta física de escala al presionar
    val escala by animateFloatAsState(
        targetValue = if (estaPresionado) 0.95f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "EscalaTarjeta"
    )

    // Elevación: pasa de flotar con 3dp a 1dp al tocarla
    val elevacion by animateDpAsState(
        targetValue = if (estaPresionado) 1.dp else 3.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "ElevacionTarjeta"
    )

    // La flecha avanza 6dp avisando hacia dónde navega
    val desplazamientoFlecha by animateDpAsState(
        targetValue = if (estaPresionado) 6.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "DesplazamientoFlecha"
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colorContenedor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevacion),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                this.translationY = offsetY
                this.scaleX = escala
                this.scaleY = escala
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = colorIconoFondo,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorIcono,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorTexto
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorTexto.copy(alpha = 0.70f)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = colorTexto.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(16.dp)
                    .offset(x = desplazamientoFlecha)
            )
        }
    }
}