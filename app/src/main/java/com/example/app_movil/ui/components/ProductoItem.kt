package com.example.app_movil.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_movil.model.Producto
import com.example.app_movil.ui.theme.APP_MOVILTheme
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape

@Composable
fun ProductoItem(
    producto: Producto,
    onCardClick: () -> Unit = {},
    onCotizarClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationLow),
        border = BorderStroke(
            width = Dimens.BorderThin,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
    ) {
        Column {
            ProductoImagenHeader(
                producto = producto,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SpaceSM)
            ) {
                Surface(
                    shape = PillShape,
                    color = categoriaContainerColor(producto.categoria)
                ) {
                    Text(
                        text = producto.categoria,
                        style = MaterialTheme.typography.labelSmall,
                        color = categoriaContentColor(producto.categoria),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(
                            horizontal = Dimens.SpaceXS,
                            vertical = Dimens.SpaceXXS
                        )
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceXS))

                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceXXS))

                Text(
                    text = "Aporte: $${producto.precio}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                Button(
                    onClick = onCotizarClick,
                    shape = PillShape,
                    contentPadding = PaddingValues(horizontal = Dimens.SpaceSM, vertical = Dimens.SpaceXS),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconSM)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpaceXXS))
                    Text(
                        text = "Cotizar",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoImagenHeader(
    producto: Producto,
    modifier: Modifier = Modifier
) {
    if (producto.imagenRes != null) {
        Image(
            painter = painterResource(id = producto.imagenRes),
            contentDescription = producto.nombre,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        val gradiente = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.tertiaryContainer
            )
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.background(gradiente)
        ) {
            Surface(
                shape = PillShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = Dimens.ElevationLow,
                modifier = Modifier.size(Dimens.IconXL)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = categoriaIcono(producto.categoria),
                        contentDescription = producto.nombre,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconLG)
                    )
                }
            }
        }
    }
}

@Composable
private fun categoriaContainerColor(categoria: String) = when (categoria) {
    "Textil" -> MaterialTheme.colorScheme.primaryContainer
    "Aromas" -> MaterialTheme.colorScheme.secondaryContainer
    "Cerámica" -> MaterialTheme.colorScheme.tertiaryContainer
    else -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun categoriaContentColor(categoria: String) = when (categoria) {
    "Textil" -> MaterialTheme.colorScheme.onPrimaryContainer
    "Aromas" -> MaterialTheme.colorScheme.onSecondaryContainer
    "Cerámica" -> MaterialTheme.colorScheme.onTertiaryContainer
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

private fun categoriaIcono(categoria: String): ImageVector = when (categoria) {
    "Textil" -> Icons.Outlined.Checkroom
    "Aromas" -> Icons.Outlined.Spa
    "Cerámica" -> Icons.Outlined.Coffee
    else -> Icons.Outlined.ShoppingBag
}

@Preview(name = "Producto Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Producto Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ProductoItemPreview() {
    APP_MOVILTheme {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(Dimens.SpaceMD)) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSM)) {
                ProductoItem(
                    producto = Producto(1, "Bolsa Alba Lab", "Bolsa ecológica de tela", 3500, "Textil"),
                    modifier = Modifier.weight(1f)
                )
                ProductoItem(
                    producto = Producto(2, "Difusor 150 ml", "Aromatizador artesanal", 6500, "Aromas"),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}