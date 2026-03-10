package com.vocabdaily.ui.screens

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vocabdaily.R
import com.vocabdaily.databinding.FragmentPracticeBinding
import com.vocabdaily.utils.SpacedRepetition

class PracticeFragment : Fragment() {

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PracticeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupClickListeners()
        viewModel.startSession()
    }

    private fun observeViewModel() {
        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            word ?: return@observe
            binding.tvWord.text = word.word
            binding.tvPronunciation.text = word.pronunciation.ifEmpty { "" }
            binding.tvPartOfSpeech.text = word.partOfSpeech.ifEmpty { "" }
            binding.tvMeaning.text = word.meaning
            binding.tvExample.text = if (word.exampleSentence.isNotEmpty())
                "\"${word.exampleSentence}\""
            else ""
            binding.tvMemoryStrength.text = "Memory: ${"●".repeat(word.memoryStrength)}${"○".repeat(5 - word.memoryStrength)}"
        }

        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            val total = viewModel.totalWords.value ?: 1
            binding.progressBar.progress = (progress * 100 / total)
            binding.tvProgress.text = "$progress / $total"
        }

        viewModel.isFlipped.observe(viewLifecycleOwner) { isFlipped ->
            if (isFlipped) {
                showAnswer()
            } else {
                showQuestion()
            }
        }

        viewModel.sessionComplete.observe(viewLifecycleOwner) { complete ->
            if (complete) {
                showCompletionScreen()
            }
        }
    }

    private fun showQuestion() {
        binding.layoutFront.visibility = View.VISIBLE
        binding.layoutBack.visibility = View.GONE
        binding.layoutRatingButtons.visibility = View.GONE
        binding.btnFlip.text = "Reveal Answer"
        binding.btnFlip.visibility = View.VISIBLE
    }

    private fun showAnswer() {
        binding.layoutFront.visibility = View.GONE
        binding.layoutBack.visibility = View.VISIBLE
        binding.layoutRatingButtons.visibility = View.VISIBLE
        binding.btnFlip.visibility = View.GONE

        // Subtle entrance animation
        binding.layoutBack.alpha = 0f
        binding.layoutBack.animate().alpha(1f).setDuration(250).start()
        binding.layoutRatingButtons.translationY = 60f
        binding.layoutRatingButtons.animate().translationY(0f).setDuration(300).start()
    }

    private fun setupClickListeners() {
        binding.btnFlip.setOnClickListener {
            viewModel.flipCard()
        }

        binding.btnAgain.setOnClickListener {
            animateButton(binding.btnAgain)
            viewModel.rateWord(SpacedRepetition.ReviewButton.AGAIN)
        }

        binding.btnHard.setOnClickListener {
            animateButton(binding.btnHard)
            viewModel.rateWord(SpacedRepetition.ReviewButton.HARD)
        }

        binding.btnGood.setOnClickListener {
            animateButton(binding.btnGood)
            viewModel.rateWord(SpacedRepetition.ReviewButton.GOOD)
        }

        binding.btnEasy.setOnClickListener {
            animateButton(binding.btnEasy)
            viewModel.rateWord(SpacedRepetition.ReviewButton.EASY)
        }
    }

    private fun animateButton(view: View) {
        view.animate()
            .scaleX(0.9f).scaleY(0.9f)
            .setDuration(80)
            .withEndAction {
                view.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(120)
                    .start()
            }.start()
    }

    private fun showCompletionScreen() {
        binding.layoutPractice.visibility = View.GONE
        binding.layoutComplete.visibility = View.VISIBLE
        binding.lottieComplete.playAnimation()

        val accuracy = viewModel.getAccuracyPercent()
        val total = viewModel.totalWords.value ?: 0
        binding.tvCompletionMessage.text = when {
            accuracy >= 90 -> "Outstanding! 🌟"
            accuracy >= 70 -> "Great job! Keep it up! 💪"
            accuracy >= 50 -> "Good effort! Practice makes perfect! 📚"
            else -> "Keep going! Every session counts! 🌱"
        }
        binding.tvCompletionStats.text = "Reviewed $total words · $accuracy% accuracy"

        binding.btnContinue.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
