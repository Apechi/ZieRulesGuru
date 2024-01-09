package com.example.zierulesguru.walikelas.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zierulesguru.ScanActivity
import com.example.zierulesguru.TinyDB
import com.example.zierulesguru.barcodeScanner.SubmitPelangganranActivity
import com.example.zierulesguru.barcodeScanner.SubmitPrestasiActivity
import com.example.zierulesguru.barcodeScanner.SubmitTugasActivity
import com.example.zierulesguru.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    lateinit private var tinyDB: TinyDB
    lateinit private var siswaScannedList_id: ArrayList<Int>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.scanButton.setOnClickListener {
            startActivity(Intent(requireContext(), ScanActivity::class.java))
        }

        binding.clearButton.setOnClickListener {
            tinyDB.putListString("siswaScannedList_nama", ArrayList() )
            tinyDB.putListInt("siswaScannedList_id", ArrayList())
            binding.tagContainer.removeAllTags()
            ButtonActivate(false)
        }

        binding.btnPelanggaran.setOnClickListener {
            startActivity(Intent(requireContext(), SubmitPelangganranActivity::class.java))
        }

        binding.btnTugas.setOnClickListener {
            startActivity(Intent(requireContext(), SubmitTugasActivity::class.java))
        }

        binding.btnPrestasi.setOnClickListener {
            startActivity(Intent(requireContext(), SubmitPrestasiActivity::class.java))
        }

        tinyDB = TinyDB(requireContext())


        return root
    }

    override fun onStart() {
        super.onStart()

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        if (tinyDB.getListString("siswaScannedList_nama").count() == 0) {
            ButtonActivate(false)
        } else {
            ButtonActivate(true)
        }



        getSiswa();

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    private fun ButtonActivate(state: Boolean) {
        binding.btnPrestasi.isClickable = state
        binding.clearButton.isClickable = state
        binding.btnPelanggaran.isClickable = state
        binding.btnTugas.isClickable = state
    }

    public fun getSiswa() {

        binding.tagContainer.removeAllTags()

        val siswaScannedList = tinyDB.getListString("siswaScannedList_nama")
        siswaScannedList_id = tinyDB.getListInt("siswaScannedList_id")

        if (siswaScannedList.count() == 0) {
            return
        }
        for (siswaScanned in siswaScannedList ) {
            binding.tagContainer.addTag(siswaScanned)
        }
        ButtonActivate(true)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}