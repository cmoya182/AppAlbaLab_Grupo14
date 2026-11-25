package com.example.app_movil.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SolicitudApiService {

    @GET("api/solicitudes")
    suspend fun listarSolicitudes(): List<SolicitudDto>

    @GET("api/solicitudes/{id}")
    suspend fun obtenerSolicitud(@Path("id") id: Long): SolicitudDto

    @POST("api/solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): Response<SolicitudDto>

    @PUT("api/solicitudes/{id}")
    suspend fun actualizarSolicitud(
        @Path("id") id: Long,
        @Body solicitud: SolicitudDto
    ): Response<SolicitudDto>

    @DELETE("api/solicitudes/{id}")
    suspend fun eliminarSolicitud(@Path("id") id: Long): Response<Unit>
}