package com.example.zierulesguru.ScanHistory

data class HistoryScan(
    val status: Int,
    val dataHistoryScan: ArrayList<dataHistoryScan>
)


data class dataHistoryScan(
    val type: String,
    val teacher: String,
    val student: String,
    val name: String,
    val date: String,
)
