package com.scifi.markirapp.ui.view

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.scifi.markirapp.data.model.ParkingLocation
import com.scifi.markirapp.databinding.FragmentProfileBinding
import com.scifi.markirapp.ui.adapter.ParkingAdapter
import com.scifi.markirapp.ui.viewmodel.FavoriteViewModel
import com.scifi.markirapp.ui.viewmodel.LocationViewModel
import com.scifi.markirapp.ui.viewmodel.SlotsViewModel
import com.scifi.markirapp.utils.AppsUtils
import com.scifi.markirapp.utils.FirebaseAuthUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuthUtils.instance }
    private lateinit var parkingAdapter: ParkingAdapter
    private val favoriteViewModel by activityViewModels<FavoriteViewModel>()
    private val locationViewModel by activityViewModels<LocationViewModel>()
    private val slotsViewModel by activityViewModels<SlotsViewModel>()
    private var currentLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction()
        setupRecyclerView()
        observeData()
        observeLocation()
        observeParkingSlots()
        slotsViewModel.fetchParkingSlots()
    }

    private fun setupAction() {
        binding.apply {
            btnLogout.setOnClickListener {
                signOut()
            }
            tvName.text = auth.currentUser?.displayName
            tvEmail.text = auth.currentUser?.email
            Glide.with(this@ProfileFragment)
                .load(auth.currentUser?.photoUrl)
                .into(ivAvatar)
        }
    }

    private fun signOut() {
        AppsUtils.showAlert(
            requireActivity(),
            isWarning = true,
            message = "Are you sure you want to sign out?",
            primaryButtonText = "Yes",
            onPrimaryButtonClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    val credentialManager = CredentialManager.create(requireActivity())
                    auth.signOut()
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                    startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
                    requireActivity().finish()
                }
            },
            secondaryButtonText = "No"
        )
    }

    private fun setupRecyclerView() {
        parkingAdapter = ParkingAdapter(listOf())
        binding.rvPark.layoutManager = LinearLayoutManager(context)
        binding.rvPark.adapter = parkingAdapter
    }

    private fun observeData() {
        favoriteViewModel.favoriteLocations.observe(viewLifecycleOwner) { locations ->
            updateLocations(locations)
        }
    }

    private fun observeLocation() {
        locationViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            currentLocation = location
            val locations = favoriteViewModel.favoriteLocations.value
            if (locations != null) {
                updateLocations(locations)
            }
        }
    }

    private fun observeParkingSlots() {
        slotsViewModel.parkingSlots.observe(viewLifecycleOwner) {
            val locations = favoriteViewModel.favoriteLocations.value
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
}