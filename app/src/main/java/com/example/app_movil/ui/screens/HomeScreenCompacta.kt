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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

/**
 * ARCHIVO: HomeScreenCompacta.kt
 * PROPÓSITO: Construye la vista principal de inicio para teléfonos móviles (WindowWidthSizeClass.Compact).
 *
 * ¿CÓMO FUNCIONA?:
 * 1. Muestra el logo institucional de CORPALIV, el propósito de Alba Lab y 3 tarjetas de acción:
 *    - Explorar Tienda Solidaria (lleva a CatalogoScreen).
 *    - Convenio Empresa / RSE (lleva a ContactoEmpresaScreen).
 *    - Postular a Voluntariado (lleva a ContactoVoluntariadoScreen).
 * 2. Al abrirse la pantalla, ejecuta una animación en cascada (Staggered Animation): cada tarjeta aparece
 *    progresivamente con retardos de 80ms, 200ms y 320ms usando animateFloatAsState y graphicsLayer.
 *    Gracias a graphicsLayer, las tarjetas ya tienen su espacio reservado desde el inicio, evitando que
 *    el logo de arriba dé saltos bruscos (Zero Layout Shift).
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - HomeScreen.kt: Invoca este archivo cuando detecta una pantalla compacta y le entrega las 3 funciones de clic.
 */
@Composable
fun HomeScreenCompacta(
    onCatalogoClick: () -> Unit = {},
    onEmpresaClick: () -> Unit = {},
    onVoluntariadoClick: () -> Unit = {}
) {
    // Variable de estado que pasa a 'true' apenas se dibuja la pantalla para iniciar la cascada de animaciones
    var animacionIniciada by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animacionIniciada = true
    }

    val duracion = 600
    val curvaSuave = FastOutSlowInEasing

    // Animación de entrada de la Tarjeta 1 (delay 80ms): opacidad de 0 a 1 y subida de 22px a 0px
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

    // Animación de entrada de la Tarjeta 2 (delay 200ms)
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

    // Animación de entrada de la Tarjeta 3 (delay 320ms)
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
        // Tarjeta que enmarca el logo oficial de CORPALIV (ubicado en res/drawable/logo.png)
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

        // Cuadro informativo con la misión social de CORPALIV
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

/**
 * Componente reutilizable para las tarjetas de acción del Home.
 *
 * ¿CÓMO FUNCIONA LA ANIMACIÓN TÁCTIL?:
 * Usa MutableInteractionSource para detectar el instante en que el usuario apoya el dedo (estaPresionado).
 * Mientras se presiona:
 * 1. La tarjeta se encoge un 5% (escala = 0.95f) con rebote de resorte (spring).
 * 2. Su sombra baja de 3.dp a 1.dp simulando que el botón se hunde físicamente.
 * 3. La flecha de la derecha avanza 6.dp invitando a navegar.
 */
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

    val escala by animateFloatAsState(
        targetValue = if (estaPresionado) 0.95f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "EscalaTarjeta"
    )

    val elevacion by animateDpAsState(
        targetValue = if (estaPresionado) 1.dp else 3.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "ElevacionTarjeta"
    )

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
