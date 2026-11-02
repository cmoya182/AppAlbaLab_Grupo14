package com.example.app_movil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_movil.R

/**
 * ARCHIVO: HomeScreenExpandida.kt
 * PROPÓSITO: Variante responsiva de la pantalla de inicio para pantallas grandes o en orientación horizontal
 * (WindowWidthSizeClass.Expanded, como tablets o teléfonos en modo horizontal/landscape).
 *
 * ¿CÓMO FUNCIONA?:
 * Divide la pantalla en dos columnas paralelas: a la izquierda el logo y nombre institucional,
 * y a la derecha el propósito institucional junto a las mismas 3 tarjetas interactivas de acción
 * que se usan en la pantalla vertical (con su ícono circular de color, título, subtítulo y flecha).
 * Ambas columnas cuentan con scroll vertical independiente para adaptarse a cualquier altura de pantalla.
 */
@Composable
fun HomeScreenExpandida(
    onCatalogoClick: () -> Unit = {},
    onEmpresaClick: () -> Unit = {},
    onVoluntariadoClick: () -> Unit = {}
) {
    val scrollStateIzquierda = rememberScrollState()
    val scrollStateDerecha = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Columna Izquierda: Logo y nombre de la Red Sociolaboral (con scroll)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = 12.dp)
                .verticalScroll(scrollStateIzquierda),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo CORPALIV Alba Lab",
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .padding(14.dp)
                )
            }
            Text(
                text = "Red Sociolaboral Alba Lab",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // Columna Derecha: Propósito institucional y las 3 tarjetas idénticas a la vista vertical
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
                .padding(start = 12.dp)
                .verticalScroll(scrollStateDerecha),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Propósito Institucional CORPALIV",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Generamos oportunidades reales de inclusión social y laboral para personas con discapacidad múltiple y sordoceguera a través de la producción artesanal y alianzas de Responsabilidad Social Empresarial (RSE).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- TARJETA 1: TIENDA SOLIDARIA (Ícono circular morado institucional) ---
            TarjetaAccionInteractiva(
                titulo = "Explorar Tienda Solidaria",
                subtitulo = "Catálogo de productos hechos en talleres",
                icono = Icons.Default.ShoppingBag,
                colorContenedor = MaterialTheme.colorScheme.surface,
                colorIconoFondo = MaterialTheme.colorScheme.primary,
                colorIcono = MaterialTheme.colorScheme.onPrimary,
                colorTexto = MaterialTheme.colorScheme.onSurface,
                alpha = 1f,
                offsetY = 0f,
                onClick = onCatalogoClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            // --- TARJETA 2: CONVENIO EMPRESA / RSE (Ícono circular rosa institucional) ---
            TarjetaAccionInteractiva(
                titulo = "Convenio Empresa / RSE",
                subtitulo = "Alianzas corporativas y compras por volumen",
                icono = Icons.Default.Business,
                colorContenedor = MaterialTheme.colorScheme.surface,
                colorIconoFondo = MaterialTheme.colorScheme.secondary,
                colorIcono = MaterialTheme.colorScheme.onSecondary,
                colorTexto = MaterialTheme.colorScheme.onSurface,
                alpha = 1f,
                offsetY = 0f,
                onClick = onEmpresaClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            // --- TARJETA 3: POSTULAR A VOLUNTARIADO (Ícono circular verde institucional) ---
            TarjetaAccionInteractiva(
                titulo = "Postular a Voluntariado",
                subtitulo = "Súmate y apoya a los jóvenes en talleres",
                icono = Icons.Default.Favorite,
                colorContenedor = MaterialTheme.colorScheme.surface,
                colorIconoFondo = MaterialTheme.colorScheme.tertiary,
                colorIcono = MaterialTheme.colorScheme.onTertiary,
                colorTexto = MaterialTheme.colorScheme.onSurface,
                alpha = 1f,
                offsetY = 0f,
                onClick = onVoluntariadoClick
            )
        }
    }
}
