package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

// Diseño adaptado para pantallas medianas combinando 2 columnas superiores y tarjeta inferior
@Composable
fun HomeScreenMediana(
    onCatalogoClick: () -> Unit = {},
    onEmpresaClick: () -> Unit = {},
    onVoluntariadoClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.SpaceLG, vertical = Dimens.SpaceMD),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TarjetaMisionInstitucional(modifier = Modifier.fillMaxWidth(0.88f))

        Spacer(modifier = Modifier.height(Dimens.SpaceLG))

        Row(
            modifier = Modifier.fillMaxWidth(0.88f),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSM)
        ) {
            TarjetaAccionInteractiva(
                titulo = "Tienda Solidaria",
                subtitulo = "Productos de talleres",
                icono = Icons.Outlined.ShoppingBag,
                colorIconoFondo = MaterialTheme.colorScheme.primaryContainer,
                colorIcono = MaterialTheme.colorScheme.primary,
                onClick = onCatalogoClick,
                modifier = Modifier.weight(1f)
            )
            TarjetaAccionInteractiva(
                titulo = "Convenio RSE",
                subtitulo = "Alianzas empresa",
                icono = Icons.Outlined.Business,
                colorIconoFondo = MaterialTheme.colorScheme.secondaryContainer,
                colorIcono = MaterialTheme.colorScheme.secondary,
                onClick = onEmpresaClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceSM))

        TarjetaAccionInteractiva(
            titulo = "Postular a Voluntariado",
            subtitulo = "Súmate y apoya a los jóvenes en talleres",
            icono = Icons.Outlined.FavoriteBorder,
            colorIconoFondo = MaterialTheme.colorScheme.tertiaryContainer,
            colorIcono = MaterialTheme.colorScheme.tertiary,
            onClick = onVoluntariadoClick,
            modifier = Modifier.fillMaxWidth(0.88f)
        )
    }
}