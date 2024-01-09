package com.example.zierulesguru.walikelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.zierulesguru.*
import com.example.zierulesguru.databinding.ActivityWaliKelasBinding
import com.example.zierulesguru.list_data.ListPelanggaran
import com.example.zierulesguru.list_data.ListPrestasi
import com.example.zierulesguru.list_data.ListTugas
import com.google.gson.Gson
import java.util.ArrayList

class WaliKelasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaliKelasBinding
    private lateinit var tinyDb: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWaliKelasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tinyDb = TinyDB(this)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_wali_kelas)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    public fun getPelanggaran() {
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/teacher/list/violation"

        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, {
                res ->
            try {
                val gson = Gson();
                val listPelanggaran = gson.fromJson(res.toString(), ListPelanggaran::class.java)
                if (listPelanggaran.status == 200) {

                    tinyDb.putListObject("listDataPelanggaran", listPelanggaran.dataViolation as ArrayList<Any>)

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
                val token = "Bearer ${tinyDb.getString("token")}"
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    public fun getPrestasi() {
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/teacher/list/achievment"

        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, {
                res ->
            try {
                val gson = Gson();
                val listPrestasi = gson.fromJson(res.toString(), ListPrestasi::class.java)
                if (listPrestasi.status == 200) {

                    tinyDb.putListObject("listDataPrestasi", listPrestasi.achievments as ArrayList<Any>)

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
                val token = "Bearer ${tinyDb.getString("token")}"
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    public fun getTask() {
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/teacher/list/task"

        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, {
                res ->
            try {
                val gson = Gson();
                val listTask = gson.fromJson(res.toString(), ListTugas::class.java)
                if (listTask.status == 200) {

                    tinyDb.putListObject("listDataTugas", listTask.dataTask as ArrayList<Any>)

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
                val token = "Bearer ${tinyDb.getString("token")}"
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    override fun onStart() {
        super.onStart()



        if (!tinyDb.getBoolean("is_login")) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        getPelanggaran()
        getPrestasi()
        getTask()
        getProfile()
    }

    public fun getProfile() {
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/teacher/profile"

        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, {
                res ->
            try {
                val gson = Gson();
                val profile = gson.fromJson(res.toString(), ProfileGuru::class.java)
                if (profile.status == 200) {

                    var image = "${MyApplication.URL}${profile.dataTeacher.image}"
                    image = image.replace("public", "storage")

                    //put data to tinyDB
                    tinyDb.putString("emailGuru", profile.dataTeacher.email)
                    tinyDb.putString("namaGuru", profile.dataTeacher.name)
                    tinyDb.putString("roleGuru", profile.dataTeacher.role)
                    tinyDb.putString("imageGuru", image)


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
                val token = "Bearer ${tinyDb.getString("token")}"
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
}