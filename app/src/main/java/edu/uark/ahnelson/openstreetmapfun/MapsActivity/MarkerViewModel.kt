package edu.uark.ahnelson.openstreetmapfun.MapsActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.uark.ahnelson.openstreetmapfun.Model.MarkerRepository
import edu.uark.ahnelson.openstreetmapfun.Model.Marker
import kotlinx.coroutines.launch
class MarkerViewModel(private val repository: MarkerRepository) : ViewModel() {

    // Factory for creating MarkerViewModel
    class Factory(private val repository: MarkerRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MarkerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MarkerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

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

    fun getMarkerDataById(markerId: Int): LiveData<Marker?> {
        val data = MutableLiveData<Marker?>()

        // Assuming you're using coroutines to fetch data asynchronously
        viewModelScope.launch {
            // Post the value to LiveData once it's fetched
            data.postValue(repository.getMarkerDataById(markerId))
        }

        return data
    }

}



