package com.example.app_movil.ui.screens

import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_movil.R // Asegúrate de que esto coincide con tu paquete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenMediana() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Alba Lab Mediana") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "¡Bienvenido a Alba Lab!",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = { /* acción futura */ }) {
                Text(text = "Presióname")
            }
            Image(
                painter = painterResource(id = R.drawable.logo), // Asegúrate de que tu imagen se llame logo
                contentDescription = "Logo App",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview("Mediana", widthDp = 700, heightDp = 1000)
@Composable
fun PreviewMediana(){
    HomeScreenMediana()
}