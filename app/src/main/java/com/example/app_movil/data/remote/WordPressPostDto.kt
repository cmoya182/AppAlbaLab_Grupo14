package com.example.app_movil.data.remote

import com.google.gson.annotations.SerializedName

// DTO principal para deserializar cada entrada JSON de la API REST de WordPress (corpaliv.cl)
data class WordPressPostDto(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("link") val link: String,
    @SerializedName("title") val title: RenderedTextDto,
    @SerializedName("excerpt") val excerpt: RenderedTextDto,
    @SerializedName("_embedded") val embedded: EmbeddedDto? = null
)

// Contenedor de texto renderizado en HTML devuelto por WordPress
data class RenderedTextDto(
    @SerializedName("rendered") val rendered: String
)

// Contenedor de recursos incrustados (_embed) como la imagen destacada
data class EmbeddedDto(
    @SerializedName("wp:featuredmedia") val featuredMedia: List<MediaDto>? = null
)

// URL directa del archivo multimedia asociado a la publicación
data class MediaDto(
    @SerializedName("source_url") val sourceUrl: String? = null
)