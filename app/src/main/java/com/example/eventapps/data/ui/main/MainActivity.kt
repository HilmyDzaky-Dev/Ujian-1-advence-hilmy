package com.example.eventapps.data.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.eventapps.data.ui.MainViewModel
import com.example.eventapps.data.ui.ViewModelFactory
import com.example.testingroomdatabase.R
import com.example.testingroomdatabase.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNav.setupWithNavController(navController)

        val factory = ViewModelFactory.getInstance(this)
        val mainViewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        mainViewModel.getThemeSetting().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}
