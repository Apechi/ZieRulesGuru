package com.example.zierulesguru.dataSiswa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.Request
import com.android.volley.Request.Method
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.zierulesguru.MyApplication
import com.example.zierulesguru.R
import com.example.zierulesguru.TinyDB
import com.example.zierulesguru.databinding.ActivityDataSiswaBinding

class DataSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataSiswaBinding
    private lateinit var tinyDb: TinyDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDataSiswaBinding.inflate(layoutInflater)

        setContentView(binding.root)



    }

    fun getSiswa() {
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/teacher/list/student"

        val jsonObjectRequest = object :  JsonObjectRequest(
            Method.GET,
            url,
            null,
            {res ->

            },
            {
                err->
            }
            ) {}
    }
}