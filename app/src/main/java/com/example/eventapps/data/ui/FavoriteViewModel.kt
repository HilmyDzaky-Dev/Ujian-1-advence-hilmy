package com.example.eventapps.data.ui

import androidx.lifecycle.ViewModel
import com.example.eventapps.data.EventRepository

class FavoriteViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getFavoriteEvents() = eventRepository.getFavoriteEvents()
}
