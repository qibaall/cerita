package com.example.cerita.presentation.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cerita.data.UserRepository
import com.example.cerita.data.response.StoryResponse
import com.example.cerita.di.Result

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    private val _stories = MutableLiveData<Result<StoryResponse>>()
    val stories: LiveData<Result<StoryResponse>> get() = _stories

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            _stories.postValue(Result.Loading)
            try {
                val response = repository.getStoriesWithLocation()
                _stories.postValue(Result.Success(response))
            } catch (e: Exception) {
                _stories.postValue(Result.Error(e.message ?: "An error occurred"))
            }
        }
    }
}
