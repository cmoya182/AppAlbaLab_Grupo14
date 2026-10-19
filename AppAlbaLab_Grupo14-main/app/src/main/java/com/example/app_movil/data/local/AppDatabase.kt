package com.example.app_movil.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Configura la base de datos Room, registrando la entidad SolicitudEntity y la versión del esquema
@Database(entities = [SolicitudEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Función abstracta que permite al ViewModel acceder al DAO correspondiente
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        // Variable volátil para garantizar visibilidad inmediata de la instancia entre hilos de ejecución
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Retorna la instancia de la base de datos o la inicializa si aún no existe (patrón Singleton)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "albalab_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}