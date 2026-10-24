package com.example.app_movil.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla en SQLite que almacena solicitudes con clasificación de contacto
@Entity(tableName = "solicitudes_vinculacion")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipoContacto: String = "Empresa", // "Empresa", "Compra Solidaria", "Voluntariado"
    val nombre: String,
    val correo: String,
    val telefono: String,
    val mensaje: String,
    val fecha: String
)