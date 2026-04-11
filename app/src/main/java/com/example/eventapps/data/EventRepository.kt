package com.example.eventapps.data

import androidx.lifecycle.LiveData
import com.example.eventapps.data.local.entity.EventEntity
import com.example.eventapps.data.local.room.EventDao
import com.example.eventapps.data.remote.retrofit.ApiService

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao
) {
    fun getFavoriteEvents(): LiveData<List<EventEntity>> {
        return eventDao.getFavoriteEvents()
    }

    fun isFavorite(id: String): LiveData<Boolean> {
        return eventDao.isEventFavorite(id)
    }

    suspend fun setFavoriteEvent(event: EventEntity, favoriteState: Boolean) {
        event.isFavorite = favoriteState
        if (favoriteState) {
            eventDao.insertEvent(event)
        } else {
            eventDao.deleteEvent(event)
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao)
            }.also { instance = it }
    }
}