package com.example.app_movil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_movil.R

@Composable
fun HomeScreenExpandida(onButtonClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo App",
            modifier = Modifier.fillMaxWidth(0.4f)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "¡Bienvenido a Alba Lab! (Expandida)",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onButtonClick) {
                Text(text = "Ir a Configuración")
            }
        }
    }
}

@Preview("Expandida", widthDp = 1000, heightDp = 800, showBackground = true)
@Composable
fun PreviewExpandida() {
    HomeScreenExpandida()
}