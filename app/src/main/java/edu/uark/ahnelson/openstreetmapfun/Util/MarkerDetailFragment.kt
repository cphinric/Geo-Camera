package edu.uark.ahnelson.openstreetmapfun.Util

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.uark.ahnelson.openstreetmapfun.MapsActivity.MarkerViewModel
import edu.uark.ahnelson.openstreetmapfun.R

class MarkerDetailFragment : Fragment() {

    private lateinit var viewModel: MarkerViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize your ViewModel here
        viewModel = ViewModelProvider(this).get(MarkerViewModel::class.java)

        // Assuming you've passed the markerId from the previous Fragment/Activity
        val markerId = arguments?.getInt("markerId") ?: return

        viewModel.getMarkerDataById(markerId).observe(viewLifecycleOwner, Observer { marker ->
            // Assuming you have a TextView in your layout for the timestamp
            val textViewTimestamp: TextView = view.findViewById(R.id.marker_timestamp)
            marker?.let {
                // Use the ViewModel's function to format the timestamp
                textViewTimestamp.text = viewModel.getFormattedDate(it.timestamp)
            }
        })
    }
}
