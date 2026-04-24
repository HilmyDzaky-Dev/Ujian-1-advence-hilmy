package com.example.eventapps.data.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventapps.data.EventRepository
import com.example.eventapps.data.Settingpreference
import com.example.eventapps.data.remote.response.FinishingResponse
import com.example.eventapps.data.remote.response.ListEventsItem
import com.example.eventapps.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val eventRepository: EventRepository, private val pref: Settingpreference) : ViewModel() {

    private val _listUpcoming = MutableLiveData<List<ListEventsItem>>()
    val listUpcoming: LiveData<List<ListEventsItem>> = _listUpcoming

    private val _listFinished = MutableLiveData<List<ListEventsItem>>()
    val listFinished: LiveData<List<ListEventsItem>> = _listFinished

    private val _listSearch = MutableLiveData<List<ListEventsItem>>()
    val listSearch: LiveData<List<ListEventsItem>> = _listSearch

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getThemeSetting(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getReminderSetting(): LiveData<Boolean> {
        return pref.getReminderSetting().asLiveData()
    }


    fun saveReminderSetting(isReminderActive: Boolean) {
        viewModelScope.launch {
            pref.saveReminderSetting(isReminderActive)
        }
    }

    fun getUpcomingEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(1)
        client.enqueue(object : Callback<FinishingResponse> {
            override fun onResponse(
                call: Call<FinishingResponse>,
                response: Response<FinishingResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listUpcoming.value = response.body()?.listEvents
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FinishingResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getFinishedEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(0)
        client.enqueue(object : Callback<FinishingResponse> {
            override fun onResponse(
                call: Call<FinishingResponse>,
                response: Response<FinishingResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listFinished.value = response.body()?.listEvents
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FinishingResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun searchEvents(query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchEvents(-1, query)
        client.enqueue(object : Callback<FinishingResponse> {
            override fun onResponse(
                call: Call<FinishingResponse>,
                response: Response<FinishingResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listSearch.value = response.body()?.listEvents
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FinishingResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getFavoriteEvents() = eventRepository.getFavoriteEvents()

    fun isFavorite(id: String) = eventRepository.isFavorite(id)

    companion object {
        private const val TAG = "MainViewModel"
    }
}