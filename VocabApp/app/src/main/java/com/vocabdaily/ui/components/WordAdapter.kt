package com.vocabdaily.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vocabdaily.R
import com.vocabdaily.data.model.Word
import com.vocabdaily.databinding.ItemWordBinding

class WordAdapter(
    private val onItemClick: (Word) -> Unit,
    private val onDeleteClick: (Word) -> Unit,
    private val onEditClick: (Word) -> Unit
) : ListAdapter<Word, WordAdapter.WordViewHolder>(WordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WordViewHolder(private val binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word) {
            binding.tvWord.text = word.word
            binding.tvMeaning.text = word.meaning
            binding.tvCategory.text = word.category
            binding.tvPartOfSpeech.text = word.partOfSpeech.ifEmpty { "" }

            // Memory strength indicator
            val strength = word.memoryStrength
            binding.tvMemoryDots.text = "●".repeat(strength) + "○".repeat(5 - strength)

            // Mastered badge
            binding.tvMastered.visibility = if (word.isMastered) View.VISIBLE else View.GONE

            // Due indicator
            val isDue = word.nextReview <= System.currentTimeMillis() && !word.isMastered
            binding.indicatorDue.visibility = if (isDue) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                // Subtle click animation
                binding.root.animate()
                    .scaleX(0.98f).scaleY(0.98f)
                    .setDuration(80)
                    .withEndAction {
                        binding.root.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(100)
                            .start()
                        onItemClick(word)
                    }.start()
            }

            binding.btnMore.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.word_item_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_edit -> { onEditClick(word); true }
                        R.id.action_delete -> { onDeleteClick(word); true }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    class WordDiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Word, newItem: Word) = oldItem == newItem
    }
}
