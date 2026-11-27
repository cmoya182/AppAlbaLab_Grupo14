package com.example.app_movil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// DAO de Room para persistencia local y sincronización con el microservicio Spring Boot
@Dao
interface SolicitudDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarSolicitud(solicitud: SolicitudEntity): Long

    @Update
    fun actualizarSolicitud(solicitud: SolicitudEntity)

    @Query("SELECT * FROM solicitudes_vinculacion ORDER BY id DESC")
    fun obtenerTodasLasSolicitudes(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes_vinculacion ORDER BY id DESC")
    fun obtenerTodasSincrono(): List<SolicitudEntity>

    @Query("SELECT * FROM solicitudes_vinculacion WHERE id = :id LIMIT 1")
    fun obtenerSolicitudPorId(id: Int): SolicitudEntity?

    @Query("DELETE FROM solicitudes_vinculacion WHERE id = :id")
    fun eliminarSolicitudPorId(id: Int)

    @Query("DELETE FROM solicitudes_vinculacion")
    fun eliminarTodasLasSolicitudes()
}