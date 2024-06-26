package com.scifi.markirapp.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.PolyUtil
import com.scifi.markirapp.BuildConfig
import com.scifi.markirapp.R
import com.scifi.markirapp.data.model.ParkingLocation
import com.scifi.markirapp.data.network.MapsApiConfig
import com.scifi.markirapp.databinding.FragmentMapsBinding
import com.scifi.markirapp.ui.custom.CustomInfoView
import com.scifi.markirapp.ui.viewmodel.LocationViewModel
import com.scifi.markirapp.ui.viewmodel.ParkingViewModel
import com.scifi.markirapp.utils.AppsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val locationViewModel by activityViewModels<LocationViewModel>()
    private val parkingViewModel by activityViewModels<ParkingViewModel>()
    private var currentLocation: LatLng? = null
    private var mapFragment: SupportMapFragment? = null

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
        if (isAdded) {
            setupMap()
            setupAction()
        }
        locationViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            location?.let {
                currentLocation = it
                updateLocation(it)
            }
        }
    }

    private fun setupMap() {
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().replace(R.id.map, mapFragment!!).commit()
            mapFragment?.getMapAsync(this)
        }
    }

    private fun setupAction() {
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setHint("Search your destination")
        autocompleteFragment.setCountries("ID")
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { latLng ->
                    updateMapWithPlace(latLng, place.name)
                    currentLocation?.let { currentLatLng ->
                        getDirections(currentLatLng, latLng)
                    }
                    binding.fabClear.visibility = View.VISIBLE
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {}
        })

        binding.apply {
            fabLocation.setOnClickListener { getMyLocation() }
            fabTraffic.setOnClickListener { mMap.isTrafficEnabled = !mMap.isTrafficEnabled }
            fabClear.setOnClickListener {
                mMap.clear()
                addParkingMarker()
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, 16f))
                binding.fabClear.visibility = View.GONE
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false
        } else {
            (activity as MainActivity).requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setMapStyle()
        setInfoWindow()
        getMyLocation()
    }

    private fun setMapStyle() {
        context?.let {
            var isDarkMode = false
            try {
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(it, R.raw.maps_style_light))
            } catch (e: Resources.NotFoundException) {
                AppsUtils.showAlert(
                    context = it,
                    message = "Light style not found"
                )
                e.printStackTrace()
            }

            binding.fabDark.setOnClickListener {
                try {
                    isDarkMode = !isDarkMode
                    val styleRes = if (isDarkMode) R.raw.maps_style_dark else R.raw.maps_style_light
                    mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            requireContext(),
                            styleRes
                        )
                    )
                } catch (e: Resources.NotFoundException) {
                    val mode = if (isDarkMode) "Dark" else "Light"
                    AppsUtils.showAlert(
                        context = requireContext(),
                        message = "$mode style not found"
                    )
                    e.printStackTrace()
                }
            }
        }
    }


    @SuppressLint("PotentialBehaviorOverride")
    private fun setInfoWindow() {
        val customInfoView = CustomInfoView(requireContext())
        mMap.setInfoWindowAdapter(customInfoView)
        mMap.setOnInfoWindowClickListener { marker ->
            if (marker == customInfoView.currentMarker) {
                customInfoView.currentMarker!!.hideInfoWindow()
                currentLocation?.let { currentLatLng ->
                    getDirections(currentLatLng, marker.position)
                }
                val selectedMarkerOptions = MarkerOptions()
                    .position(marker.position)
                    .title(marker.title)
                    .icon(
                        AppsUtils.vectorToBitmap(
                            R.drawable.parking_icon,
                            requireContext(),
                            60,
                            60
                        )
                    )
                mMap.clear()
                mMap.addMarker(selectedMarkerOptions)
                animateCameraToBounds(marker.position)
                binding.fabClear.visibility = View.VISIBLE
            }
        }
    }

    private fun getMyLocation() {
        if (this::mMap.isInitialized) {
            currentLocation?.let { updateLocation(it) }
        }
    }

    private fun updateLocation(location: LatLng) {
        if (this::mMap.isInitialized) {
            currentLocation = location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
            addParkingMarker()
        }
    }

    private fun addParkingMarker() {
        if (!isAdded || context == null) return

        val userLocation = currentLocation?.let { LatLng(it.latitude, it.longitude) } ?: return
        val apiKey = BuildConfig.PLACES_API_KEY
        val radius = 10000

        CoroutineScope(Dispatchers.IO).launch {
            val placesService = MapsApiConfig.getMapsApiService()
            val parkingLocations = mutableListOf<ParkingLocation>()

            val query = "Mall OR Plaza"
            val response = placesService.getPlaces(
                query,
                "${userLocation.latitude},${userLocation.longitude}",
                radius,
                apiKey
            )

            if (response.isSuccessful) {
                response.body()?.results?.forEach { place ->
                    val location = place.geometry.location
                    val latLng = LatLng(location.latitude, location.longitude)
                    val distance = FloatArray(1)
                    Location.distanceBetween(
                        userLocation.latitude,
                        userLocation.longitude,
                        latLng.latitude,
                        latLng.longitude,
                        distance
                    )
                    if (distance[0] <= radius && (place.name.contains(
                            "Mall",
                            ignoreCase = true
                        ) || place.name.contains("Plaza", ignoreCase = true))
                    ) {
                        if (isAdded) {
                            withContext(Dispatchers.Main) {
                                addMarkerForPlace(latLng, place.name)
                            }
                            val parkingLocation = createParkingLocation(
                                place,
                                latLng
                            )
                            parkingLocations.add(parkingLocation)
                        }
                    }
                }
            } else {
                if (isAdded) {
                    withContext(Dispatchers.Main) {
                        AppsUtils.showAlert(
                            context = requireContext(),
                            message = "Error getting places"
                        )
                    }
                }
            }

            if (isAdded) {
                withContext(Dispatchers.Main) {
                    parkingViewModel.setParkingLocations(parkingLocations)
                }
            }
        }
    }


    private fun createParkingLocation(
        place: com.scifi.markirapp.data.network.response.Place,
        latLng: LatLng,
    ): ParkingLocation {
        val photoReference = place.photos?.firstOrNull()?.photoReference
        val imageUrl = if (photoReference != null) {
            val apiKey = BuildConfig.PLACES_API_KEY
            val maxWidth = 700
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth&photoreference=$photoReference&key=$apiKey"
        } else {
            ""
        }

        return ParkingLocation(
            placeId = place.placeId,
            name = place.name,
            latLng = latLng,
            imageUrl = imageUrl
        )
    }

    private fun addMarkerForPlace(latLng: LatLng, placeName: String?) {
        if (isAdded) {
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(placeName)
                .icon(
                    AppsUtils.vectorToBitmap(
                        R.drawable.parking_icon,
                        requireContext(),
                        60,
                        60
                    )
                )
            mMap.addMarker(markerOptions)
        }
    }

    private fun updateMapWithPlace(latLng: LatLng, placeName: String?) {
        if (isAdded) {
            mMap.clear()
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(placeName)
                .icon(
                    AppsUtils.vectorToBitmap(
                        R.drawable.baseline_location_pin_24,
                        requireContext(),
                        100,
                        100
                    )
                )
            mMap.addMarker(markerOptions)
            animateCameraToBounds(latLng)
        }
    }

    private fun animateCameraToBounds(markerLatLng: LatLng) {
        if (isAdded) {
            currentLocation?.let { currentLatLng ->
                val bounds = LatLngBounds.builder()
                    .include(currentLatLng)
                    .include(markerLatLng)
                    .build()
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
    }

    private fun getDirections(origin: LatLng, destination: LatLng) {
        val originLatLng = "${origin.latitude},${origin.longitude}"
        val destinationLatLng = "${destination.latitude},${destination.longitude}"
        val apiKey = BuildConfig.DIRECTIONS_API_KEY

        CoroutineScope(Dispatchers.IO).launch {
            val directionsService = MapsApiConfig.getMapsApiService()
            val response = directionsService.getDirections(originLatLng, destinationLatLng, apiKey)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && isAdded) {
                    response.body()?.let { directionsResponse ->
                        if (isAdded) {
                            val route = directionsResponse.routes.firstOrNull()
                            if (route != null) {
                                val polyline = route.overview_polyline.points
                                val decodedPath = PolyUtil.decode(polyline)
                                if (isAdded) {
                                    mMap.addPolyline(
                                        PolylineOptions()
                                            .addAll(decodedPath)
                                            .color(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.secondary_blue
                                                )
                                            )
                                    )
                                }
                            } else {
                                AppsUtils.showAlert(
                                    context = requireContext(),
                                    message = "No routes found"
                                )
                            }
                        }
                    }
                } else if (isAdded) {
                    AppsUtils.showAlert(
                        context = requireContext(),
                        message = "Error getting directions"
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mapFragment = null
    }
}



