package com.example.glasscal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.glasscal.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFab()
    }

    /**
     * FAB 클릭 시 메뉴 표시
     */
    private fun setupFab() {
        binding.fab.setOnClickListener {
            showMenuDialog()
        }
    }

    /**
     * 메뉴 다이얼로그 표시
     */
    private fun showMenuDialog() {
        val navController = findNavController(R.id.nav_host_fragment)
        val currentDestination = navController.currentDestination?.id

        // 설정 화면으로 이동
        if (currentDestination != R.id.settingsFragment) {
            navController.navigate(R.id.settingsFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}