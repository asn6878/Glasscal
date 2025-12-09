package com.example.glasscal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.glasscal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFab()
    }

    /**
     * FAB 클릭 시 설정 화면으로 이동
     */
    private fun setupFab() {
        binding.fab.setOnClickListener {
            navigateToSettings()
        }
    }

    /**
     * 설정 화면으로 이동
     */
    private fun navigateToSettings() {
        val navController = findNavController(R.id.nav_host_fragment)
        val currentDestination = navController.currentDestination?.id

        if (currentDestination != R.id.settingsFragment) {
            navController.navigate(R.id.settingsFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}