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
 * ARCHIVO: HomeScreenExpandida.kt
 * PROPÓSITO: Variante responsiva de la pantalla de inicio para pantallas grandes o en orientación horizontal
 * (WindowWidthSizeClass.Expanded, como tablets de 10 pulgadas o modo landscape).
 *
 * ¿CÓMO FUNCIONA?:
 * Divide la pantalla en dos columnas paralelas dentro de un Row: a la izquierda muestra el logo e identidad
 * de Alba Lab, y a la derecha presenta la misión institucional junto con los 3 botones de acción
 * (Tienda Solidaria, Convenio Empresa / RSE y Postular a Voluntariado).
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - HomeScreen.kt: La invoca automáticamente cuando el ancho de ventana es Expanded.
 */
@Composable
fun HomeScreenExpandida(
    onCatalogoClick: () -> Unit = {},
    onEmpresaClick: () -> Unit = {},
    onVoluntariadoClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo CORPALIV Alba Lab",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(24.dp)
                )
            }
            Text(
                text = "Red Sociolaboral Alba Lab",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier
                .weight(1.2f)
                .padding(start = 24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Propósito Institucional CORPALIV",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Generamos oportunidades reales de inclusión social y laboral para personas con discapacidad múltiple y sordoceguera a través de la producción artesanal y alianzas de Responsabilidad Social Empresarial (RSE).",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onCatalogoClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Explorar Catálogo de Productos Solidarios", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onEmpresaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Business, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Convenio Empresa / RSE", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onVoluntariadoClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Postular a Voluntariado", fontWeight = FontWeight.Bold)
            }
        }
    }
}
