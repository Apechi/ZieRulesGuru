package com.example.zierulesguru.ScanHistory

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zierulesguru.databinding.HistoryLayoutItemBinding

class scanHistoryAdapter(
    private var dataListScanHistroy: ArrayList<dataHistoryScan>
): RecyclerView.Adapter<scanHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: HistoryLayoutItemBinding): RecyclerView.ViewHolder(binding.root) {
        var name = binding.nama
        var siswa = binding.namaSiswa
        var type = binding.type
        var tanggal = binding.tanggal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HistoryLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataListScanHistroy.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listDataScanHistory = dataListScanHistroy[position]

        holder.type.text = listDataScanHistory.type

        if (listDataScanHistory.type == "violation") {
            holder.type.setBackgroundColor(Color.parseColor("#bb2124"))
        } else if (listDataScanHistory.type == "achievment") {
            holder.type.setBackgroundColor(Color.parseColor("#22bb33"))
        } else if (listDataScanHistory.type == "task") {
            holder.type.setBackgroundColor(Color.parseColor("#477CAC"))
        }

        holder.name.text = listDataScanHistory.name
        holder.tanggal.text = listDataScanHistory.date
        holder.siswa.text = listDataScanHistory.student

    }

}