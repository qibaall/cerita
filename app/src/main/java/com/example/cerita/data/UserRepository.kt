package com.example.cerita.data

import com.example.cerita.data.api.ApiService
import com.example.cerita.data.pref.UserModel
import com.example.cerita.data.pref.UserPreference
import com.example.cerita.data.response.LoginResponse
import com.example.cerita.data.response.RegisterResponse
import com.example.cerita.data.response.StoryDetailResponse
import com.example.cerita.data.response.StoryResponse
import com.example.cerita.di.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepository(
    private val userPreference: UserPreference,
    private var apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun login(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)


    fun register(name: String, email: String, password: String): Flow<Result<RegisterResponse>> =
        flow {
            try {
                val response = apiService.register(name, email, password)
                emit(Result.Success(response))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(e.message ?: "An error occurred"))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getStories(): Flow<Result<StoryResponse>> = flow {
        val response = apiService.getStories()
        emit(Result.Success(response))
    }.catch { e ->
        e.printStackTrace()
        (Result.Error(e.message ?: "An error occurred"))
    }
    suspend fun detailStories(id: String): Flow<Result<StoryDetailResponse>> = flow {
        val response = apiService.detailStories(id)
        emit(Result.Success(response))
    }.catch { e ->
        e.printStackTrace()
        (Result.Error(e.message ?: "An error occurred"))
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun updateApiService(apiService: ApiService) {
        this.apiService = apiService
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService).also { instance = it }
            }
    }
}
