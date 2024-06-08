package com.scifi.markirapp.data.network.response

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
    @SerializedName("results")
    val results: List<Place>,
    @SerializedName("status")
    val status: String,
)

data class Place(
    @SerializedName("geometry")
    val geometry: Geometry,
    @SerializedName("name")
    val name: String,
    @SerializedName("place_id")
    val placeId: String,
    @SerializedName("photos")
    val photos: List<PhotosItem>? = emptyList(),
)

data class Geometry(
    @SerializedName("location")
    val location: Location,
)

data class Location(
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lng")
    val longitude: Double,
)

data class PhotosItem(
    @SerializedName("photo_reference")
    val photoReference: String?,
    @SerializedName("width")
    val width: Int?,
    @SerializedName("html_attributions")
    val htmlAttributions: List<String>?,
    @SerializedName("height")
    val height: Int?,
)
