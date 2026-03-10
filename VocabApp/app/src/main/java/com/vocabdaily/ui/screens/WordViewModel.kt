package com.vocabdaily.ui.screens

import android.app.Application
import androidx.lifecycle.*
import com.vocabdaily.data.db.VocabDatabase
import com.vocabdaily.data.model.Word
import com.vocabdaily.data.repository.VocabRepository
import kotlinx.coroutines.launch

class WordViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VocabDatabase.getDatabase(application)
    val repository = VocabRepository(db.wordDao(), db.practiceSessionDao(), db.achievementDao())

    val allWords: LiveData<List<Word>> = repository.allWords
    val allCategories: LiveData<List<String>> = repository.allCategories

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _selectedCategory = MutableLiveData<String?>(null)
    val selectedCategory: LiveData<String?> = _selectedCategory

    private val _insertResult = MutableLiveData<Long>()
    val insertResult: LiveData<Long> = _insertResult

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> = _deleteResult

    val filteredWords: LiveData<List<Word>> = MediatorLiveData<List<Word>>().apply {
        fun update() {
            val query = _searchQuery.value ?: ""
            val category = _selectedCategory.value
            val words = allWords.value ?: emptyList()
            value = words.filter { word ->
                val matchesQuery = query.isEmpty() ||
                        word.word.contains(query, ignoreCase = true) ||
                        word.meaning.contains(query, ignoreCase = true)
                val matchesCategory = category == null || word.category == category
                matchesQuery && matchesCategory
            }
        }
        addSource(allWords) { update() }
        addSource(_searchQuery) { update() }
        addSource(_selectedCategory) { update() }
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setCategory(category: String?) { _selectedCategory.value = category }

    fun addWord(word: Word) {
        viewModelScope.launch {
            val id = repository.insertWord(word)
            _insertResult.value = id
        }
    }

    fun updateWord(word: Word) {
        viewModelScope.launch { repository.updateWord(word) }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.deleteWord(word)
            _deleteResult.value = true
        }
    }

    suspend fun getWordById(id: Long): Word? = repository.getWordById(id)
}
