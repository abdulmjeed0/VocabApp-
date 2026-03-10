package com.vocabdaily.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vocabdaily.databinding.FragmentWordDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordDetailFragment : Fragment() {

    private var _binding: FragmentWordDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private val args: WordDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        loadWord()
    }

    private fun loadWord() {
        CoroutineScope(Dispatchers.Main).launch {
            val word = viewModel.getWordById(args.wordId) ?: return@launch
            binding.tvWord.text = word.word
            binding.tvPronunciation.text = word.pronunciation
            binding.tvPartOfSpeech.text = word.partOfSpeech
            binding.tvMeaning.text = word.meaning
            binding.tvExampleSentence.text = word.exampleSentence.ifEmpty { "No example added" }
            binding.tvUserSentence.text = word.userSentence.ifEmpty { "You haven't written a sentence yet." }
            binding.tvNotes.text = word.notes.ifEmpty { "" }
            binding.tvCategory.text = word.category
            binding.tvMemoryStrength.text = "●".repeat(word.memoryStrength) + "○".repeat(5 - word.memoryStrength)
            binding.tvReviewCount.text = "Reviewed ${word.reviewCount} times"
            binding.tvIsMastered.visibility = if (word.isMastered) View.VISIBLE else View.GONE

            binding.btnEdit.setOnClickListener {
                val action = WordDetailFragmentDirections.actionWordDetailFragmentToAddWordFragment(word.id)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
