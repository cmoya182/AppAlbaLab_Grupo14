package com.example.app_movil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Objeto de Acceso a Datos (DAO - Data Access Object) para la entidad [SolicitudEntity].
 *
 * Abstrae las consultas SQL y provee operaciones CRUD seguras contra SQLite mediante Room.
 * Utiliza Kotlin Flow para observar reactivamente las mutaciones en la tabla, permitiendo
 * que la interfaz de usuario se actualice automáticamente ante cualquier inserción o borrado.
 */
@Dao
interface SolicitudDao {
    /**
     * Inserta un nuevo registro de solicitud en la tabla 'solicitudes_vinculacion'.
     * En caso de conflicto de clave primaria, se aplica la estrategia REPLACE para actualizar la fila existente.
     * @param solicitud Instancia de la entidad con los datos capturados en el formulario.
     * @return El identificador de fila (rowid) generado por la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarSolicitud(solicitud: SolicitudEntity): Long

    /**
     * Obtiene un flujo reactivo (Flow) con la totalidad de solicitudes registradas en la base de datos.
     * Los registros se ordenan de forma descendente por su clave primaria ([SolicitudEntity.id]),
     * garantizando que las solicitudes más recientes se presenten en la parte superior del listado.
     * @return [Flow] que emite la lista actualizada de [SolicitudEntity] cada vez que la tabla sufre cambios.
     */
    @Query("SELECT * FROM solicitudes_vinculacion ORDER BY id DESC")
    fun obtenerTodasLasSolicitudes(): Flow<List<SolicitudEntity>>

    /**
     * Elimina de forma permanente una solicitud específica a partir de su clave primaria.
     * @param id Identificador numérico único de la solicitud a remover.
     */
    @Query("DELETE FROM solicitudes_vinculacion WHERE id = :id")
    fun eliminarSolicitudPorId(id: Int)

    /**
     * Vacía completamente la tabla 'solicitudes_vinculacion'.
     * Utilizado para tareas de mantenimiento, pruebas unitarias o restablecimiento del sistema.
     */
    @Query("DELETE FROM solicitudes_vinculacion")
    fun eliminarTodasLasSolicitudes()
}