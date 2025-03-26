package com.example.presensi_app_diskominfo.ui.fragment.intern.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.presensi_app_diskominfo.databinding.FragmentDashboardBinding
import android.os.Handler
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import com.example.presensi_app_diskominfo.R
import com.example.presensi_app_diskominfo.api.ApiClient
import com.example.presensi_app_diskominfo.auth.SharedPreferencesManager
import com.example.presensi_app_diskominfo.data.CheckRequest
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_IMAGE_CAPTURE = 1
    private var isCheckIn: Boolean = false

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var barcodeLauncherCheckIn: ActivityResultLauncher<ScanOptions>
    private lateinit var barcodeLauncherCheckOut: ActivityResultLauncher<ScanOptions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barcodeLauncherCheckIn = registerForActivityResult(ScanContract()) { result ->
            if (result.contents != null) {
                val scannedCode = result.contents
                checkIn(scannedCode)
            }
        }

        barcodeLauncherCheckOut = registerForActivityResult(ScanContract()) { result ->
            if (result.contents != null) {
                val scannedCode = result.contents
                checkOut(scannedCode)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler = Handler(Looper.getMainLooper())

        setUpWelcomeText()

        setUpProfile()

        setUpButtonCheck()

        showCurrentTime()

        updateTime()
    }

    private fun setUpProfile() {
        val prefMan = SharedPreferencesManager.getInstance(requireContext())
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

    private fun setUpButtonCheck() {

        binding.btnCheckIn.setOnClickListener {
            ScanCheckIn()
        }

        binding.btnCheckOut.setOnClickListener {
            ScanCheckOut()

        }
    }

    private fun setUpWelcomeText(){
        val prefMan = SharedPreferencesManager.getInstance(requireContext()).getUserName()
        binding.textWelcome.text = "How's Your Day?\n$prefMan"
    }

    private fun showCurrentTime() {
        if (_binding == null) return

        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        binding.textTime.text = currentTime

        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("us", "ID"))
        val currentDate = dateFormat.format(Date())

        binding.textDate.text = currentDate
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (_binding != null) {
                showCurrentTime()
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun updateTime() {
        handler.postDelayed(updateTimeRunnable, 1000)
    }

    private fun checkIn(qrCode: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val prefMan = SharedPreferencesManager.getInstance(requireContext())
            val token = prefMan.getAuthToken()

            if (token != null) {
                val authHeader = "Bearer $token"

                val response = ApiClient.authService.checkIn(authHeader, CheckRequest(qrCode))

                try {
                    if (response.isSuccessful) {
                        Toast.makeText(activity, "Check In Success", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(activity, "Check In Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private  fun checkOut(qrCode: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val prefMan = SharedPreferencesManager.getInstance(requireContext())
            val token = prefMan.getAuthToken()

            if (token != null) {
                val authHeader = "Bearer $token"

                val response = ApiClient.authService.checkOut(authHeader, CheckRequest(qrCode))

                try {
                    if (response.isSuccessful) {
                        Toast.makeText(activity, "Check Out Success", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(activity, "Check Out Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun ScanCheckIn() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            setPrompt("Scan Check In Barcode")
            setCameraId(0)
            setBeepEnabled(true)
            setBarcodeImageEnabled(false)
        }
        barcodeLauncherCheckIn.launch(options)
    }

    private fun ScanCheckOut() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            setPrompt("Scan Check Out Barcode")
            setCameraId(0)
            setBeepEnabled(true)
            setBarcodeImageEnabled(false)
        }
        barcodeLauncherCheckOut.launch(options)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateTimeRunnable)
        _binding = null
    }
}