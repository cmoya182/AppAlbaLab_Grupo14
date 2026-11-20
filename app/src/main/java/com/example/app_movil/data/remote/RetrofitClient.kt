package com.example.app_movil.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Cliente Singleton de Retrofit apuntando al portal oficial de CORPALIV
object RetrofitClient {

    private const val BASE_URL = "https://corpaliv.cl/"

    // Cliente HTTP con tiempos de espera controlados para evitar bloqueos ante red lenta
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    // Instancia única del servicio API con conversor Gson
    val apiService: CorpalivApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CorpalivApiService::class.java)
    }
}