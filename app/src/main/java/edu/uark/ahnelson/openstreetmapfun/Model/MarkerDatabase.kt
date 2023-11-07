package edu.uark.ahnelson.openstreetmapfun.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Marker::class], version = 1, exportSchema = false)
abstract class MarkerDatabase : RoomDatabase() {
    abstract fun markerDao(): MarkerDao

    companion object {
        // Singleton pattern to prevent multiple instances of the database opening at the same time.
        @Volatile
        private var INSTANCE: MarkerDatabase? = null

        fun getDatabase(context: Context, applicationScope: CoroutineScope): MarkerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MarkerDatabase::class.java,
                    "marker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

