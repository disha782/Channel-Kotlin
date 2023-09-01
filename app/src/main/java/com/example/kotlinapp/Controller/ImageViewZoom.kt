package com.example.kotlinapp.Controller

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinapp.R
import com.example.kotlinapp.Services.MessageService

class ImageViewZoom : AppCompatActivity() {

    lateinit var ViewZoom : ImageView
    lateinit var CancelBtn : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagezoom)
        ViewZoom = findViewById(R.id.sentImageIV)
        CancelBtn = findViewById(R.id.cancelViewBtn)
        val uri = intent.getStringExtra("BITMAP")
        val bitmap = Uri.parse(uri)
        ViewZoom.setImageURI(bitmap)
        CancelBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        Log.d("IMAGE VIEW", "ZOOM")

    }
}