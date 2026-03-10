package com.vocabdaily.ui.screens

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.vocabdaily.R
import com.vocabdaily.data.model.Word
import com.vocabdaily.databinding.FragmentWordListBinding
import com.vocabdaily.ui.components.WordAdapter

class WordListFragment : Fragment() {

    private var _binding: FragmentWordListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private lateinit var adapter: WordAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeViewModel()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = WordAdapter(
            onItemClick = { word -> navigateToWordDetail(word) },
            onDeleteClick = { word -> deleteWord(word) },
            onEditClick = { word -> navigateToEdit(word) }
        )
        binding.rvWords.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@WordListFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewModel.filteredWords.observe(viewLifecycleOwner) { words ->
            adapter.submitList(words)
            binding.tvEmptyState.visibility = if (words.isEmpty()) View.VISIBLE else View.GONE
            binding.tvWordCount.text = "${words.size} words"
        }

        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            binding.chipGroupCategories.removeAllViews()

            // Add "All" chip
            val allChip = Chip(requireContext()).apply {
                text = "All"
                isCheckable = true
                isChecked = viewModel.selectedCategory.value == null
                setOnClickListener { viewModel.setCategory(null) }
            }
            binding.chipGroupCategories.addView(allChip)

            categories.forEach { category ->
                val chip = Chip(requireContext()).apply {
                    text = category
                    isCheckable = true
                    setOnClickListener { viewModel.setCategory(category) }
                }
                binding.chipGroupCategories.addView(chip)
            }
        }
    }

    private fun setupFab() {
        binding.fabAddWord.setOnClickListener {
            findNavController().navigate(R.id.action_wordListFragment_to_addWordFragment)
        }
    }

    private fun navigateToWordDetail(word: Word) {
        val action = WordListFragmentDirections.actionWordListFragmentToWordDetailFragment(word.id)
        findNavController().navigate(action)
    }

    private fun navigateToEdit(word: Word) {
        val action = WordListFragmentDirections.actionWordListFragmentToAddWordFragment(word.id)
        findNavController().navigate(action)
    }

    private fun deleteWord(word: Word) {
        viewModel.deleteWord(word)
        Snackbar.make(binding.root, "\"${word.word}\" deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") { viewModel.addWord(word) }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
