package com.scifi.markirapp.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.scifi.markirapp.R
import com.scifi.markirapp.data.ParkingLocation
import com.scifi.markirapp.databinding.ItemParkBinding
import com.scifi.markirapp.view.ParkingSlotActivity
import com.scifi.markirapp.view.utils.InterfaceUtils

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

    class ParkingViewHolder(private val binding: ItemParkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(parkingLocation: ParkingLocation) {
            binding.apply {
                tvLocationName.text = parkingLocation.name
                tvLocationSlots.text = "${parkingLocation.slotsAvailable} slots available"
                tvLocationRange.text = parkingLocation.distance
            }
            val context = binding.root.context
            if (parkingLocation.imageUrl.isEmpty()) {
                binding.ivImage.setImageResource(R.drawable.baseline_broken_image_24)
            } else {
                Glide.with(context)
                    .load(parkingLocation.imageUrl)
                    .into(binding.ivImage)
            }
            itemView.setOnClickListener {
                val intent = Intent(context, ParkingSlotActivity::class.java)
                context.startActivity(intent)
            }
            itemView.setOnLongClickListener {
                InterfaceUtils.showAlert(
                    context,
                    "Save this location?",
                    primaryButtonText = "Yes",
                    onPrimaryButtonClick = {
                        Snackbar.make(it, "Location saved", Snackbar.LENGTH_SHORT).show()
                    },
                    secondaryButtonText = "No"
                )
                true
            }

        }
    }
}
