package com.example.kotlinapp.Controller

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinapp.R
import com.example.kotlinapp.Services.AuthenticateService

class ResetPasswordAuthenticateActivity : AppCompatActivity() {
    lateinit var et_optno1 : EditText
    lateinit var et_optno2 : EditText
    lateinit var et_optno3 : EditText
    lateinit var et_optno4 : EditText
    lateinit var timer : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassauthenticate)
        et_optno1 = findViewById(R.id.otpNumber1)
        et_optno2 = findViewById(R.id.otpNumber2)
        et_optno3 = findViewById(R.id.otpNumber3)
        et_optno4 = findViewById(R.id.otpNumber4)
        timer = findViewById(R.id.timer_tv)

        et_optno1.addTextChangedListener(object  : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length == 1){
                    et_optno2.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        et_optno2.addTextChangedListener(object  : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length == 1){
                    et_optno3.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        et_optno3.addTextChangedListener(object  : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length == 1){
                    et_optno4.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val valuetimer = object : CountDownTimer(300000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                timer.text = (millisUntilFinished/1000).toString() + " seconds"
            }

            override fun onFinish() {
                timer.text = "Click Here To Resend OTP"
            }
        }
        valuetimer.start()
    }

    fun submitOtp(view : View){
        val otp_no1 = et_optno1.text.toString()
        val otp_no2 = et_optno2.text.toString()
        val otp_no3 = et_optno3.text.toString()
        val otp_no4 = et_optno4.text.toString()

        val otp = otp_no1+otp_no2+otp_no3+otp_no4
        Log.d("OTP NUMBER", otp)
        AuthenticateService.verifyotp(otp) { complete ->
            if (complete){
                val email = intent.getStringExtra("email")
                val intent : Intent = Intent(this, ForgotPassword::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
                Toast.makeText(this, "OTP Verified", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_LONG).show()
            }
        }
    }
}