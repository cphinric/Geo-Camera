package edu.uark.ahnelson.openstreetmapfun

import android.app.Application
import edu.uark.ahnelson.openstreetmapfun.Model.MarkerRepository
import edu.uark.ahnelson.openstreetmapfun.Model.MarkerDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MarkerApplication : Application() {
    // Define a coroutine scope for background tasks
    val applicationScope = CoroutineScope(SupervisorJob())

    // Create the database lazily so it's only created when needed
    val database by lazy { MarkerDatabase.getDatabase(this, applicationScope) }

    // Create the repository lazily, using the database's DAO
    val repository by lazy { MarkerRepository(database.markerDao()) }
}