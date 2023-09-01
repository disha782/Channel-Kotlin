package com.example.kotlinapp.Utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

class SharedPreferences(context : Context) {
    val PREFERNCE_FILENAME = "preference"
    val pref : SharedPreferences = context.getSharedPreferences(PREFERNCE_FILENAME, 0)

    val IS_LOGGEG_IN = "isLoggedIn"
    val AUTH_TOKEN = "authToken"
    val USER_EMAIL = "userEmail"

    //variables can have getters and setters
    var isLoggedIn : Boolean
        get() = pref.getBoolean(IS_LOGGEG_IN, false)
        set(value) = pref.edit().putBoolean(IS_LOGGEG_IN, value).apply()

    var authToken : String?
        get() = pref.getString(AUTH_TOKEN, "")
        set(value) = pref.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail : String?
        get() = pref.getString(USER_EMAIL, "")
        set(value) = pref.edit().putString(USER_EMAIL, value).apply()

    var queue = Volley.newRequestQueue(context)
}