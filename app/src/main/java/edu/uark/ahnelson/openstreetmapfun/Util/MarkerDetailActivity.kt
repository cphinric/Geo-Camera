package edu.uark.ahnelson.openstreetmapfun.Util

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.uark.ahnelson.openstreetmapfun.MapsActivity.MarkerViewModel
import edu.uark.ahnelson.openstreetmapfun.Model.MarkerDatabase
import edu.uark.ahnelson.openstreetmapfun.Model.MarkerRepository
import edu.uark.ahnelson.openstreetmapfun.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

// MarkerDetailActivity.kt
class MarkerDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: MarkerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_detail)
        val applicationScope = CoroutineScope(SupervisorJob())

        // Obtain the MarkerDao from the Room database instance
        val markerDao = MarkerDatabase.getDatabase(application, applicationScope).markerDao()
        // Create the repository instance
        val repository = MarkerRepository(markerDao)
        // Create a ViewModelFactory instance
        val factory = MarkerViewModel.Factory(repository)
        // Initialize the ViewModel using the factory
        viewModel = ViewModelProvider(this, factory)[MarkerViewModel::class.java]

        findViewById<FloatingActionButton>(R.id.fabBack).setOnClickListener{
            finish()
        }

        val imagePath = intent.getStringExtra("IMAGE_PATH")
        val description = intent.getStringExtra("DESCRIPTION")

        val imageView: ImageView = findViewById(R.id.marker_image)
        val descriptionView: TextView = findViewById(R.id.marker_description)

        // ... other code ...

        val timestamp = intent.getLongExtra("TIMESTAMP", 0L) // Retrieve the timestamp

        val textViewTimestamp: TextView = findViewById(R.id.marker_timestamp) // Make sure you have a TextView with this ID in your layout

// Format and set the timestamp text
        textViewTimestamp.text = viewModel.getFormattedDate(timestamp)

// ... other code ...


        // Load the image into the ImageView. You might use a library like Glide or Picasso.
        Glide.with(this).load(imagePath).into(imageView)


        // Set the description text
        descriptionView.text = description
    }
}
