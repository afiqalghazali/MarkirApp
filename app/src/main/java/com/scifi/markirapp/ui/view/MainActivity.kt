package com.scifi.markirapp.ui.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.scifi.markirapp.BuildConfig
import com.scifi.markirapp.R
import com.scifi.markirapp.databinding.ActivityMainBinding
import com.scifi.markirapp.ui.viewmodel.LocationViewModel
import com.scifi.markirapp.utils.AppsUtils
import com.scifi.markirapp.utils.FirebaseAuthUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuthUtils.instance }
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val locationViewModel by viewModels<LocationViewModel>()

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) startLocationUpdates()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupBottomNavigation()
        setupLocationServices()

        if (savedInstanceState == null) {
            if (!AppsUtils.isNetworkAvailable(this)) {
                AppsUtils.showAlert(
                    this,
                    "No Internet Connection.",
                    isWarning = true,
                    primaryButtonText = "OK",
                    onPrimaryButtonClick = {
                        startActivity(Intent(this, WelcomeActivity::class.java))
                        finish()
                    }
                )
            } else {
                binding.bottomNavigation.selectedItemId = R.id.nav_maps

                val firebaseUser = auth.currentUser
                if (firebaseUser == null) {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                    return
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setupAction() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            FirebaseAuthUtils.sessionEndedAlert(this)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_maps -> {
                    replaceFragment(MapsFragment())
                    true
                }

                R.id.nav_park -> {
                    replaceFragment(ParkingFragment())
                    true
                }

                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(this, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateDistanceMeters(50f)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    val newLocation = LatLng(location.latitude, location.longitude)
                    locationViewModel.updateLocation(newLocation)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val newLocation = LatLng(it.latitude, it.longitude)
                    locationViewModel.updateLocation(newLocation)
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
        stopLocationUpdates()
    }
}