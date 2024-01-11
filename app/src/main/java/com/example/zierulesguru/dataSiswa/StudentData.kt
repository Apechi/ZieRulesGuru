package com.example.zierulesguru
data class StudentData (
    val status: Long,
    val students: List<StudentElement>
)

data class StudentElement (
    val id: Long,
    val nis: String,
    val name: String,
    val image: String,
    val password: String
)

