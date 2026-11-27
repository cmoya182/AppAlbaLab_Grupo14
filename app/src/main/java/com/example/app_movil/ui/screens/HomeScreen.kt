package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.components.AlbaTopBar
import com.example.app_movil.ui.components.AppMainScaffold
import com.example.app_movil.ui.components.BannerUfDiaria
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape
import com.example.app_movil.viewmodels.AuthViewModel
import com.example.app_movil.viewmodels.IndicadorViewModel
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.viewmodels.UserRole

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    indicadorViewModel: IndicadorViewModel,
    windowSizeClass: WindowSizeClass
) {
    // Estructura principal con barra superior de marca y navegación por rol
    AppMainScaffold(
        currentScreen = Screen.Home,
        rolActual = authViewModel.rolActual,
        onNavigate = { mainViewModel.navigateTo(it, authViewModel.rolActual) },
        topBar = {
            AlbaTopBar(
                title = "Alba Lab",
                subtitle = "Red Sociolaboral CORPALIV",
                showBrandLogo = true,
                actions = {
                    if (authViewModel.rolActual == UserRole.INVITADO) {
                        FilledTonalButton(
                            onClick = { mainViewModel.navigateTo(Screen.Login, authViewModel.rolActual) },
                            shape = PillShape,
                            contentPadding = PaddingValues(horizontal = Dimens.SpaceMD),
                            modifier = Modifier
                                .padding(end = Dimens.SpaceXS)
                                .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                        ) {
                            Text(
                                text = "Acceder",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Banner compacto de UF diaria ubicado justo debajo del encabezado superior
            BannerUfDiaria(
                state = indicadorViewModel.uiState,
                onReintentar = { indicadorViewModel.cargarUfDiaria() },
                modifier = Modifier.padding(
                    horizontal = Dimens.SpaceMD,
                    vertical = Dimens.SpaceXS
                )
            )

            // Contenedor adaptable que conserva el diseño responsivo (Compact, Medium, Expanded)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val rolActual = authViewModel.rolActual
                val irACatalogo = remember(mainViewModel, rolActual) {
                    { mainViewModel.navigateTo(Screen.Catalogo, rolActual) }
                }
                val irAEmpresa = remember(mainViewModel, rolActual) {
                    { mainViewModel.navigateTo(Screen.ContactoEmpresa, rolActual) }
                }
                val irAVoluntariado = remember(mainViewModel, rolActual) {
                    { mainViewModel.navigateTo(Screen.ContactoVoluntariado, rolActual) }
                }

                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> HomeScreenCompacta(
                        onCatalogoClick = irACatalogo,
                        onEmpresaClick = irAEmpresa,
                        onVoluntariadoClick = irAVoluntariado
                    )
                    WindowWidthSizeClass.Medium -> HomeScreenMediana(
                        onCatalogoClick = irACatalogo,
                        onEmpresaClick = irAEmpresa,
                        onVoluntariadoClick = irAVoluntariado
                    )
                    WindowWidthSizeClass.Expanded -> HomeScreenExpandida(
                        onCatalogoClick = irACatalogo,
                        onEmpresaClick = irAEmpresa,
                        onVoluntariadoClick = irAVoluntariado
                    )
                    else -> HomeScreenCompacta(
                        onCatalogoClick = irACatalogo,
                        onEmpresaClick = irAEmpresa,
                        onVoluntariadoClick = irAVoluntariado
                    )
                }
            }
        }
    }
}