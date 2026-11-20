package com.example.app_movil.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// Interfaz Retrofit para consultar las noticias públicas desde el sitio oficial de CORPALIV
interface CorpalivApiService {

    // Obtiene las últimas publicaciones en formato JSON con sus metadatos incrustados
    @GET("wp-json/wp/v2/posts")
    suspend fun obtenerNoticias(
        @Query("per_page") cantidad: Int = 10,
        @Query("_embed") embed: Boolean = true
    ): List<WordPressPostDto>
}