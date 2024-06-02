package com.scifi.markirapp.data

import com.google.android.gms.maps.model.LatLng

data class ParkingLocation(
    val name: String,
    val latLng: LatLng,
    val slotsAvailable: Int,
    val distance: String,
    val imageUrl: String
)
