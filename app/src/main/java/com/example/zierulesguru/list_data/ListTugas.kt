package com.example.zierulesguru.list_data

data class ListTugas(
    val status: Int,
    val dataTask: ArrayList<dataTask>
)


data class dataTask(
    val id: Int,
    val name: String,
)
