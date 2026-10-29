package com.example.app_movil.ui.screens

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// =========================================================================================
// TORPEDO PARA LA DEFENSA - FORMULARIOS SEGMENTADOS E INDEPENDIENTES
// =========================================================================================
// Archivo: ContactoScreen.kt
// Propósito: Contiene las 3 pantallas de contacto de la aplicación, totalmente desacopladas.
// Al entrar a cada una, el tipo de formulario queda fijo y el usuario NO lo puede cambiar.
// - ContactoEmpresaScreen: Convenios RSE y compras por volumen (permite logo/documento nativo).
// - ContactoCompraScreen: Cotización directa de tienda (producto fijo bloqueado, SIN fotos).
// - ContactoVoluntariadoScreen: Postulación de personas para apoyar talleres (permite foto nativa).
// Cumple Rúbrica: IE 2.2.1 (Uso de recursos nativos del dispositivo: Cámara y Galería).
// =========================================================================================

// --- 1. FORMULARIO CONVENIO EMPRESA / RSE ---
@Composable
fun ContactoEmpresaScreen(viewModel: MainViewModel) {
    // Al entrar, fijamos el tipo como "Empresa" en el ViewModel
    LaunchedEffect(Unit) { viewModel.prepararFormulario("Empresa") }

    FormularioBaseScreen(
        tituloPantalla = "Convenio Empresa / RSE",
        subtitulo = "Alianzas corporativas, compras institucionales o donaciones de Responsabilidad Social.",
        labelNombre = "Razón Social o Nombre de Empresa *",
        placeholderNombre = "Ej: Sodimac S.A., Banco de Chile",
        labelMensaje = "Propuesta de Convenio o Requerimiento *",
        placeholderMensaje = "Cuéntanos qué tipo de alianza o compra corporativa desean realizar...",
        textoBotonImagen = "Adjuntar Logo o Documento de Empresa (Opcional)",
        permitirAdjuntos = true, // Permite usar Cámara y Galería
        tarjetaProductoFijo = null,
        viewModel = viewModel
    )
}

// --- 2. FORMULARIO COTIZACIÓN COMPRA SOLIDARIA ---
// Cumple la regla: Producto seleccionado BLOQUEADO y SIN cámara/galería (solo datos de contacto)
@Composable
fun ContactoCompraScreen(viewModel: MainViewModel) {
    // El tipo queda fijo como "Compra Solidaria"
    LaunchedEffect(Unit) { viewModel.prepararFormulario("Compra Solidaria", viewModel.mensaje) }

    val detalleProducto = if (viewModel.mensaje.isNotBlank()) viewModel.mensaje else "Producto seleccionado del Catálogo Alba Lab"

    FormularioBaseScreen(
        tituloPantalla = "Cotización Compra Solidaria",
        subtitulo = "Déjanos tus datos de contacto para que el equipo de CORPALIV coordine stock, entrega y aporte.",
        labelNombre = "Nombre Completo de Contacto *",
        placeholderNombre = "Ej: Carlos Mendoza",
        labelMensaje = "Cantidad Deseada o Notas de Entrega *",
        placeholderMensaje = "Ej: Necesito 5 unidades para entrega en Santiago Centro...",
        textoBotonImagen = "",
        permitirAdjuntos = false, // REGLA: SIN cámara ni galería para compras
        tarjetaProductoFijo = {
            // TARJETA DE PRODUCTO FIJO BLOQUEADO: No se puede cambiar ni editar
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock, // Candado visual que indica producto fijado
                        contentDescription = "Producto bloqueado",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PRODUCTO SELECCIONADO (FIJO)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = detalleProducto,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Fijado desde la tienda. CORPALIV te contactará para coordinar.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        },
        viewModel = viewModel
    )
}

// --- 3. FORMULARIO POSTULACIÓN VOLUNTARIADO ---
@Composable
fun ContactoVoluntariadoScreen(viewModel: MainViewModel) {
    // El tipo queda fijo como "Voluntariado"
    LaunchedEffect(Unit) { viewModel.prepararFormulario("Voluntariado") }

    FormularioBaseScreen(
        tituloPantalla = "Postulación Voluntariado",
        subtitulo = "Súmate como voluntario apoyando a los jóvenes en los talleres sociolaborales.",
        labelNombre = "Nombre Completo del Voluntario *",
        placeholderNombre = "Ej: Sofía Valenzuela",
        labelMensaje = "Motivación y Horarios Disponibles *",
        placeholderMensaje = "Cuéntanos tu motivación para apoyar y en qué días tienes disponibilidad...",
        textoBotonImagen = "Adjuntar Foto o Identificación (Opcional)",
        permitirAdjuntos = true, // Permite Cámara y Galería
        tarjetaProductoFijo = null,
        viewModel = viewModel
    )
}

