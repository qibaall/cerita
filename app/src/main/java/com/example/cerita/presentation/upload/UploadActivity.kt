package com.example.cerita.presentation.upload

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cerita.R
import com.example.cerita.databinding.ActivityUploadBinding
import com.example.cerita.di.Result
import com.example.cerita.presentation.ViewModelFactory
import com.example.cerita.presentation.main.MainActivity
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private var imageUri: Uri? = null
    private val viewModel: UploadViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory.getInstance(this))[UploadViewModel::class.java]
    }
    private val galleryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            imageUri = data?.data
            binding.previewImage.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { gallery() }
        binding.uploadButton.setOnClickListener { uploadImg() }
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryResultLauncher.launch(intent)
    }

    private fun uploadImg() {
        imageUri?.let { uri ->
            val fileImage = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImg: ${fileImage.path}")
            val description = binding.edAddDescription.text.toString()

            lifecycleScope.launch {
                viewModel.uploadStories(fileImage, description).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            loading(true)
                        }

                        is Result.Success -> {
                            toast(result.data.message)
                            loading(false)

                            val intent = Intent(this@UploadActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)

                        }

                        is Result.Error -> {
                            toast(result.error)
                            loading(false)
                        }
                    }
                }
            }
        } ?: toast(getString(R.string.NoImg))
    }

    private fun uriToFile(imageUri: Uri, context: Context): File {
        val tempFile = createTempFile(context)
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    private fun createTempFile(context: Context): File {
        val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
        return File(context.cacheDir, fileName)
    }

    private fun File.reduceFileImage(): File {
        // Implement image compression logic here if needed
        return this
    }

    private fun loading(isLoading: Boolean) {
        // Implement loading indicator logic here if needed
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
