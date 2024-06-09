package com.example.cerita.presentation.register

import androidx.lifecycle.ViewModel
import com.example.cerita.data.UserRepository
import com.example.cerita.data.response.RegisterResponse
import com.example.cerita.di.Result
import kotlinx.coroutines.flow.Flow

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    fun register(name: String, email: String, password: String): Flow<Result<RegisterResponse>> {
        return repository.register(name, email, password)
    }
}
