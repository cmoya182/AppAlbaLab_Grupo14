package com.example.app_movil.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.app_movil.ui.components.AlbaTopBar
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.viewmodels.SolicitudesViewModel

// Formulario 1: Convenio Empresa / RSE (conserva datos escritos al rotar)
@Composable
fun ContactoEmpresaScreen(
    mainViewModel: MainViewModel,
    solicitudesViewModel: SolicitudesViewModel
) {
    LaunchedEffect(Unit) {
        if (solicitudesViewModel.tipoContacto != "Empresa") {
            solicitudesViewModel.prepararFormulario("Empresa")
        }
    }

    FormularioBaseScreen(
        tituloPantalla = "Convenio Empresa / RSE",
        subtitulo = "Alianzas corporativas, compras institucionales o donaciones de Responsabilidad Social.",
        labelNombre = "Razón social o nombre de empresa *",
        placeholderNombre = "Ej: Sodimac S.A., Banco de Chile",
        labelMensaje = "Propuesta de convenio o requerimiento *",
        placeholderMensaje = "Cuéntanos qué tipo de alianza o compra corporativa desean realizar...",
        textoBotonImagen = "Adjuntar logo o documento de empresa (opcional)",
        permitirAdjuntos = true,
        tarjetaProductoFijo = null,
        mainViewModel = mainViewModel,
        solicitudesViewModel = solicitudesViewModel
    )
}

