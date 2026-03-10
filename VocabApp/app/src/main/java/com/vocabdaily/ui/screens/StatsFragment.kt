package com.vocabdaily.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vocabdaily.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val wordViewModel: WordViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun observeData() {
        wordViewModel.allWords.observe(viewLifecycleOwner) { words ->
            val total = words.size
            val mastered = words.count { it.isMastered }
            val learning = words.count { it.reviewCount > 0 && !it.isMastered }
            val newWords = words.count { it.reviewCount == 0 }

            binding.tvTotalWordsValue.text = total.toString()
            binding.tvMasteredValue.text = mastered.toString()
            binding.tvLearningValue.text = learning.toString()
            binding.tvNewValue.text = newWords.toString()

            // Progress
            val masteryPercent = if (total > 0) (mastered * 100 / total) else 0
            binding.progressMastery.progress = masteryPercent
            binding.tvMasteryPercent.text = "$masteryPercent%"

            // Category breakdown
            val categoryCounts = words.groupBy { it.category }
                .mapValues { it.value.size }
                .entries.sortedByDescending { it.value }

            val categoryText = categoryCounts.take(5).joinToString("\n") { (cat, count) ->
                "$cat: $count words"
            }
            binding.tvCategoryBreakdown.text = categoryText.ifEmpty { "No words yet" }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
