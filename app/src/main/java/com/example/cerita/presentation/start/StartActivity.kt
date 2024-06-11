package com.example.cerita.presentation.start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.cerita.R
import com.example.cerita.databinding.ActivityStartBinding
import com.example.cerita.presentation.ViewModelFactory
import com.example.cerita.presentation.login.LoginActivity
import com.example.cerita.presentation.main.MainActivity
import com.example.cerita.presentation.register.RegisterActivity

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var startViewModel: StartViewModel
    private lateinit var motionLayout: MotionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        motionLayout = findViewById(R.id.motionLayout)
        motionLayout.transitionToEnd()

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
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            binding.btnLogin,
            "button_transition"
        )
        startActivity(intent, option.toBundle())
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            binding.btnRegister,
            "button_transition"
        )
        startActivity(intent, option.toBundle())
    }
}
