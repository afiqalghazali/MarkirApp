package com.scifi.markirapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.database.database
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

        val db = Firebase.database
        val messagesRef = db.reference.child(MESSAGES_CHILD)
        val favoritesRef = db.reference.child("favorites")

        holder.binding.ivBookmark.setOnClickListener {
            parkingLocation.isBookmarked = !parkingLocation.isBookmarked

            if (parkingLocation.isBookmarked) {
                holder.binding.ivBookmark.setImageDrawable(ContextCompat.getDrawable(holder.binding.ivBookmark.context, R.drawable.ic_bookmark_active))
                // Create a new unique reference for the parking location
                val newLocationRef = messagesRef.push()

                // Save the unique key in the parkingLocation object
                parkingLocation.id = newLocationRef.key.toString()

                // Set the value of the new reference to the parking location
                newLocationRef.setValue(parkingLocation) { error, _ ->
                    if (error != null) {
                        // Show an error message
                        Toast.makeText(holder.itemView.context, "Failed to save parking location: ${error.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Parking location saved successfully", Toast.LENGTH_SHORT).show()
                    }
                }
                // Save to favorites
                favoritesRef.child(parkingLocation.id!!).setValue(parkingLocation)
            } else {
                holder.binding.ivBookmark.setImageDrawable(ContextCompat.getDrawable(holder.binding.ivBookmark.context, R.drawable.ic_bookmark_inactive))

                // Remove the parking location from the database using the saved unique key
                val locationToRemoveRef = messagesRef.child(parkingLocation.id!!)
                locationToRemoveRef.removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(holder.itemView.context, "Parking location removed successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(holder.itemView.context, "Failed to remove parking location", Toast.LENGTH_SHORT).show()
                        }
                    }
                favoritesRef.child(parkingLocation.id!!).removeValue()

                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount() = parkingList.size

    fun updateData(newParkingList: List<ParkingLocation>) {
        parkingList = newParkingList
        notifyDataSetChanged()
    }

    fun sortByDistance() {
        val sortedList = parkingList.sortedBy { it.distance.toDouble() }
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
                tvLocationRange.text =
                    context.getString(R.string.count_range, parkingLocation.distance)
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
                context.startActivity(intent)
            }


        }
    }

    companion object {
    const val MESSAGES_CHILD = "favorites"
    }
}