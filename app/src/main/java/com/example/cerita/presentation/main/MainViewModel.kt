package com.example.cerita.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cerita.data.UserRepository
import com.example.cerita.data.pref.UserModel
import com.example.cerita.data.response.ListStoryItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.cerita.di.Result

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> get() = _listStory

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getList() {
        viewModelScope.launch {
            when (val result = repository.getStories().first()) {
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