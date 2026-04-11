package com.example.eventapps.di

import android.content.Context
import com.example.eventapps.data.EventRepository
import com.example.eventapps.data.local.room.EventDatabase
import com.example.eventapps.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(apiService, dao)
    }
}