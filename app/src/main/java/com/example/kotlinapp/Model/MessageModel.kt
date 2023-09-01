package com.example.kotlinapp.Model

import android.graphics.Bitmap

sealed class MessageContent {
    data class BitmapData(val bitmap: Bitmap) : MessageContent()
    data class StringData(val data : String) : MessageContent()
}

class MessageModel(
    val message: MessageContent, val messagetype: String, val userId: String, val channelId: String, val userName: String,
    val userAvatar: String, val userAvatarColor: String,
    val id: String,
    val timestamp: String)
{
}