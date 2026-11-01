package com.example.app_movil.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Capa de abstracción central de la base de datos SQLite construida sobre Android Room.
 *
 * Centraliza la definición del esquema relacional y provee acceso singleton a los DAOs de la aplicación.
 * Define la versión de base de datos actual (versión 2) y gestiona la recreación de tablas ante cambios de esquema.
 */
@Database(entities = [SolicitudEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provee el punto de acceso al Objeto de Acceso a Datos de solicitudes.
     * @return Instancia generada por Room de [SolicitudDao].
     */
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        /**
         * Referencia singleton volátil que garantiza lectura/escritura atómica inmediata
         * entre múltiples hilos de ejecución en memoria principal.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retorna la instancia única de [AppDatabase] para toda la aplicación.
         * Implementa el patrón Singleton con doble verificación y sincronización explícita (`synchronized`)
         * para evitar la apertura concurrente de múltiples conexiones a SQLite, protegiendo contra fugas
         * de memoria y bloqueos de base de datos.
         * @param context Contexto de la aplicación utilizado para inicializar el constructor de Room.
         * @return Instancia única y compartida de [AppDatabase].
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alba_lab_db"
                )
                    .fallbackToDestructiveMigration() // Reconstrucción limpia de tablas en fase de desarrollo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}