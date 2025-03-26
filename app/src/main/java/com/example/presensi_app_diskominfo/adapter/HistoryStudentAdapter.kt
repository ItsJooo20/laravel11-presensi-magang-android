package com.example.presensi_app_diskominfo.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.presensi_app_diskominfo.R
import com.example.presensi_app_diskominfo.data.HistoryStudentModel

class HistoryStudentAdapter(
    private val historyStudentList: List<HistoryStudentModel>) :
    RecyclerView.Adapter<HistoryStudentAdapter.HistoryStudentViewHolder>(){

    class HistoryStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvCheckIn: TextView = itemView.findViewById(R.id.tvCheckInTime)
        private val tvCheckOut: TextView = itemView.findViewById(R.id.tvCheckOutTime)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(history: HistoryStudentModel) {
            tvDate.text = history.date
            tvCheckIn.text = history.check_in ?: "--:--"
            tvCheckOut.text = history.check_out ?: "--:--"
            tvStatus.text = history.status

            when (history.status.lowercase()) {
                "late" -> {
                    tvStatus.setTextColor(Color.RED)
                }

                "present" -> {
                    tvStatus.setTextColor(Color.GREEN)
                }

                else -> {
                    tvStatus.setTextColor(Color.BLACK)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryStudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryStudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryStudentViewHolder, position: Int) {
        holder.bind(historyStudentList[position])
    }

    override fun getItemCount(): Int = historyStudentList.size
}