package com.example.app_movil.ui.components

import android.content.res.Configuration
import android.provider.Settings
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.theme.APP_MOVILTheme
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape
import com.example.app_movil.viewmodels.UserRole

// Modelo interno inmutable para definir cada pestaña de la barra inferior
@Immutable
private data class NavTabItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

// Verifica si el usuario activó "reducir animaciones" en accesibilidad de Android
@Composable
fun rememberReduceMotion(): Boolean {
    if (LocalInspectionMode.current) return false
    val context = LocalContext.current
    return remember(context) {
        try {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) == 0f
        } catch (_: Exception) {
            false
        }
    }
}

// PRUÉBALO AQUÍ: Barra flotante persistente estilo "raised pill + pressed label" sin parpadeos al cambiar de pestaña.
@Composable
fun FloatingExpandingNavBar(
    currentScreen: Screen,
    rolActual: UserRole,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val reduceMotion = rememberReduceMotion()

    // Adapta la tercera pestaña según el rol (Noticias para invitado, Panel para administrador)
    val tabs = remember(rolActual) {
        val terceraTab = if (rolActual == UserRole.ADMINISTRADOR) {
            NavTabItem(Screen.Profile, "Panel", Icons.Outlined.Dashboard)
        } else {
            NavTabItem(Screen.Noticias, "Noticias", Icons.Outlined.Newspaper)
        }
        listOf(
            NavTabItem(Screen.Home, "Inicio", Icons.Outlined.Home),
            NavTabItem(Screen.Catalogo, "Tienda", Icons.Outlined.ShoppingBag),
            terceraTab,
            NavTabItem(Screen.Settings, "Ajustes", Icons.Outlined.Settings)
        )
    }

    // Contenedor flotante que respeta la barra de gestos inferior del sistema
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = Dimens.SpaceMD, vertical = Dimens.SpaceXS)
    ) {
        Surface(
            shape = PillShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = Dimens.ElevationHigh,
            border = BorderStroke(
                width = Dimens.BorderThin,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.FloatingBarHeight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.SpaceXS, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceXXS)
            ) {
                tabs.forEach { tab ->
                    val selected = currentScreen == tab.screen
                    val onTabClick = remember(tab.screen, selected, onNavigate) {
                        { if (!selected) onNavigate(tab.screen) }
                    }
                    ExpandingNavPill(
                        tab = tab,
                        selected = selected,
                        reduceMotion = reduceMotion,
                        onClick = onTabClick,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
        }
    }
}

// Píldora elevada (raised pill) con expansión simultánea por física de resorte y sombra animada
@Composable
private fun androidx.compose.foundation.layout.RowScope.ExpandingNavPill(
    tab: NavTabItem,
    selected: Boolean,
    reduceMotion: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Transición unificada en un único reloj para progreso, elevación y colores de la píldora
    val transition = updateTransition(targetState = selected, label = "PillTransition_${tab.label}")

    val expandProgress by transition.animateFloat(
        transitionSpec = {
            if (reduceMotion) {
                tween(0)
            } else {
                spring(
                    dampingRatio = 0.85f,
                    stiffness = Spring.StiffnessMedium
                )
            }
        },
        label = "ExpandProgress_${tab.label}"
    ) { isSelected -> if (isSelected) 1f else 0f }

    val pillElevation by transition.animateDp(
        transitionSpec = {
            if (reduceMotion) tween(0) else tween(durationMillis = 200, easing = FastOutSlowInEasing)
        },
        label = "PillElevation_${tab.label}"
    ) { isSelected -> if (isSelected) 4.dp else 0.dp }

    val pillColor by transition.animateColor(
        transitionSpec = {
            tween(durationMillis = if (reduceMotion) 0 else 200, easing = FastOutSlowInEasing)
        },
        label = "PillColor_${tab.label}"
    ) { isSelected ->
        if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    }

    val contentColor by transition.animateColor(
        transitionSpec = {
            tween(durationMillis = if (reduceMotion) 0 else 200, easing = FastOutSlowInEasing)
        },
        label = "ContentColor_${tab.label}"
    ) { isSelected ->
        if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    }

    // Cálculo memorizado de desplazamiento y borde sin asignaciones por frame
    val density = LocalDensity.current
    val maxSlidePx = remember(density) { with(density) { 8.dp.toPx() } }
    val itemWeight = 1f + (0.95f * expandProgress)
    val primaryColor = MaterialTheme.colorScheme.primary
    val borderStroke = remember(selected, primaryColor) {
        if (selected) {
            BorderStroke(width = Dimens.BorderThin, color = primaryColor.copy(alpha = 0.22f))
        } else {
            null
        }
    }

    Surface(
        shape = PillShape,
        color = pillColor,
        shadowElevation = pillElevation,
        border = borderStroke,
        modifier = modifier
            .weight(itemWeight)
            .defaultMinSize(minWidth = Dimens.MinTouchTarget, minHeight = Dimens.MinTouchTarget)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    role = Role.Tab
                )
                .padding(horizontal = Dimens.SpaceSM)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label,
                    tint = contentColor,
                    modifier = Modifier.size(Dimens.IconLG)
                )
                if (selected || expandProgress > 0.02f) {
                    Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.graphicsLayer {
                            val labelAlpha = ((expandProgress - 0.30f) / 0.70f).coerceIn(0f, 1f)
                            alpha = labelAlpha
                            translationX = maxSlidePx * (1f - labelAlpha)
                        }
                    )
                }
            }
        }
    }
}

// Vista previa de la barra flotante en modo claro y oscuro
@Preview(name = "NavBar Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "NavBar Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun FloatingExpandingNavBarPreview() {
    APP_MOVILTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FloatingExpandingNavBar(
                currentScreen = Screen.Catalogo,
                rolActual = UserRole.INVITADO,
                onNavigate = {}
            )
        }
    }
}