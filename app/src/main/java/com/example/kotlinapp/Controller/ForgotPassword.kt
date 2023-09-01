package com.example.kotlinapp.Controller

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinapp.R
import com.example.kotlinapp.Services.AuthenticateService

class ForgotPassword : AppCompatActivity() {
    lateinit var email_tv : EditText
    lateinit var password_tv : EditText
    lateinit var password_tv_repeat : EditText
    lateinit var submit_btn : Button
    lateinit var progressBar: ProgressBar
    lateinit var pass_visible : ImageButton
    lateinit var repass_visible : ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)
        email_tv = findViewById(R.id.forgotpassEmailTxt)
        password_tv = findViewById(R.id.forgotpassPasswordText)
        password_tv_repeat = findViewById(R.id.forgotpassPasswordTextRepeat)
        submit_btn = findViewById(R.id.forgotpassChangeBtn)
        progressBar = findViewById(R.id.progressBar)
        pass_visible = findViewById(R.id.password_visibility)
        repass_visible = findViewById(R.id.repassword_visibility)
        val email = intent.getStringExtra("email")
        email_tv.setText(email)
        pass_visible.tag = R.drawable.baseline_remove_red_eye_24
        repass_visible.tag = R.drawable.baseline_remove_red_eye_24
    }
    fun resetPassword(view : View){
        val email_text = email_tv.text.toString()
        val password = password_tv.text.toString()
        val repeatpassword = password_tv_repeat.text.toString()

        if (email_text.isNotEmpty() && password.isNotEmpty() && repeatpassword.isNotEmpty()){
            if (email_text.contains("@") && password.length >= 6){
                if(password == repeatpassword){
                    Log.d("EMAIL", email_text)
                    AuthenticateService.resetpassword(email_text, password) { complete ->
                        if (complete){
                            progressBar.visibility = View.VISIBLE
                            val intent : Intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this, "Password Reset Error", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Match the Passwords", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Enter Valid Email and Password", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Enter All Fields", Toast.LENGTH_SHORT).show()
        }
    }

    fun visiblity_passowrd(view: View){
        val currentDrawableId_pass = pass_visible.tag as Int
        val currentDrawableId_repass = repass_visible.tag as Int

        if (currentDrawableId_pass == R.drawable.baseline_remove_red_eye_24 && pass_visible.isPressed){
            pass_visible.setImageResource(R.drawable.baseline_visibility_off_24)
            pass_visible.tag = R.drawable.baseline_visibility_off_24
            password_tv.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        else if (currentDrawableId_pass == R.drawable.baseline_visibility_off_24 && pass_visible.isPressed){
            pass_visible.setImageResource(R.drawable.baseline_remove_red_eye_24)
            pass_visible.tag = R.drawable.baseline_remove_red_eye_24
            password_tv.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        else if (currentDrawableId_repass == R.drawable.baseline_remove_red_eye_24 && repass_visible.isPressed){
            repass_visible.setImageResource(R.drawable.baseline_visibility_off_24)
            repass_visible.tag = R.drawable.baseline_visibility_off_24
            password_tv_repeat.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        else if (currentDrawableId_repass == R.drawable.baseline_visibility_off_24 && repass_visible.isPressed){
            repass_visible.setImageResource(R.drawable.baseline_remove_red_eye_24)
            repass_visible.tag = R.drawable.baseline_remove_red_eye_24
            password_tv_repeat.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }
}