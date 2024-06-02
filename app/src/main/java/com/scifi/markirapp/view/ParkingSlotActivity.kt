package com.scifi.markirapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.scifi.markirapp.databinding.ActivityParkingSlotBinding
import com.scifi.markirapp.view.custom.ParkingSlotView

class ParkingSlotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParkingSlotBinding
    data class ParkingSlot(val id: String, val isOccupied: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParkingSlotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val parkingSlotView: ParkingSlotView = binding.parkingSlotView
        val slots = listOf(
            ParkingSlot("A01", true), ParkingSlot("A02", false), ParkingSlot("A03", true), ParkingSlot("A04", true),
            ParkingSlot("A05", false), ParkingSlot("A06", true), ParkingSlot("A07", false), ParkingSlot("A08", true),
            ParkingSlot("A09", true), ParkingSlot("A10", false), ParkingSlot("A11", true), ParkingSlot("A12", false),
            ParkingSlot("A13", true), ParkingSlot("A14", false), ParkingSlot("A15", true), ParkingSlot("A16", false),

            ParkingSlot("B01", true), ParkingSlot("B02", false), ParkingSlot("B03", false), ParkingSlot("B04", true),
            ParkingSlot("B05", true), ParkingSlot("B06", false), ParkingSlot("B07", true), ParkingSlot("B08", false),
            ParkingSlot("B09", true), ParkingSlot("B10", false), ParkingSlot("B11", true), ParkingSlot("B12", false),
            ParkingSlot("B13", true), ParkingSlot("B14", false), ParkingSlot("B15", true), ParkingSlot("B16", false),

            ParkingSlot("C01", false), ParkingSlot("C02", true), ParkingSlot("C03", false), ParkingSlot("C04", true),
            ParkingSlot("C05", true), ParkingSlot("C06", false), ParkingSlot("C07", true), ParkingSlot("C08", false),
            ParkingSlot("C09", true), ParkingSlot("C10", false), ParkingSlot("C11", true), ParkingSlot("C12", false),
            ParkingSlot("C13", true), ParkingSlot("C14", false), ParkingSlot("C15", true), ParkingSlot("C16", false),

            ParkingSlot("D01", true), ParkingSlot("D02", false), ParkingSlot("D03", true), ParkingSlot("D04", false),
            ParkingSlot("D05", true), ParkingSlot("D06", false), ParkingSlot("D07", true), ParkingSlot("D08", false),
            ParkingSlot("D09", true), ParkingSlot("D10", false), ParkingSlot("D11", true), ParkingSlot("D12", false),
            ParkingSlot("D13", true), ParkingSlot("D14", false), ParkingSlot("D15", true), ParkingSlot("D16", false)
        )

        parkingSlotView.parkingSlots = slots
    }
}