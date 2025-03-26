package com.example.presensi_app_diskominfo.ui.fragment.mentor.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.presensi_app_diskominfo.R
import com.example.presensi_app_diskominfo.adapter.StudentAdapter
import com.example.presensi_app_diskominfo.api.ApiClient
import com.example.presensi_app_diskominfo.auth.SharedPreferencesManager
import com.example.presensi_app_diskominfo.data.LogoutResponse
import com.example.presensi_app_diskominfo.data.StudentSchedule
import com.example.presensi_app_diskominfo.databinding.FragmentListStudentBinding
import com.example.presensi_app_diskominfo.ui.fragment.mentor.activityHistory.HistoryStudentActivity
import com.example.presensi_app_diskominfo.ui.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListStudentFragment : Fragment() {

    private var _binding: FragmentListStudentBinding? = null
    private val binding get() = _binding!!

    private lateinit var studentAdapter: StudentAdapter
    private val studentList = mutableListOf<StudentSchedule>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRV()
        logout()
        fetchStudentData()
    }

    private fun logout() {
        binding.btnFilter.setOnClickListener{
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Warning")
            setMessage("Are you sure you want to logout?")
            setPositiveButton("Confirm") { _, _ ->
                showLoading(true)
                setUpLogOut()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
            show()
        }
    }

    private fun setUpRV() {
        studentAdapter = StudentAdapter(studentList) { studentId ->
            Toast.makeText(requireContext(), "Load History...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), HistoryStudentActivity::class.java).apply {
                putExtra("STUDENT_ID", studentId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        binding.rvListStudent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
        }
    }

    private fun setUpLogOut(){
        val prefMan = SharedPreferencesManager.getInstance(requireContext())
        val token = prefMan.getAuthToken()

        if (token != null){
            val authHeader = "Bearer $token"

            ApiClient.authService.logout(authHeader).enqueue(object : Callback<LogoutResponse> {
                override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                    prefMan.clearUserData()

                    navigateToLogin()
                }

                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                    Toast.makeText(context, "logout error: " + t.message, Toast.LENGTH_SHORT).show()

                    prefMan.clearUserData()

                    navigateToLogin()
                    showLoading(false)
                }
            })
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigateToLogin() {
        if (isAdded && activity != null) { // Pastikan fragment masih terhubung ke activity
            startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            requireActivity().finish()
        }
    }

    private fun fetchStudentData() {
        viewLifecycleOwner.lifecycleScope.launch {
            showLoading(true)

            val prefMan = SharedPreferencesManager.getInstance(requireContext()).getAuthToken()
            val authHeader = "Bearer $prefMan"

            val response = ApiClient.authService.getStudentIntern(authHeader)
            try {
                if (response.isSuccessful && response.body() != null) {
                    showLoading(false)
                    val newData = response.body()!!.data

                    studentList.clear()
                    studentList.addAll(newData)
                    studentAdapter.notifyDataSetChanged()
                } else {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Failed fetch data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
