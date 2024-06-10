package com.scifi.markirapp.ui.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.scifi.markirapp.R
import com.scifi.markirapp.data.model.ParkingLocation
import com.scifi.markirapp.databinding.ActivityParkingSlotBinding
import com.scifi.markirapp.ui.adapter.ParkingViewAdapter
import com.scifi.markirapp.utils.FirebaseAuthUtils
import com.scifi.markirapp.utils.InterfaceUtils

class ParkingSlotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParkingSlotBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuthUtils.instance }
    private var parkingLocation: ParkingLocation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityParkingSlotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        parkingLocation = intent.getParcelableExtra("parkingLocation")

        if (parkingLocation == null) {
            InterfaceUtils.showAlert(
                this,
                "No parking location data available"
            )
            finish()
            return
        }

        checkIfBookmarked(parkingLocation?.placeId)

        val floors = listOf(1, 2)
        val parkingViewAdapter = ParkingViewAdapter(this, floors)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = parkingViewAdapter
        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = getString(R.string.count_floor, floors[position])
        }.attach()

        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            btnSave.setOnClickListener {
                saveParkingLocation()
                toggleButtons(true)
            }
            btnRemove.setOnClickListener {
                removeParkingLocation()
                toggleButtons(false)
            }
        }
    }

    private fun setupAction() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            FirebaseAuthUtils.sessionEndedAlert(this)
        }
    }

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    private fun checkIfBookmarked(parkingLocationId: String?) {
        val userId = getUserId()
        if (parkingLocationId == null) return
        val db = Firebase.database
        val favoritesRef = db.reference.child(USERS_CHILD).child(userId).child(FAVORITES_CHILD).child(parkingLocationId)

        favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isBookmarked = snapshot.exists()
                toggleButtons(isBookmarked)
            }

            override fun onCancelled(error: DatabaseError) {
                InterfaceUtils.showAlert(this@ParkingSlotActivity, "Failed to check bookmark status: ${error.message}")
            }
        })
    }


    private fun toggleButtons(isBookmarked: Boolean) {
        if (isBookmarked) {
            binding.btnSave.visibility = View.GONE
            binding.btnRemove.visibility = View.VISIBLE
        } else {
            binding.btnSave.visibility = View.VISIBLE
            binding.btnRemove.visibility = View.GONE
        }
    }

    private fun saveParkingLocation() {
        val userId = getUserId()
        val db = Firebase.database
        val userFavoritesRef = db.reference.child(USERS_CHILD).child(userId).child(FAVORITES_CHILD)

        parkingLocation?.isBookmarked = true

        parkingLocation?.let { location ->
            location.placeId?.let {
                userFavoritesRef.child(it).setValue(location) { error, _ ->
                    if (error != null) {
                        InterfaceUtils.showAlert(this, "Failed to save parking location: ${error.message}")
                    } else {
                        Toast.makeText(this, "Location saved", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun removeParkingLocation() {
        val userId = getUserId()
        val db = Firebase.database
        val userFavoritesRef = db.reference.child(USERS_CHILD).child(userId).child(FAVORITES_CHILD)

        parkingLocation?.let { location ->
            location.isBookmarked = false
            location.placeId?.let {
                userFavoritesRef.child(it).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Location removed", Toast.LENGTH_SHORT).show()
                    } else {
                        InterfaceUtils.showAlert(this, "Failed to remove parking location")
                    }
                }
            }
        }
    }

    companion object {
        const val USERS_CHILD = "users"
        const val FAVORITES_CHILD = "favorites"
    }
}
