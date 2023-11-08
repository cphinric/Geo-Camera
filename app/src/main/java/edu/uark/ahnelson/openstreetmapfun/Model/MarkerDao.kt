package edu.uark.ahnelson.openstreetmapfun.Model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkerDao {
    @Insert
    suspend fun insertMarker(marker: Marker)

    @Delete
    suspend fun deleteMarker(marker: Marker)

    @Update
    suspend fun updateMarker(marker: Marker)

    @Query("SELECT * FROM marker_table")
    fun getAllMarkers(): Flow<List<Marker>>

    @Query("SELECT * FROM marker_table WHERE id = :markerId")
    suspend fun getMarkerById(markerId: Int): Marker?

    @Query("DELETE FROM marker_table WHERE id = :markerId")
    suspend fun deleteMarkerById(markerId: Int)

    @Query("SELECT * FROM marker_table ORDER BY timestamp DESC")
    fun getAllMarkersByTimestamp(): LiveData<List<Marker>>
}



