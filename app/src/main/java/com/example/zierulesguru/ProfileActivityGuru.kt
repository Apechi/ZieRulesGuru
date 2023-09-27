package com.example.zierulesguru

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.zierulesguru.databinding.ActivityProfileGuruBinding
import com.example.zierulesguru.walikelas.ui.notifications.NotificationsFragment

class ProfileActivityGuru : AppCompatActivity() {

    lateinit var binding: ActivityProfileGuruBinding
    lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileGuruBinding.inflate(layoutInflater)

        tinyDB = TinyDB(this)


        setContentView(binding.root)


        binding.namaGuru.text = tinyDB.getString("namaGuru");
        binding.emailGuru.text = tinyDB.getString("emailGuru")
        binding.role.text = tinyDB.getString("roleGuru")
        Glide.with(this)
            .load(tinyDB.getString("imageGuru"))
            .into(binding.profileImage)


        binding.logout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun Logout() {
        tinyDB.putString("token", "")
        tinyDB.putString("role", "")
        tinyDB.putBoolean("is_login", false)
        startActivity(Intent(this, LoginActivity::class.java))
    }
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Anda yakin ingin Logout?")
        builder.setPositiveButton("Logout") { dialog: DialogInterface?, which: Int ->
            Logout()
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int ->
            // Handle cancel action here, if needed
        }

        val dialog = builder.create()
        dialog.show()
    }
}