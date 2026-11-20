package com.example.app_movil.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_movil.R
import com.example.app_movil.navigation.Screen
import com.example.app_movil.ui.theme.Dimens
import com.example.app_movil.ui.theme.PillShape
import com.example.app_movil.viewmodels.AuthViewModel
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.viewmodels.UserRole

@Composable
fun LoginScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.SpaceLG),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Cabecera visual con el logotipo oficial de CORPALIV Alba Lab
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = Dimens.ElevationLow
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo CORPALIV Alba Lab",
                    modifier = Modifier
                        .size(124.dp)
                        .padding(Dimens.SpaceMD)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceMD))

            Text(
                text = "Acceso Colaboradores",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Red Sociolaboral Alba Lab - CORPALIV",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            // Tarjeta con formulario de credenciales y acceso como invitado
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Dimens.SpaceLG)) {
                    OutlinedTextField(
                        value = authViewModel.loginEmail,
                        onValueChange = { authViewModel.onLoginEmailChange(it) },
                        label = { Text("Correo institucional *") },
                        placeholder = { Text("admin@corpaliv.cl") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        trailingIcon = {
                            if (authViewModel.loginEmailError != null) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = "Error en correo",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = authViewModel.loginEmailError != null,
                        supportingText = authViewModel.loginEmailError?.let { errorMsg ->
                            { Text(text = errorMsg, color = MaterialTheme.colorScheme.error) }
                        },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceXS))

                    OutlinedTextField(
                        value = authViewModel.loginPassword,
                        onValueChange = { authViewModel.onLoginPasswordChange(it) },
                        label = { Text("Contraseña *") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { authViewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (authViewModel.passwordVisible) {
                                        Icons.Outlined.VisibilityOff
                                    } else {
                                        Icons.Outlined.Visibility
                                    },
                                    contentDescription = if (authViewModel.passwordVisible) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    }
                                )
                            }
                        },
                        visualTransformation = if (authViewModel.passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = authViewModel.loginPasswordError != null,
                        supportingText = authViewModel.loginPasswordError?.let { errorMsg ->
                            { Text(text = errorMsg, color = MaterialTheme.colorScheme.error) }
                        },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceMD))

                    // Ingreso con rol de administrador limpiando historial previo
                    Button(
                        onClick = {
                            authViewModel.iniciarSesion {
                                mainViewModel.navigateTo(
                                    screen = Screen.Home,
                                    rolActual = UserRole.ADMINISTRADOR,
                                    clearBackStack = true
                                )
                            }
                        },
                        shape = PillShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                    ) {
                        Text("Iniciar sesión", style = MaterialTheme.typography.labelLarge)
                    }

                    Spacer(modifier = Modifier.height(Dimens.SpaceXS))

                    // Ingreso con rol de invitado limpiando historial previo
                    OutlinedButton(
                        onClick = {
                            authViewModel.limpiarErrores()
                            mainViewModel.navigateTo(
                                screen = Screen.Home,
                                rolActual = UserRole.INVITADO,
                                clearBackStack = true
                            )
                        },
                        shape = PillShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                    ) {
                        Text("Continuar como invitado / donante", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}