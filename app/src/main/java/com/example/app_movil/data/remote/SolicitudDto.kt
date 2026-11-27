package com.example.app_movil.data.remote

import com.google.gson.annotations.SerializedName

// DTO alineado exactamente con la entidad Solicitud.java del microservicio alba-lab-api
data class SolicitudDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("tipoContacto") val tipoContacto: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("estado") val estado: String = "PENDIENTE"
)