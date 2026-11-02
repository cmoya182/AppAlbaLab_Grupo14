package com.example.app_movil.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_movil.model.Producto

/**
 * ARCHIVO: ProductoItem.kt
 * PROPÓSITO: Dibuja la tarjeta individual e interactiva de cada producto dentro de la Tienda Solidaria.
 *
 * ¿CÓMO FUNCIONA?:
 * 1. Mantiene una variable de estado local llamada 'expandido' (true o false).
 * 2. Al tocar la tarjeta o la flecha derecha, 'expandido' cambia de valor y el modificador animateContentSize()
 *    anima suavemente el alto de la tarjeta con un efecto de resorte (Spring).
 * 3. Cuando está expandida, muestra la descripción del producto y el botón "Solicitar o Consultar por este Producto".
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - Producto.kt: Recibe los datos del producto (nombre, precio, descripción).
 * - CatalogoScreen.kt: Invoca este componente por cada producto filtrado y le pasa la acción onCotizarClick.
 */
@Composable
fun ProductoItem(
    producto: Producto,
    onCotizarClick: () -> Unit = {}
) {
    // Estado local que recuerda si la tarjeta está desplegada o contraída
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            // Animación funcional (IE 2.2.2): suaviza el cambio de tamaño al mostrar/ocultar los detalles
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable { expandido = !expandido },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Icono de producto",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Aporte: $${producto.precio}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                IconButton(onClick = { expandido = !expandido }) {
                    Icon(
                        imageVector = if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expandido) "Ver menos" else "Ver más"
                    )
                }
            }

            // Bloque condicional: solo se dibuja en pantalla cuando el usuario expande la tarjeta
            if (expandido) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Elaborado artesanalmente en los talleres laborales de CORPALIV por jóvenes con sordoceguera y retos múltiples.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onCotizarClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Solicitar o Consultar por este Producto")
                }
            }
        }
    }
}
