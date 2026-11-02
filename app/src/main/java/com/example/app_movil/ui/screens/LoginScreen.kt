package com.example.app_movil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.app_movil.R
import com.example.app_movil.viewmodels.MainViewModel

/**
 * ARCHIVO: LoginScreen.kt
 * PROPÓSITO: Construye la pantalla de inicio de sesión para colaboradores de Alba Lab - CORPALIV.
 *
 * ¿CÓMO FUNCIONA?:
 * 1. Los campos de correo y contraseña no guardan su texto aquí, sino que leen y actualizan las variables
 *    del MainViewModel (loginEmail y loginPassword), cumpliendo con el desacoplamiento de estado (IE 2.2.1).
 * 2. Al presionar "Iniciar Sesión", ejecuta viewModel.iniciarSesion(). Si algún campo tiene formato inválido
 *    o las credenciales no coinciden con admin@corpaliv.cl / albalab2026, el campo se pinta en rojo (isError),
 *    muestra ícono de error y despliega el mensaje explicativo debajo (supportingText).
 * 3. También incluye el botón "Continuar como Invitado / Donante" para que empresas o compradores puedan
 *    explorar la tienda y enviar solicitudes sin necesidad de tener cuenta administrativa.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - MainActivity.kt: Es la pantalla inicial del NavHost cuando no hay una sesión activa guardada.
 * - MainViewModel.kt: Ejecuta toda la validación de credenciales y guarda la sesión en SharedPreferences.
 */
@Composable
fun LoginScreen(viewModel: MainViewModel) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo CORPALIV Alba Lab",
                modifier = Modifier
                    .size(140.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "Acceso Colaboradores",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo 1: Correo Institucional con validación visual e ícono de error
            OutlinedTextField(
                value = viewModel.loginEmail,
                onValueChange = { viewModel.onLoginEmailChange(it) },
                label = { Text("Correo Institucional *") },
                placeholder = { Text("admin@corpaliv.cl") },
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.loginEmailError != null) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error en correo",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = viewModel.loginEmailError != null,
                supportingText = {
                    viewModel.loginEmailError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo 2: Contraseña con botón de ojo para mostrar/ocultar caracteres y validación visual
            OutlinedTextField(
                value = viewModel.loginPassword,
                onValueChange = { viewModel.onLoginPasswordChange(it) },
                label = { Text("Contraseña *") },
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (viewModel.passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (viewModel.passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = viewModel.loginPasswordError != null,
                supportingText = {
                    viewModel.loginPasswordError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón principal que dispara la validación de credenciales en MainViewModel
            Button(
                onClick = { viewModel.iniciarSesion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Iniciar Sesión", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón secundario para entrar en modo público sin credenciales de colaborador
            OutlinedButton(
                onClick = { viewModel.ingresarComoInvitado() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Continuar como Invitado / Donante")
            }
        }
    }
}
