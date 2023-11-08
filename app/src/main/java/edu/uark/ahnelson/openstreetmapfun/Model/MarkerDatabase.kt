package edu.uark.ahnelson.openstreetmapfun.Model

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Marker::class], version = 2, exportSchema = false)
abstract class MarkerDatabase : RoomDatabase() {
    abstract fun markerDao(): MarkerDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        // Override the onCreate method to populate the database when it's created
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("Database", "Here1")
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: MarkerDatabase? = null

        // Create or get the database instance
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): MarkerDatabase {
            // If the INSTANCE is not null, return it; otherwise, create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MarkerDatabase::class.java,
                    "word_database"
                )
                    .addMigrations(migration1to2) // Add migrations here
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration to update the database schema from version 1 to 2
        private val migration1to2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Recreate the table with the correct column order
                database.execSQL(
                    "CREATE TABLE word_table_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "word TEXT NOT NULL, quantity INTEGER NOT NULL, description TEXT)"
                )
                // Copy the data from the old table to the new table
                database.execSQL(
                    "INSERT INTO word_table_new (id, word, quantity, description) SELECT id, word, quantity, description FROM word_table"
                )
                // Remove the old table
                database.execSQL("DROP TABLE word_table")
                // Change the table name to the correct name
                database.execSQL("ALTER TABLE word_table_new RENAME TO word_table")
            }
        }
    }
}

