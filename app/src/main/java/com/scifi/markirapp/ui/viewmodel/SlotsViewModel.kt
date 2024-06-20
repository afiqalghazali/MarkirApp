package com.scifi.markirapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.scifi.markirapp.data.network.ApiConfig
import com.scifi.markirapp.data.network.response.SlotResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SlotsViewModel : ViewModel() {
    private val _parkingSlots = MutableLiveData<List<SlotResponse?>?>()
    val parkingSlots: LiveData<List<SlotResponse?>?> get() = _parkingSlots

    fun fetchParkingSlots() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService().getSlots()
            if (response.isSuccessful) {
                _parkingSlots.postValue(response.body())
            } else {
                _parkingSlots.postValue(null)
            }
        }
    }
}