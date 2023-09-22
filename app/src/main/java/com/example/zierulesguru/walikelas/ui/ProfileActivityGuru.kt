package com.example.zierulesguru.walikelas.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zierulesguru.databinding.ActivityProfileGuruBinding

class ProfileActivityGuru : AppCompatActivity() {

    lateinit var binding: ActivityProfileGuruBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileGuruBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()


    }
}