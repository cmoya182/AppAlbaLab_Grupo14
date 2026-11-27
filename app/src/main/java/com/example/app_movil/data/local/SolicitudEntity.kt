package com.example.app_movil.data.local

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad Room SQLite inmutable sincronizada con el microservicio alba-lab-api
@Immutable
@Entity(tableName = "solicitudes_vinculacion")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Canal de origen: "Empresa", "Compra Solidaria" o "Voluntariado"
    @ColumnInfo(name = "tipoContacto")
    val tipoContacto: String = "Empresa",

    // Nombre completo del solicitante o razón social
    @ColumnInfo(name = "nombre")
    val nombre: String,

    // Correo electrónico validado
    @ColumnInfo(name = "correo")
    val correo: String,

    // Teléfono de contacto
    @ColumnInfo(name = "telefono")
    val telefono: String,

    // Propuesta, cotización o motivación de voluntariado
    @ColumnInfo(name = "mensaje")
    val mensaje: String,

    // Fecha de envío en formato dd/MM/yyyy HH:mm
    @ColumnInfo(name = "fecha")
    val fecha: String,

    // Ruta interna de la foto adjunta en el dispositivo (opcional)
    @ColumnInfo(name = "imagenPath")
    val imagenPath: String? = null,

    // Estado de seguimiento administrativo: "PENDIENTE" o "ATENDIDA"
    @ColumnInfo(name = "estado")
    val estado: String = "PENDIENTE",

    // Identificador generado por Spring Boot (H2) para ejecutar PUT y DELETE remotos
    @ColumnInfo(name = "remoteId")
    val remoteId: Long? = null
)