package com.example.presensi_app_diskominfo.ui.fragment.intern.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.presensi_app_diskominfo.api.ApiClient
import com.example.presensi_app_diskominfo.auth.SharedPreferencesManager
import com.example.presensi_app_diskominfo.data.LogoutResponse
import com.example.presensi_app_diskominfo.databinding.FragmentProfileBinding
import com.example.presensi_app_diskominfo.ui.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.presensi_app_diskominfo.R
import com.example.presensi_app_diskominfo.ui.fragment.intern.UpdatePasswordFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.FileInputStream
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var isUploading = false

    private var pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imageViewProfile.setImageURI(it)
            uploadPhoto(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                uploadPhoto(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewProfile.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.cardChangePass.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UpdatePasswordFragment())
                .addToBackStack(null)
                .commit()
        }

        setUpProfile()
        logout()
    }

    private fun uploadPhoto(imageUri: Uri) {
        if (isUploading) {
            Toast.makeText(activity, "Uploading please wait...", Toast.LENGTH_SHORT).show()
            return
        }

        isUploading = true

        Toast.makeText(activity, "Saving...", Toast.LENGTH_SHORT).show()

        val file = File(getRealPathFromURI(imageUri)!!)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

        lifecycleScope.launch {
            val prefMan = SharedPreferencesManager.getInstance(requireContext())
            val token = prefMan.getAuthToken()

            if (token != null) {
                val authHeader = "Bearer $token"

                try {
                    val response = ApiClient.authService.uploadPhoto(authHeader, body)

                    if (response.isSuccessful) {
                        val photoUrl = response.body()?.photo
                        if (!photoUrl.isNullOrEmpty()) {
                            prefMan.saveUserData(
                                prefMan.getUserName(),
                                prefMan.getUserEmail(),
                                prefMan.getUserRole(),
                                photoUrl
                            )
                            Picasso.get()
                                .load(photoUrl)
                                .placeholder(R.drawable.ic_refresh)
                                .error(R.drawable.ic_refresh)
                                .into(binding.imageViewProfile)
                            setUpProfile()
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(activity, "File too big or not supported!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, "File too big or not supported!", Toast.LENGTH_LONG).show()
                } finally {
                    isUploading = false
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        return try {
            val contentResolver = requireActivity().contentResolver

            contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                val file = File(requireContext().cacheDir, "temp_image.jpg")
                FileInputStream(descriptor.fileDescriptor).use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                file.absolutePath // Return path
            } ?: run {
                Toast.makeText(requireContext(), "Failed to open!", Toast.LENGTH_SHORT).show()
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error uknown", Toast.LENGTH_SHORT).show()
            null
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

    private fun logout() {
        binding.btnLogout.setOnClickListener{
            showLogoutConfirmation()
        }
    }

    private fun setUpProfile(){
        val prefMan = SharedPreferencesManager.getInstance(requireContext())
            binding.textViewName.text = prefMan.getUserName()
            binding.textViewEmail.text = prefMan.getUserEmail()
            binding.textViewRole.text = prefMan.getUserRole()
            binding.tvMentorName.text = prefMan.getMentorName()
            binding.tvStartTime.text = prefMan.getStartTime()
            binding.tvFinishTime.text = prefMan.getFinishTime()

        val photoUrl = prefMan.getUserPhoto()
        if (!photoUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.ic_refresh)
                .error(R.drawable.ic_refresh)
                .into(binding.imageViewProfile)
        } else {
            binding.imageViewProfile.setImageResource(R.drawable.ic_profile)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}