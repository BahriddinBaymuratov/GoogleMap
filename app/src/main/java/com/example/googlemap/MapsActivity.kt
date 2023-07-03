package com.example.googlemap

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.googlemap.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class MapsActivity() : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private var locationPermissionGranted = false
    private val TAG = "MapsActivity"
    private var lastKnownLocation: Location? = null

    private val COLOR_BLACK_ARGB = -0x1000000
    private val COLOR_GREEN_ARGB = -0xFF03DAC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Places.initialize(applicationContext, getString(R.string.api_key))
        placesClient = Places.createClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)

    }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                locationPermissionGranted = true
                updateLocationUi()
                getDeviceLocation()

            }
        }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID  // googledan koring  // google map type android
        updateLocationUi()
        getDeviceLocation()


//        val sydney = LatLng(-34.0, 151.0)  // 40.753848, 72.357554
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//
//        val polyline1 = googleMap.addPolyline(  // addPolygon
//            PolylineOptions()  // PolygonOptions
//                .clickable(true)
//                .add(
//                    LatLng(40.752053, 72.363508),
//                    LatLng(40.750470, 72.359723),
//                    LatLng(40.753891, 72.357262),
//                    LatLng(40.755541, 72.361053),
//                )
//        )
//        polyline1.tag = "B"
//        stylePolyline(polyline1)
//
//        // appga kirganda camera shu kordinataga borib qoladi
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(40.757820, 72.348827), 10f))
//        // Bosilganini eshtib turadi
//        googleMap.setOnPolylineClickListener(this)
//        googleMap.setOnPolygonClickListener(this)
    }


    private fun stylePolyline(polyline: Polyline) {
        // Get the data object stored with the polyline.
        val type = polyline.tag?.toString() ?: ""
        when (type) {
            "A" -> {
                polyline.startCap = CustomCap(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background),
                    25f
                )
            }
            "B" -> {
                polyline.startCap = RoundCap()
            }
        }
        polyline.endCap = RoundCap()
        polyline.width = 12.toFloat()
        polyline.color = COLOR_GREEN_ARGB
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), 8.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null, Using defaults ")
                        Log.d(TAG, "Exception: ${task.exception} ")
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    40.752053,
                                    72.363508
                                ), 9.toFloat()
                            )
                        )
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.d(TAG, "Exception: ${e.message}")
        }
    }

    private fun updateLocationUi() {
        if (mMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } catch (e: SecurityException) {
            Log.d(TAG, "Exception: ${e.message}")
        }
    }

    override fun onPolylineClick(p0: Polyline) {
        Toast.makeText(this, "${p0.points}", Toast.LENGTH_SHORT).show()
    }

    override fun onPolygonClick(p0: Polygon) {
        Toast.makeText(this, "Bosmaaaa", Toast.LENGTH_SHORT).show()
    }
}