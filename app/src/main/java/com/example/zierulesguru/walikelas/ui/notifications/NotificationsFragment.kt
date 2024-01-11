package com.example.zierulesguru.walikelas.ui.notifications

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.zierulesguru.LoginActivity
import com.example.zierulesguru.TinyDB
import com.example.zierulesguru.dataSiswa.DataSiswaActivity
import com.example.zierulesguru.databinding.ActivityProfileGuruBinding


class NotificationsFragment : Fragment() {

    private var _binding: ActivityProfileGuruBinding? = null
    lateinit var tinyDB: TinyDB

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ActivityProfileGuruBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tinyDB = TinyDB(requireContext())



        binding.namaGuru.text = tinyDB.getString("namaGuru");
        binding.emailGuru.text = tinyDB.getString("emailGuru")
        binding.role.text = tinyDB.getString("roleGuru")
        Glide.with(this)
            .load(tinyDB.getString("imageGuru"))
            .into(binding.profileImage)

        binding.logout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.buttonDataSiswa.setOnClickListener {
            startActivity(Intent(requireContext(), DataSiswaActivity::class.java))
        }

        return root
    }

    private fun Logout() {
        tinyDB.putString("token", "")
        tinyDB.putString("role", "")
        tinyDB.putBoolean("is_login", false)
        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Anda Yakin Ingin Logout?")
        builder.setPositiveButton("Logout") { dialog: DialogInterface?, which: Int ->
            Logout()
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int ->
            // Handle cancel action here, if needed
        }

        val dialog = builder.create()
        dialog.show()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}