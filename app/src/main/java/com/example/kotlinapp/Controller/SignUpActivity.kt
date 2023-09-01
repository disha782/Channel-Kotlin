package com.example.kotlinapp.Controller

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.kotlinapp.R
import com.example.kotlinapp.Services.AuthenticateService
import com.example.kotlinapp.Services.UserDataService
import com.example.kotlinapp.Utilities.BROADCAST_USERDATA
import kotlin.random.Random

class SignUpActivity : AppCompatActivity() {

    public var userAvatar = "profiledefault"
    public var colorIos = "[0.5, 0.5, 0.5, 1]"
    lateinit var userProfileImg : ImageView
    lateinit var et_email : EditText
    lateinit var et_password : EditText
    lateinit var et_repeat_password : EditText
    lateinit var et_username : EditText
    lateinit var progressBar: ProgressBar
    lateinit var createUserBtn : Button
    lateinit var bgBtn : Button
    lateinit var viewpassbtn : ImageButton
    lateinit var reviewpassbtn : ImageButton
    lateinit var condition_Password : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        userProfileImg = findViewById(R.id.createAvatarImageView)
        et_email = findViewById(R.id.signupEmailText)
        et_password = findViewById(R.id.signupPasswordText)
        et_repeat_password = findViewById(R.id.signupPasswordTextRepeat)
        et_username = findViewById(R.id.signupUsernameText)
        progressBar = findViewById(R.id.signup_progressBar)
        createUserBtn = findViewById(R.id.signupcreateUserBtn)
        bgBtn = findViewById(R.id.backgroundColorBtn)
        condition_Password = findViewById(R.id.password_condition)
        viewpassbtn = findViewById(R.id.pass_view_btn)
        reviewpassbtn = findViewById(R.id.repass_view_btn)
        condition_Password.visibility = View.INVISIBLE
        enableSpinner(false)

        viewpassbtn.tag = R.drawable.baseline_remove_red_eye_24
        reviewpassbtn.tag = R.drawable.baseline_remove_red_eye_24
       // val email = et_email.text.toString()
       // val password = et_password.text.toString()
    }

    fun generateColorClicked(view : View){
        val r = Random.nextInt(255)
        val g = Random.nextInt(255)
        val b = Random.nextInt(255)
        userProfileImg.setBackgroundColor(Color.rgb(r,g,b))

        val Iosr = r.toDouble()/255
        val Iosg = g.toDouble()/255
        val Iosb = b.toDouble()/255
        colorIos = "[$Iosr, $Iosg, $Iosb, 1]"
        print(colorIos)
    }

    fun createUserClicked(view: View)
    {
        val username = et_username.text.toString()
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        val re_password = et_repeat_password.text.toString()
       // enableSpinner(true)

        hidekeyboard()
        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && re_password.isNotEmpty())
        {
            if (password == re_password) {
                if (email.contains("@") && password.length >= 6){
                    enableSpinner(true)
                    AuthenticateService.registerUser(email ,password) { registerUser ->
                        if (registerUser) {
                            Log.d("AUTHENTICATION", "SUCCESS")
                            AuthenticateService.loginUser(email, password) {loginUser ->
                                if (loginUser){
                                    Log.d("LOGIN", "LOGIN SUCCESSFUL")
                                    AuthenticateService.createUser(username, email, userAvatar, colorIos) { createUser ->
                                        if (createUser) {
//                                        println(UserDataService.uname)
//                                        println(UserDataService.email)
//                                        println(UserDataService.userAvatar)
//                                        println(UserDataService.userAvatarColor)
                                            Log.d("activity", "login")

                                            val userDataChange = Intent(BROADCAST_USERDATA)
                                            LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                            finish()

                                        }
                                        else{
                                            errorToast()
                                            enableSpinner(false)
                                        }
                                    }
                                }
                                else{
                                    errorToast()
                                    enableSpinner(false)
                                }
                            }
                        }
                        else{
                            Log.d("AUTHENTICATION", "ERROR OCCURED")
                            errorToast()
                            enableSpinner(false)
                        }
                    }
                }
                else
                {
                    Toast.makeText(this, "Enter Valid Email or Password", Toast.LENGTH_LONG ).show()
                    condition_Password.visibility = View.VISIBLE
                }
            }
            else
            {
                Toast.makeText(this, "PLease Match the Passwords", Toast.LENGTH_LONG ).show()
                condition_Password.visibility = View.VISIBLE
            }
        }
        else{
            Toast.makeText(this, "Enter All Fields", Toast.LENGTH_LONG ).show()
        }


    }

    fun errorToast(){
        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_LONG ).show()
    }

    fun generateUserAvatar(view: View)
    {
        val icon_color = Random.nextInt(2)
        val icon_num = Random.nextInt(28)
        userAvatar = if (icon_color == 0 ) {
            "light$icon_num"
        } else {
            "dark$icon_num"
        }
        val resources = resources.getIdentifier(userAvatar, "drawable", packageName)
        userProfileImg.setImageResource(resources)

    }

    fun enableSpinner(enable : Boolean){
        if(enable){
            progressBar.visibility = View.VISIBLE
        }
        else{
            progressBar.visibility = View.INVISIBLE
        }
        bgBtn.isEnabled = !enable
        createUserBtn.isEnabled = !enable
        et_email.isEnabled = !enable
        et_username.isEnabled = !enable
        et_password.isEnabled = !enable

    }

    fun hidekeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken , 0)
        }
    }

    fun password_visible_login(view: View) {
        val currentDrawableId_pass = viewpassbtn.tag as Int
        val currentDrawableId_repass = reviewpassbtn.tag as Int

        if (currentDrawableId_pass == R.drawable.baseline_remove_red_eye_24 && viewpassbtn.isPressed){
            viewpassbtn.setImageResource(R.drawable.baseline_visibility_off_24)
            viewpassbtn.tag = R.drawable.baseline_visibility_off_24
            et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        else if (currentDrawableId_pass == R.drawable.baseline_visibility_off_24 && viewpassbtn.isPressed){
            viewpassbtn.setImageResource(R.drawable.baseline_remove_red_eye_24)
            viewpassbtn.tag = R.drawable.baseline_remove_red_eye_24
            et_password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        else if (currentDrawableId_repass == R.drawable.baseline_remove_red_eye_24 && reviewpassbtn.isPressed){
            reviewpassbtn.setImageResource(R.drawable.baseline_visibility_off_24)
            reviewpassbtn.tag = R.drawable.baseline_visibility_off_24
            et_repeat_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        else if (currentDrawableId_repass == R.drawable.baseline_visibility_off_24 && reviewpassbtn.isPressed){
            reviewpassbtn.setImageResource(R.drawable.baseline_remove_red_eye_24)
            reviewpassbtn.tag = R.drawable.baseline_remove_red_eye_24
            et_repeat_password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }
}