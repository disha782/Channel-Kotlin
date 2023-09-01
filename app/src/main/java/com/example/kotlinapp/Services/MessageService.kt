package com.example.kotlinapp.Services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.example.kotlinapp.Controller.App
import com.example.kotlinapp.Model.ChannelModel
import com.example.kotlinapp.Model.MessageContent
import com.example.kotlinapp.Model.MessageModel
import com.example.kotlinapp.Utilities.TIMEOUT
import com.example.kotlinapp.Utilities.URL_CHANNEL_GET
import com.example.kotlinapp.Utilities.URL_MESSAGE_DELETE
import com.example.kotlinapp.Utilities.URL_MESSAGE_VIEW
import org.json.JSONArray
import org.json.JSONException

object MessageService{
    val channels = ArrayList<ChannelModel>()
    val message = ArrayList<MessageModel>()

    fun getChannel(complete : (Boolean) -> Unit){
        val channelRequest = object : JsonArrayRequest(Method.GET, URL_CHANNEL_GET,
        null, Response.Listener { response ->
                clearChannel()
                try {
                    for (x in 0 until response.length()){
                        val channels = response.getJSONObject(x)
                        val name = channels.getString("name")
                        val channelDesc = channels.getString("description")
                        val channelId = channels.getString("_id")
                        val newChannel = ChannelModel(name, channelDesc, channelId)
                        this.channels.add(newChannel)
                        complete(true)
                    }
                }catch (e : JSONException){
                    Log.d("JSON", "EXCEPTION ${e.localizedMessage}")
                    complete(false)
                }
            }, Response.ErrorListener {
                error -> Log.d("ERROR IN CHANNEL", "ERROR $error")
                        complete(false)
             })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val header : MutableMap<String, String> = HashMap()
                header["Content-Type"] = "application/json"
                header["Accept"] = "*/*"
                header["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return header
            }
        }
        channelRequest.retryPolicy = DefaultRetryPolicy(TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        App.sharedPreferences.queue.add(channelRequest)
    }

    fun getMessage(channel_Id : String, complete: (Boolean) -> Unit){
        val messageRequest = object : JsonArrayRequest(Method.GET, "$URL_MESSAGE_VIEW$channel_Id",
        null, Response.Listener<JSONArray> {response ->
                clearMessage()
                try {
                   // val jsonArray : JSONArray = JSONArray(response)
                    for (x in 0 until response.length()){
                        val msgobj = response.getJSONObject(x)
                        val messageType = msgobj.getString("messagetype")
                        val messagetext = msgobj.getString("message")
                        val userId = msgobj.getString("userId")
                        val channelId = msgobj.getString("channelId")
                        val userName = msgobj.getString("userName")
                        val userAvatar = msgobj.getString("userAvatar")
                        val userAvatarColor = msgobj.getString("userAvatarColor")
                        val timestamp = msgobj.getString("timestamp")
                        val id = msgobj.getString("_id")
                        if (messageType == "image"){
                            val imgBitmap = decodeImageString(messagetext)
                            val newMessage = MessageModel(MessageContent.BitmapData(imgBitmap), messageType, userId,channelId, userName, userAvatar, userAvatarColor,
                                id, timestamp)
                            this.message.add(newMessage)
                            complete(true)
                        }
                        else if (messageType == "text"){
                            val newMessage = MessageModel(MessageContent.StringData(messagetext), messageType,  userId, channelId, userName, userAvatar, userAvatarColor,
                                id, timestamp)
                            this.message.add(newMessage)
                            complete(true)
                        }
                    }
                }catch (e : JSONException){
                    Log.d("MEESAGE", "Error : ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener {error ->
                Log.d("MESSAGE ERROR", "ERROR : ${error.localizedMessage}")
                complete(false)
            })
        {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val header : MutableMap<String, String> = HashMap()
                header["Content-Type"] = "application/json"
                header["Accept"] = "*/*"
                header["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return header
            }
        }
        messageRequest.retryPolicy = DefaultRetryPolicy(TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        App.sharedPreferences.queue.add(messageRequest)
    }

    fun deleteMsgfunc(msg_Id : String, complete: (Boolean) -> Unit){
        val deleteReq = object : StringRequest(Method.DELETE, "$URL_MESSAGE_DELETE$msg_Id", Response.Listener {
            response ->
            Log.d("SUCCESSFUL", "DELETED")
            complete(true)
        }, Response.ErrorListener {
            error ->
            Log.d("ERROR", "$error")
            complete(false)
        })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val header : MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return header
            }
        }
        deleteReq.retryPolicy = DefaultRetryPolicy(TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        App.sharedPreferences.queue.add(deleteReq)
    }

    private fun decodeImageString(messagetext: String): Bitmap {
        val decodeBytes = Base64.decode(messagetext, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodeBytes, 0, decodeBytes.size)
    }

    fun clearChannel(){
        channels.clear()
    }
    fun clearMessage(){
        message.clear()
    }



}