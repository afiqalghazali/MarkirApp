package com.scifi.markirapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.scifi.markirapp.R
import com.scifi.markirapp.data.model.ParkingLocation
import com.scifi.markirapp.databinding.ItemFavBinding
import com.scifi.markirapp.ui.view.ParkingSlotActivity

class FavoriteAdapter(private var favoriteList: MutableList<ParkingLocation>) :
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteLocation = favoriteList[position]
        holder.bind(favoriteLocation)
    }

    override fun getItemCount() = favoriteList.size

    fun updateData(newFavoriteList: List<ParkingLocation>) {
        favoriteList = newFavoriteList.toMutableList()
        notifyDataSetChanged()
    }

    class FavoriteViewHolder(val binding: ItemFavBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favoriteLocation: ParkingLocation) {
            val context = binding.root.context
            binding.apply {
                tvLocationName.text = favoriteLocation.name
            }
            if (favoriteLocation.imageUrl.isEmpty()) {
                binding.ivImage.setImageResource(R.drawable.baseline_broken_image_24)
            } else {
                Glide.with(context)
                    .load(favoriteLocation.imageUrl)
                    .into(binding.ivImage)
            }
            itemView.setOnClickListener {
                val intent = Intent(context, ParkingSlotActivity::class.java)
                intent.putExtra("parkingLocation", favoriteLocation)
                context.startActivity(intent)
            }
        }
    }
}