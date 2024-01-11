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
import com.example.zierulesguru.databinding.ActivitySubmitPrestasiBinding
import com.example.zierulesguru.guru_mapel.GuruMapelActivity
import com.example.zierulesguru.guru_mapel.SubmitMessage
import com.example.zierulesguru.list_data.dataAchievements
import com.example.zierulesguru.list_data.dataViolations
import com.example.zierulesguru.walikelas.WaliKelasActivity
import com.google.gson.Gson
import org.json.JSONObject
import java.time.LocalDate

class SubmitPrestasiActivity : AppCompatActivity() {

    lateinit var binding: ActivitySubmitPrestasiBinding
    lateinit var prestasi: Array<String>
    lateinit var tinyDB: TinyDB
    var PrestasiSelectedIds = 0
    lateinit var PrestasiIds: Array<Int>
    lateinit var dataAchievementsArray: ArrayList<dataAchievements>
    lateinit var achievementsPoints: Array<Int>

    override fun onResume() {
        super.onResume()

        dropDownImplement()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySubmitPrestasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tinyDB = TinyDB(this)

        dataAchievementsArray = tinyDB.getListObject("listDataPrestasi", dataAchievements::class.java) as ArrayList<dataAchievements>

        binding.listAutoComplete.setOnItemClickListener { adapterView: AdapterView<*>?, view: View, i: Int, l: Long ->
            PrestasiSelectedIds = PrestasiIds[i]
            binding.pointPrestasi.text = "Point: ${achievementsPoints[i].toString()}"

        }


        binding.submit.setOnClickListener {
            if (binding.deskripsi.text.isNullOrEmpty()) {
                Toast.makeText(this, "Deskripsi Harus Di Isi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitPrestasi(PrestasiSelectedIds, binding.deskripsi.text.toString())
        }

        dropDownImplement()

    }


    public fun dropDownImplement() {

        prestasi = dataAchievementsArray.map {it.name}.toTypedArray()
        PrestasiIds = dataAchievementsArray.map { it.id }.toTypedArray()
        achievementsPoints = dataAchievementsArray.map { it.point }.toTypedArray()
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_layout, prestasi)
        binding.listAutoComplete.setAdapter(arrayAdapter)
    }

    public fun submitPrestasi(prestasiId: Int, deskripsi: String)
    {
        val gson = Gson()
        val studentId = tinyDB.getListInt("siswaScannedList_id")
        val date = LocalDate.now().toString()
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/teacher/add/students/achievment"

        val requestData = HashMap<String, Any>()
        requestData["achievment_id"] = prestasiId
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
                        val role = tinyDB.getString("role")
                        when (role) {
                            "wali-kelas" -> startActivity(Intent(this, WaliKelasActivity::class.java))
                            "guru-mapel" -> startActivity(Intent(this, GuruMapelActivity::class.java))
                        }


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