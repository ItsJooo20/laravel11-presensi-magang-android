package com.example.presensi_app_diskominfo.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.presensi_app_diskominfo.ui.main.AdminActivity
import com.example.presensi_app_diskominfo.api.ApiClient
import com.example.presensi_app_diskominfo.databinding.ActivityLoginBinding
import com.example.presensi_app_diskominfo.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.presensi_app_diskominfo.auth.SharedPreferencesManager
import com.example.presensi_app_diskominfo.data.LoginRequest
import com.example.presensi_app_diskominfo.data.LoginResponse
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (SharedPreferencesManager.getInstance(this).isLoggedIn()) {
            val prefManager = SharedPreferencesManager.getInstance(this)
            when (prefManager.getUserRole()) {
                "Peserta Magang" -> navigateToMainActivity()
                "Administrator" -> navigateToAdminActivity()
                else -> {
//                    Toast.makeText(this@LoginActivity, "Mentor belum dibikin", Toast.LENGTH_SHORT).show()
                    navigateToAdminActivity()
                }
            }
            finish()
            return
        }
        loadingDone()
        setupLogin()
    }

    private fun loadingDone() {
        binding.btnLogin.isEnabled = true
        binding.btnLogin.text = "Login"
        binding.tilEmail.isEnabled = true
        binding.tilPassword.isEnabled = true
    }

    private fun setupLogin(){
        binding.btnLogin.setOnClickListener{
            login()
        }
    }

    private fun login(){
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmail.error = "Email required"
            return
        }

        if (pass.isEmpty()) {
            binding.etPassword.error = "Password required"
            return
        }

        showLoading(true)

        loadingProgres()

        val req = LoginRequest(email, pass)

        ApiClient.authService.login(req).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val prefManager = SharedPreferencesManager.getInstance(this@LoginActivity)

                        loadingDone()
                        showLoading(false)

                        loginResponse.data?.let { data ->
                            prefManager.saveAuthToken(data.token ?: "")

                            data.mentor?.let { mentor ->
                                prefManager.saveMentorData(
                                    mentor.name ?: "",
                                    mentor.start_work_hour ?: "",
                                    mentor.finish_work_hour ?: ""
                                )
                            }

                            data.user?.let { user ->
                                prefManager.saveUserData(
                                    user.name ?: "",
                                    user.email ?: "",
                                    user.role ?: "",
                                    user.photo ?: "",
                                )
                                when (user.role) {
                                    "Peserta Magang" -> navigateToMainActivity()
                                    "Mentor" -> navigateToAdminActivity()
                                    else -> {
                                        Toast.makeText(this@LoginActivity, "Admin belum dibikin", Toast.LENGTH_SHORT).show()
                                        navigateToAdminActivity()
                                    }
                                }
                                finish()
                            }
                        }

                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody).getString("message")
                        } catch (e: Exception) {
                            response.body()!!.message
                        }

                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("message")
                    } catch (e: Exception) {
                        "Server Error"
                    }
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                    loadingDone()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Wrong Email or Password!", Toast.LENGTH_SHORT).show()
                showLoading(false)
                loadingDone()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun loadingProgres() {
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Loading..."
        binding.tilEmail.isEnabled = false
        binding.tilPassword.isEnabled = false
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun navigateToAdminActivity() {
        startActivity(Intent(this, AdminActivity::class.java))
    }
}