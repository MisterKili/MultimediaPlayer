package com.example.multimediaplayer

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import java.io.File

class PhotoDetailsActivity : AppCompatActivity() {
    private lateinit var image: File

    private lateinit var imageView: ImageView
    private lateinit var imageNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)

        imageView = findViewById(R.id.imageView)
        imageNameTextView = findViewById(R.id.nameTextView)

        val extras = intent.extras
        if (extras != null && extras.containsKey("photoURI")) {
            val imageURI = extras.getString("photoURI")
            image = File(imageURI)
        }
    }

    override fun onStart() {
        super.onStart()

        imageView.setImageURI(image.path.toUri())
        imageNameTextView.text = image.name
    }
}