package com.vocabdaily.ui.screens

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.vocabdaily.databinding.FragmentSettingsBinding
import com.vocabdaily.utils.ReminderWorker
import com.vocabdaily.utils.UserPreferences
import kotlinx.coroutines.launch
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPrefs: UserPreferences

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            enableReminder()
        } else {
            binding.switchReminder.isChecked = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPrefs = UserPreferences(requireContext())
        observePreferences()
        setupClickListeners()
    }

    private fun observePreferences() {
        userPrefs.reminderEnabled.asLiveData().observe(viewLifecycleOwner) { enabled ->
            binding.switchReminder.isChecked = enabled
        }

        userPrefs.reminderHour.asLiveData().observe(viewLifecycleOwner) { hour ->
            userPrefs.reminderMinute.asLiveData().observe(viewLifecycleOwner) { minute ->
                val amPm = if (hour < 12) "AM" else "PM"
                val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                binding.tvReminderTime.text = String.format("%d:%02d %s", hour12, minute, amPm)
            }
        }

        userPrefs.dailyGoal.asLiveData().observe(viewLifecycleOwner) { goal ->
            binding.tvDailyGoal.text = "$goal words per day"
            binding.sliderDailyGoal.value = goal.toFloat()
        }

        userPrefs.streakCount.asLiveData().observe(viewLifecycleOwner) { streak ->
            binding.tvCurrentStreak.text = "$streak days"
        }

        userPrefs.totalPracticeDays.asLiveData().observe(viewLifecycleOwner) { days ->
            binding.tvTotalDays.text = "$days days"
        }
    }

    private fun setupClickListeners() {
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestNotificationPermission()
            } else {
                disableReminder()
            }
        }

        binding.tvReminderTime.setOnClickListener {
            showTimePicker()
        }

        binding.btnPickTime.setOnClickListener {
            showTimePicker()
        }

        binding.sliderDailyGoal.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                val goal = value.toInt()
                binding.tvDailyGoal.text = "$goal words per day"
                lifecycleScope.launch { userPrefs.setDailyGoal(goal) }
            }
        }
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                lifecycleScope.launch {
                    userPrefs.setReminderTime(hourOfDay, minute, true)
                    ReminderWorker.schedule(requireContext(), hourOfDay, minute)
                }
            },
            currentHour, currentMinute, false
        ).show()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        enableReminder()
    }

    private fun enableReminder() {
        lifecycleScope.launch {
            val hour = userPrefs.reminderHour
            val minute = userPrefs.reminderMinute
            // Use default values if not set
            userPrefs.setReminderTime(20, 0, true)
            ReminderWorker.schedule(requireContext(), 20, 0)
        }
    }

    private fun disableReminder() {
        lifecycleScope.launch {
            userPrefs.setReminderTime(20, 0, false)
            ReminderWorker.cancel(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
