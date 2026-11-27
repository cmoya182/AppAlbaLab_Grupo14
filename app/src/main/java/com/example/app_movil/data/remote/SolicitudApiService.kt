package com.example.app_movil.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Interfaz Retrofit con los 4 métodos CRUD conectados al microservicio alba-lab-api
interface SolicitudApiService {

    // GET /api/solicitudes -> Obtiene todas las solicitudes guardadas en Spring Boot (H2)
    @GET("api/solicitudes")
    suspend fun listarSolicitudes(): List<SolicitudDto>

    // POST /api/solicitudes -> Crea una nueva solicitud en la base de datos del servidor
    @POST("api/solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): Response<SolicitudDto>

    // PUT /api/solicitudes/{id} -> Actualiza mensaje o estado (PENDIENTE / ATENDIDA) por ID
    @PUT("api/solicitudes/{id}")
    suspend fun actualizarSolicitud(
        @Path("id") id: Long,
        @Body solicitud: SolicitudDto
    ): Response<SolicitudDto>

    // DELETE /api/solicitudes/{id} -> Elimina una solicitud específica por su ID remoto
    @DELETE("api/solicitudes/{id}")
    suspend fun eliminarSolicitud(@Path("id") id: Long): Response<Unit>
}