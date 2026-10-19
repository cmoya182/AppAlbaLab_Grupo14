package com.example.app_movil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Objeto de Acceso a Datos (DAO) para interactuar con la base de datos Room
@Dao
interface SolicitudDao {

    // Inserta la solicitud en la base de datos y retorna el identificador autogenerado (Long).
    // Se define como función síncrona para compatibilidad con KSP2; se ejecuta en segundo plano con Dispatchers.IO.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarSolicitud(solicitud: SolicitudEntity): Long

    // Consulta reactiva que retorna la lista completa de solicitudes ordenadas por ID descendente
    @Query("SELECT * FROM solicitudes_vinculacion ORDER BY id DESC")
    fun obtenerTodasLasSolicitudes(): Flow<List<SolicitudEntity>>
}