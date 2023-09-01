package com.example.kotlinapp.Controller

import android.app.Application
import com.example.kotlinapp.Utilities.SharedPreferences

class App : Application() {

    companion object{
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = SharedPreferences(applicationContext)
    }
}