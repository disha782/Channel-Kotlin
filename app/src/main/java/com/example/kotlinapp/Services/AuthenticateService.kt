package com.example.kotlinapp.Services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.kotlinapp.Controller.App
import com.example.kotlinapp.Utilities.BROADCAST_USERDATA
import com.example.kotlinapp.Utilities.URL_FORGOT_PASSWORD
import com.example.kotlinapp.Utilities.TIMEOUT
import com.example.kotlinapp.Utilities.URL_CREATE_USER
import com.example.kotlinapp.Utilities.URL_LOGIN
import com.example.kotlinapp.Utilities.URL_LOGIN_FINDEMAIL
import com.example.kotlinapp.Utilities.URL_REGISTER
import com.example.kotlinapp.Utilities.URL_RESET_PASSWORD
import com.example.kotlinapp.Utilities.URL_VERIFY_OTP
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

object AuthenticateService {
    fun registerUser(
        email: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val stringReq: StringRequest =
            object : StringRequest(Method.POST, URL_REGISTER,
                Response.Listener { response ->
                    // response
                    var strResp = response.toString()
                    Log.d("API", strResp)
                    complete(true)
                },
                Response.ErrorListener { error ->
                    Log.d("API", "error => $error")
                    complete(false)
                }
            ) {

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers["Content-Type"] = "application/json"
                    headers["Accept"] = "*/*"
                    //headers["Authorization"] = "Bearer your_token_here"
                    // Add other required headers if applicable
                    return headers
                }
            }
        stringReq.retryPolicy = DefaultRetryPolicy(
            TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        App.sharedPreferences.queue.add(stringReq)
    }

    fun loginUser(email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()
        val jsonObjectRequest =
            object : JsonObjectRequest(Request.Method.POST, URL_LOGIN, null, { response ->
                println(response)
                try {
                    App.sharedPreferences.isLoggedIn = true
                    App.sharedPreferences.userEmail = response.getString("user")
                    App.sharedPreferences.authToken = response.getString("token")
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", "EXCEPTION" + e.localizedMessage)
                    complete(false)
                }
            },
                { error ->
                    Log.d("LOGIN ERROR", "ERROR $error")
                    complete(false)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers["Content-Type"] = "application/json"
                    headers["Accept"] = "*/*"
                    //headers["Authorization"] = "Bearer your_token_here"
                    // Add other required headers if applicable
                    return headers
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            TIMEOUT,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        App.sharedPreferences.queue.add(jsonObjectRequest)
    }

    fun createUser(uname: String, email: String, userAvatar: String,
        userAvatarColor: String, complete: (Boolean) -> Unit)
    {
        val jsonBody = JSONObject()
        jsonBody.put("uname", uname)
        jsonBody.put("email", email)
        jsonBody.put("userAvatar", userAvatar)
        jsonBody.put("userAvatarColor", userAvatarColor)
        val requestBody = jsonBody.toString()
        //Log.d("HELP ME", "REPSONSE CHECK")

        val jsonObjectBody = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, {response ->
            println(response)
            try {
                UserDataService.uname = response.getString("uname")
                UserDataService.email = response.getString("email")
                UserDataService.userAvatar = response.getString("userAvatar")
                UserDataService.userAvatarColor = response.getString("userAvatarColor")
                UserDataService.id = response.getString("_id")
                Log.d("user", "sucess")
                complete(true)
            }catch (e : JSONException){
                Log.d("CREATE USER", "error => $e")
                complete(false)
            }
        }, {error ->
            Log.e("CREATE USER", "Error: ${error.networkResponse?.statusCode}, ${error.message}", error)
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

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }
        jsonObjectBody.retryPolicy = DefaultRetryPolicy(TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        App.sharedPreferences.queue.add(jsonObjectBody)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit){

        val findUserRequest = object : JsonObjectRequest(Method.GET, "$URL_LOGIN_FINDEMAIL${App.sharedPreferences.userEmail}", null, Response.Listener{response ->
            try {
                UserDataService.uname = response.getString("uname")
                UserDataService.email = response.getString("email")
                UserDataService.userAvatar = response.getString("userAvatar")
                UserDataService.userAvatarColor = response.getString("userAvatarColor")
                UserDataService.id = response.getString("_id")
                val userDataChange = Intent(BROADCAST_USERDATA)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                complete(true)

            }catch (e : JSONException){
                Log.d("JSON", "error $e")
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", error.toString())
            complete(false)
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers : MutableMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "*/*"
                headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return headers
            }
        }
        findUserRequest.retryPolicy = DefaultRetryPolicy(TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        App.sharedPreferences.queue.add(findUserRequest)
    }

    fun verifyotp(otp : String, complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("otp", otp)
        val requestBody = jsonBody.toString()
        val verifyotpreq = object : JsonObjectRequest(Method.POST, URL_VERIFY_OTP, null, Response.Listener { response ->
            try {
                complete(true)
            }catch (e : JSONException){
                Log.d("OTP VERIFICATION ERROR JSON", e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("OTP VERIFICATION ERROR", error.toString())
            complete(false)
        })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "*/*"
                //headers["Authorization"] = "Bearer your_token_here"
                // Add other required headers if applicable
                return headers
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }
        verifyotpreq.retryPolicy = DefaultRetryPolicy(
            TIMEOUT,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        App.sharedPreferences.queue.add(verifyotpreq)
    }

    fun forgotpassword(email: String, complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        val requestBody = jsonBody.toString()
        val forgotPasswordReq = object : JsonObjectRequest(Method.POST, URL_FORGOT_PASSWORD, null, Response.Listener {response ->
            try {
                complete(true)
            }catch (e : JSONException){
                Log.d("JSON", "EXCEPTION" + e.localizedMessage)
                complete(false)
            }
        },
        Response.ErrorListener { error ->
            Log.d("LOGIN ERROR", "ERROR $error")
            complete(false)
        }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers["Content-Type"] = "application/json"
                    headers["Accept"] = "*/*"
                    headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                    return headers
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
        }
        forgotPasswordReq.retryPolicy = DefaultRetryPolicy(
            TIMEOUT,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        App.sharedPreferences.queue.add(forgotPasswordReq)
    }

    fun resetpassword(email: String, password: String, complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val resetBody = jsonBody.toString()
        val resetReq = object : JsonObjectRequest(Method.POST, URL_RESET_PASSWORD, null ,Response.Listener {response ->
            try{
                complete(true)
            }
            catch (e : JSONException){
                e.localizedMessage?.let { Log.d("ERROR IN PASSWORD", it.toString()) }
                complete(false)
            }
        }, Response.ErrorListener { error ->
            error.localizedMessage?.let { Log.d("ERROR IN RESET PASSWORD", it.toString()) }
            complete(false)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "*/*"
                headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return headers
            }
            override fun getBody(): ByteArray {
                return resetBody.toByteArray(Charset.defaultCharset())
            }

        }
        resetReq.retryPolicy = DefaultRetryPolicy(
            TIMEOUT,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        App.sharedPreferences.queue.add(resetReq)
    }

}