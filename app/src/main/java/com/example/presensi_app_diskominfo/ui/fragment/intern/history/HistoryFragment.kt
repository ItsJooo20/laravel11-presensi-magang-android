package com.example.presensi_app_diskominfo.ui.fragment.intern.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import kotlinx.coroutines.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.presensi_app_diskominfo.adapter.HistoryAdapter
import com.example.presensi_app_diskominfo.api.ApiClient
import com.example.presensi_app_diskominfo.auth.SharedPreferencesManager
import com.example.presensi_app_diskominfo.data.PresensiModel
import com.example.presensi_app_diskominfo.databinding.FragmentHistoryBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: HistoryAdapter
    private val historyList = mutableListOf<PresensiModel>()

    private var isAscending = true
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setUpSort()
        fetchHistoryData()
        setUpSwipetoRefresh()
    }

    private fun setUpSwipetoRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
//            showLoading(false)
            historyList.clear()
            historyAdapter.notifyDataSetChanged()
            fetchHistoryData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(historyList)
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun fetchHistoryData() {
        binding.swipeRefreshLayout.isRefreshing = true

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                try {
                    val prefMan = SharedPreferencesManager.getInstance(requireContext())
                    val token = prefMan.getAuthToken()

                    if (token != null) {
                        val authHeader = "Bearer $token"
                        val response = ApiClient.authService.getHistory(authHeader)

                        if (response.isSuccessful && response.body() != null) {
                            val newData = response.body()!!.data

                            historyList.clear()
                            historyList.addAll(newData)
                            sortHistory(keepOrder = true)
                            historyAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(requireContext(), "Failed fetch data", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
//                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    if (binding != null) {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }


    private fun setUpSort() {
        binding.btnFilter.setOnClickListener {
            Toast.makeText(activity, "Sorting done\n Swipe to refresh!", Toast.LENGTH_SHORT).show()
            sortHistory(keepOrder = false)
        }
    }

    private fun sortHistory(keepOrder: Boolean = false) {
        if (historyList.isNotEmpty()) {
            if (!keepOrder) {
                isAscending = !isAscending
            }

            historyList.sortWith(compareBy { presensi ->
                try {
                    dateFormat.parse(presensi.date)
                } catch (e: Exception) {
                    null
                }
            })

            if (!isAscending) {
                historyList.reverse()
            }

//            fetchHistoryData()
            historyAdapter.notifyDataSetChanged()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}