package com.example.cerita.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cerita.data.UserRepository
import com.example.cerita.data.response.ListStoryItem
import com.example.cerita.di.Result
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> get() = _listStory

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getList() {
        viewModelScope.launch {
            when (val result = repository.getStories().firstOrNull()) {
                is Result.Success -> {
                    _listStory.value = result.data.listStory
                }

                is Result.Error -> {
                }

                else -> {

                }
            }
        }
    }
}