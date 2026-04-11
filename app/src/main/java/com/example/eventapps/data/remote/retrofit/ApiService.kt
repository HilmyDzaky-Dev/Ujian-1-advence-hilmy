package com.example.eventapps.data.remote.retrofit

import com.example.eventapps.data.remote.response.DetailResponse
import com.example.eventapps.data.remote.response.FinishingResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int
    ): Call<FinishingResponse>

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") keyword: String
    ): Call<FinishingResponse>

    @GET("events/{id}")
    fun getEventDetail(
        @Path("id") id: String
    ): Call<DetailResponse>
}
