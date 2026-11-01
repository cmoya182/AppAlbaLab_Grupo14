package com.example.app_movil.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de persistencia relacional que define la estructura de la tabla 'solicitudes_vinculacion' en SQLite.
 *
 * Mapea cada registro generado por los tres canales de interacción de la aplicación Alba Lab:
 * 1. "Empresa": Alianzas corporativas y compras institucionales por volumen (RSE).
 * 2. "Compra Solidaria": Pedidos y cotizaciones directas de productos confeccionados en talleres.
 * 3. "Voluntariado": Postulaciones individuales para acompañamiento técnico y laboral.
 *
 * @property id Clave primaria autoincremental administrada automáticamente por el motor SQLite.
 * @property tipoContacto Categoría institucional del registro ("Empresa", "Compra Solidaria", "Voluntariado").
 * @property nombre Nombre completo de la persona natural o razón social de la entidad solicitante.
 * @property correo Dirección electrónica validada para la coordinación y respuesta institucional.
 * @property telefono Número telefónico de contacto directo (mínimo 8 dígitos).
 * @property mensaje Contenido descriptivo del requerimiento, producto cotizado o motivación de apoyo.
 * @property fecha Marca temporal del registro en formato legible ("dd/MM/yyyy HH:mm").
 */
@Entity(tableName = "solicitudes_vinculacion")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "tipoContacto")
    val tipoContacto: String = "Empresa",
    @ColumnInfo(name = "nombre")
    val nombre: String,
    @ColumnInfo(name = "correo")
    val correo: String,
    @ColumnInfo(name = "telefono")
    val telefono: String,
    @ColumnInfo(name = "mensaje")
    val mensaje: String,
    @ColumnInfo(name = "fecha")
    val fecha: String
)