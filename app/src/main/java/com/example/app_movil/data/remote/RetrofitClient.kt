package com.example.app_movil.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Cliente Singleton de Retrofit para las APIs consumidas por la app
object RetrofitClient {

    private const val CORPALIV_BASE_URL = "https://corpaliv.cl/"
    private const val MINDICADOR_BASE_URL = "https://mindicador.cl/"
    // Con túnel ADB (adb reverse tcp:8080 tcp:8080) usa 127.0.0.1; sin túnel usa 10.0.2.2 (emulador) o la IP local del PC
    private const val ALBA_LAB_BASE_URL = "http://127.0.0.1:8080/"

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

    // Servicio Retrofit para el microservicio local alba-lab-api (puerto 8080 en el host)
    val solicitudApiService: SolicitudApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ALBA_LAB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SolicitudApiService::class.java)
    }
}