package com.example.app_movil.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Define la tabla 'solicitudes_vinculacion' dentro de la base de datos SQLite de la aplicación
@Entity(tableName = "solicitudes_vinculacion")
data class SolicitudEntity(
    // Clave primaria autoincremental: genera automáticamente un identificador numérico único para cada fila
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Columnas que almacenan los datos ingresados en el formulario de vinculación
    val nombre: String,
    val correo: String,
    val telefono: String,
    val mensaje: String,
    val fecha: String
)