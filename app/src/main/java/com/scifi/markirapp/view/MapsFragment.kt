package com.scifi.markirapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.scifi.markirapp.BuildConfig
import com.scifi.markirapp.R
import com.scifi.markirapp.data.ParkingLocation
import com.scifi.markirapp.databinding.FragmentMapsBinding

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private lateinit var parkingViewModel: ParkingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parkingViewModel = ViewModelProvider(requireActivity())[ParkingViewModel::class.java]
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        context?.let {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
            Places.initialize(it, BuildConfig.API_KEY)
            placesClient = Places.createClient(it)
        }

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateLocation(location)
                }
            }
        }

        binding.fabLocation.setOnClickListener {
            getMyLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()
        setMapStyle()
    }

    private fun setMapStyle() {
        if (isAdded && context != null) {
            try {
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.maps_style
                    )
                )
            } catch (exception: Resources.NotFoundException) {
                exception.printStackTrace()
            }
        }
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = context?.let { ResourcesCompat.getDrawable(it.resources, id, null) }
            ?: return BitmapDescriptorFactory.defaultMarker()
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (context == null || !isAdded) return

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
                        updateLocation(location)
                    }
                }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun updateLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        addParkingMarker()
    }

    private fun addParkingMarker() {
        if (!isAdded || context == null) return

        val request = FindCurrentPlaceRequest.newInstance(
            listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES)
        )

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("addParkingMarker", "Location permissions are not granted")
            return
        }

        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response: FindCurrentPlaceResponse ->
                if (!isAdded || context == null) return@addOnSuccessListener  // Check again before updating UI
                val parkingLocations = mutableListOf<ParkingLocation>()
                for (placeLikelihood in response.placeLikelihoods) {
                    val place = placeLikelihood.place
                    val placeTypes = listOf(
                        "parking",
                        "shopping_mall",
                        "supermarket",
                        "lodging",
                        "park",
                        "hospital",
                        "train_station",
                        "bus_station",
                        "airport"
                    )

                    if (place.placeTypes?.any { it in placeTypes } == true) {
                        Log.i("addParkingMarker", "Found place: ${place.name}")
                        place.latLng?.let { latLng ->
                            val markerOptions = MarkerOptions()
                                .position(latLng)
                                .title(place.name)
                                .icon(
                                    vectorToBitmap(
                                        R.drawable.parking_icon,
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.primary_blue
                                        )
                                    )
                                )
                            mMap.addMarker(markerOptions)
                            parkingLocations.add(
                                ParkingLocation(
                                    name = place.name ?: "",
                                    latLng = latLng,
                                    slotsAvailable = 10,
                                    distance = "100m",
                                    imageUrl = place.iconUrl?.toString() ?: "",
                                )
                            )
                        }
                    }
                }
                parkingViewModel.setParkingLocations(parkingLocations)
            }
            .addOnFailureListener { exception: Exception ->
                Log.e("addParkingMarker", "Error finding current place", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _binding = null
    }
}
