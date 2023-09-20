package com.example.zierulesguru.list_data

data class ListPrestasi(
    val status: Int,
    val achievments: ArrayList<dataAchievements>
)

data class dataAchievements(
    val id: Int,
    val name: String,
    val point: Int

)
