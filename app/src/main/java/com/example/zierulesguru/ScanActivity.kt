package com.example.zierulesguru

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.zierulesguru.barcodeScanner.ScannedData
import com.example.zierulesguru.databinding.ActivityScanBinding
import com.example.zierulesguru.guru_mapel.ui.dashboard.DashboardFragment
import com.google.gson.Gson

data class SiswaScanned(
    val status: Int,
    val id: Int,
    val name: String,
    val `class`: String,
    val message: String?
)

class ScanActivity : AppCompatActivity() {


    lateinit var binding: ActivityScanBinding
    lateinit var codeScanner: CodeScanner
    lateinit var tinyDB: TinyDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        codeScanner = CodeScanner(this, binding.scannerView)

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 123)
        } else {
            qrCodeScanner()
        }

        tinyDB = TinyDB(this)

    }

    override fun onStart() {
        super.onStart()
        qrCodeScanner()
    }

    private fun qrCodeScanner() {
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.CONTINUOUS // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            this.runOnUiThread {
                getSiswa(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            this.runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun getSiswa(siswaNis: String) {
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/student/${siswaNis}"
        var sameResult: Boolean = false
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, {
                res ->
            try {
                val gson = Gson();
                val scannedSiswa = gson.fromJson(res.toString(), SiswaScanned::class.java);
                if (scannedSiswa.status == 200) {
                    for (scannedValue in tinyDB.getListInt("siswaScannedList_id")) {
                        if (scannedValue == scannedSiswa.id) {
                            sameResult = true
                            break
                        }
                    }
                    if (!sameResult) {
                        tinyDB.putListString("siswaScannedList_nama", arrayListOf(scannedSiswa.name));
                        tinyDB.putListInt("siswaScannedList_id", arrayListOf(scannedSiswa.id));
                        Toast.makeText(this, tinyDB.getListString("siswaScannedList_nama").size, Toast.LENGTH_SHORT).show();
                        switchToFragment(DashboardFragment());
                    } else {
                        Toast.makeText(this, "Siswa Sudah Di Scan", Toast.LENGTH_SHORT).show()
                        codeScanner.startPreview()
                    }
                } else {
                    Toast.makeText(this, scannedSiswa.message, Toast.LENGTH_SHORT).show()
                    codeScanner.startPreview()
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                codeScanner.startPreview()
            }
        }, {
                err ->
            Toast.makeText(this, err.toString(), Toast.LENGTH_SHORT).show()
            codeScanner.startPreview()
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${tinyDB.getString("token")}"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun switchToFragment(fragment: Fragment) {
        val manager = supportFragmentManager
        manager.beginTransaction().replace(R.id.navigation_dashboard, fragment).commit()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Akses Di Izinkan", Toast.LENGTH_SHORT).show()
                qrCodeScanner()
            } else {
                Toast.makeText(this, "Camera Akses Tidak Di Izinkan", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


}