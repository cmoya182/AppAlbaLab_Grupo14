package com.example.app_movil

import android.app.Application
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.app_movil.data.local.SolicitudEntity
import com.example.app_movil.ui.screens.CatalogoScreen
import com.example.app_movil.ui.screens.LoginScreen
import com.example.app_movil.ui.screens.SolicitudCard
import com.example.app_movil.ui.theme.APP_MOVILTheme
import com.example.app_movil.viewmodels.AuthViewModel
import com.example.app_movil.viewmodels.CatalogoViewModel
import com.example.app_movil.viewmodels.MainViewModel
import com.example.app_movil.viewmodels.SolicitudesViewModel
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Pruebas instrumentadas de interfaz (Jetpack Compose) para pantallas y componentes reales de Alba Lab
@RunWith(AndroidJUnit4::class)
class AlbaLabUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Obtiene la instancia Application del contexto instrumentado para los AndroidViewModel reales
    private fun obtenerApplication(): Application =
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

    // Verifica que LoginScreen muestre errores de validación al intentar iniciar sesión con campos vacíos
    @Test
    fun loginScreen_conCamposVacios_muestraErroresDeValidacion() {
        val authViewModel = AuthViewModel(obtenerApplication())
        val mainViewModel = MainViewModel()

        composeTestRule.setContent {
            APP_MOVILTheme {
                LoginScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel
                )
            }
        }

        // Espera a que termine la composición inicial y presiona el botón de ingreso
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Iniciar sesión").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Comprueba los mensajes de error mostrados bajo cada campo
        composeTestRule
            .onNodeWithText("Ingrese un correo institucional válido (@corpaliv.cl).")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("La contraseña debe contener al menos 6 caracteres.")
            .assertIsDisplayed()
    }

    // Verifica filtrado por categoría y validación del buscador en la Tienda Solidaria (CatalogoScreen)
    @Test
    fun catalogoScreen_filtraPorCategoriaYValidaCaracteresEspeciales() {
        val app = obtenerApplication()
        val mainViewModel = MainViewModel()
        val authViewModel = AuthViewModel(app)
        val catalogoViewModel = CatalogoViewModel()
        val solicitudesViewModel = SolicitudesViewModel(app)

        composeTestRule.setContent {
            APP_MOVILTheme {
                CatalogoScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    catalogoViewModel = catalogoViewModel,
                    solicitudesViewModel = solicitudesViewModel
                )
            }
        }

        // Sincroniza el árbol de Compose y desplaza el chip Cerámica a vista antes de activarlo
        composeTestRule.waitForIdle()
        val chipCeramica = composeTestRule.onNode(
            hasText("Cerámica") and (isSelectable() or isToggleable())
        )
        chipCeramica.performScrollTo()
        chipCeramica.assertIsDisplayed()
        chipCeramica.performSemanticsAction(SemanticsActions.OnClick)
        composeTestRule.waitForIdle()

        // Comprueba que aparezca solo el producto de Cerámica y se oculten otras categorías
        composeTestRule.onNodeWithText("Tazón Institucional").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bolsa Alba Lab").assertDoesNotExist()

        // Ingresa símbolos inválidos en el buscador y verifica el mensaje de error sin interferencia del teclado
        composeTestRule.onNodeWithText("Buscar producto o taller...").performTextInput("Difusor@@")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No se permiten caracteres especiales.").assertIsDisplayed()
    }

    // Verifica renderizado e interacción CRUD (estado, editar y eliminar) en SolicitudCard del Panel
    @Test
    fun solicitudCard_muestraDatosYDisparaAccionesCrud() {
        var estadoAlternado = false
        var edicionSolicitada = false
        var eliminacionSolicitada = false

        val solicitudDemo = SolicitudEntity(
            id = 1,
            tipoContacto = "Empresa",
            nombre = "Fundación Aliada SPA",
            correo = "alianzas@aliada.cl",
            telefono = "+56987654321",
            mensaje = "Solicitud de convenio de inclusión sociolaboral 2026.",
            fecha = "26/11/2026 19:00",
            estado = "PENDIENTE",
            remoteId = 42L
        )

        composeTestRule.setContent {
            APP_MOVILTheme {
                SolicitudCard(
                    solicitud = solicitudDemo,
                    onAlternarEstado = { estadoAlternado = true },
                    onEditar = { edicionSolicitada = true },
                    onEnviarCorreo = {},
                    onLlamar = {},
                    onCompartir = {},
                    onSolicitarEliminar = { eliminacionSolicitada = true }
                )
            }
        }

        composeTestRule.waitForIdle()

        // Valida información principal visible en la tarjeta
        composeTestRule.onNodeWithText("Fundación Aliada SPA").assertIsDisplayed()
        composeTestRule.onNodeWithText("#42").assertIsDisplayed()
        composeTestRule.onNodeWithText("PENDIENTE").assertIsDisplayed()

        // Dispara acciones de cambio de estado (PUT), edición (PUT) y borrado (DELETE)
        composeTestRule.onNodeWithText("PENDIENTE").performClick()
        composeTestRule.onNodeWithContentDescription("Editar solicitud").performClick()
        composeTestRule.onNodeWithContentDescription("Eliminar solicitud").performClick()

        assertTrue(estadoAlternado)
        assertTrue(edicionSolicitada)
        assertTrue(eliminacionSolicitada)
    }
}
