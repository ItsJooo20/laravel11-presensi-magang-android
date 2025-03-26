package com.example.presensi_app_diskominfo.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.presensi_app_diskominfo.ui.fragment.intern.home.DashboardFragment
import com.example.presensi_app_diskominfo.ui.fragment.intern.history.HistoryFragment
import com.example.presensi_app_diskominfo.ui.fragment.intern.profile.ProfileFragment
import com.example.presensi_app_diskominfo.R
import com.example.presensi_app_diskominfo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val dashboardFragment = DashboardFragment()
    private val profileFragment = ProfileFragment()
    private val historyFragment = HistoryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, dashboardFragment)
                .commit()
        }

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    loadFragment(dashboardFragment)
                    true
                }
                R.id.nav_history -> {
                    loadFragment(historyFragment)
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}