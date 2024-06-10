package com.example.cerita.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cerita.data.UserRepository
import com.example.cerita.data.pref.UserModel
import com.example.cerita.data.response.LoginResponse
import com.example.cerita.di.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> {
        return repository.login(email, password)
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}