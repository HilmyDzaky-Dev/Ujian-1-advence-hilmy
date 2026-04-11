package com.example.eventapps.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.eventapps.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event ORDER BY name ASC")
    fun getAllEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isFavorite = 1")
    fun getFavoriteEvents(): LiveData<List<EventEntity>>

    @Query("SELECT EXISTS(SELECT * FROM event WHERE id = :id AND isFavorite = 1)")
    fun isEventFavorite(id: String): LiveData<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)
}