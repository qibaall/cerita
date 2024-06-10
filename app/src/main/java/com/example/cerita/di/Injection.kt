package com.example.cerita.di

import android.content.Context
import com.example.cerita.data.UserRepository
import com.example.cerita.data.api.ApiConfig
import com.example.cerita.data.pref.UserPreference
import com.example.cerita.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }
}
