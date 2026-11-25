package com.example.app_movil.data.remote

import retrofit2.http.GET

// Interfaz Retrofit para consultar el valor oficial diario de la UF en Chile
interface MindicadorApiService {

    // Obtiene el historial reciente de la UF (el primer elemento de "serie" es el valor de hoy)
    @GET("api/uf")
    suspend fun obtenerUfDiaria(): MindicadorResponseDto
}