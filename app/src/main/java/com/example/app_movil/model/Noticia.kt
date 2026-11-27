package com.example.app_movil.model

import androidx.compose.runtime.Immutable

// Modelo inmutable de dominio que representa una noticia oficial publicada por CORPALIV
@Immutable
data class Noticia(
    val id: Int,
    val titulo: String,
    val resumen: String,
    val fechaPublicacion: String,
    val enlace: String,
    val imagenUrl: String? = null
)