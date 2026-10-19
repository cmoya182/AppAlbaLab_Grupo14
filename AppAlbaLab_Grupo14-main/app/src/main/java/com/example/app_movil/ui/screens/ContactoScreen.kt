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
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
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

// Dibuja la pantalla del formulario de vinculación con validaciones, recursos nativos y animación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Recurso Nativo 1: Abre la galería y convierte la imagen seleccionada a Bitmap de forma nativa
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

    // Recurso Nativo 2: Abre la cámara para capturar una fotografía en vivo
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        viewModel.onFotoCapturada(bitmap)
    }

    // Solicita el permiso de cámara al usuario si aún no está otorgado
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
                            contentDescription = "Volver a la pantalla anterior"
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "Complete este formulario para solicitar convenios de inclusión laboral, donaciones o compras corporativas con Alba Lab.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo 1: Nombre o Razón Social
            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre o Empresa *") },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                isError = viewModel.nombreError != null,
                supportingText = {
                    if (viewModel.nombreError != null) {
                        Text(text = viewModel.nombreError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (viewModel.nombreError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Dato erróneo", tint = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo 2: Correo electrónico
            OutlinedTextField(
                value = viewModel.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo Electrónico *") },
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
                        Icon(Icons.Default.Error, contentDescription = "Dato erróneo", tint = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo 3: Teléfono de contacto
            OutlinedTextField(
                value = viewModel.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono de Contacto *") },
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
                        Icon(Icons.Default.Error, contentDescription = "Dato erróneo", tint = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo 4: Mensaje o propuesta
            OutlinedTextField(
                value = viewModel.mensaje,
                onValueChange = { viewModel.onMensajeChange(it) },
                label = { Text("Propuesta o Consulta *") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null) },
                isError = viewModel.mensajeError != null,
                supportingText = {
                    if (viewModel.mensajeError != null) {
                        Text(text = viewModel.mensajeError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (viewModel.mensajeError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Dato erróneo", tint = MaterialTheme.colorScheme.error)
                    }
                },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SECCIÓN DE RECURSOS NATIVOS: Galería y Cámara
            Text(
                text = "Adjuntar Respaldo o Logo (Opcional):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Selector de la galería
                OutlinedButton(
                    onClick = { galeriaLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Galería")
                }

                // Disparador de la cámara
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

            // ANIMACIÓN FUNCIONAL: Muestra u oculta la foto de forma suave al adjuntarla o quitarla
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
                                contentDescription = "Foto o logo adjunto",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón para descartar la foto
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

            // Botón de envío
            Button(
                onClick = { viewModel.enviarFormulario() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar Solicitud de Vinculación")
            }
        }
    }

    // Diálogo de confirmación
    if (viewModel.mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarConfirmacion() },
            title = { Text("¡Solicitud Recibida!") },
            text = {
                Text("Gracias por contactar a CORPALIV. El equipo de vinculación de Alba Lab revisará su propuesta y se comunicará a la brevedad.")
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