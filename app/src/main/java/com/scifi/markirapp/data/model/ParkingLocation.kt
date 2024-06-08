package com.scifi.markirapp.data.model

import com.google.android.gms.maps.model.LatLng

data class ParkingLocation(
    val name: String,
    val latLng: LatLng,
    val slotsAvailable: Int,
    val distance: Int,
    val imageUrl: String,
)
