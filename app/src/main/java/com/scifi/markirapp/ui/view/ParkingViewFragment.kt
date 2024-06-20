package com.scifi.markirapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.scifi.markirapp.R
import com.scifi.markirapp.data.network.response.SlotResponse
import com.scifi.markirapp.databinding.FragmentParkingViewBinding
import com.scifi.markirapp.ui.custom.ParkingSlotView


class ParkingViewFragment : Fragment() {

    private lateinit var binding: FragmentParkingViewBinding
    private lateinit var parkingSlots: List<SlotResponse>

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
        parkingSlots = arguments?.getParcelableArrayList(ARG_PARKING_SLOTS) ?: return
        val slots = getParkingSlotsForFloor(floor)
        parkingSlotView.parkingSlots = slots
        binding.tvFloor.text = getString(R.string.count_floor, floor)
        binding.tvSlots.text = getString(R.string.count_slots, slots.count { it.occupied == 0 })
    }

    private fun getParkingSlotsForFloor(floor: Int): List<SlotResponse> {
        return parkingSlots.filter { it.floor == floor }
    }

    companion object {
        private const val ARG_FLOOR = "floor"
        private const val ARG_PARKING_SLOTS = "parkingSlots"

        fun newInstance(floor: Int, parkingSlots: ArrayList<SlotResponse?>): ParkingViewFragment {
            val fragment = ParkingViewFragment()
            val args = Bundle().apply {
                putInt(ARG_FLOOR, floor)
                putParcelableArrayList(ARG_PARKING_SLOTS, parkingSlots)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
