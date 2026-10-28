package com.example.app_movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_movil.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoScreen(viewModel: MainViewModel) {
    val tiposContacto = listOf("Empresa", "Compra Solidaria", "Voluntariado")

    if (viewModel.mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarConfirmacion() },
            title = { Text("¡Solicitud Enviada!") },
            text = { Text("Gracias por contactar a Alba Lab. Revisaremos tu solicitud y te contactaremos pronto.") },
            confirmButton = {
                Button(onClick = { viewModel.cerrarConfirmacion() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vinculación Alba Lab") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Formulario de Contacto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Tipo de vinculación:", fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tiposContacto.forEach { tipo ->
                    FilterChip(
                        // Aquí están las variables corregidas que daban error
                        selected = viewModel.tipoContacto == tipo,
                        onClick = { viewModel.onTipoContactoChange(tipo) },
                        label = { Text(tipo) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo o Empresa") },
                isError = viewModel.nombreError != null,
                supportingText = { viewModel.nombreError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo Electrónico") },
                isError = viewModel.correoError != null,
                supportingText = { viewModel.correoError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono de Contacto") },
                isError = viewModel.telefonoError != null,
                supportingText = { viewModel.telefonoError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.mensaje,
                onValueChange = { viewModel.onMensajeChange(it) },
                label = { Text("Propuesta o Mensaje") },
                minLines = 3,
                isError = viewModel.mensajeError != null,
                supportingText = { viewModel.mensajeError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.enviarFormulario() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Solicitud")
            }
        }
    }
}