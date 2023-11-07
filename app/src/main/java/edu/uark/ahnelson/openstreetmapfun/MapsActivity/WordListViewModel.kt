package edu.uark.ahnelson.openstreetmapfun.MapsActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.uark.ahnelson.openstreetmapfun.Model.MarkerRepository
import edu.uark.ahnelson.openstreetmapfun.Model.Marker
import kotlinx.coroutines.launch

// ViewModel for managing the list of Markers displayed in the UI
class WordListViewModel(private val repository: MarkerRepository) : ViewModel() {

    // LiveData for observing the list of all words from the repository
    val allMarkers: LiveData<List<Marker>> = repository.allMarkers.asLiveData()

    // Function to insert a marker to the repository
    fun insertMarker(marker:Marker) {
        viewModelScope.launch {
            repository.insertMarker(marker)
        }
    }

    // Function to delete a marker from the repository
    fun deleteMarker(marker: Marker) {
        viewModelScope.launch {
            repository.deleteMarker(marker)
        }
    }
}

// Factory for creating the WordListViewModel
class WordListViewModelFactory(private val repository: MarkerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
