package com.example.presensi_app_diskominfo.ui.fragment.mentor.activityHistory

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.presensi_app_diskominfo.adapter.HistoryStudentAdapter
import com.example.presensi_app_diskominfo.api.ApiClient
import com.example.presensi_app_diskominfo.auth.SharedPreferencesManager
import com.example.presensi_app_diskominfo.data.HistoryStudentModel
import com.example.presensi_app_diskominfo.databinding.ActivityHistoryStudentBinding
import com.example.presensi_app_diskominfo.ui.main.AdminActivity
import kotlinx.coroutines.launch

class HistoryStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryStudentBinding
    private lateinit var historyAdapter: HistoryStudentAdapter
    private val historyList = mutableListOf<HistoryStudentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val studentId = intent.getIntExtra("STUDENT_ID", -1)

        historyAdapter = HistoryStudentAdapter(historyList)
        binding.rvHistoryStudent.layoutManager = LinearLayoutManager(this)
        binding.rvHistoryStudent.adapter = historyAdapter

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

            finish()
        }

        if (studentId != -1) {
            fetchStudentHistory(studentId)
        } else {
            Toast.makeText(this, "Invalid Student ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchStudentHistory(studentId: Int) {
        lifecycleScope.launch {
            val prefMan = SharedPreferencesManager.getInstance(this@HistoryStudentActivity).getAuthToken()
            val authHeader = "Bearer $prefMan"

            val response = ApiClient.authService.getStudentHistory(authHeader, studentId)
            try {
                if (response.isSuccessful && response.body() != null) {
                    val newData = response.body()!!.data
                    historyList.clear()
                    historyList.addAll(newData)
                    historyAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@HistoryStudentActivity, "Failed to fetch history", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@HistoryStudentActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed() // Ini akan kembali ke activity sebelumnya dalam stack tanpa keluar aplikasi
    }
}
