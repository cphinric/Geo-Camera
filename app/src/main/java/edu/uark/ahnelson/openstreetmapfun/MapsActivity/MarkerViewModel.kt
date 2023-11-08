package edu.uark.ahnelson.openstreetmapfun.MapsActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import edu.uark.ahnelson.openstreetmapfun.Model.MarkerRepository
import edu.uark.ahnelson.openstreetmapfun.Model.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    fun getFormattedDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    fun getMarkerDataById(markerId: Int): LiveData<Marker?> {
        val data = MutableLiveData<Marker?>()

        viewModelScope.launch(Dispatchers.IO) { // Use the IO dispatcher for database operations
            val markerData = repository.getMarkerDataById(markerId)
            data.postValue(markerData)
        }

        return data
    }
}



