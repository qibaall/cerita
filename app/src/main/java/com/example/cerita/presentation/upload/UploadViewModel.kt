package com.example.cerita.presentation.upload

import androidx.lifecycle.ViewModel
import com.example.cerita.data.UserRepository
import java.io.File

class UploadViewModel(private val repository: UserRepository) : ViewModel() {
    fun uploadStories(file: File, description: String) = repository.uploadStories(file, description)
}