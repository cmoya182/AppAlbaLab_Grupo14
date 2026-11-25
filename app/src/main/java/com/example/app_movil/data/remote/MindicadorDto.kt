package com.example.app_movil.data.remote

import com.google.gson.annotations.SerializedName

// DTO principal para la respuesta JSON de https://mindicador.cl/api/uf
data class MindicadorResponseDto(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("serie") val serie: List<SerieIndicadorDto>
)

// Representa cada registro diario devuelto dentro del arreglo "serie"
data class SerieIndicadorDto(
    @SerializedName("fecha") val fecha: String,
    @SerializedName("valor") val valor: Double
)