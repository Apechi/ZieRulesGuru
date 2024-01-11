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
import com.example.zierulesguru.databinding.ActivitySubmitPelangganranBinding
import com.example.zierulesguru.guru_mapel.GuruMapelActivity
import com.example.zierulesguru.guru_mapel.SubmitMessage

import com.example.zierulesguru.list_data.dataViolations
import com.example.zierulesguru.walikelas.WaliKelasActivity
import com.google.gson.Gson
import org.json.JSONObject
import java.time.LocalDate


class SubmitPelangganranActivity : AppCompatActivity() {

    lateinit var binding: ActivitySubmitPelangganranBinding
    lateinit var pelanggaran: Array<String>
    lateinit var tinyDB: TinyDB
    var PelanggaranSelectedIds = 0
    lateinit var PelaranggaranIds: Array<Int>
    lateinit var dataViolationsArray: ArrayList<dataViolations>
    lateinit var PelanggaranPoints: Array<Int>


    override fun onResume() {
        super.onResume()


        dropDownImplement()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tinyDB = TinyDB(this)

        dataViolationsArray = tinyDB.getListObject("listDataPelanggaran", dataViolations::class.java) as ArrayList<dataViolations>

        binding = ActivitySubmitPelangganranBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.listAutoComplete.setOnItemClickListener { adapterView: AdapterView<*>?, view: View, i: Int, l: Long ->
            PelanggaranSelectedIds = PelaranggaranIds[i]
            binding.pointPelanggaran.text = "Point: ${PelanggaranPoints[i].toString()}"

        }

        binding.submit.setOnClickListener {
            if (binding.deskripsi.text.isNullOrEmpty()) {
                Toast.makeText(this, "Deskripsi Harus Di Isi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitPelanggaran(PelanggaranSelectedIds, binding.deskripsi.text.toString())
        }

        dropDownImplement()


    }

    public fun dropDownImplement() {

        pelanggaran = dataViolationsArray.map {it.name}.toTypedArray()
        PelaranggaranIds = dataViolationsArray.map { it.id }.toTypedArray()
        PelanggaranPoints = dataViolationsArray.map { it.point }.toTypedArray()
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_layout, pelanggaran)
        binding.listAutoComplete.setAdapter(arrayAdapter)
    }



    public fun submitPelanggaran(pelanggaranId: Int, deskripsi: String)
    {
        val gson = Gson()
        val studentId = tinyDB.getListInt("siswaScannedList_id")
        val date = LocalDate.now().toString()
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/teacher/add/students/violation"

        val requestData = HashMap<String, Any>()
        requestData["violation_id"] = pelanggaranId
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