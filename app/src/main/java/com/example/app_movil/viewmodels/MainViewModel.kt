package com.example.app_movil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_movil.navigation.NavigationEvent
import com.example.app_movil.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Flujo compartido de eventos de navegación hacia MainActivity
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // Valida autorización por rol antes de navegar; redirige a Noticias si un invitado intenta entrar al Panel
    fun navigateTo(
        screen: Screen,
        rolActual: UserRole = UserRole.INVITADO,
        clearBackStack: Boolean = false
    ) {
        val destinoSeguro = if (screen.requiereAdministrador && rolActual != UserRole.ADMINISTRADOR) {
            Screen.Noticias
        } else {
            screen
        }

        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(
                    route = destinoSeguro,
                    clearBackStack = clearBackStack
                )
            )
        }
    }

    // Emite orden para retroceder en el historial de pantallas
    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }
}