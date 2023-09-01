package com.example.kotlinapp.Services

import android.graphics.Color
import com.example.kotlinapp.Controller.App
import java.util.Scanner

object UserDataService {
    var uname = ""
    var email = ""
    var userAvatar = ""
    var userAvatarColor = ""
    var id = ""

    fun logout(){
        email = ""
        userAvatar = ""
        userAvatarColor = ""
        id = ""
        App.sharedPreferences.authToken = ""
        App.sharedPreferences.userEmail = ""
        App.sharedPreferences.isLoggedIn = false
    }

    fun returnAvatarColor(components : String) : Int {
        val strippedColor = components.replace("[", "")
            .replace("]", "")
            .replace(",","")

        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)
        if (scanner.hasNext()){
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }
        return Color.rgb(r, g, b)
    }

}