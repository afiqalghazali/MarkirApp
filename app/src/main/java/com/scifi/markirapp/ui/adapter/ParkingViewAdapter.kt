package com.scifi.markirapp.ui.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scifi.markirapp.ui.view.ParkingViewFragment

class ParkingViewAdapter(activity: AppCompatActivity, private val floors: List<Int>) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = floors.size

    override fun createFragment(position: Int): Fragment {
        val fragment = ParkingViewFragment()
        fragment.arguments = Bundle().apply {
            putInt(ARG_FLOORS, floors[position])
        }
        return fragment
    }

    companion object {
        private const val ARG_FLOORS = "floor"
    }
}