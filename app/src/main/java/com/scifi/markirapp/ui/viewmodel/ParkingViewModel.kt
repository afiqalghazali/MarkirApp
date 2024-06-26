package com.scifi.markirapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.scifi.markirapp.data.model.ParkingLocation

class ParkingViewModel : ViewModel() {

    private val _parkingLocations = MutableLiveData<List<ParkingLocation>>()
    val parkingLocations: LiveData<List<ParkingLocation>> get() = _parkingLocations

    fun setParkingLocations(locations: List<ParkingLocation>) {
        _parkingLocations.value = locations
    }
}