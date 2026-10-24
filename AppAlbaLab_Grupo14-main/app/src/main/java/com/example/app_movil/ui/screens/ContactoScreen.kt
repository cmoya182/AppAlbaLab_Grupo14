package com.example.app_movil.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.app_movil.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val tiposContacto = listOf("Empresa", "Compra Solidaria", "Voluntariado")

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel.onFotoCapturada(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        viewModel.onFotoCapturada(bitmap)
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { fueAceptado ->
        if (fueAceptado) {
            camaraLauncher.launch(null)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vinculación Institucional", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
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
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Tipo de Solicitud:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tiposContacto.forEach { tipo ->
                    FilterChip(
                        selected = viewModel.tipoContactoSeleccionado == tipo,
                        onClick = { viewModel.onTipoContactoSeleccionado(tipo) },
                        label = { Text(tipo) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val labelNombre = when (viewModel.tipoContactoSeleccionado) {
                "Empresa" -> "Razón Social o Nombre de Empresa *"
                "Compra Solidaria" -> "Nombre de Contacto Particular *"
                else -> "Nombre Completo del Postulante *"
            }

            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text(labelNombre) },
                leadingIcon = {
                    Icon(
                        imageVector = if (viewModel.tipoContactoSeleccionado == "Empresa") Icons.Default.Business else Icons.Default.Person,
                        contentDescription = null
                    )
                },
                isError = viewModel.nombreError != null,
                supportingText = {
                    if (viewModel.nombreError != null) {
                        Text(text = viewModel.nombreError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (viewModel.nombreError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo Electrónico de Contacto *") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = viewModel.correoError != null,
                supportingText = {
                    if (viewModel.correoError != null) {
                        Text(text = viewModel.correoError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (viewModel.correoError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono de Contacto (Ej: +56912345678) *") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = viewModel.telefonoError != null,
                supportingText = {
                    if (viewModel.telefonoError != null) {
                        Text(text = viewModel.telefonoError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (viewModel.telefonoError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            val labelMensaje = when (viewModel.tipoContactoSeleccionado) {
                "Empresa" -> "Detalle de propuesta corporativa o requerimiento *"
                "Compra Solidaria" -> "Productos, unidades o consultas a cotizar *"
                else -> "Disponibilidad horaria y motivación para voluntariado *"
            }

            OutlinedTextField(
                value = viewModel.mensaje,
                onValueChange = { viewModel.onMensajeChange(it) },
                label = { Text(labelMensaje) },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                isError = viewModel.mensajeError != null,
                supportingText = {
                    if (viewModel.mensajeError != null) {
                        Text(text = viewModel.mensajeError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (viewModel.mensajeError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Adjuntar Respaldo o Logotipo (Opcional):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { galeriaLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Galería")
                }

                OutlinedButton(
                    onClick = {
                        val permiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permiso == PackageManager.PERMISSION_GRANTED) {
                            camaraLauncher.launch(null)
                        } else {
                            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cámara")
                }
            }

            AnimatedVisibility(
                visible = viewModel.fotoBitmap != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        viewModel.fotoBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Archivo adjunto",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { viewModel.eliminarImagen() },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Quitar imagen")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón con texto profesional orientado al usuario
            Button(
                onClick = { viewModel.enviarFormulario() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar Solicitud de Vinculación", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Diálogo con redacción formal e institucional
    if (viewModel.mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarConfirmacion() },
            title = { Text("¡Solicitud Enviada con Éxito!") },
            text = {
                Text("Hemos recibido su solicitud de tipo '${viewModel.tipoContactoSeleccionado}'. El equipo de vinculación sociolaboral de Alba Lab revisará su propuesta y se comunicará a la brevedad.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cerrarConfirmacion()
                        viewModel.navigateBack()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}