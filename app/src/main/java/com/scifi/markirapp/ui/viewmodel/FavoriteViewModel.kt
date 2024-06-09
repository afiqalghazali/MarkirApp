package com.scifi.markirapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scifi.markirapp.data.model.FavoriteModel

//class FavoriteViewModel:ViewModel() {
//    private val _favoriteLocations = MutableLiveData<List<FavoriteModel>>()
//    val favoriteLocations: LiveData<List<FavoriteModel>> get() = _favoriteLocations
//
////    fun setFavoriteLocations(locations: List<FavoriteModel>) {
////        _favoriteLocations.value = locations
////    }
//}
class FavoriteViewModel : ViewModel() {
    private val _favoriteLocations = MutableLiveData<List<FavoriteModel>>()
    val favoriteLocations: LiveData<List<FavoriteModel>> get() = _favoriteLocations

    init {
        loadFavoriteLocations()
    }

    private fun loadFavoriteLocations() {
        val db = FirebaseDatabase.getInstance()
        val favoritesRef = db.reference.child("favorites")

        favoritesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorites = mutableListOf<FavoriteModel>()
                snapshot.children.forEach {
                    val favorite = it.getValue(FavoriteModel::class.java)
                    if (favorite != null) {
                        favorites.add(favorite)
                    }
                }
                _favoriteLocations.value = favorites
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}