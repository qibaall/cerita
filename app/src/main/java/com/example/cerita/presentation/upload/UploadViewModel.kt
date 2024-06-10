package com.example.cerita.presentation.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cerita.data.UserRepository
import com.example.cerita.data.response.UploadResponse
import com.example.cerita.di.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File

class UploadViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uploadResult = MutableStateFlow<Result<UploadResponse>>(Result.Loading)
    val uploadResult: StateFlow<Result<UploadResponse>> = _uploadResult

    fun uploadStories(image: File, description: String) {
        viewModelScope.launch {
            userRepository.uploadStories(image, description)
                .onStart { _uploadResult.value = Result.Loading }
                .catch { e ->
                    _uploadResult.value = Result.Error(e.message ?: "An error occurred")
                }
                .collect { result ->
                    _uploadResult.value = result
                }
        }
    }
}
