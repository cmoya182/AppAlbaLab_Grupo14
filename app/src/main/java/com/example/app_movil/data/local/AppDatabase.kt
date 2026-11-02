package com.example.app_movil.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * ARCHIVO: AppDatabase.kt
 * PROPÓSITO: Configura y construye el archivo físico de la base de datos SQLite ("alba_lab_db") en el teléfono.
 *
 * ¿CÓMO FUNCIONA?:
 * Implementa el patrón de diseño "Singleton" (instancia única). Abrir una conexión a base de datos consume
 * mucha memoria RAM; por eso, la variable INSTANCE guarda la conexión abierta para reutilizarla en toda la app.
 * Además, incluye una migración segura (MIGRATION_2_3) que agrega la columna "imagenPath" conservando
 * todas las solicitudes que el usuario haya guardado previamente.
 *
 * ¿CON QUÉ ARCHIVOS SE CONECTA?:
 * - SolicitudEntity.kt: Le indica a Room qué tablas conforman la base de datos (entities).
 * - SolicitudDao.kt: Provee acceso al DAO mediante la función abstracta solicitudDao().
 * - MainViewModel.kt: Llama a AppDatabase.getDatabase(application) al arrancar para conectarse a SQLite.
 */
@Database(entities = [SolicitudEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Conecta la base de datos con las consultas SQL definidas en SolicitudDao.kt
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        // @Volatile asegura que si un hilo del procesador crea la base de datos, todos los demás hilos
        // se enteren de inmediato y ninguno intente crear una segunda conexión repetida.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migración oficial de la versión 2 a la versión 3 de la base de datos.
         * Ejecuta un comando SQL ALTER TABLE que añade la columna 'imagenPath' a la tabla existente
         * sin borrar ni perder las solicitudes registradas anteriormente en el teléfono.
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE solicitudes_vinculacion ADD COLUMN imagenPath TEXT")
            }
        }

        /**
         * Devuelve la instancia única de la base de datos. Si aún no existe (es null), entra al bloque
         * synchronized(this) para construirla de forma segura.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alba_lab_db"
                )
                    .addMigrations(MIGRATION_2_3) // Aplica la migración que protege los datos al pasar de v2 a v3
                    .fallbackToDestructiveMigration() // Respaldo de seguridad si existiera una versión antigua incompatible (v1)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
