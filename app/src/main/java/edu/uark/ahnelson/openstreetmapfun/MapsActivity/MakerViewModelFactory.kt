package edu.uark.ahnelson.openstreetmapfun.MapsActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.uark.ahnelson.openstreetmapfun.Model.MarkerRepository

class MarkerViewModelFactory(private val repository: MarkerRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarkerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarkerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
