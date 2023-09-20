package com.example.zierulesguru.list_data

import java.util.ArrayList

data class ListPelanggaran(
    val status: Int,
    val dataViolation: ArrayList<dataViolations>

)

data class dataViolations(
    val id: Int,
    val name: String,
    val point: Int
)