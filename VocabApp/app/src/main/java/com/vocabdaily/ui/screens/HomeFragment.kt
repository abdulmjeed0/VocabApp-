package com.vocabdaily.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.vocabdaily.R
import com.vocabdaily.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGreeting()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
        binding.tvGreeting.text = greeting

        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(Date())
    }

    private fun observeViewModel() {
        viewModel.totalWords.observe(viewLifecycleOwner) { count ->
            binding.tvTotalWords.text = count.toString()
        }

        viewModel.masteredWords.observe(viewLifecycleOwner) { count ->
            binding.tvMasteredWords.text = count.toString()
        }

        viewModel.dueWords.observe(viewLifecycleOwner) { count ->
            binding.tvDueWords.text = count.toString()
            binding.btnStartPractice.text = if (count > 0)
                "Practice $count words"
            else
                "All caught up! ✨"
            binding.btnStartPractice.isEnabled = count > 0
        }

        viewModel.motivationalMessage.observe(viewLifecycleOwner) { message ->
            binding.tvMotivation.text = message
        }

        viewModel.streakCount.asLiveData().observe(viewLifecycleOwner) { streak ->
            binding.tvStreakCount.text = streak.toString()
            binding.tvStreakLabel.text = if (streak == 1) "day streak" else "day streak"

            // Fire animation for any streak > 0
            if (streak > 0) {
                binding.ivStreakFire.animate()
                    .scaleX(1.2f).scaleY(1.2f)
                    .setDuration(300)
                    .withEndAction {
                        binding.ivStreakFire.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(200)
                            .start()
                    }.start()
            }
        }

        viewModel.todaySessionComplete.observe(viewLifecycleOwner) { complete ->
            if (complete) {
                binding.tvPracticeStatus.text = "✅ Practice complete today!"
                binding.tvPracticeStatus.visibility = View.VISIBLE
            } else {
                binding.tvPracticeStatus.visibility = View.GONE
            }
        }

        viewModel.wordsAddedToday.observe(viewLifecycleOwner) { count ->
            binding.tvWordsToday.text = "+$count today"
        }
    }

    private fun setupClickListeners() {
        binding.btnStartPractice.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_practiceFragment)
        }

        binding.btnAddWord.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addWordFragment)
        }

        binding.cardStats.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_statsFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
