package com.example.presensi_app_diskominfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.presensi_app_diskominfo.R
import com.example.presensi_app_diskominfo.data.StudentSchedule

class StudentAdapter(
    private val studentList: List<StudentSchedule>,
    private val onItemClick: (Int) -> Unit
) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvStudentEmail)
        val tvStartTime: TextView = itemView.findViewById(R.id.tvStartTime)
        val tvFinishTime: TextView = itemView.findViewById(R.id.tvFinishTime)
        val tvId:TextView = itemView.findViewById(R.id.tvId)

        fun bind(studentList: StudentSchedule, onItemClick: (Int) -> Unit) {
            tvName.text = studentList.student.name
            tvEmail.text = "See Detail"
            tvId.text = studentList.student.id.toString()
            tvFinishTime.text = studentList.finish_working_time
            tvStartTime.text = studentList.start_working_time

            itemView.setOnClickListener {
                onItemClick(studentList.student.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(studentList[position], onItemClick)
    }

    override fun getItemCount() = studentList.size
}
