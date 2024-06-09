package com.example.cerita.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)