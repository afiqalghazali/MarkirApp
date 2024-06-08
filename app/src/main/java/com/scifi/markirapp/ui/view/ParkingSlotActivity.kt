package com.scifi.markirapp.ui.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scifi.markirapp.R
import com.scifi.markirapp.databinding.ActivityParkingSlotBinding
import com.scifi.markirapp.ui.adapter.ParkingViewAdapter

class ParkingSlotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParkingSlotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParkingSlotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

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
                Toast.makeText(this@ParkingSlotActivity, "Location saved", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
