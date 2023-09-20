package com.example.zierulesguru.barcodeScanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.zierulesguru.MyApplication
import com.example.zierulesguru.R
import com.example.zierulesguru.TinyDB
import com.example.zierulesguru.databinding.ActivitySubmitTugasBinding
import com.example.zierulesguru.guru_mapel.GuruMapelActivity
import com.example.zierulesguru.guru_mapel.SubmitMessage
import com.example.zierulesguru.list_data.dataAchievements
import com.example.zierulesguru.list_data.dataTask
import com.google.gson.Gson
import org.json.JSONObject
import java.time.LocalDate

class SubmitTugasActivity : AppCompatActivity() {

    lateinit var binding: ActivitySubmitTugasBinding
    lateinit var task: Array<String>
    lateinit var tinyDB: TinyDB
    var taskSelectedId =  0
    lateinit var TaskIds: Array<Int>
    lateinit var dataTaskArray: ArrayList<dataTask>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tinyDB = TinyDB(this)

        binding = ActivitySubmitTugasBinding.inflate(layoutInflater)

        setContentView(binding.root)

        dataTaskArray = tinyDB.getListObject("listDataTugas", dataTask::class.java) as ArrayList<dataTask>

        binding.listAutoComplete
            .setOnItemClickListener { adapterView: AdapterView<*>?, view: View, i: Int, l: Long ->
            taskSelectedId = TaskIds[i]

        }

        binding.submit.setOnClickListener {
            if (binding.deskripsi.text.isNullOrEmpty()) {
                Toast.makeText(this, "Deskripsi Harus Di Isi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitPrestasi(taskSelectedId, binding.deskripsi.text.toString())
        }

        dropDownImplement()
    }


    public fun dropDownImplement() {

        task = dataTaskArray.map {it.name}.toTypedArray()
        TaskIds = dataTaskArray.map { it.id }.toTypedArray()
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_layout, task)
        binding.listAutoComplete.setAdapter(arrayAdapter)
    }

    public fun submitPrestasi(tugasId: Int, deskripsi: String)
    {
        val gson = Gson()
        val studentId = tinyDB.getListInt("siswaScannedList_id")
        val date = LocalDate.now().toString()
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/teacher/add/students/task"

        val requestData = HashMap<String, Any>()
        requestData["task_id"] = taskSelectedId
        requestData["date"] = date
        requestData["description"] = deskripsi
        requestData["student_id"] = studentId

        val jsonObject = JSONObject(requestData as Map<*, *>?)

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonObject,
            {
                    res ->
                try {

                    val submitMesssage = gson.fromJson(res.toString(), SubmitMessage::class.java)
                    if (submitMesssage.status == 200) {
                        tinyDB.putListString("siswaScannedList_nama", ArrayList() )
                        tinyDB.putListInt("siswaScannedList_id", ArrayList() )
                        Toast.makeText(this, submitMesssage.message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, GuruMapelActivity::class.java))

                    } else {
                        Toast.makeText(this, res.toString(), Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

                }
            }, {
                    err ->
                Toast.makeText(this, err.toString(), Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${tinyDB.getString("token")}"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

}