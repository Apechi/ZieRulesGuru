package com.example.zierulesguru

import android.content.Intent
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
import com.example.zierulesguru.guru_mapel.GuruMapelActivity
import com.example.zierulesguru.guru_mapel.ui.dashboard.DashboardFragment
import com.example.zierulesguru.walikelas.WaliKelasActivity
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
    lateinit var arrayNama: ArrayList<String>
    lateinit var arrayId: ArrayList<Int>


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

        arrayNama = tinyDB.getListString("siswaScannedList_nama")
        arrayId = tinyDB.getListInt("siswaScannedList_id")

        binding.stopButton.setOnClickListener {
            val role = tinyDB.getString("role")
            when (role) {
                "wali-kelas" -> startActivity(Intent(this, WaliKelasActivity::class.java))
                "guru-mapel" -> startActivity(Intent(this, GuruMapelActivity::class.java))
            }
        }



    }

    override fun onStart() {
        super.onStart()

        binding.scanText.text = "Scanning..."
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

    private fun getSiswa(siswaCode: String) {
        val queue = Volley.newRequestQueue(this)
        val  url = "${MyApplication.BASE_URL}/student/${siswaCode}"
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
                        arrayNama.add(scannedSiswa.name)
                        arrayId.add(scannedSiswa.id)
                        tinyDB.putListString("siswaScannedList_nama", arrayNama);
                        tinyDB.putListInt("siswaScannedList_id", arrayId);
                        binding.scanText.text = "Terscan: ${arrayId.count()}"
                        codeScanner.startPreview()
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