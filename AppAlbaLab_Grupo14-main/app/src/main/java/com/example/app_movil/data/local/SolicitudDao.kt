package com.example.app_movil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Objeto de Acceso a Datos (DAO) para la persistencia de solicitudes en Room
@Dao
interface SolicitudDao {

    // Inserta una nueva solicitud en la base de datos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarSolicitud(solicitud: SolicitudEntity): Long

    // Retorna el flujo continuo con todas las solicitudes registradas ordenadas por id descendente
    @Query("SELECT * FROM solicitudes_vinculacion ORDER BY id DESC")
    fun obtenerTodasLasSolicitudes(): Flow<List<SolicitudEntity>>

    // Elimina una solicitud específica mediante su clave primaria
    @Query("DELETE FROM solicitudes_vinculacion WHERE id = :id")
    fun eliminarSolicitudPorId(id: Int)

    // Elimina todas las solicitudes registradas (vaciado total)
    @Query("DELETE FROM solicitudes_vinculacion")
    fun eliminarTodasLasSolicitudes()
}