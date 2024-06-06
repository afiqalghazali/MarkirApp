package com.scifi.markirapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.os.Looper
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
import androidx.fragment.app.activityViewModels
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.scifi.markirapp.BuildConfig
import com.scifi.markirapp.R
import com.scifi.markirapp.data.ParkingLocation
import com.scifi.markirapp.databinding.FragmentMapsBinding
import com.scifi.markirapp.view.utils.InterfaceUtils
import java.lang.ref.WeakReference

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val parkingViewModel by activityViewModels<ParkingViewModel>()
    private var currentLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        setupLocationServices()
        setupAction()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setupLocationServices() {
        context?.let {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
            Places.initialize(it, BuildConfig.API_KEY)
            placesClient = Places.createClient(it)
        }

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
            .setMinUpdateIntervalMillis(5000L)
            .setMinUpdateDistanceMeters(500f)
            .build()

        locationCallback = MapsFragmentLocationCallback(this)
    }

    private fun setupAction() {
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setHint("Search your destination")
        autocompleteFragment.setCountries("ID")
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { latLng ->
                    updateMapWithPlace(latLng, place.name)
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {}
        })

        binding.apply {
            fabLocation.setOnClickListener { getMyLocation() }
            fabTraffic.setOnClickListener { mMap.isTrafficEnabled = !mMap.isTrafficEnabled }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()
        setMapStyle()
    }

    private fun setMapStyle() {
        context?.let {
            try {
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(it, R.raw.maps_style))
            } catch (e: Resources.NotFoundException) {
                InterfaceUtils.showAlert(
                    context = requireContext(),
                    message = "Style not found"
                )
                e.printStackTrace()
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
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) getMyLocation()
        }

    private fun getMyLocation() {
        if (context == null || !isAdded) return

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { updateLocation(it) }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            InterfaceUtils.showAlert(
                context = requireContext(),
                message = "Location permissions are not granted"
            )
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
    }

    private fun updateLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        currentLocation = currentLatLng
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
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
            InterfaceUtils.showAlert(
                context = requireContext(),
                message = "Location permissions are not granted"
            )
            return
        }

        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response ->
                if (!isAdded || context == null) return@addOnSuccessListener

                val userLocation = currentLocation?.let {
                    Location("").apply {
                        latitude = it.latitude
                        longitude = it.longitude
                    }
                }

                val parkingLocations = mutableListOf<ParkingLocation>()
                response.placeLikelihoods.forEach { placeLikelihood ->
                    val place = placeLikelihood.place
                    if (place.placeTypes?.any { it in parkingPlaceTypes } == true) {
                        place.latLng?.let { latLng ->
                            addMarkerForPlace(latLng, place.name)
                            parkingLocations.add(createParkingLocation(place, userLocation, latLng))
                        }
                    }
                }
                parkingViewModel.setParkingLocations(parkingLocations)
            }
            .addOnFailureListener { exception ->
                InterfaceUtils.showAlert(
                    context = requireContext(),
                    message = "Error finding current place + $exception"
                )
            }
    }

    private fun createParkingLocation(place: Place, userLocation: Location?, latLng: LatLng): ParkingLocation {
        val distance = calculateDistance(userLocation, latLng)
        return ParkingLocation(
            name = place.name ?: "",
            latLng = latLng,
            slotsAvailable = 10,
            distance = distance,
            imageUrl = place.iconUrl?.toString() ?: ""
        )
    }

    private fun calculateDistance(userLocation: Location?, placeLatLng: LatLng): String {
        return userLocation?.let {
            val placeLocation = Location("").apply {
                latitude = placeLatLng.latitude
                longitude = placeLatLng.longitude
            }
            val distanceInMeters = it.distanceTo(placeLocation)
            if (distanceInMeters < 1000) "${distanceInMeters.toInt()}m" else "${(distanceInMeters / 1000)}km"
        } ?: "unknown"
    }

    private fun addMarkerForPlace(latLng: LatLng, placeName: String?) {
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(placeName)
            .icon(vectorToBitmap(R.drawable.parking_icon, ContextCompat.getColor(requireContext(), R.color.primary_blue)))
        mMap.addMarker(markerOptions)
    }

    private fun updateMapWithPlace(latLng: LatLng, placeName: String?) {
        mMap.clear()
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(placeName)
            .icon(vectorToBitmap(R.drawable.baseline_location_pin_24, ContextCompat.getColor(requireContext(), R.color.primary_blue)))
        mMap.addMarker(markerOptions)
        addParkingMarker()
        animateCameraToBounds(latLng)
    }

    private fun animateCameraToBounds(markerLatLng: LatLng) {
        currentLocation?.let { currentLatLng ->
            val bounds = LatLngBounds.builder()
                .include(currentLatLng)
                .include(markerLatLng)
                .build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _binding = null
    }

    private class MapsFragmentLocationCallback(fragment: MapsFragment) : LocationCallback() {
        private val fragmentRef = WeakReference(fragment)

        override fun onLocationResult(locationResult: LocationResult) {
            fragmentRef.get()?.let { fragment ->
                if (fragment.isAdded) {
                    locationResult.locations.forEach { fragment.updateLocation(it) }
                }
            }
        }
    }

    companion object {
        private val parkingPlaceTypes = listOf(
            "parking", "shopping_mall", "supermarket", "lodging", "park",
            "hospital", "train_station", "bus_station", "airport"
        )
    }
}


