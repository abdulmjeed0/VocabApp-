package com.vocabdaily.ui.screens

import android.app.Application
import androidx.lifecycle.*
import com.vocabdaily.data.db.VocabDatabase
import com.vocabdaily.data.model.PracticeSession
import com.vocabdaily.data.model.Word
import com.vocabdaily.data.repository.VocabRepository
import com.vocabdaily.utils.SpacedRepetition
import com.vocabdaily.utils.UserPreferences
import kotlinx.coroutines.launch

class PracticeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VocabDatabase.getDatabase(application)
    private val repository = VocabRepository(db.wordDao(), db.practiceSessionDao(), db.achievementDao())
    private val userPrefs = UserPreferences(application)

    private val _currentWord = MutableLiveData<Word?>()
    val currentWord: LiveData<Word?> = _currentWord

    private val _sessionWords = MutableLiveData<List<Word>>(emptyList())
    val sessionWords: LiveData<List<Word>> = _sessionWords

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    private val _totalWords = MutableLiveData(0)
    val totalWords: LiveData<Int> = _totalWords

    private val _isFlipped = MutableLiveData(false)
    val isFlipped: LiveData<Boolean> = _isFlipped

    private val _sessionComplete = MutableLiveData(false)
    val sessionComplete: LiveData<Boolean> = _sessionComplete

    private val _correctCount = MutableLiveData(0)
    val correctCount: LiveData<Int> = _correctCount

    private var currentSessionId: Long = -1
    private var reviewQueue: MutableList<Word> = mutableListOf()
    private var currentIndex = 0
    private val startTime = System.currentTimeMillis()

    fun startSession() {
        viewModelScope.launch {
            val wordsForReview = repository.getWordsForReview(20)
            if (wordsForReview.isEmpty()) {
                _sessionComplete.value = true
                return@launch
            }
            reviewQueue = wordsForReview.toMutableList()
            _totalWords.value = reviewQueue.size
            _progress.value = 0
            currentIndex = 0
            _isFlipped.value = false

            val session = PracticeSession(
                wordsReviewed = 0,
                isCompleted = false
            )
            currentSessionId = repository.insertSession(session)
            showCurrentWord()
        }
    }

    private fun showCurrentWord() {
        if (currentIndex < reviewQueue.size) {
            _currentWord.value = reviewQueue[currentIndex]
            _isFlipped.value = false
        } else {
            completeSession()
        }
    }

    fun flipCard() {
        _isFlipped.value = !(_isFlipped.value ?: false)
    }

    fun rateWord(button: SpacedRepetition.ReviewButton) {
        val word = _currentWord.value ?: return
        viewModelScope.launch {
            val quality = SpacedRepetition.buttonToQuality(button)
            val updatedWord = SpacedRepetition.calculateNextReview(word, quality)
            repository.updateWord(updatedWord)

            if (quality >= 3) {
                _correctCount.value = (_correctCount.value ?: 0) + 1
            }

            currentIndex++
            _progress.value = currentIndex
            showCurrentWord()
        }
    }

    fun submitUserSentence(sentence: String) {
        val word = _currentWord.value ?: return
        viewModelScope.launch {
            repository.updateWord(word.copy(userSentence = sentence))
        }
    }

    private fun completeSession() {
        viewModelScope.launch {
            val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            val correct = _correctCount.value ?: 0
            val total = _totalWords.value ?: 0

            if (currentSessionId > 0) {
                repository.updateSession(
                    PracticeSession(
                        id = currentSessionId,
                        wordsReviewed = total,
                        correctAnswers = correct,
                        durationSeconds = duration,
                        isCompleted = true
                    )
                )
            }

            userPrefs.updateStreak(true)
            _sessionComplete.value = true
        }
    }

    fun getAccuracyPercent(): Int {
        val correct = _correctCount.value ?: 0
        val total = _totalWords.value ?: 1
        return if (total > 0) (correct * 100 / total) else 0
    }
}
