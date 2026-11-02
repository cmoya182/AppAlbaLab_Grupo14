package com.example.app_movil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * ARCHIVO: SolicitudDao.kt
 * PROPÓSITO: Define el DAO (Data Access Object / Objeto de Acceso a Datos), que actúa como puente
 * traductor entre las funciones de Kotlin y las instrucciones SQL que ejecuta SQLite.
 *
 * ¿CÓMO FUNCIONA?:
 * Es una interfaz donde declaramos qué operaciones queremos hacer sobre la tabla "solicitudes_vinculacion".
 * El plugin KSP lee estas etiquetas (@Insert, @Query) al compilar y escribe automáticamente el código SQL.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - SolicitudEntity.kt: Es el tipo de dato que recibe al insertar y que devuelve al consultar.
 * - AppDatabase.kt: Expone la función solicitudDao() para que el ViewModel pueda usar esta interfaz.
 * - MainViewModel.kt: Llama a estas funciones dentro de corrutinas (Dispatchers.IO) para no trabar la pantalla.
 */
@Dao
interface SolicitudDao {

    /**
     * Guarda una nueva solicitud en la tabla SQLite.
     * OnConflictStrategy.REPLACE indica que si existiera un registro con el mismo ID, lo actualice.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarSolicitud(solicitud: SolicitudEntity): Long

    /**
     * Obtiene todas las solicitudes ordenadas desde la más nueva a la más antigua (ORDER BY id DESC).
     * Retorna un Flow (flujo reactivo): funciona como un canal en vivo que avisa automáticamente a la
     * pantalla cada vez que se agrega o se elimina una solicitud en la base de datos.
     */
    @Query("SELECT * FROM solicitudes_vinculacion ORDER BY id DESC")
    fun obtenerTodasLasSolicitudes(): Flow<List<SolicitudEntity>>

    /**
     * Busca una solicitud específica por su ID. Se utiliza antes de borrar un registro para conocer
     * si tiene una fotografía asociada en el almacenamiento interno y poder eliminar el archivo físico.
     */
    @Query("SELECT * FROM solicitudes_vinculacion WHERE id = :id LIMIT 1")
    fun obtenerSolicitudPorId(id: Int): SolicitudEntity?

    /**
     * Elimina una única solicitud cuyo ID coincida con el parámetro recibido (botón basurero del Panel).
     */
    @Query("DELETE FROM solicitudes_vinculacion WHERE id = :id")
    fun eliminarSolicitudPorId(id: Int)

    /**
     * Vacía todos los registros de la tabla (utilizado desde la opción "Vaciar Historial" en Ajustes).
     */
    @Query("DELETE FROM solicitudes_vinculacion")
    fun eliminarTodasLasSolicitudes()
}
