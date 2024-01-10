package com.example.zierulesguru.guru_mapel.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.zierulesguru.MyApplication
import com.example.zierulesguru.ScanHistory.HistoryScan
import com.example.zierulesguru.ScanHistory.scanHistoryAdapter
import com.example.zierulesguru.TinyDB
import com.example.zierulesguru.databinding.FragmentHomeBinding
import com.example.zierulesguru.list_data.ListPelanggaran
import com.example.zierulesguru.walikelas.ui.home.HomeViewModel
import com.google.gson.Gson
import java.util.ArrayList


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    lateinit var recycleView: RecyclerView
    lateinit var tinyDB: TinyDB

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tinyDB = TinyDB(requireContext())
        recycleView =  binding.recycleView

        binding.swipeRefresh.setOnRefreshListener {
            getHistory()
            binding.swipeRefresh.isRefreshing = false
        }

        return root
    }

    override fun onStart() {
        super.onStart()

        getHistory()
    }



    public fun getHistory() {
        val queue = Volley.newRequestQueue(requireContext())
        val  url = "${MyApplication.BASE_URL}/teacher/history/scan"

        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, {
                res ->
            try {
                val gson = Gson();
                val listHistory = gson.fromJson(res.toString(), HistoryScan::class.java)
                if (listHistory.status == 200) {
                    if (listHistory.dataHistoryScan.count() <= 0) {
                        binding.checkData.text = "Data Tidak Ada!"
                    }
                    val adapter = scanHistoryAdapter(listHistory.dataHistoryScan)
                    recycleView.layoutManager = LinearLayoutManager(requireContext())
                    recycleView.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), res.toString(), Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }, {
                err ->
            Toast.makeText(requireContext(), err.toString(), Toast.LENGTH_SHORT).show()
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                val token = "Bearer ${tinyDB.getString("token")}"
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}