// =========================================================================================
// PLANTILLA BASE REUTILIZABLE PARA TODOS LOS FORMULARIOS
// =========================================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioBaseScreen(
    tituloPantalla: String,
    subtitulo: String,
    labelNombre: String,
    placeholderNombre: String,
    labelMensaje: String,
    placeholderMensaje: String,
    textoBotonImagen: String,
    permitirAdjuntos: Boolean,
    tarjetaProductoFijo: (@Composable () -> Unit)? = null,
    viewModel: MainViewModel
) {
    val context = LocalContext.current

    // Permisos y recursos nativos de Cámara y Galería
    var tienePermisoCamara by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> viewModel.onImagenSeleccionada(uri) }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> viewModel.onFotoCapturada(bitmap) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        tienePermisoCamara = isGranted
        if (isGranted) launcherCamara.launch(null)
    }

    // Decodificación nativa de la imagen de galería sin librerías externas
    val bitmapGaleria = remember(viewModel.imagenUri) {
        viewModel.imagenUri?.let { uri ->
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    val imagenAMostrar = viewModel.fotoBitmap ?: bitmapGaleria

    // Ventana emergente al enviar exitosamente la solicitud a la base de datos Room
    if (viewModel.mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarConfirmacion() },
            title = { Text("¡Solicitud Enviada con Éxito!") },
            text = { Text("Tu solicitud de ${viewModel.tipoContacto} fue guardada en el sistema. El equipo de CORPALIV se pondrá en contacto contigo.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.cerrarConfirmacion()
                    viewModel.navigateBack()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloPantalla, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Muestra la tarjeta del producto fijado si estamos en el formulario de compra
            tarjetaProductoFijo?.invoke()

            // Cuadro explicativo del formulario
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = subtitulo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMPO 1: NOMBRE COMPLETO ---
            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text(labelNombre) },
                placeholder = { Text(placeholderNombre) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = viewModel.nombreError != null,
                supportingText = { viewModel.nombreError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- CAMPO 2: CORREO ELECTRÓNICO ---
            OutlinedTextField(
                value = viewModel.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo Electrónico de Contacto *") },
                placeholder = { Text("ejemplo@correo.cl") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = viewModel.correoError != null,
                supportingText = { viewModel.correoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- CAMPO 3: TELÉFONO DE CONTACTO ---
            OutlinedTextField(
                value = viewModel.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono de Contacto (Celular) *") },
                placeholder = { Text("+56 9 1234 5678") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = viewModel.telefonoError != null,
                supportingText = { viewModel.telefonoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- CAMPO 4: MENSAJE O DETALLES ---
            OutlinedTextField(
                value = viewModel.mensaje,
                onValueChange = { viewModel.onMensajeChange(it) },
                label = { Text(labelMensaje) },
                placeholder = { Text(placeholderMensaje) },
                minLines = 3,
                isError = viewModel.mensajeError != null,
                supportingText = { viewModel.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            // --- SECCIÓN NATIVA DE CÁMARA Y GALERÍA (SOLO SI SE PERMITE) ---
            if (permitirAdjuntos) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = textoBotonImagen,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (tienePermisoCamara) launcherCamara.launch(null)
                            else requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cámara")
                    }

                    OutlinedButton(
                        onClick = { launcherGaleria.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Galería")
                    }
                }

                AnimatedVisibility(
                    visible = imagenAMostrar != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        imagenAMostrar?.let { bmp ->
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Foto adjunta",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }

                        TextButton(onClick = { viewModel.eliminarImagen() }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Quitar imagen", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- BOTÓN PRINCIPAL DE ENVÍO HACIA LA BASE DE DATOS ROOM ---
            Button(
                onClick = { viewModel.enviarFormulario() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar Solicitud a CORPALIV", fontWeight = FontWeight.Bold)
            }
        }
    }
}