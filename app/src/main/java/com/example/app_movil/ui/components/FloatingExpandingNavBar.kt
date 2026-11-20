package com.example.app_movil.ui.components

import android.content.res.Configuration
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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

// Modelo interno para definir cada pestaña de la barra inferior
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
                    ExpandingNavPill(
                        tab = tab,
                        selected = selected,
                        reduceMotion = reduceMotion,
                        onClick = {
                            if (!selected) onNavigate(tab.screen)
                        },
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
    // Animación orgánica de progreso (0 = solo ícono, 1 = píldora elevada con etiqueta)
    val expandProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = if (reduceMotion) {
            tween(0)
        } else {
            spring(
                dampingRatio = 0.82f,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "ExpandProgress_${tab.label}"
    )

    // Elevación animada que da el efecto físico de píldora levantada al activarse
    val pillElevation by animateDpAsState(
        targetValue = if (selected) 5.dp else 0.dp,
        animationSpec = if (reduceMotion) {
            tween(0)
        } else {
            tween(durationMillis = 280)
        },
        label = "PillElevation_${tab.label}"
    )

    // Transición suave del color de fondo de la píldora activa
    val pillColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = if (reduceMotion) 0 else 260),
        label = "PillColor_${tab.label}"
    )

    // Transición suave del color del ícono y el texto
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = if (reduceMotion) 0 else 260),
        label = "ContentColor_${tab.label}"
    )

    // Cálculo de peso dinámico para expandir la píldora sin alterar el ancho total de la barra
    val itemWeight = 1f + (0.95f * expandProgress)
    val labelAlpha = ((expandProgress - 0.30f) / 0.70f).coerceIn(0f, 1f)
    val slideOffsetPx = with(LocalDensity.current) { (8.dp * (1f - labelAlpha)).toPx() }

    Surface(
        shape = PillShape,
        color = pillColor,
        shadowElevation = pillElevation,
        border = if (expandProgress > 0.1f) {
            BorderStroke(
                width = Dimens.BorderThin,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f * expandProgress)
            )
        } else {
            null
        },
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
                if (expandProgress > 0.05f) {
                    Spacer(modifier = Modifier.width(Dimens.SpaceXS * labelAlpha))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.graphicsLayer {
                            alpha = labelAlpha
                            translationX = slideOffsetPx
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