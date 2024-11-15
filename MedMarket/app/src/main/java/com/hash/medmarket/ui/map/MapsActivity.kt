package com.hash.medmarket.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hash.medmarket.R
import com.hash.medmarket.databinding.ActivityMapsBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var locationPick: Location? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationMarker: Marker? = null
    var isSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        showLocationUpdate()
        setClicks()
    }

    private fun setClicks() {

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.doneBtn.setOnClickListener {
            try {
                if (locationPick != null) {

                    locationPick = Location("")
                    locationPick!!.latitude = locationMarker!!.position.latitude
                    locationPick!!.longitude = locationMarker!!.position.longitude

                    val resultIntent = Intent()
                    resultIntent.putExtra(
                        "LOCATION",
                        locationPick!!.latitude.toString() + "," + locationPick!!.longitude
                    )
                    setResult(RESULT_OK, resultIntent)
                    finish()

                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Please turn on your location to select location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (ex: Exception) {
                Toast.makeText(this@MapsActivity, "Location fetching error!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showLocationUpdate() {

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@MapsActivity);

        Dexter.withContext(this@MapsActivity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    requestLocationUpdates()
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                    Toast.makeText(this@MapsActivity, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest?, permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).check()

    }

    private fun requestLocationUpdates() {
        if (isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()
            )
        } else {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            enableLocationLauncher.launch(intent)
        }
    }

    private var locationRequest =
        LocationRequest.create().setInterval(10000).setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
        }

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            Log.i("Setting", locationResult.toString() + "")

            if (!isSet) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        locationPick = Location(location)
                        val latLng = LatLng(location.latitude, location.longitude)
                        addMarker(latLng)
                        break
                    }
                }
            }
        }
    }

    private fun addMarker(latLng: LatLng) {
        if (locationMarker != null) {
            locationMarker!!.remove()
        }

        isSet = true
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.draggable(true)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        locationMarker = mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return locationManager.isLocationEnabled
        } else {
            val mode: Int = Settings.Secure.getInt(
                contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF
            )
            return mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }

    private val enableLocationLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (isLocationEnabled()) {
            requestLocationUpdates()
        } else {
            Toast.makeText(
                this@MapsActivity, "Please enable location services", Toast.LENGTH_SHORT
            ).show()
        }
    }

}