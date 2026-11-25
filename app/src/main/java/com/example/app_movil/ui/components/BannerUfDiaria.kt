package com.example.app_movil.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_movil.ui.theme.APP_MOVILTheme
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape
import com.example.app_movil.viewmodels.UfUiState

// PRUÉBALO AQUÍ: Banner compacto de UF diaria ubicado entre la barra superior y la tarjeta de misión.
@Composable
fun BannerUfDiaria(
    state: UfUiState,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = Dimens.ElevationLow,
        border = BorderStroke(
            width = Dimens.BorderThin,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpaceMD, vertical = Dimens.SpaceSM)
        ) {
            // Ícono indicador y etiqueta de referencia para aportes/convenios
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = PillShape,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.CurrencyExchange,
                            contentDescription = "Indicador UF diaria",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(Dimens.IconMD)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Dimens.SpaceSM))

                Column {
                    Text(
                        text = "Valor UF hoy (Chile)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when (state) {
                            is UfUiState.Cargando -> "Actualizando desde mindicador.cl..."
                            is UfUiState.Exito -> "Actualizada el ${state.fecha} · Referencia RSE"
                            is UfUiState.Error -> "Sin conexión para consultar UF"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.SpaceXS))

            // Valor numérico destacado, indicador de carga o botón de reintento
            when (state) {
                is UfUiState.Cargando -> {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                is UfUiState.Exito -> {
                    Surface(
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = state.valorFormateado,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(
                                horizontal = Dimens.SpaceSM,
                                vertical = Dimens.SpaceXXS
                            )
                        )
                    }
                }

                is UfUiState.Error -> {
                    IconButton(
                        onClick = onReintentar,
                        modifier = Modifier.defaultMinSize(
                            minWidth = Dimens.MinTouchTarget,
                            minHeight = Dimens.MinTouchTarget
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Reintentar carga de UF",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// Vista previa del banner en modo claro y oscuro
@Preview(name = "Banner UF Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Banner UF Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun BannerUfDiariaPreview() {
    APP_MOVILTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(Dimens.SpaceMD)
        ) {
            BannerUfDiaria(
                state = UfUiState.Exito(valorFormateado = "$38.450,12", fecha = "24/11/2026"),
                onReintentar = {}
            )
        }
    }
}