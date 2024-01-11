package com.example.zierulesguru.dataSiswa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Request.Method
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.zierulesguru.MyApplication
import com.example.zierulesguru.R
import com.example.zierulesguru.StudentData
import com.example.zierulesguru.TinyDB
import com.example.zierulesguru.databinding.ActivityDataSiswaBinding
import com.google.gson.Gson
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

class DataSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataSiswaBinding
    private lateinit var tinyDb: TinyDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDataSiswaBinding.inflate(layoutInflater)
        tinyDb = TinyDB(this)

        setContentView(binding.root)

        getSiswa()

        binding.swipeRefresh.setOnRefreshListener {
            getSiswa()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    fun getSiswa() {
        binding.swipeRefresh.isRefreshing = true
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/teacher/list/student"

        val jsonObjectRequest = object :  JsonObjectRequest(
            Method.GET,
            url,
            null,
            {res ->
                try {
                    val gson = Gson();
                    val studentData = gson.fromJson(res.toString(), StudentData::class.java)
                    if (studentData.status == 200) {
                        if (studentData.students.count() <= 0) {
                            binding.checkData.text = "Data Tidak Ada!"

                        }
                        val itemAdapter = ItemAdapter<StudentDataBinding>()
                        val fastAdapter = FastAdapter.with(itemAdapter)
                        binding.recycleView.layoutManager = LinearLayoutManager(this)
                        binding.recycleView.adapter = fastAdapter
                        for (student in studentData.students) {
                            var image = "${MyApplication.URL}${student.image}"
                            image = image.replace("public", "storage")
                            val item = StudentDataBinding().apply {
                                this.nis = student.nis
                                this.name = student.name
                                this.password = student.password
                                this.image = image
                            }
                            itemAdapter.add(item)
                        }
                        binding.swipeRefresh.isRefreshing = false
                    } else {
                        Toast.makeText(this, res.toString(), Toast.LENGTH_SHORT).show()
                    }

                } catch (e:Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }

            },
            {
                err->
                Toast.makeText(this, err.message, Toast.LENGTH_SHORT).show()

            }
            ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                val token = "Bearer ${tinyDb.getString("token")}"
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
}