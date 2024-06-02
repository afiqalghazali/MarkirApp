package com.scifi.markirapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.scifi.markirapp.R
import com.scifi.markirapp.data.ParkingLocation
import com.scifi.markirapp.databinding.ItemParkBinding

class ParkingAdapter(private var parkingList: List<ParkingLocation>) : RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder>() {

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

    class ParkingViewHolder(private val binding: ItemParkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parkingLocation: ParkingLocation) {
            binding.apply {
                tvLocationName.text = parkingLocation.name
                tvLocationSlots.text = "${parkingLocation.slotsAvailable} slots available"
                tvLocationRange.text = parkingLocation.distance

                if (parkingLocation.imageUrl.isEmpty()) {
                    ivImage.setImageResource(R.drawable.baseline_broken_image_24)
                } else {
                    Glide.with(itemView.context)
                        .load(parkingLocation.imageUrl)
                        .into(ivImage)
                }

            }

        }
    }
}
