package com.example.zierulesguru

data class ProfileGuru(
    val status: Int,
    val dataTeacher: dataTeacher
)

data class dataTeacher(
    val email: String,
    val name: String,
    val image: String,
    val role: String
)
