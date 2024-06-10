package com.example.cerita.presentation.upload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.cerita.data.response.UploadResponse
import com.example.cerita.databinding.ActivityUploadBinding
import com.example.cerita.di.Injection
import com.example.cerita.di.Result
import com.example.cerita.presentation.ViewModelFactory
import com.example.cerita.presentation.main.MainActivity
import com.example.cerita.utils.Upload
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var uploadViewModel: UploadViewModel

    private var selectedImageUri: Uri? = null

    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let { uri ->
                    Glide.with(this).load(uri).into(binding.previewImage)
                }
            }
        }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    selectedImageUri = Upload.getImageUri(this, it)
                    Glide.with(this).load(selectedImageUri).into(binding.previewImage)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uploadViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Injection.provideRepository(this))
        )[UploadViewModel::class.java]

        binding.galleryButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
                )
            } else {
                openGalleryForImage()
            }
        }

        binding.cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            } else {
                openCameraForImage()
            }
        }

        binding.uploadButton.setOnClickListener {
            val description = binding.edAddDescription.text.toString()
            if (selectedImageUri != null && description.isNotBlank()) {
                val imageFile = selectedImageUri?.let { uri -> Upload.uriToFile(uri, this) }
                    ?: return@setOnClickListener
                Upload.reduceFileImage(imageFile)
                binding.progressIndicator.visibility = LinearProgressIndicator.VISIBLE
                lifecycleScope.launch {
                    val result = uploadViewModel.uploadResult.firstOrNull()
                    if (result != null) {
                        when (result) {
                            is Result.Success -> {
                                binding.progressIndicator.visibility = LinearProgressIndicator.GONE
                                handleUploadSuccess(result.data)
                            }

                            is Result.Error -> {
                                binding.progressIndicator.visibility = LinearProgressIndicator.GONE
                                handleUploadError(result.error)
                            }

                            is Result.Loading -> binding.progressIndicator.visibility =
                                LinearProgressIndicator.VISIBLE
                        }
                    } else {
                        binding.progressIndicator.visibility = LinearProgressIndicator.GONE
                        Toast.makeText(
                            this@UploadActivity,
                            "No result from upload",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Please select an image and provide a description",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleUploadSuccess(uploadResponse: UploadResponse) {
        Toast.makeText(this, "Upload successful: ${uploadResponse.message}", Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun handleUploadError(errorMessage: String) {
        Toast.makeText(this, "Upload failed: $errorMessage", Toast.LENGTH_SHORT).show()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResultLauncher.launch(intent)
    }

    private fun openCameraForImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryForImage()
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraForImage()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 100
        private const val REQUEST_CAMERA_PERMISSION = 101
    }
}