package edu.uark.ahnelson.openstreetmapfun.MapsActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.uark.ahnelson.openstreetmapfun.Model.MarkerRepository
import edu.uark.ahnelson.openstreetmapfun.Model.Marker
import kotlinx.coroutines.launch
class MarkerViewModel(private val repository: MarkerRepository) : ViewModel() {

    val allMarkers: LiveData<List<Marker>> = repository.allMarkers.asLiveData()

    fun insert(marker: Marker) = viewModelScope.launch {
        repository.insert(marker)
    }

    fun delete(marker: Marker) = viewModelScope.launch {
        repository.delete(marker)
    }

    fun update(marker: Marker) = viewModelScope.launch {
        repository.update(marker)
    }

    fun deleteMarkerById(markerId: Int) = viewModelScope.launch {
        repository.deleteMarkerById(markerId)
    }
}


