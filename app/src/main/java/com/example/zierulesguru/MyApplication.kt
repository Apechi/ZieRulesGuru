package com.example.zierulesguru

import android.app.Application

open class MyApplication : Application() {
    companion object{
        val BASE_URL = "https://ebda-120-89-90-59.ngrok-free.app/api"
        val URL = "https://ebda-120-89-90-59.ngrok-free.app/"
    }
}