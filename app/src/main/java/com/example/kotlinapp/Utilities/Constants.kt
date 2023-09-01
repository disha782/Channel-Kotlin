package com.example.kotlinapp.Utilities

const val TIMEOUT = 300000

const val BASE_URL = "http://xxx.xxx.xx.xxx:5000/v1/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_FORGOT_PASSWORD = "${BASE_URL}account/forgot-password"
const val URL_VERIFY_OTP = "${BASE_URL}account/otp-verify"
const val URL_RESET_PASSWORD = "${BASE_URL}account/update-password"
const val URL_CREATE_USER = "${BASE_URL}user/addUser"
const val URL_LOGIN_FINDEMAIL = "${BASE_URL}user/byEmail/"
const val URL_CHANNEL_GET = "${BASE_URL}channel/get"
const val URL_MESSAGE_VIEW = "${BASE_URL}message/channel/"
const val URL_MESSAGE_DELETE = "${BASE_URL}message/delete/"

const val URL_SOCKET = "http://xxx.xxx.xx.xxx:5000/"

const val BROADCAST_USERDATA = "BROADCAST_CREATE_USER"