package com.scifi.markirapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.scifi.markirapp.data.model.ParkingLocation

class FavoriteViewModel : ViewModel() {
    private val _favoriteLocations = MutableLiveData<List<ParkingLocation>>()
    val favoriteLocations: LiveData<List<ParkingLocation>> get() = _favoriteLocations

    init {
        loadFavoriteLocations()
    }

    private fun loadFavoriteLocations() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val db = FirebaseDatabase.getInstance().reference.child(USERS_CHILD).child(userId)
            .child(FAVORITES_CHILD)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorites = snapshot.children.mapNotNull { it.toParkingLocation() }
                _favoriteLocations.value = favorites
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    private fun DataSnapshot.toParkingLocation(): ParkingLocation? {
        val placeId = child("placeId").getValue(String::class.java)
        val name = child("name").getValue(String::class.java)
        val latitude = child("latLng").child("latitude").getValue(Double::class.java)
        val longitude = child("latLng").child("longitude").getValue(Double::class.java)
        val latLng =
            if (latitude != null && longitude != null) LatLng(latitude, longitude) else null
        val imageUrl = child("imageUrl").getValue(String::class.java) ?: ""
        val isBookmarked = child("isBookmarked").getValue(Boolean::class.java) ?: false

        return if (latLng != null) {
            ParkingLocation(
                placeId = placeId,
                name = name,
                latLng = latLng,
                imageUrl = imageUrl,
                isBookmarked = isBookmarked
            )
        } else {
            null
        }
    }

    companion object {
        const val USERS_CHILD = "users"
        const val FAVORITES_CHILD = "favorites"
    }
}
