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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

/**
 * ARCHIVO: ContactoScreen.kt
 * PROPÓSITO: Contiene los 3 formularios de vinculación de Alba Lab y la integración con los recursos nativos
 * del teléfono (Cámara física y Galería de imágenes).
 *
 * ¿CÓMO ESTÁ ORGANIZADO?:
 * Para evitar repetir código tres veces, utiliza una plantilla base privada llamada FormularioBaseScreen
 * y expone 3 pantallas públicas adaptadas a cada necesidad de CORPALIV:
 * 1. ContactoEmpresaScreen: Fija el tipo "Empresa" y habilita Cámara y Galería (permitirAdjuntos = true).
 * 2. ContactoCompraScreen: Fija el tipo "Compra Solidaria", muestra una tarjeta con candado con el producto
 *    elegido en la tienda y oculta la Cámara y Galería (permitirAdjuntos = false).
 * 3. ContactoVoluntariadoScreen: Fija el tipo "Voluntariado" y habilita Cámara y Galería (permitirAdjuntos = true).
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - MainActivity.kt: Registra las 3 pantallas en rutas independientes del NavHost.
 * - MainViewModel.kt: Valida los 4 campos de texto, guarda la foto en almacenamiento interno (filesDir)
 *   e inserta el registro completo en Room SQLite.
 * - AndroidManifest.xml: Declara el permiso <uses-permission android:name="android.permission.CAMERA" />.
 */

// --- 1. PANTALLA DE FORMULARIO: CONVENIO EMPRESA / RSE ---
@Composable
fun ContactoEmpresaScreen(viewModel: MainViewModel) {
    // Al abrirse la pantalla, limpia los campos y fija el tipo como "Empresa"
    LaunchedEffect(Unit) { viewModel.prepararFormulario("Empresa") }

    FormularioBaseScreen(
        tituloPantalla = "Convenio Empresa / RSE",
        subtitulo = "Alianzas corporativas, compras institucionales o donaciones de Responsabilidad Social.",
        labelNombre = "Razón Social o Nombre de Empresa *",
        placeholderNombre = "Ej: Sodimac S.A., Banco de Chile",
        labelMensaje = "Propuesta de Convenio o Requerimiento *",
        placeholderMensaje = "Cuéntanos qué tipo de alianza o compra corporativa desean realizar...",
        textoBotonImagen = "Adjuntar Logo o Documento de Empresa (Opcional)",
        permitirAdjuntos = true,
        tarjetaProductoFijo = null,
        viewModel = viewModel
    )
}

// --- 2. PANTALLA DE FORMULARIO: COTIZACIÓN COMPRA SOLIDARIA ---
@Composable
fun ContactoCompraScreen(viewModel: MainViewModel) {
    // Mantiene el producto que fue precargado al presionar "Solicitar" en CatalogoScreen
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
        permitirAdjuntos = false, // En compras solidarias no se solicitan fotografías
        tarjetaProductoFijo = {
            // Tarjeta informativa con ícono de candado que muestra el producto elegido en el catálogo
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
                        imageVector = Icons.Default.Lock,
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

// --- 3. PANTALLA DE FORMULARIO: POSTULACIÓN VOLUNTARIADO ---
@Composable
fun ContactoVoluntariadoScreen(viewModel: MainViewModel) {
    // Al abrirse la pantalla, limpia los campos y fija el tipo como "Voluntariado"
    LaunchedEffect(Unit) { viewModel.prepararFormulario("Voluntariado") }

    FormularioBaseScreen(
        tituloPantalla = "Postulación Voluntariado",
        subtitulo = "Súmate como voluntario apoyando a los jóvenes en los talleres sociolaborales.",
        labelNombre = "Nombre Completo del Voluntario *",
        placeholderNombre = "Ej: Sofía Valenzuela",
        labelMensaje = "Motivación y Horarios Disponibles *",
        placeholderMensaje = "Cuéntanos tu motivación para apoyar y en qué días tienes disponibilidad...",
        textoBotonImagen = "Adjuntar Foto o Identificación (Opcional)",
        permitirAdjuntos = true,
        tarjetaProductoFijo = null,
        viewModel = viewModel
    )
}

/**
 * Plantilla base reutilizable que construye los campos validados y gestiona los permisos y contratos
 * nativos de Cámara y Galería (Indicadores IE 2.1.2, IE 2.2.1 e IE 2.4.1).
 */
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

    // 1. Verificación inicial de permiso de Cámara en el sistema operativo Android
    var tienePermisoCamara by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 2. Lanzador nativo para abrir el selector de Galería filtrando por imágenes ("image/*")
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> viewModel.onImagenSeleccionada(uri) }

    // 3. Lanzador nativo para abrir la aplicación de Cámara y recibir la foto como un Bitmap
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> viewModel.onFotoCapturada(bitmap) }

    // 4. Lanzador del cuadro de diálogo oficial de Android para pedir permiso de Cámara en tiempo real
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        tienePermisoCamara = isGranted
        if (isGranted) launcherCamara.launch(null)
    }

    // Convierte la URI seleccionada de la galería en un Bitmap para previsualizarla en pantalla
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

    // Ventana emergente (AlertDialog) que confirma cuando la solicitud se guardó con éxito en SQLite
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
            // Si estamos en Compra Solidaria, dibuja arriba la tarjeta con candado del producto elegido
            tarjetaProductoFijo?.invoke()

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

            // --- CAMPO 1: NOMBRE O RAZÓN SOCIAL ---
            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text(labelNombre) },
                placeholder = { Text(placeholderNombre) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.nombreError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error en nombre", tint = MaterialTheme.colorScheme.error)
                    }
                },
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
                trailingIcon = {
                    if (viewModel.correoError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error en correo", tint = MaterialTheme.colorScheme.error)
                    }
                },
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
                trailingIcon = {
                    if (viewModel.telefonoError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error en teléfono", tint = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = viewModel.telefonoError != null,
                supportingText = { viewModel.telefonoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- CAMPO 4: MENSAJE O PROPUESTA ---
            OutlinedTextField(
                value = viewModel.mensaje,
                onValueChange = { viewModel.onMensajeChange(it) },
                label = { Text(labelMensaje) },
                placeholder = { Text(placeholderMensaje) },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.mensajeError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error en mensaje", tint = MaterialTheme.colorScheme.error)
                    }
                },
                minLines = 3,
                isError = viewModel.mensajeError != null,
                supportingText = { viewModel.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            // --- SECCIÓN DE RECURSOS NATIVOS: CÁMARA Y GALERÍA ---
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
                    // Botón Cámara: verifica si tiene permiso; si no lo tiene, lo solicita antes de abrir la cámara
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

                    // Botón Galería: abre el selector nativo de archivos de imagen del teléfono
                    OutlinedButton(
                        onClick = { launcherGaleria.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Galería")
                    }
                }

                // Muestra la miniatura de la foto con animación de aparición suave (AnimatedVisibility)
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

            // Botón de envío: ejecuta enviarFormulario() en MainViewModel para validar y guardar en SQLite
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
