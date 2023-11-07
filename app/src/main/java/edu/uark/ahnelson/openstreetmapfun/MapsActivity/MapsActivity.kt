package edu.uark.ahnelson.openstreetmapfun.MapsActivity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.uark.ahnelson.openstreetmapfun.MarkerApplication
import edu.uark.ahnelson.openstreetmapfun.R
import edu.uark.ahnelson.openstreetmapfun.TakeShowPictureActivity.TakeShowPictureActivity
import edu.uark.ahnelson.openstreetmapfun.Util.LocationUtilCallback
import edu.uark.ahnelson.openstreetmapfun.Util.createLocationCallback
import edu.uark.ahnelson.openstreetmapfun.Util.createLocationRequest
import edu.uark.ahnelson.openstreetmapfun.Util.replaceFragmentInActivity
import androidx.lifecycle.ViewModelProvider
import edu.uark.ahnelson.openstreetmapfun.MapsActivity.MarkerViewModel
import edu.uark.ahnelson.openstreetmapfun.MapsActivity.MarkerViewModelFactory
import edu.uark.ahnelson.openstreetmapfun.Model.Marker


import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

class MapsActivity : AppCompatActivity() {
    private lateinit var mapsFragment: OpenStreetMapFragment
    private var numMarkers:Int = 0

    //Boolean to keep track of whether permissions have been granted
    private var locationPermissionEnabled: Boolean = false

    //Boolean to keep track of whether activity is currently requesting location Updates
    private var locationRequestsEnabled: Boolean = false

    //Member object for the FusedLocationProvider
    private lateinit var locationProviderClient: FusedLocationProviderClient

    //Member object for the last known location
    private lateinit var mCurrentLocation: Location

    //Member object to hold onto locationCallback object
    //Needed to remove requests for location updates
    private lateinit var mLocationCallback: LocationCallback

    private val markerViewModel: MarkerViewModel by viewModels {
        MarkerViewModelFactory((application as MarkerApplication).repository)
    }

    val takePictureResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode == RESULT_CANCELED){
            Log.d("MainActivity","Take Picture Activity Cancelled")
        }else {
            Log.d("MainActivity", "Picture Taken")

            val takeShowPictureActivityIntent: Intent =
                Intent(this, TakeShowPictureActivity::class.java)
            takeShowPictureActivityIntent.putExtra(
                "GEOPHOTO_LOC",
                result.data?.getStringExtra("GEOPHOTO_LOC")
            )
            takeShowPictureActivityIntent.putExtra("GEOPHOTO_ID", 10)

            val imagePath = result.data?.getStringExtra("IMAGE_PATH")
            val description = result.data?.getStringExtra("DESCRIPTION")
            val deleteMarkerId = result.data?.getIntExtra("DELETE_MARKER_ID", -1)
            if (deleteMarkerId != null && deleteMarkerId != -1) {
                // Delete the marker from the database
                markerViewModel.deleteMarkerById(deleteMarkerId)
            }
            if (imagePath != null && description != null) {
                drawMarker(mCurrentLocation, imagePath, description)
                //startActivity(takeShowPictureActivityIntent)
            }
        }

    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            //If successful, startLocationRequests
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationPermissionEnabled = true
                startLocationRequests()
            }
            //If successful at coarse detail, we still want those
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationPermissionEnabled = true
                startLocationRequests()
            }

            else -> {
                //Otherwise, send toast saying location is not enabled
                locationPermissionEnabled = false
                Toast.makeText(this, "Location Not Enabled", Toast.LENGTH_LONG)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener{
            takeNewPhoto()
        }

        markerViewModel.allMarkers.observe(this) { markers ->
            // Update your UI here with the list of markers
        }

        //Get preferences for tile cache
        Configuration.getInstance().load(this, getSharedPreferences(
            "${packageName}_preferences", MODE_PRIVATE
        ))
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //Check for location permissions
        checkForLocationPermission()

        //Get access to mapsFragment object
        mapsFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                as OpenStreetMapFragment? ?:OpenStreetMapFragment.newInstance().also{
            replaceFragmentInActivity(it,R.id.fragmentContainerView)
        }
        observeMarkers()
    }
    private fun observeMarkers() {
        markerViewModel.allMarkers.observe(this) { markers ->
            // Log the markers for debugging
            Log.d("MapsActivity", "Markers: $markers")

            // Update the map with markers
            markers.forEach { marker ->
                val geoPoint = GeoPoint(marker.latitude, marker.longitude)
                mapsFragment.addMarker(geoPoint, marker.id)
            }
        }
    }
    private fun takeNewPhoto(){
        val takeShowPictureActivityIntent: Intent = Intent(this,TakeShowPictureActivity::class.java)
        takePictureResultLauncher.launch(takeShowPictureActivityIntent)
    }
    private fun checkForLocationPermission(){
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationRequests()
                //getLastKnownLocation()
                //registerLocationUpdateCallbacks()
            }
            else -> {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
        }
    }

    private val locationUtilCallback = object : LocationUtilCallback {
        //If locationUtil request fails because of permission issues
        //Ask for permissions
        override fun requestPermissionCallback() {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        override fun locationUpdatedCallback(location: Location) {
            mCurrentLocation = location
            mapsFragment.changeCenterLocation(GeoPoint(location.latitude,location.longitude))
            Log.d(
                "MainActivity",
                "Location is [Lat: ${location.latitude}, Long: ${location.longitude}]"
            )
        }
    }
    private fun startLocationRequests() {
        //If we aren't currently getting location updates
        if (!locationRequestsEnabled) {
            //create a location callback
            mLocationCallback = createLocationCallback(locationUtilCallback)
            //and request location updates, setting the boolean equal to whether this was successful
            locationRequestsEnabled =
                createLocationRequest(this, locationProviderClient, mLocationCallback)
        }
    }
    private fun drawMarker(location: Location, imagePath: String, description: String) {
        val geoPoint = GeoPoint(location.latitude, location.longitude)
        val markerId = 1

        // Call the addMarker function
        mapsFragment.addMarker(geoPoint, markerId)

        // Create a new Marker entity
        val newMarker = Marker(
            latitude = location.latitude,
            longitude = location.longitude,
            imagePath = imagePath,
            description = description
        )

        // Insert the new marker into the database
        markerViewModel.insert(newMarker)

        // Optional: Increment numMarkers if you are using it to track the number of markers
        numMarkers++
    }
}