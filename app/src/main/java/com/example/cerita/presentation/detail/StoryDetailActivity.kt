package com.example.cerita.presentation.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cerita.databinding.ActivityStoryDetailBinding

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyName = intent.getStringExtra("storyName")
        val storyTime = intent.getStringExtra("storyTime")
        val storyDescription = intent.getStringExtra("storyDescription")
        val storyPhotoUrl = intent.getStringExtra("storyPhotoUrl")

        binding.tvDetailName.text = storyName
        binding.tvTime.text = storyTime
        binding.tvDetailDescription.text = storyDescription
        Glide.with(this)
            .load(storyPhotoUrl)
            .into(binding.ivDetailPhoto)
    }
}
