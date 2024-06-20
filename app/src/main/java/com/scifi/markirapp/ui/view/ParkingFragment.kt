package com.scifi.markirapp.ui.view

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.scifi.markirapp.data.model.ParkingLocation
import com.scifi.markirapp.databinding.FragmentParkingBinding
import com.scifi.markirapp.ui.adapter.ParkingAdapter
import com.scifi.markirapp.ui.viewmodel.LocationViewModel
import com.scifi.markirapp.ui.viewmodel.ParkingViewModel
import com.scifi.markirapp.ui.viewmodel.SlotsViewModel


class ParkingFragment : Fragment() {

    private var _binding: FragmentParkingBinding? = null
    private val binding get() = _binding!!
    private lateinit var parkingAdapter: ParkingAdapter
    private val parkingViewModel by activityViewModels<ParkingViewModel>()
    private val locationViewModel by activityViewModels<LocationViewModel>()
    private val slotsViewModel by activityViewModels<SlotsViewModel>()
    private var currentLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentParkingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        observeLocation()
        observeParkingSlots()
        slotsViewModel.fetchParkingSlots()

        binding.swipeRefresh.setOnRefreshListener {
            slotsViewModel.fetchParkingSlots()
        }

        slotsViewModel.parkingSlots.observe(viewLifecycleOwner) { parkingSlots ->
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupRecyclerView() {
        parkingAdapter = ParkingAdapter(listOf())
        binding.rvPark.layoutManager = LinearLayoutManager(context)
        binding.rvPark.adapter = parkingAdapter
    }

    private fun observeData() {
        parkingViewModel.parkingLocations.observe(viewLifecycleOwner) { locations ->
            updateLocations(locations)
        }
    }

    private fun observeLocation() {
        locationViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            currentLocation = location
            val locations = parkingViewModel.parkingLocations.value
            if (locations != null) {
                updateLocations(locations)
            }
        }
    }

    private fun observeParkingSlots() {
        slotsViewModel.parkingSlots.observe(viewLifecycleOwner) {
            val locations = parkingViewModel.parkingLocations.value
            if (locations != null) {
                updateLocations(locations)
            }
        }
    }

    private fun updateLocations(locations: List<ParkingLocation>) {
        if (currentLocation != null) {
            val updatedLocations = locations.map { location ->
                location.copy(
                    distance = calculateDistance(currentLocation!!, location.latLng!!),
                    parkingSlots = slotsViewModel.parkingSlots.value
                )
            }
            parkingAdapter.updateData(updatedLocations)
        } else {
            val updatedLocations = locations.map { location ->
                location.copy(
                    parkingSlots = slotsViewModel.parkingSlots.value
                )
            }
            parkingAdapter.updateData(updatedLocations)
        }
        binding.viewEmpty.visibility = if (locations.isEmpty()) View.VISIBLE else View.GONE
        binding.rvPark.visibility = if (locations.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun calculateDistance(userLocation: LatLng, placeLatLng: LatLng): Float {
        val userLocationObj = Location("").apply {
            latitude = userLocation.latitude
            longitude = userLocation.longitude
        }
        val placeLocation = Location("").apply {
            latitude = placeLatLng.latitude
            longitude = placeLatLng.longitude
        }
        return userLocationObj.distanceTo(placeLocation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}