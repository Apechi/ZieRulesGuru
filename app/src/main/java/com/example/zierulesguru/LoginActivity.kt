package com.example.zierulesguru

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request.Method
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.zierulesguru.databinding.ActivityLoginBinding
import com.example.zierulesguru.guru_mapel.GuruMapelActivity
import com.example.zierulesguru.walikelas.WaliKelasActivity
import com.google.gson.Gson
import org.json.JSONObject


data class DataProfile(
    val status: Int,
    val message: String,
    val role: String,
    val token: String,
)

data class Version (
    val status: Int,
    val data: Data
)

data class Data (
    val version: String,
    val releaseTime: String
)


class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var tinyDB: TinyDB
    lateinit var APP_VERSION: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingSwitch(false)

        tinyDB = TinyDB(this)
        binding.buttonLogin.setOnClickListener {

            if (binding.editTextEmail.text.isNullOrEmpty() || binding.editTextPassword.text.isNullOrEmpty()) {
                Toast.makeText(this, "Input Harus Di isi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = binding.editTextEmail.text.toString()
            val pass = binding.editTextPassword.text.toString()

            login(email, pass)
        }

        APP_VERSION = BuildConfig.VERSION_NAME;

    }


    private fun needUpdate() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Aplikasi Perlu Di Update!")
            .setCancelable(false)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id -> finish() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    fun loadingSwitch(switch: Boolean) {
        when (switch) {
            true -> {
                binding.loadingBackground.visibility = View.VISIBLE
                binding.progressBar.visibility = View.VISIBLE
            }
            false -> {
                binding.loadingBackground.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun checkVersion() {
        loadingSwitch(true)
        val queue = Volley.newRequestQueue(this)
        val url = "${MyApplication.BASE_URL}/version"

        val jsonRequest =  object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            {res->
                try {
                    val gson = Gson()
                    val versionJson = gson.fromJson(res.toString(), Version::class.java)
                    if (versionJson.status == 200) {
                       if (versionJson.data.version == APP_VERSION) {
                           if (tinyDB.getBoolean("is_login")) {
                               val role = tinyDB.getString("role")
                               when (role) {
                                   "wali-kelas" -> {
                                       startActivity(Intent(this, WaliKelasActivity::class.java))
                                       finish()
                                   }
                                   "guru-mapel" -> {
                                       startActivity(Intent(this, GuruMapelActivity::class.java))
                                       finish()
                                   }
                               }
                           }
                           loadingSwitch(false)
                       } else {
                           loadingSwitch(false)
                           needUpdate()
                       }
                    } else {
                        loadingSwitch(false)
                        println(res.toString())
                    }

                } catch (e : Exception) {
                    loadingSwitch(false)
                    println(e.message.toString())
                }

            },
            {err ->
                println("tes")
            }
        ) {

        }
        queue.add(jsonRequest)

    }


    override fun onStart() {
        super.onStart()



        checkVersion()




    }







    private fun login(email: String, pass: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "${MyApplication.BASE_URL}/teacher/login"
        val body = JSONObject()
        body.put("email", email)
        body.put("password", pass)

        val jsonRequest = object : JsonObjectRequest(Method.POST, url, body,
            {
            res ->
                try {
                    val gson = Gson()
                    val profileSaya = gson.fromJson(res.toString(), DataProfile::class.java)
                    if (profileSaya.status == 200) {
                        val token = profileSaya.token
                        val role = profileSaya.role
                        tinyDB.putString("token", token)
                        tinyDB.putString("role", role)
                        tinyDB.putBoolean("is_login", true)
                        Toast.makeText(this, "Berhasil Login!", Toast.LENGTH_SHORT).show()
                        when (role) {
                            "wali-kelas" -> startActivity(Intent(this, WaliKelasActivity::class.java))
                            "guru-mapel" -> startActivity(Intent(this, GuruMapelActivity::class.java))
                        }
                    } else {
                        Toast.makeText(this, "Email Atau Password Salah", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: java.lang.Exception) {
                    Toast.makeText(this, "Email Atau Password Salah", Toast.LENGTH_SHORT).show()

                }
        },{err->

                Toast.makeText(this, "Email Atau Password Salah", Toast.LENGTH_SHORT).show()
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }

        queue.add(jsonRequest)

    }
}