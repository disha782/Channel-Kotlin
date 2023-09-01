package com.example.kotlinapp.Adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapp.Model.MessageContent
import com.example.kotlinapp.Model.MessageModel
import com.example.kotlinapp.R
import com.example.kotlinapp.Services.UserDataService
import java.text.ParseException
import java.text.SimpleDateFormat
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.kotlinapp.Controller.ImageViewZoom
import com.example.kotlinapp.Services.MessageService
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class MessageAdapter(val context: Context, val messages : ArrayList<MessageModel>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : View?) : RecyclerView.ViewHolder(itemView!!){
        val userName = itemView?.findViewById<TextView>(R.id.UserName)
        val ivmsg = itemView?.findViewById<ImageView>(R.id.messageImageView)
        val timeStamp = itemView?.findViewById<TextView>(R.id.timestamp)
        val msg = itemView?.findViewById<TextView>(R.id.message)
        val userImage = itemView?.findViewById<ImageView>(R.id.userProfilePic)
        val deleteMessage = itemView?.findViewById<ImageView>(R.id.deleteMsg)
        fun bindMessage(context: Context, message : MessageModel){
            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            userName?.text = message.userName
            timeStamp?.text = setDate(message.timestamp)
            if (message.userId != UserDataService.id){
                deleteMessage?.visibility = View.GONE
            }
            deleteMessage?.setOnClickListener{
                try {
                    val builder : AlertDialog.Builder = AlertDialog.Builder(deleteMessage.context)
                    builder.setMessage("Do You Want To Delete the Message?")
                        .setPositiveButton("Yes") { _, _ ->
                            MessageService.deleteMsgfunc(message.id) {
                                Toast.makeText(deleteMessage.context, "MESSAGE DELETED", Toast.LENGTH_SHORT).show()
                                notifyItemRemoved(position)
                            }
                        }.setNegativeButton("No"){
                                dialog, _ ->
                            dialog.dismiss()
                        }.create()
                    builder.show()
                }catch (e : Exception){
                    Log.d("IDALOG ERROR", "$e")
                }

            }
            if (message.message is MessageContent.BitmapData && message.messagetype == "image"){
                ivmsg?.visibility = View.VISIBLE
                msg?.visibility = View.GONE
                val bitmap = message.message.bitmap

                Log.d("BITMAP", "Width: ${bitmap.width}, Height: ${bitmap.height}")
                Log.d("BITMAP", "Byte Count: ${bitmap.byteCount}")

                ivmsg?.setImageBitmap(bitmap)

//                val uri = saveBitmap(context, bitmap, quality = 80)
//                val urival = Uri.parse(uri)
//                ivmsg?.setImageURI(uri)

                ivmsg?.setOnClickListener {
                    val intent : Intent = Intent(context, ImageViewZoom::class.java)
                    val uri = saveBitmap(context, bitmap, quality = 80)
                    intent.putExtra("BITMAP", uri.toString())
                    Log.d("CLICKED", "IMAGE")
                    context.startActivity(intent)
                }
//                }catch (e : Exception){
//                    Log.e("BITMAP", "Error loading bitmap: ${e.message}")
//                    ivmsg?.visibility = View.GONE
//                    msg?.visibility = View.VISIBLE
//                    msg?.text = "Error loading image"
//                }
            }
            else if (message.message is MessageContent.StringData){
                ivmsg?.visibility = View.GONE
                msg?.visibility = View.VISIBLE
                msg?.text = message.message.data
            }
        }

        fun setDate(isoDate : String) : String{
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")
            var convertedDate = Date()
            try{
                convertedDate = isoFormat.parse(isoDate) as Date
            }catch (e: ParseException){
                Log.d("DATE", "PARSE : $e")
            }
            val opString = SimpleDateFormat("EEE MMM/dd/yyyy HH:mm", Locale.getDefault())
            return opString.format(convertedDate)
        }

        fun saveBitmap(context: Context, bitmap: Bitmap, quality : Int) : Uri {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val tempFile = File(context.cacheDir, "temp_image.jpg")
            tempFile.outputStream().use {
                it.write(outputStream.toByteArray())
            }
            return Uri.fromFile(tempFile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list, null, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessage(context, messages[position])
    }


}