package com.example.cerita.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.cerita.data.api.ApiService
import com.example.cerita.data.api.StoryPagingSource
import com.example.cerita.data.pref.UserModel
import com.example.cerita.data.pref.UserPreference
import com.example.cerita.data.response.ListStoryItem
import com.example.cerita.data.response.LoginResponse
import com.example.cerita.data.response.RegisterResponse
import com.example.cerita.data.response.StoryDetailResponse
import com.example.cerita.data.response.StoryResponse
import com.example.cerita.data.response.UploadResponse
import com.example.cerita.di.Result
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

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

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 3
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation(): StoryResponse {
        val client = apiService.getStoriesWithLocation()
        if (client.listStory.isNotEmpty()) {
            Log.d("AppRepository", "onSuccess")
            return client
        } else {
            Log.d("AppRepository", "onFailure : ${client.message}")
        }
        return client
    }
    suspend fun detailStories(id: String): Flow<Result<StoryDetailResponse>> = flow {
        val response = apiService.detailStories(id)
        emit(Result.Success(response))
    }.catch { e ->
        e.printStackTrace()
        (Result.Error(e.message ?: "An error occurred"))
    }


    fun uploadStories(imageFile: File, description: String): Flow<Result<UploadResponse>> = flow {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImg = imageFile.asRequestBody("image/jpg".toMediaType())
        val bodyMultipart = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImg
        )
        try {
            val responseSuccess = apiService.uploadStories(bodyMultipart, requestBody)
            emit(Result.Success(responseSuccess))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val responseError = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(Result.Error(responseError.message))
        }
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
