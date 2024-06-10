package com.example.cerita.presentation.detail

import androidx.lifecycle.ViewModel
import com.example.cerita.data.UserRepository

class DetailStoryViewModel(private val repository: UserRepository) : ViewModel() {
    suspend fun detailStories(id: String) = repository.detailStories(id)
}