package edu.uark.ahnelson.openstreetmapfun.Model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkerDao {
    // Insert a new marker into the database
    @Insert
    suspend fun insertMarker(marker: Marker)

    // Update an existing marker in the database
    @Update
    suspend fun updateMarker(marker: Marker)

    // Delete a specific marker from the database
    @Delete
    suspend fun deleteMarker(marker: Marker)

    //Delete all tasks
    @Query("DELETE FROM marker_table")
    suspend fun deleteAll()

    // Retrieve a list of all markers from the database using Flow
    @Query("SELECT * FROM marker_table")
    fun getAllMarkers(): Flow<List<Marker>>

    // Retrieve a specific marker by its unique ID using Flow
    @Query("SELECT * FROM marker_table WHERE id = :id")
    fun getMarkerById(id: Int): Flow<Marker>
}


