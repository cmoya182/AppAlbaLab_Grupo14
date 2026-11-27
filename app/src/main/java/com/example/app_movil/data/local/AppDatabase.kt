package com.example.app_movil.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Base de datos SQLite local (Singleton) con migraciones seguras hasta la versión 4
@Database(entities = [SolicitudEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun solicitudDao(): SolicitudDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE solicitudes_vinculacion ADD COLUMN imagenPath TEXT")
            }
        }

        // Agrega columnas estado y remoteId para sincronización CRUD con Spring Boot
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE solicitudes_vinculacion ADD COLUMN estado TEXT NOT NULL DEFAULT 'PENDIENTE'")
                db.execSQL("ALTER TABLE solicitudes_vinculacion ADD COLUMN remoteId INTEGER")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alba_lab_db"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}