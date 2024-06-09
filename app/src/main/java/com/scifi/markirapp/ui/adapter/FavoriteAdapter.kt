package com.scifi.markirapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.scifi.markirapp.R
import com.scifi.markirapp.data.model.FavoriteModel
import com.scifi.markirapp.databinding.ItemFavBinding
import com.scifi.markirapp.ui.view.ParkingSlotActivity

class FavoriteAdapter(private var favoriteList: MutableList<FavoriteModel>) :
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteLocation = favoriteList[position]
        holder.bind(favoriteLocation)

        holder.binding.ivBookmark.setOnClickListener {
            removeFavorite(favoriteLocation, position, holder)
        }
    }

    override fun getItemCount() = favoriteList.size

    fun updateData(newFavoriteList: List<FavoriteModel>) {
        favoriteList = newFavoriteList.toMutableList()
        notifyDataSetChanged()
    }

    private fun removeFavorite(favoriteLocation: FavoriteModel, position: Int, holder: FavoriteViewHolder) {
        favoriteLocation.isBookmarked = false

        holder.binding.ivBookmark.setImageDrawable(
            ContextCompat.getDrawable(
                holder.binding.ivBookmark.context,
                R.drawable.baseline_delete_outline_24
            )
        )

        val db = FirebaseDatabase.getInstance()
        val favoritesRef = db.reference.child("favorites")
        favoritesRef.child(favoriteLocation.id!!).removeValue()

        favoriteList.removeAt(position)
        notifyItemRemoved(position)
    }

    class FavoriteViewHolder(val binding: ItemFavBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favoriteLocation: FavoriteModel) {
            val context = binding.root.context
            binding.apply {
                tvLocationName.text = favoriteLocation.name
                // Bind other views with favorite data if necessary
            }
            // You can add image loading with Glide or similar library if there are images
            if (favoriteLocation.imageUrl.isEmpty()) {
                binding.ivImage.setImageResource(R.drawable.baseline_broken_image_24)
            } else {
                Glide.with(context)
                    .load(favoriteLocation.imageUrl)
                    .into(binding.ivImage)
            }
            itemView.setOnClickListener {
                val intent = Intent(context, ParkingSlotActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}