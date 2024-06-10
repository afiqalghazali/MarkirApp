package com.scifi.markirapp.data.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParkingLocation(
    var placeId: String? = null,
    val name: String? = null,
    val latLng: LatLng? = null,
    val slotsAvailable: Int? = null,
    val distance: Float? = null,
    val imageUrl: String = "",
    var isBookmarked: Boolean = false
) :Parcelable {
    constructor() : this( null, null, null, null, null, "", false)
}
