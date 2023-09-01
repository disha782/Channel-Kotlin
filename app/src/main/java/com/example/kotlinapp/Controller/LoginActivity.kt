package com.example.kotlinapp.Controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.kotlinapp.Controller.SignUpActivity
import com.example.kotlinapp.R
import com.example.kotlinapp.Services.AuthenticateService

class LoginActivity : AppCompatActivity() {
    lateinit var email_et : EditText
    lateinit var password_et : EditText
    lateinit var progressBar : ProgressBar
    lateinit var login_btn : Button
    lateinit var signin_btn : Button
    lateinit var forgotpasstv : TextView
    lateinit var view_pass : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email_et = findViewById(R.id.loginEmailTxt)
        password_et = findViewById(R.id.loginPasswordText)
        progressBar = findViewById(R.id.progressBar)
        login_btn = findViewById(R.id.loginLoginBtn)
        signin_btn = findViewById(R.id.loginCreateUserBtn)
        forgotpasstv = findViewById(R.id.forgotpasstv)
        view_pass = findViewById(R.id.password_view)
        enableSpinner(false)
        view_pass.tag = R.drawable.baseline_remove_red_eye_24
    }

    fun forgotpassword(view: View){
        val email = email_et.text.toString()
        AuthenticateService.forgotpassword(email){mail_sent ->
            if (mail_sent){
                Toast.makeText(this, "Mail Sent to the Register Email", Toast.LENGTH_LONG).show()
                val intent : Intent = Intent(this, ResetPasswordAuthenticateActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Error In Sending Email", Toast.LENGTH_LONG).show()
            }
        }
//        val intent : Intent = Intent(this, ForgotPassword::class.java)
//       // intent.putExtra("email_id", email)
//        startActivity(intent)
    }

    fun loginLoginBtnClicked(view: View)
    {
        val email = email_et.text.toString()
        val password = password_et.text.toString()
        hidekeyboard()
        if(email.isNotEmpty() && password.isNotEmpty()){
            enableSpinner(true)
            AuthenticateService.loginUser(email, password) { login ->
                if (login) {
                    AuthenticateService.findUserByEmail(this) { findSuccess ->
                        if (findSuccess){
                            val intent : Intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
//                        else if (){
//
//                        }
                        else{
                            enableSpinner(false)
                            errorToast()
                        }
                    }
                }
                else{
                    enableSpinner(false)
                    errorToast()
                }
            }
        }
        else{
            Toast.makeText(this, "Please Fill all details", Toast.LENGTH_LONG).show()
        }

    }
    fun errorToast(){
        Toast.makeText(this, "Incorrect Login Credentials", Toast.LENGTH_LONG ).show()
    }

    fun loginCreateUserBtnClicked(view: View)
    {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun enableSpinner(enable : Boolean){
        if(enable){
            progressBar.visibility = View.VISIBLE
        }
        else{
            progressBar.visibility = View.INVISIBLE
        }
        email_et.isEnabled = !enable
        password_et.isEnabled = !enable
        login_btn.isEnabled = !enable
        signin_btn.isEnabled = !enable

    }

    fun hidekeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken , 0)
        }
    }

    fun password_visible(view: View){
        val statusBtn = view_pass.tag as Int
        if (statusBtn == R.drawable.baseline_remove_red_eye_24){
            view_pass.setImageResource(R.drawable.baseline_visibility_off_24)
            view_pass.tag = R.drawable.baseline_visibility_off_24
            password_et.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }else{
            view_pass.setImageResource(R.drawable.baseline_remove_red_eye_24)
            view_pass.tag = R.drawable.baseline_remove_red_eye_24
            password_et.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }
}