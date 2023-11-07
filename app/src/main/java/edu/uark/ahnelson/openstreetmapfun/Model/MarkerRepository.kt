package edu.uark.ahnelson.openstreetmapfun.Model

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class MarkerRepository(private val markerDao: MarkerDao) {
    // LiveData-like Flow that provides an observable stream of all markers from the database
    val allMarkers: Flow<List<Marker>> = markerDao.getAllMarkers()

    // Get a specific marker by its ID from the database
    fun getMarker(id: Int): Flow<Marker> {
        return markerDao.getMarkerById(id)
    }

    // Insert a new marker into the database
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertMarker(marker: Marker) {
        markerDao.insertMarker(marker)
    }

    // Update an existing marker in the database
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateMarker(marker: Marker) {
        markerDao.updateMarker(marker)
    }

    // Delete a specific marker from the database
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteMarker(marker: Marker) {
        markerDao.deleteMarker(marker)
    }
}

