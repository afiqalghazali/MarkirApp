package com.scifi.markirapp.data.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.scifi.markirapp.data.network.response.SlotResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParkingLocation(
    var placeId: String? = null,
    val name: String? = null,
    val latLng: LatLng? = null,
    val parkingSlots: List<SlotResponse?>? = emptyList(),
    val distance: Float? = null,
    val imageUrl: String = "",
    var isBookmarked: Boolean = false,
) : Parcelable {
    constructor() : this(null, null, null, emptyList(), null, "", false)
}
