package com.example.cerita.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cerita.databinding.ActivityMainBinding
import com.example.cerita.di.Injection
import com.example.cerita.presentation.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listAdapter: ListAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this, ViewModelFactory(Injection.provideRepository(this)))[MainViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        mainViewModel.getList()
    }

    private fun setupRecyclerView() {
        listAdapter = ListAdapter(emptyList())
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = listAdapter
    }

    private fun observeViewModel() {
        mainViewModel.listStory.observe(this) { stories ->
            listAdapter.updateStories(stories)
        }

        mainViewModel.getSession().observe(this) {
            // Handle session data if needed
        }
    }
}
