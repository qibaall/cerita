package com.example.cerita.presentation.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cerita.data.response.RegisterResponse
import com.example.cerita.databinding.ActivityRegisterBinding
import com.example.cerita.di.Result
import com.example.cerita.presentation.ViewModelFactory
import com.example.cerita.presentation.login.LoginActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ViewModelFactory.getInstance(this)
        registerViewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            lifecycleScope.launch {
                registerViewModel.register(name, email, password).collect { result ->
                    handleRegisterResult(result)
                }
            }
        }
    }

    private fun handleRegisterResult(result: Result<RegisterResponse>) {
        when (result) {
            is Result.Success -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            is Result.Error -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Register Failed: ${result.error}", Toast.LENGTH_SHORT).show()
            }
            is Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                Toast.makeText(this, "Registering...", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
