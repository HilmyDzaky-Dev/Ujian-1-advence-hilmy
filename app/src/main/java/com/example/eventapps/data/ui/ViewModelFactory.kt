package com.example.eventapps.data.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eventapps.data.EventRepository
import com.example.eventapps.data.Settingpreference
import com.example.eventapps.data.dataStore
import com.example.eventapps.di.Injection

class ViewModelFactory private constructor(
    private val eventRepository: EventRepository,
    private val pref: Settingpreference
) : ViewModelProvider.NewInstanceFactory() {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(eventRepository, pref) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(eventRepository) as T
        } else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(eventRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    Settingpreference.getInstance(context.dataStore)
                )
            }.also { instance = it }
    }
}
