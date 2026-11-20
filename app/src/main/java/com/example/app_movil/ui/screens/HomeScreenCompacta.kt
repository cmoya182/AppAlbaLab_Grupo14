package com.example.app_movil.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBag
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.app_movil.R
import com.example.app_movil.ui.components.rememberReduceMotion
import com.example.app_movil.ui.theme.APP_MOVILTheme
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape

@Composable
fun HomeScreenCompacta(
    onCatalogoClick: () -> Unit = {},
    onEmpresaClick: () -> Unit = {},
    onVoluntariadoClick: () -> Unit = {}
) {
    val reduceMotion = rememberReduceMotion()

    // Recuerda si la animación de entrada ya se ejecutó para no repetirla al rotar el teléfono
    var animacionIniciada by rememberSaveable { mutableStateOf(reduceMotion) }

    LaunchedEffect(Unit) {
        animacionIniciada = true
    }

    val duracion = if (reduceMotion) 0 else 420
    val curvaSuave = FastOutSlowInEasing

    // Animación en cascada de las 3 tarjetas de acción
    val alpha1 by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = tween(durationMillis = duracion, delayMillis = if (reduceMotion) 0 else 50, easing = curvaSuave),
        label = "Alpha1"
    )
    val offsetY1 by animateFloatAsState(
        targetValue = if (animacionIniciada) 0f else 16f,
        animationSpec = tween(durationMillis = duracion, delayMillis = if (reduceMotion) 0 else 50, easing = curvaSuave),
        label = "OffsetY1"
    )

    val alpha2 by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = tween(durationMillis = duracion, delayMillis = if (reduceMotion) 0 else 130, easing = curvaSuave),
        label = "Alpha2"
    )
    val offsetY2 by animateFloatAsState(
        targetValue = if (animacionIniciada) 0f else 16f,
        animationSpec = tween(durationMillis = duracion, delayMillis = if (reduceMotion) 0 else 130, easing = curvaSuave),
        label = "OffsetY2"
    )

    val alpha3 by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = tween(durationMillis = duracion, delayMillis = if (reduceMotion) 0 else 210, easing = curvaSuave),
        label = "Alpha3"
    )
    val offsetY3 by animateFloatAsState(
        targetValue = if (animacionIniciada) 0f else 16f,
        animationSpec = tween(durationMillis = duracion, delayMillis = if (reduceMotion) 0 else 210, easing = curvaSuave),
        label = "OffsetY3"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.SpaceMD, vertical = Dimens.SpaceXS),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TarjetaMisionInstitucional()

        Spacer(modifier = Modifier.height(Dimens.SpaceLG))

        TarjetaAccionInteractiva(
            titulo = "Explorar Tienda Solidaria",
            subtitulo = "Catálogo de productos hechos en talleres",
            icono = Icons.Outlined.ShoppingBag,
            colorIconoFondo = MaterialTheme.colorScheme.primary,
            colorIcono = MaterialTheme.colorScheme.onPrimary,
            alpha = alpha1,
            offsetY = offsetY1,
            onClick = onCatalogoClick
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceSM))

        TarjetaAccionInteractiva(
            titulo = "Convenio Empresa / RSE",
            subtitulo = "Alianzas corporativas y compras por volumen",
            icono = Icons.Outlined.Business,
            colorIconoFondo = MaterialTheme.colorScheme.secondary,
            colorIcono = MaterialTheme.colorScheme.onSecondary,
            alpha = alpha2,
            offsetY = offsetY2,
            onClick = onEmpresaClick
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceSM))

        TarjetaAccionInteractiva(
            titulo = "Postular a Voluntariado",
            subtitulo = "Súmate y apoya a los jóvenes en talleres",
            icono = Icons.Outlined.FavoriteBorder,
            colorIconoFondo = MaterialTheme.colorScheme.tertiary,
            colorIcono = MaterialTheme.colorScheme.onTertiary,
            alpha = alpha3,
            offsetY = offsetY3,
            onClick = onVoluntariadoClick
        )
    }
}

// Tarjeta de misión institucional con logotipo y degradado de marca
@Composable
fun TarjetaMisionInstitucional(modifier: Modifier = Modifier) {
    val gradienteMarca = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f),
            MaterialTheme.colorScheme.surface
        )
    )

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationLow),
        border = BorderStroke(
            width = Dimens.BorderThin,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradienteMarca)
                .padding(Dimens.SpaceLG),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = Dimens.ElevationLow,
                border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo CORPALIV Alba Lab",
                    modifier = Modifier
                        .fillMaxWidth(0.46f)
                        .padding(horizontal = Dimens.SpaceMD, vertical = Dimens.SpaceSM)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceMD))

            Surface(
                shape = PillShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "INCLUSIÓN SOCIOLABORAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = Dimens.SpaceSM, vertical = Dimens.SpaceXXS)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceXS))

            Text(
                text = "Promovemos la autonomía y formación laboral de jóvenes y adultos con discapacidad múltiple y sordoceguera.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Tarjeta interactiva con efecto táctil de escala y elevación al pulsar
@Composable
fun TarjetaAccionInteractiva(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    colorIconoFondo: Color,
    colorIcono: Color,
    alpha: Float,
    offsetY: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reduceMotion = rememberReduceMotion()
    val interactionSource = remember { MutableInteractionSource() }
    val estaPresionado by interactionSource.collectIsPressedAsState()

    val escala by animateFloatAsState(
        targetValue = if (estaPresionado && !reduceMotion) 0.96f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "EscalaTarjeta"
    )

    val elevacion by animateDpAsState(
        targetValue = if (estaPresionado) Dimens.ElevationNone else Dimens.ElevationLow,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "ElevacionTarjeta"
    )

    val desplazamientoFlecha by animateDpAsState(
        targetValue = if (estaPresionado && !reduceMotion) Dimens.SpaceXXS else Dimens.ElevationNone,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "DesplazamientoFlecha"
    )

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = elevacion),
        border = BorderStroke(
            width = Dimens.BorderThin,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Dimens.MinTouchTarget)
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
                .padding(horizontal = Dimens.SpaceMD, vertical = Dimens.SpaceSM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = PillShape,
                color = colorIconoFondo,
                modifier = Modifier.size(Dimens.IconXL)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorIcono,
                        modifier = Modifier.size(Dimens.IconLG)
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.SpaceSM))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(Dimens.SpaceXS))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(Dimens.IconSM)
                    .offset(x = desplazamientoFlecha)
            )
        }
    }
}

// Vista previa de la tarjeta interactiva en modo claro y oscuro
@Preview(name = "ActionCard Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "ActionCard Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TarjetaAccionPreview() {
    APP_MOVILTheme {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(Dimens.SpaceMD)) {
            TarjetaAccionInteractiva(
                titulo = "Explorar Tienda Solidaria",
                subtitulo = "Catálogo de productos hechos en talleres",
                icono = Icons.Outlined.ShoppingBag,
                colorIconoFondo = MaterialTheme.colorScheme.primary,
                colorIcono = MaterialTheme.colorScheme.onPrimary,
                alpha = 1f,
                offsetY = 0f,
                onClick = {}
            )
        }
    }
}