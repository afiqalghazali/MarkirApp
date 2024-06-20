package com.scifi.markirapp.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scifi.markirapp.data.network.response.SlotResponse
import com.scifi.markirapp.ui.view.ParkingViewFragment

class ParkingViewAdapter(
    activity: AppCompatActivity,
    private val floors: List<Int>,
    private val parkingSlots: List<SlotResponse?>?,
) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = floors.size

    override fun createFragment(position: Int): Fragment {
        val fragment =
            parkingSlots?.let { parkingSlots ->
                ArrayList(parkingSlots).let {
                    ParkingViewFragment.newInstance(
                        floors[position],
                        it
                    )
                }
            }
        return fragment!!
    }

}