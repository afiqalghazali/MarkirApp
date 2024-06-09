package com.scifi.markirapp.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.scifi.markirapp.databinding.ActivityFavoriteBinding
import com.scifi.markirapp.ui.adapter.FavoriteAdapter
import com.scifi.markirapp.ui.viewmodel.FavoriteViewModel

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val favoriteViewModel by viewModels<FavoriteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter(mutableListOf())
        binding.rvFav.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
        }
    }

    private fun observeViewModel() {
        favoriteViewModel.favoriteLocations.observe(this, Observer { locations ->
            favoriteAdapter.updateData(locations)
        })
    }
}
//    private fun loadFavoriteLocations() {
//        val db = Firebase.database
//        val messagesRef = db.reference.child(MESSAGES_CHILD)
//
//        messagesRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                favorites.clear()
//
//                for (childSnapshot in snapshot.children) {
//                    val parkingLocation = intent.getParcelableExtra<ParkingLocation>("parkingLocation")
//                    if (parkingLocation != null && parkingLocation.isBookmarked) {
//                        favorites.add(parkingLocation)
//                    }
//                }
//
//                favoriteAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle the error
//            }
//        })
//    }

//    private fun loadFavorites() {
//        val db = Firebase.database
//        val messagesRef = db.reference.child(ParkingAdapter.MESSAGES_CHILD)
//
//        // Load favorites from Firebase
//        messagesRef.addValueEventListener(object : ValueEventListener {
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onDataChange(snapshot: DataSnapshot) {
//                favorites.clear()
//                for (dataSnapshot in snapshot.children) {
//                    val rawData = dataSnapshot.value
//                    Log.d("FavoriteActivity", "Raw data: $rawData")
//
//                    val parkingLocation = dataSnapshot.getValue(ParkingLocation::class.java)
//                    if (parkingLocation != null && parkingLocation.isBookmarked) {
//                        favorites.add(parkingLocation)
//                    }
//                }
//                favoriteAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@FavoriteActivity, "Failed to load favorites: ${error.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

//}