// Formulario 2: Cotización de Compra Solidaria con tarjeta fija de producto
@Composable
fun ContactoCompraScreen(
    mainViewModel: MainViewModel,
    solicitudesViewModel: SolicitudesViewModel
) {
    LaunchedEffect(Unit) {
        if (solicitudesViewModel.tipoContacto != "Compra Solidaria") {
            solicitudesViewModel.prepararFormulario("Compra Solidaria", solicitudesViewModel.mensaje)
        }
    }

    val detalleProducto = if (solicitudesViewModel.mensaje.isNotBlank()) {
        solicitudesViewModel.mensaje
    } else {
        "Producto seleccionado del Catálogo Alba Lab"
    }

    FormularioBaseScreen(
        tituloPantalla = "Cotización Solidaria",
        subtitulo = "Déjanos tus datos para coordinar stock, entrega y aporte solidario.",
        labelNombre = "Nombre completo de contacto *",
        placeholderNombre = "Ej: Carlos Mendoza",
        labelMensaje = "Cantidad deseada o notas de entrega *",
        placeholderMensaje = "Ej: Necesito 5 unidades para entrega en Santiago Centro...",
        textoBotonImagen = "",
        permitirAdjuntos = false,
        tarjetaProductoFijo = {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(
                    width = Dimens.BorderThin,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.SpaceSM)
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.SpaceMD),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Producto seleccionado",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconLG)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpaceSM))
                    Column {
                        Text(
                            text = "PRODUCTO SELECCIONADO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = detalleProducto,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Fijado desde la tienda. CORPALIV te contactará para coordinar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        mainViewModel = mainViewModel,
        solicitudesViewModel = solicitudesViewModel
    )
}

// Formulario 3: Postulación a Voluntariado
@Composable
fun ContactoVoluntariadoScreen(
    mainViewModel: MainViewModel,
    solicitudesViewModel: SolicitudesViewModel
) {
    LaunchedEffect(Unit) {
        if (solicitudesViewModel.tipoContacto != "Voluntariado") {
            solicitudesViewModel.prepararFormulario("Voluntariado")
        }
    }

    FormularioBaseScreen(
        tituloPantalla = "Postulación Voluntariado",
        subtitulo = "Súmate como voluntario apoyando a los jóvenes en los talleres sociolaborales.",
        labelNombre = "Nombre completo del voluntario *",
        placeholderNombre = "Ej: Sofía Valenzuela",
        labelMensaje = "Motivación y horarios disponibles *",
        placeholderMensaje = "Cuéntanos tu motivación para apoyar y en qué días tienes disponibilidad...",
        textoBotonImagen = "Adjuntar foto o identificación (opcional)",
        permitirAdjuntos = true,
        tarjetaProductoFijo = null,
        mainViewModel = mainViewModel,
        solicitudesViewModel = solicitudesViewModel
    )
}

// Plantilla común para los 3 formularios y contratos nativos de Cámara y Galería
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
    mainViewModel: MainViewModel,
    solicitudesViewModel: SolicitudesViewModel
) {
    val context = LocalContext.current

    // Conserva el estado del permiso de cámara ante rotaciones de pantalla
    var tienePermisoCamara by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Lanzadores nativos de Android para Galería, Cámara y solicitud de permisos
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> solicitudesViewModel.onImagenSeleccionada(uri) }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> solicitudesViewModel.onFotoCapturada(bitmap) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        tienePermisoCamara = isGranted
        if (isGranted) launcherCamara.launch(null)
    }

    // Decodifica la imagen seleccionada desde galería para previsualización
    val bitmapGaleria = remember(solicitudesViewModel.imagenUri) {
        solicitudesViewModel.imagenUri?.let { uri ->
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    val imagenAMostrar = solicitudesViewModel.fotoBitmap ?: bitmapGaleria

    // Diálogo de confirmación al guardar la solicitud
    if (solicitudesViewModel.mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { solicitudesViewModel.cerrarConfirmacion() },
            title = { Text("Solicitud enviada") },
            text = {
                Text("Tu solicitud de ${solicitudesViewModel.tipoContacto} fue guardada. El equipo de CORPALIV se pondrá en contacto contigo.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        solicitudesViewModel.cerrarConfirmacion()
                        mainViewModel.navigateBack()
                    },
                    shape = PillShape
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AlbaTopBar(
                title = tituloPantalla,
                onBackClick = { mainViewModel.navigateBack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.SpaceMD, vertical = Dimens.SpaceXS),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            tarjetaProductoFijo?.invoke()

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                ),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.SpaceMD),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpaceSM))
                    Text(
                        text = subtitulo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceMD))

            // Contenedor con los 4 campos validados y recursos nativos de imagen
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    width = Dimens.BorderThin,
                    color = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Dimens.SpaceMD)) {
                    OutlinedTextField(
                        value = solicitudesViewModel.nombre,
                        onValueChange = { solicitudesViewModel.onNombreChange(it) },
                        label = { Text(labelNombre) },
                        placeholder = { Text(placeholderNombre) },
                        leadingIcon = { Icon(Icons.Outlined.PersonOutline, contentDescription = null) },
                        trailingIcon = {
                            if (solicitudesViewModel.nombreError != null) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = "Error en nombre",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        isError = solicitudesViewModel.nombreError != null,
                        supportingText = solicitudesViewModel.nombreError?.let { errorMsg ->
                            { Text(errorMsg, color = MaterialTheme.colorScheme.error) }
                        },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceXS))

                    OutlinedTextField(
                        value = solicitudesViewModel.correo,
                        onValueChange = { solicitudesViewModel.onCorreoChange(it) },
                        label = { Text("Correo electrónico *") },
                        placeholder = { Text("ejemplo@correo.cl") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        trailingIcon = {
                            if (solicitudesViewModel.correoError != null) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = "Error en correo",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = solicitudesViewModel.correoError != null,
                        supportingText = solicitudesViewModel.correoError?.let { errorMsg ->
                            { Text(errorMsg, color = MaterialTheme.colorScheme.error) }
                        },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceXS))

                    OutlinedTextField(
                        value = solicitudesViewModel.telefono,
                        onValueChange = { solicitudesViewModel.onTelefonoChange(it) },
                        label = { Text("Teléfono celular *") },
                        placeholder = { Text("+56 9 1234 5678") },
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                        trailingIcon = {
                            if (solicitudesViewModel.telefonoError != null) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = "Error en teléfono",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = solicitudesViewModel.telefonoError != null,
                        supportingText = solicitudesViewModel.telefonoError?.let { errorMsg ->
                            { Text(errorMsg, color = MaterialTheme.colorScheme.error) }
                        },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceXS))

                    OutlinedTextField(
                        value = solicitudesViewModel.mensaje,
                        onValueChange = { solicitudesViewModel.onMensajeChange(it) },
                        label = { Text(labelMensaje) },
                        placeholder = { Text(placeholderMensaje) },
                        leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = null) },
                        trailingIcon = {
                            if (solicitudesViewModel.mensajeError != null) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = "Error en mensaje",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        minLines = 3,
                        isError = solicitudesViewModel.mensajeError != null,
                        supportingText = solicitudesViewModel.mensajeError?.let { errorMsg ->
                            { Text(errorMsg, color = MaterialTheme.colorScheme.error) }
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (permitirAdjuntos) {
                        Spacer(modifier = Modifier.height(Dimens.SpaceMD))

                        Text(
                            text = textoBotonImagen,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(Dimens.SpaceXS))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSM)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (tienePermisoCamara) launcherCamara.launch(null)
                                    else requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                                shape = PillShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                            ) {
                                Icon(Icons.Outlined.PhotoCamera, contentDescription = null)
                                Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                                Text("Cámara")
                            }

                            OutlinedButton(
                                onClick = { launcherGaleria.launch("image/*") },
                                shape = PillShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                            ) {
                                Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
                                Spacer(modifier = Modifier.width(Dimens.SpaceXS))
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = Dimens.SpaceSM)
                            ) {
                                imagenAMostrar?.let { bmp ->
                                    Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = "Foto adjunta",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                    )
                                }

                                TextButton(
                                    onClick = { solicitudesViewModel.eliminarImagen() },
                                    modifier = Modifier.defaultMinSize(minHeight = Dimens.MinTouchTarget)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(Dimens.SpaceXXS))
                                    Text("Quitar imagen", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.SpaceLG))

                    Button(
                        onClick = { solicitudesViewModel.enviarFormulario() },
                        shape = PillShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                        Text("Enviar solicitud", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))
        }
    }
}