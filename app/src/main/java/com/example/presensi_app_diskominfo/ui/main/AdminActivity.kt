package com.example.presensi_app_diskominfo.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.presensi_app_diskominfo.R
import com.example.presensi_app_diskominfo.api.ApiClient
import com.example.presensi_app_diskominfo.auth.SharedPreferencesManager
import com.example.presensi_app_diskominfo.data.LogoutResponse
import com.example.presensi_app_diskominfo.databinding.ActivityAdminBinding
import com.example.presensi_app_diskominfo.ui.fragment.mentor.home.ListStudentFragment
import com.example.presensi_app_diskominfo.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    private val listStudentFragment = ListStudentFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, listStudentFragment)
                .commit()
        }
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setUpLogOut(){
        val prefMan = SharedPreferencesManager.getInstance(this)
        val token = prefMan.getAuthToken()

        if (token != null){
            val authHeader = "Bearer $token"

            ApiClient.authService.logout(authHeader).enqueue(object : Callback<LogoutResponse> {
                override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                    prefMan.clearUserData()

                    navigateToLogin()
                }

                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                    Toast.makeText(this@AdminActivity, "logout error: " + t.message, Toast.LENGTH_SHORT).show()

                    prefMan.clearUserData()

                    navigateToLogin()
                }
            })
        }
    }

    private fun navigateToLogin() {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

