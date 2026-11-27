package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.app_movil.ui.theme.Dimens

// Diseño horizontal de 2 columnas paralelas con desplazamiento independiente
@Composable
fun HomeScreenExpandida(
    onCatalogoClick: () -> Unit = {},
    onEmpresaClick: () -> Unit = {},
    onVoluntariadoClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.SpaceLG, vertical = Dimens.SpaceXS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceLG)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TarjetaMisionInstitucional()
        }

        Column(
            modifier = Modifier
                .weight(1.25f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            TarjetaAccionInteractiva(
                titulo = "Explorar Tienda Solidaria",
                subtitulo = "Catálogo de productos hechos en talleres",
                icono = Icons.Outlined.ShoppingBag,
                colorIconoFondo = MaterialTheme.colorScheme.primaryContainer,
                colorIcono = MaterialTheme.colorScheme.primary,
                onClick = onCatalogoClick
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSM))

            TarjetaAccionInteractiva(
                titulo = "Convenio Empresa / RSE",
                subtitulo = "Alianzas corporativas y compras por volumen",
                icono = Icons.Outlined.Business,
                colorIconoFondo = MaterialTheme.colorScheme.secondaryContainer,
                colorIcono = MaterialTheme.colorScheme.secondary,
                onClick = onEmpresaClick
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSM))

            TarjetaAccionInteractiva(
                titulo = "Postular a Voluntariado",
                subtitulo = "Súmate y apoya a los jóvenes en talleres",
                icono = Icons.Outlined.FavoriteBorder,
                colorIconoFondo = MaterialTheme.colorScheme.tertiaryContainer,
                colorIcono = MaterialTheme.colorScheme.tertiary,
                onClick = onVoluntariadoClick
            )
        }
    }
}