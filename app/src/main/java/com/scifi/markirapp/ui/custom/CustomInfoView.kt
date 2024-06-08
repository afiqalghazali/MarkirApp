package com.scifi.markirapp.ui.custom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.scifi.markirapp.R

class CustomInfoView(private val context: Context) : GoogleMap.InfoWindowAdapter {

    var currentMarker: Marker? = null

    override fun getInfoContents(marker: Marker): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_info, null)
        val title = view.findViewById<TextView>(R.id.title)
        title.text = marker.title
        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        currentMarker = marker
        return null
    }
}
