package com.vocabdaily.ui.screens

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.vocabdaily.R
import com.vocabdaily.data.model.Word
import com.vocabdaily.databinding.FragmentAddWordBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddWordFragment : Fragment() {

    private var _binding: FragmentAddWordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private val args: AddWordFragmentArgs by navArgs()
    private var editingWord: Word? = null

    private val categories = listOf(
        "General", "Business", "Academic", "Literature",
        "Science", "Travel", "Emotions", "Phrasal Verbs",
        "Idioms", "Technology"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryDropdown()
        loadWordIfEditing()
        setupSaveButton()
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
        binding.actvCategory.setText("General", false)
    }

    private fun loadWordIfEditing() {
        val wordId = args.wordId
        if (wordId > 0) {
            binding.toolbar.title = "Edit Word"
            binding.btnSave.text = "Update Word"
            CoroutineScope(Dispatchers.Main).launch {
                val word = viewModel.getWordById(wordId)
                word?.let {
                    editingWord = it
                    binding.etWord.setText(it.word)
                    binding.etMeaning.setText(it.meaning)
                    binding.etPronunciation.setText(it.pronunciation)
                    binding.etPartOfSpeech.setText(it.partOfSpeech)
                    binding.etExampleSentence.setText(it.exampleSentence)
                    binding.etNotes.setText(it.notes)
                    binding.actvCategory.setText(it.category, false)
                }
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                saveWord()
            }
        }
    }

    private fun validateInput(): Boolean {
        val word = binding.etWord.text.toString().trim()
        val meaning = binding.etMeaning.text.toString().trim()

        return when {
            word.isEmpty() -> {
                binding.tilWord.error = "Word is required"
                false
            }
            meaning.isEmpty() -> {
                binding.tilMeaning.error = "Meaning is required"
                false
            }
            else -> {
                binding.tilWord.error = null
                binding.tilMeaning.error = null
                true
            }
        }
    }

    private fun saveWord() {
        val word = Word(
            id = editingWord?.id ?: 0,
            word = binding.etWord.text.toString().trim(),
            meaning = binding.etMeaning.text.toString().trim(),
            pronunciation = binding.etPronunciation.text.toString().trim(),
            partOfSpeech = binding.etPartOfSpeech.text.toString().trim(),
            exampleSentence = binding.etExampleSentence.text.toString().trim(),
            notes = binding.etNotes.text.toString().trim(),
            category = binding.actvCategory.text.toString().ifEmpty { "General" },
            dateAdded = editingWord?.dateAdded ?: System.currentTimeMillis(),
            easeFactor = editingWord?.easeFactor ?: 2.5f,
            interval = editingWord?.interval ?: 1,
            nextReview = editingWord?.nextReview ?: System.currentTimeMillis(),
            reviewCount = editingWord?.reviewCount ?: 0
        )

        if (editingWord != null) {
            viewModel.updateWord(word)
            Snackbar.make(binding.root, "Word updated!", Snackbar.LENGTH_SHORT).show()
        } else {
            viewModel.addWord(word)
            // Show save animation
            binding.btnSave.animate()
                .scaleX(0.95f).scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    binding.btnSave.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(100)
                        .start()
                }.start()
        }

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
