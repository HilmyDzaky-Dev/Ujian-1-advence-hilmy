package com.example.eventapps.data.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.eventapps.data.ui.MainViewModel
import com.example.eventapps.data.ui.ViewModelFactory
import com.example.eventapps.data.work.DailyReminderWorker
import com.example.testingroomdatabase.databinding.FragmentSettingBinding
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var workManager: WorkManager

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Notifications permission rejected", Toast.LENGTH_SHORT).show()
                binding.switchReminder.isChecked = false
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workManager = WorkManager.getInstance(requireContext())

        val factory = ViewModelFactory.getInstance(requireContext())
        val mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        mainViewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }

        mainViewModel.getReminderSetting().observe(viewLifecycleOwner) { isReminderActive: Boolean ->
            binding.switchReminder.isChecked = isReminderActive
        }

        binding.switchReminder.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                mainViewModel.saveReminderSetting(true)
                startPeriodicWork()
            } else {
                mainViewModel.saveReminderSetting(false)
                cancelPeriodicWork()
            }
        }
    }

    private fun startPeriodicWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            DailyReminderWorker::class.java,
            1,
            TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag(REMINDER_WORK_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }

    private fun cancelPeriodicWork() {
        workManager.cancelUniqueWork(REMINDER_WORK_NAME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REMINDER_WORK_TAG = "daily_reminder_work"
        private const val REMINDER_WORK_NAME = "daily_reminder"
    }
}
