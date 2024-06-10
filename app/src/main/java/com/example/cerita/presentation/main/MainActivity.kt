package com.example.cerita.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cerita.R
import com.example.cerita.databinding.ActivityMainBinding
import com.example.cerita.di.Injection
import com.example.cerita.presentation.ViewModelFactory
import com.example.cerita.presentation.start.StartActivity
import com.example.cerita.presentation.upload.UploadActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listAdapter: ListAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Injection.provideRepository(this))
        )[MainViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        mainViewModel.getList()

        setupUploadButton()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
                val intent = Intent(this@MainActivity, StartActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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


    }

    private fun setupUploadButton() {
        binding.buttonUpload.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }
}
