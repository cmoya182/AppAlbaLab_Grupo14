package com.example.app_movil.data.repository

import com.example.app_movil.data.local.SolicitudDao
import com.example.app_movil.data.local.SolicitudEntity
import com.example.app_movil.data.remote.RetrofitClient
import com.example.app_movil.data.remote.SolicitudApiService
import com.example.app_movil.data.remote.SolicitudDto
import kotlinx.coroutines.flow.Flow

// Capa Repositorio: coordina Retrofit (alba-lab-api) con el respaldo local en Room SQLite
class SolicitudesRepository(
    private val solicitudDao: SolicitudDao,
    private val apiService: SolicitudApiService = RetrofitClient.solicitudApiService
) {

    // Flujo reactivo que actualiza la interfaz automáticamente ante cualquier cambio local o remoto
    fun observarSolicitudes(): Flow<List<SolicitudEntity>> =
        solicitudDao.obtenerTodasLasSolicitudes()

    // POST pendientes locales + GET /api/solicitudes -> Sincroniza Spring Boot con Room
    suspend fun sincronizarConServidor(): Result<Int> {
        return try {
            // Sube primero los registros creados en modo local (remoteId == null) antes de consultar el servidor
            val pendientesLocales = solicitudDao.obtenerTodasSincrono()
                .filter { it.remoteId == null }
                .reversed()

            pendientesLocales.forEach { pendiente ->
                val respuestaPost = apiService.crearSolicitud(pendiente.toDto())
                if (respuestaPost.isSuccessful && respuestaPost.body()?.id != null) {
                    val creada = respuestaPost.body()!!
                    solicitudDao.actualizarSolicitud(
                        pendiente.copy(
                            remoteId = creada.id,
                            estado = creada.estado.ifBlank { pendiente.estado }
                        )
                    )
                }
            }

            val remotas = apiService.listarSolicitudes()
            val locales = solicitudDao.obtenerTodasSincrono()
            val localesPorRemoteId = locales
                .filter { it.remoteId != null }
                .associateBy { it.remoteId!! }
            val idsRemotosActivos = remotas.mapNotNull { it.id }.toSet()

            // Elimina en local los registros que ya fueron borrados en el servidor
            locales
                .filter { it.remoteId != null && it.remoteId !in idsRemotosActivos }
                .forEach { solicitudDao.eliminarSolicitudPorId(it.id) }

            // Inserta o actualiza cada solicitud remota conservando su imagen local si existe
            remotas.reversed().forEach { dto ->
                val existente = dto.id?.let { localesPorRemoteId[it] }
                val estadoSeguro = dto.estado.ifBlank { "PENDIENTE" }
                if (existente != null) {
                    solicitudDao.actualizarSolicitud(
                        existente.copy(
                            tipoContacto = dto.tipoContacto,
                            nombre = dto.nombre,
                            correo = dto.correo,
                            telefono = dto.telefono,
                            mensaje = dto.mensaje,
                            fecha = dto.fecha,
                            estado = estadoSeguro
                        )
                    )
                } else {
                    solicitudDao.insertarSolicitud(
                        SolicitudEntity(
                            tipoContacto = dto.tipoContacto,
                            nombre = dto.nombre,
                            correo = dto.correo,
                            telefono = dto.telefono,
                            mensaje = dto.mensaje,
                            fecha = dto.fecha,
                            estado = estadoSeguro,
                            remoteId = dto.id
                        )
                    )
                }
            }
            Result.success(remotas.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // POST /api/solicitudes -> Envía una nueva solicitud al microservicio y guarda el remoteId en Room
    suspend fun crearSolicitud(entidad: SolicitudEntity): Result<SolicitudEntity> {
        return try {
            val respuesta = apiService.crearSolicitud(entidad.toDto())
            if (respuesta.isSuccessful && respuesta.body() != null) {
                val creada = respuesta.body()!!
                val conIdRemoto = entidad.copy(
                    remoteId = creada.id,
                    estado = creada.estado.ifBlank { "PENDIENTE" }
                )
                val idLocal = solicitudDao.insertarSolicitud(conIdRemoto).toInt()
                Result.success(conIdRemoto.copy(id = idLocal))
            } else {
                solicitudDao.insertarSolicitud(entidad)
                Result.failure(Exception("HTTP ${respuesta.code()} al crear solicitud"))
            }
        } catch (e: Exception) {
            // Si el backend no está encendido, guarda localmente para no perder los datos
            solicitudDao.insertarSolicitud(entidad)
            Result.failure(e)
        }
    }

    // PUT /api/solicitudes/{id} -> Actualiza mensaje y estado en Spring Boot y luego en Room
    suspend fun actualizarSolicitud(entidad: SolicitudEntity): Result<SolicitudEntity> {
        return try {
            val idRemoto = entidad.remoteId
            if (idRemoto != null) {
                val respuesta = apiService.actualizarSolicitud(idRemoto, entidad.toDto())
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val actualizada = respuesta.body()!!
                    val entidadFinal = entidad.copy(
                        mensaje = actualizada.mensaje,
                        estado = actualizada.estado.ifBlank { entidad.estado }
                    )
                    solicitudDao.actualizarSolicitud(entidadFinal)
                    Result.success(entidadFinal)
                } else {
                    solicitudDao.actualizarSolicitud(entidad)
                    Result.failure(Exception("HTTP ${respuesta.code()} al actualizar solicitud"))
                }
            } else {
                // Si se creó sin servidor, la sube primero mediante POST para obtener su ID remoto
                val respuestaPost = apiService.crearSolicitud(entidad.toDto())
                if (respuestaPost.isSuccessful && respuestaPost.body() != null) {
                    val sincronizada = entidad.copy(remoteId = respuestaPost.body()!!.id)
                    solicitudDao.actualizarSolicitud(sincronizada)
                    Result.success(sincronizada)
                } else {
                    solicitudDao.actualizarSolicitud(entidad)
                    Result.failure(Exception("Actualizada solo en almacenamiento local"))
                }
            }
        } catch (e: Exception) {
            solicitudDao.actualizarSolicitud(entidad)
            Result.failure(e)
        }
    }

    // DELETE /api/solicitudes/{id} -> Elimina la solicitud en Spring Boot y en la tabla local
    suspend fun eliminarSolicitud(idLocal: Int): Result<String?> {
        val existente = solicitudDao.obtenerSolicitudPorId(idLocal)
        var errorRemoto: Exception? = null

        val idRemoto = existente?.remoteId
        if (idRemoto != null) {
            try {
                val respuesta = apiService.eliminarSolicitud(idRemoto)
                if (!respuesta.isSuccessful && respuesta.code() != 404) {
                    errorRemoto = Exception("HTTP ${respuesta.code()} al eliminar en servidor")
                }
            } catch (e: Exception) {
                errorRemoto = e
            }
        }

        solicitudDao.eliminarSolicitudPorId(idLocal)
        return if (errorRemoto == null) {
            Result.success(existente?.imagenPath)
        } else {
            Result.failure(errorRemoto)
        }
    }

    // Elimina todas las solicitudes sincronizadas en el servidor y vacía Room
    suspend fun eliminarTodasLasSolicitudes(): Result<Unit> {
        var errorRemoto: Exception? = null
        val locales = solicitudDao.obtenerTodasSincrono()
        locales.forEach { solicitud ->
            solicitud.remoteId?.let { idRemoto ->
                try {
                    apiService.eliminarSolicitud(idRemoto)
                } catch (e: Exception) {
                    errorRemoto = e
                }
            }
        }
        solicitudDao.eliminarTodasLasSolicitudes()
        return if (errorRemoto == null) Result.success(Unit) else Result.failure(errorRemoto)
    }

    // Mapea la entidad local de Room al DTO JSON esperado por Spring Boot
    private fun SolicitudEntity.toDto(): SolicitudDto = SolicitudDto(
        id = remoteId,
        tipoContacto = tipoContacto,
        nombre = nombre,
        correo = correo,
        telefono = telefono,
        mensaje = mensaje,
        fecha = fecha,
        estado = estado
    )
}