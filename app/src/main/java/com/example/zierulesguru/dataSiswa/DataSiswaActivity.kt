package com.example.zierulesguru.dataSiswa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}