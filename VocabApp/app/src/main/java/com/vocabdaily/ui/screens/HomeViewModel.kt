package com.vocabdaily.ui.screens

import android.app.Application
import androidx.lifecycle.*
import com.vocabdaily.data.db.VocabDatabase
import com.vocabdaily.data.model.Word
import com.vocabdaily.data.repository.VocabRepository
import com.vocabdaily.utils.UserPreferences
import com.vocabdaily.utils.UserPreferences.Companion.getEndOfDay
import com.vocabdaily.utils.UserPreferences.Companion.getStartOfDay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VocabDatabase.getDatabase(application)
    private val repository = VocabRepository(db.wordDao(), db.practiceSessionDao(), db.achievementDao())
    private val userPrefs = UserPreferences(application)

    val totalWords: LiveData<Int> = repository.totalWordCount
    val masteredWords: LiveData<Int> = repository.masteredWordCount
    val dueWords: LiveData<Int> = repository.dueWordCount
    val unlockedAchievements: LiveData<Int> = repository.unlockedAchievementCount

    val streakCount: Flow<Int> = userPrefs.streakCount
    val dailyGoal: Flow<Int> = userPrefs.dailyGoal

    private val _wordsAddedToday = MutableLiveData(0)
    val wordsAddedToday: LiveData<Int> = _wordsAddedToday

    private val _motivationalMessage = MutableLiveData<String>()
    val motivationalMessage: LiveData<String> = _motivationalMessage

    private val _todaySessionComplete = MutableLiveData(false)
    val todaySessionComplete: LiveData<Boolean> = _todaySessionComplete

    init {
        loadTodayData()
        loadMotivationalMessage()
    }

    private fun loadTodayData() {
        viewModelScope.launch {
            val added = repository.getWordsAddedToday(getStartOfDay(), getEndOfDay())
            _wordsAddedToday.value = added

            val session = repository.getTodaySession(getStartOfDay())
            _todaySessionComplete.value = session?.isCompleted == true
        }
    }

    private fun loadMotivationalMessage() {
        val messages = listOf(
            "Every word you learn opens a new door. 🚪",
            "Small steps every day lead to big progress. 🌱",
            "Your future self will thank you for practicing today. ✨",
            "Language is the road map of a culture. 🗺️",
            "Consistency beats intensity. Show up today. 💪",
            "You are one word closer to fluency. 📖",
            "Learning is a gift you give yourself. 🎁",
            "The best time to practice was yesterday. The second best is now. ⏰"
        )
        _motivationalMessage.value = messages.random()
    }

    fun refreshData() {
        loadTodayData()
    }
}
