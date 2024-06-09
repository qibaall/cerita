package com.example.cerita.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cerita.data.api.ApiConfig
import com.example.cerita.data.pref.UserModel
import com.example.cerita.data.response.LoginResponse
import com.example.cerita.databinding.ActivityLoginBinding
import com.example.cerita.di.Injection
import com.example.cerita.di.Result
import com.example.cerita.presentation.ViewModelFactory
import com.example.cerita.presentation.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            lifecycleScope.launch {
                loginViewModel.userLogin(email, password).collect{ result ->
                    handleLoginResult(result, email)
                }
            }
        }
    }

    private fun handleLoginResult(result: Result<LoginResponse>, email: String) {
        when (result) {
            is Result.Success -> {
                binding.progressBar.visibility = View.GONE
                val token = result.data.loginResult.token
                val userModel = UserModel(email, token, isLogin = true)
                val userRepository = Injection.provideRepository(applicationContext)
                userRepository.updateApiService(ApiConfig.getApiService(token))
                loginViewModel.saveSession(userModel)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            is Result.Error -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Login Failed: ${result.error}", Toast.LENGTH_SHORT).show()
            }
            is Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }
}
