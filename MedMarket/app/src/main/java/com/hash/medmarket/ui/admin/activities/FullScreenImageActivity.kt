package com.hash.medmarket.ui.admin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import com.hash.medmarket.R
import com.squareup.picasso.Picasso

class FullScreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val photoView: PhotoView = findViewById(R.id.photo_view)

        val imageUrl = intent.getStringExtra("image_url")

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(photoView)
        }
        photoView.setOnClickListener {
            finish()
        }
    }
}
