package com.scifi.markirapp.data.network

import com.scifi.markirapp.data.network.response.DirectionsResponse
import com.scifi.markirapp.data.network.response.PlacesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsApiService {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
    ): Response<DirectionsResponse>

    @GET("place/textsearch/json")
    suspend fun getPlaces(
        @Query("query") query: String,
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("key") apiKey: String,
    ): Response<PlacesResponse>
}