package com.example.zierulesguru.guru_mapel.ui.dashboard

import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.zierulesguru.MyApplication
import com.example.zierulesguru.ScanActivity
import com.example.zierulesguru.TinyDB
import com.example.zierulesguru.barcodeScanner.ScannedData
import com.example.zierulesguru.databinding.FragmentDashboardBinding
import com.google.gson.Gson



class DashboardFragment : Fragment() {


    private var _binding: FragmentDashboardBinding? = null
  lateinit private var tinyDB: TinyDB

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.scanButton.setOnClickListener {
            startActivity(Intent(requireContext(), ScanActivity::class.java))
        }

        tinyDB = TinyDB(requireContext())

        return root

    }

    override fun onStart() {
        super.onStart()

        val siswaScannedList = tinyDB.getListString("siswaScannedList_nama")

        for (siswaScanned in siswaScannedList ) {
            binding.tagContainer.addTag(siswaScanned)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}