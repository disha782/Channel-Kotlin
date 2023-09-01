package com.example.kotlinapp.Model

class ChannelModel(val name : String, val description : String, val id : String) {
    override fun toString(): String {
        return "#$name"
    }
}