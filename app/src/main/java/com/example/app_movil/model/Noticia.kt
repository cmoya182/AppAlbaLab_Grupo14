package com.example.app_movil.model

// Modelo de dominio que representa una noticia oficial publicada por CORPALIV
data class Noticia(
    val id: Int,
    val titulo: String,
    val resumen: String,
    val fechaPublicacion: String,
    val enlace: String,
    val imagenUrl: String? = null
)