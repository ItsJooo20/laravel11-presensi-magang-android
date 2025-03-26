package com.example.presensi_app_diskominfo.ui.fragment.intern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.presensi_app_diskominfo.R
import com.example.presensi_app_diskominfo.api.ApiClient
import com.example.presensi_app_diskominfo.auth.SharedPreferencesManager
import com.example.presensi_app_diskominfo.data.PasswordRequest
import com.example.presensi_app_diskominfo.databinding.FragmentUpdatePasswordBinding
import com.example.presensi_app_diskominfo.ui.fragment.intern.profile.ProfileFragment
import kotlinx.coroutines.launch
import org.json.JSONObject

class UpdatePasswordFragment : Fragment() {

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            validatePasswords()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }
    }


    private fun validatePasswords() {
        val newPass = binding.etNewPass.text.toString().trim()
        val confirmNewPass = binding.etConfirmNewPass.text.toString().trim()

        when {
            newPass.isEmpty() -> {
                binding.tilNewPass.error = "Password cannot be empty!"
            }
            confirmNewPass.isEmpty() -> {
                binding.tilConfirmNewPassword.error = "Confirmation password cannot be empty"
            }
            newPass.length < 8 -> {
                binding.tilNewPass.error = "Password must be at least 8 characters"
            }
            newPass != confirmNewPass -> {
                binding.tilConfirmNewPassword.error = "Password not the same!"
            }
            else -> {
                binding.tilNewPass.error = null
                binding.tilConfirmNewPassword.error = null
                savePassword(newPass)
            }
        }
    }

    private fun savePassword(password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tilNewPass.isEnabled = false
        binding.tilConfirmNewPassword.isEnabled = false
        binding.btnSave.isEnabled = false
        binding.btnSave.text = "Saving..."

        viewLifecycleOwner.lifecycleScope.launch {
            val prefMan = SharedPreferencesManager.getInstance(requireContext())
            val token = prefMan.getAuthToken()

            if (token != null) {
                val authHeader = "Bearer $token"

                val response = ApiClient.authService.updatePassword(authHeader, PasswordRequest(password.trim()))

                try {
                    if (response.isSuccessful) {
                        Toast.makeText(activity, "Update Password Success", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody).getString("message")
                        } catch (e: Exception) {
                            "Server Error"
                        }
                        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, "Update Password Failed", Toast.LENGTH_SHORT).show()
                } finally {
                    loadingDone()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                }
            }
        }
    }

    private fun loadingDone() {
        binding.progressBar.visibility = View.GONE
        binding.tilNewPass.isEnabled = true
        binding.tilConfirmNewPassword.isEnabled = true
        binding.btnSave.isEnabled = true
        binding.btnSave.text = "Save Changes"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
