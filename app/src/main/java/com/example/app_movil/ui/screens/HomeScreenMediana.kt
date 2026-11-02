package com.example.app_movil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
 * ARCHIVO: HomeScreenMediana.kt
 * PROPÓSITO: Variante responsiva de la pantalla de inicio para dispositivos de ancho mediano
 * (WindowWidthSizeClass.Medium, como tablets compactas o teléfonos plegables).
 *
 * ¿CÓMO FUNCIONA?:
 * Distribuye las opciones de navegación aprovechando el ancho intermedio, manteniendo acceso
 * directo a los 3 canales: Tienda Solidaria, Convenio Empresa / RSE y Postulación a Voluntariado.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - HomeScreen.kt: La invoca automáticamente cuando el ancho de ventana es Medium.
 */
@Composable
fun HomeScreenMediana(
    onCatalogoClick: () -> Unit = {},
    onEmpresaClick: () -> Unit = {},
    onVoluntariadoClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo CORPALIV Alba Lab",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(16.dp)
            )
        }
        Text(
            text = "Red Sociolaboral Alba Lab - CORPALIV",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = "Promovemos la autonomía, formación sociolaboral e inclusión efectiva de jóvenes y adultos con discapacidad múltiple y sordoceguera.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Fila superior con Tienda Solidaria y Convenio Empresa / RSE
        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCatalogoClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Explorar Tienda")
            }
            OutlinedButton(
                onClick = onEmpresaClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Business, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Convenio Empresa")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tercer botón de acceso directo a Postulación a Voluntariado
        OutlinedButton(
            onClick = onVoluntariadoClick,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Postular a Voluntariado")
        }
    }
}
