package com.example.eventapps.data.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapps.data.EventRepository
import com.example.eventapps.data.local.entity.EventEntity
import com.example.eventapps.data.remote.response.DetailResponse
import com.example.eventapps.data.remote.response.ListEventsItem
import com.example.eventapps.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _eventDetail = MutableLiveData<ListEventsItem?>()
    val eventDetail: LiveData<ListEventsItem?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDetailEvent(eventId: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEventDetail(eventId.toString())
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()?.event
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun isFavorite(id: String) = eventRepository.isFavorite(id)

    fun setFavoriteEvent(event: ListEventsItem, favoriteState: Boolean) {
        viewModelScope.launch {
            val eventEntity = EventEntity(
                id = event.id.toString(),
                name = event.name,
                mediaCover = event.mediaCover,
                summary = event.summary,
                imageLogo = event.imageLogo,
                isFavorite = favoriteState
            )
            eventRepository.setFavoriteEvent(eventEntity, favoriteState)
        }
    }

    companion object {
        private const val TAG = "DetailViewModel"
    }
}
