package com.example.app_movil.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Cliente Singleton de Retrofit para las APIs externas consumidas por la app
object RetrofitClient {

    private const val CORPALIV_BASE_URL = "https://corpaliv.cl/"
    private const val MINDICADOR_BASE_URL = "https://mindicador.cl/"

    // Cliente HTTP compartido con tiempos de espera controlados
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    // Servicio Retrofit para noticias oficiales de CORPALIV
    val apiService: CorpalivApiService by lazy {
        Retrofit.Builder()
            .baseUrl(CORPALIV_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CorpalivApiService::class.java)
    }

    // Servicio Retrofit para indicadores económicos diarios de Chile (UF)
    val mindicadorService: MindicadorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MINDICADOR_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MindicadorApiService::class.java)
    }
}