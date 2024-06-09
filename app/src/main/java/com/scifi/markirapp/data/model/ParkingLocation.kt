package com.scifi.markirapp.data.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParkingLocation(
    val name: String,
    val latLng: LatLng,
    val slotsAvailable: Int,
    val distance: Int,
    val imageUrl: String,
    var isBookmarked:Boolean,
    var id: String = ""
) :Parcelable
