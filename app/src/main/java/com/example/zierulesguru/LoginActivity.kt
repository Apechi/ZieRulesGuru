package com.example.zierulesguru

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
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


class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }


    override fun onStart() {
        super.onStart()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (!tinyDB.getBoolean("is_login")) {
            return
        }

        val role = tinyDB.getString("role")
        when (role) {
            "wali-kelas" -> startActivity(Intent(this, WaliKelasActivity::class.java))
            "guru-mapel" -> startActivity(Intent(this, GuruMapelActivity::class.java))
        }
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