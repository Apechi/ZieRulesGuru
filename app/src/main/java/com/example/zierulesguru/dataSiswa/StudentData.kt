package com.example.zierulesguru
data class StudentData (
    val status: Int,
    val students: List<StudentElement>
)

data class StudentElement (
    val id: Int,
    val nis: String,
    val name: String,
    val image: String,
    val password: String
)

