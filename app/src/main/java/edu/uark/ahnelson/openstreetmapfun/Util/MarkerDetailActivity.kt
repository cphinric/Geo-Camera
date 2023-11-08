package edu.uark.ahnelson.openstreetmapfun.Util

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.uark.ahnelson.openstreetmapfun.MapsActivity.MarkerViewModel
import edu.uark.ahnelson.openstreetmapfun.R

// MarkerDetailActivity.kt
class MarkerDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: MarkerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_detail)

        findViewById<FloatingActionButton>(R.id.fabBack).setOnClickListener{
            finish()
        }

        val imagePath = intent.getStringExtra("IMAGE_PATH")
        val description = intent.getStringExtra("DESCRIPTION")

        val imageView: ImageView = findViewById(R.id.marker_image)
        val descriptionView: TextView = findViewById(R.id.marker_description)

        // Load the image into the ImageView. You might use a library like Glide or Picasso.
        Glide.with(this).load(imagePath).into(imageView)


        // Set the description text
        descriptionView.text = description
    }
}
