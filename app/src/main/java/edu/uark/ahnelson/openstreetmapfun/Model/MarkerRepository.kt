package edu.uark.ahnelson.openstreetmapfun.Model

import kotlinx.coroutines.flow.Flow

class MarkerRepository(private val markerDao: MarkerDao) {
    val allMarkers: Flow<List<Marker>> = markerDao.getAllMarkers()

    suspend fun insert(marker: Marker) {
        markerDao.insertMarker(marker)
    }

    suspend fun delete(marker: Marker) {
        markerDao.deleteMarker(marker)
    }

    suspend fun update(marker: Marker) {
        markerDao.updateMarker(marker)
    }

    suspend fun deleteMarkerById(markerId: Int) {
        markerDao.deleteMarkerById(markerId)
    }
}

