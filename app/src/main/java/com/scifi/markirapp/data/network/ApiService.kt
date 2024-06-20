package com.scifi.markirapp.data.network

import com.scifi.markirapp.data.network.response.SlotResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("layout")
    suspend fun getSlots(): Response<List<SlotResponse?>?>
}