package com.example.cerita.presentation.start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cerita.databinding.ActivityStartBinding
import com.example.cerita.presentation.ViewModelFactory
import com.example.cerita.presentation.login.LoginActivity
import com.example.cerita.presentation.main.MainActivity
import com.example.cerita.presentation.register.RegisterActivity

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var startViewModel: StartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModelFactory = ViewModelFactory.getInstance(this)
        startViewModel = ViewModelProvider(this, viewModelFactory)[StartViewModel::class.java]
        binding.btnLogin.setOnClickListener {
            navigateToLogin()
        }
        binding.btnRegister.setOnClickListener {
            navigateToRegister()
        }
        startViewModel.getSession().observe(this) { userModel ->
            if (userModel.token.isNotEmpty()) {
                navigateToMainActivity()
            } else {
                // Token kosong, tetap di StartActivity
            }
        }

    }
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
