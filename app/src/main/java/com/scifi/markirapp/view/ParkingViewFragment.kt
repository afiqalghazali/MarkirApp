package com.scifi.markirapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.scifi.markirapp.data.DummyData
import com.scifi.markirapp.data.ParkingSlot
import com.scifi.markirapp.databinding.FragmentParkingViewBinding
import com.scifi.markirapp.view.custom.ParkingSlotView


class ParkingViewFragment : Fragment() {

    private lateinit var binding: FragmentParkingViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentParkingViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parkingSlotView: ParkingSlotView = binding.parkingSlotView
        val floor = arguments?.getInt(ARG_FLOOR) ?: return
        val slots = getParkingSlotsForFloor(floor)
        parkingSlotView.parkingSlots = slots
        binding.tvFloor.text = "Floor $floor"
        binding.tvSlots.text = "Available Slots: ${slots.count { !it.isOccupied }}"
    }

    private fun getParkingSlotsForFloor(floor: Int): List<ParkingSlot> {
        val allSlots = DummyData.allSlots
        return allSlots.filter { it.floor == floor }
    }

    companion object {
        private const val ARG_FLOOR = "floor"
    }
}
