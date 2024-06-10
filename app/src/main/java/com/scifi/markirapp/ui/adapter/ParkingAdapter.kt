package com.scifi.markirapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.scifi.markirapp.R
import com.scifi.markirapp.data.model.ParkingLocation
import com.scifi.markirapp.databinding.ItemParkBinding
import com.scifi.markirapp.ui.view.ParkingSlotActivity

class ParkingAdapter(private var parkingList: List<ParkingLocation>) :
    RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingViewHolder {
        val binding = ItemParkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParkingViewHolder, position: Int) {
        val parkingLocation = parkingList[position]
        holder.bind(parkingLocation)
    }

    override fun getItemCount() = parkingList.size

    fun updateData(newParkingList: List<ParkingLocation>) {
        parkingList = newParkingList
        notifyDataSetChanged()
    }

    fun sortByDistance() {
        val sortedList = parkingList.sortedBy { it.distance?.toDouble() }
        updateData(sortedList)
    }

    class ParkingViewHolder(val binding: ItemParkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(parkingLocation: ParkingLocation) {
            val context = binding.root.context
            binding.apply {
                tvLocationName.text = parkingLocation.name
                tvLocationSlots.text = context.getString(
                    R.string.count_slots_available,
                    parkingLocation.slotsAvailable
                )
                if (parkingLocation.distance!! >= 1000) {
                    val distanceInKilometers = parkingLocation.distance / 1000
                    tvLocationRange.text = context.getString(R.string.count_range_kilometer, distanceInKilometers)
                } else {
                    tvLocationRange.text = context.getString(R.string.count_range_meter, parkingLocation.distance?.toInt())
                }
            }
            if (parkingLocation.imageUrl.isEmpty()) {
                binding.ivImage.setImageResource(R.drawable.baseline_broken_image_24)
            } else {
                Glide.with(context)
                    .load(parkingLocation.imageUrl)
                    .into(binding.ivImage)
            }
            itemView.setOnClickListener {
                val intent = Intent(context, ParkingSlotActivity::class.java)
                intent.putExtra("parkingLocation", parkingLocation)
                context.startActivity(intent)
            }
        }
    }
}