package com.example.app_movil.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ARCHIVO: SolicitudEntity.kt
 * PROPÓSITO: Representa el modelo de datos (tabla SQLite) donde se guardan las solicitudes de vinculación.
 *
 * ¿CÓMO FUNCIONA?:
 * La anotación @Entity le indica a la librería Room que debe crear una tabla llamada "solicitudes_vinculacion"
 * dentro de la base de datos interna del teléfono. Cada objeto de esta clase equivale a una fila en esa tabla.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - SolicitudDao.kt: Utiliza esta clase para saber qué datos insertar, consultar o eliminar.
 * - AppDatabase.kt: Registra esta entidad dentro de la configuración general de la base de datos.
 * - MainViewModel.kt: Crea objetos SolicitudEntity cuando el usuario envía un formulario válido.
 * - ProfileScreen.kt: Recibe una lista de estos objetos para dibujar las tarjetas en el Panel Institucional.
 */
@Entity(tableName = "solicitudes_vinculacion")
data class SolicitudEntity(
    // @PrimaryKey(autoGenerate = true) genera un número único automático (1, 2, 3...) para cada solicitud.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Clasifica el origen del formulario: "Empresa", "Compra Solidaria" o "Voluntariado".
    @ColumnInfo(name = "tipoContacto")
    val tipoContacto: String = "Empresa",

    // Nombre de la persona de contacto o razón social de la empresa.
    @ColumnInfo(name = "nombre")
    val nombre: String,

    // Correo electrónico validado mediante expresión regular (RegEx).
    @ColumnInfo(name = "correo")
    val correo: String,

    // Teléfono de contacto (mínimo 8 dígitos numéricos).
    @ColumnInfo(name = "telefono")
    val telefono: String,

    // Detalle de la propuesta corporativa, producto cotizado o motivación de voluntariado.
    @ColumnInfo(name = "mensaje")
    val mensaje: String,

    // Fecha y hora exacta en que se envió la solicitud (formato dd/MM/yyyy HH:mm).
    @ColumnInfo(name = "fecha")
    val fecha: String,

    // Ruta del archivo de imagen guardado en el almacenamiento interno del dispositivo (filesDir).
    // Es nullable (String?) porque las solicitudes de Compra Solidaria o sin foto adjunta guardan null.
    @ColumnInfo(name = "imagenPath")
    val imagenPath: String? = null
)
