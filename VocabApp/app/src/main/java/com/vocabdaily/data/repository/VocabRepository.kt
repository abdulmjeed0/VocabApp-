package com.vocabdaily.data.repository

import androidx.lifecycle.LiveData
import com.vocabdaily.data.db.AchievementDao
import com.vocabdaily.data.db.PracticeSessionDao
import com.vocabdaily.data.db.WordDao
import com.vocabdaily.data.model.Achievement
import com.vocabdaily.data.model.PracticeSession
import com.vocabdaily.data.model.Word
import com.vocabdaily.utils.UserPreferences

class VocabRepository(
    private val wordDao: WordDao,
    private val sessionDao: PracticeSessionDao,
    private val achievementDao: AchievementDao
) {

    // Words
    val allWords: LiveData<List<Word>> = wordDao.getAllWords()
    val allCategories: LiveData<List<String>> = wordDao.getAllCategories()
    val totalWordCount: LiveData<Int> = wordDao.getTotalWordCount()
    val masteredWordCount: LiveData<Int> = wordDao.getMasteredWordCount()
    val dueWordCount: LiveData<Int> = wordDao.getDueWordCount()

    // Sessions
    val allSessions: LiveData<List<PracticeSession>> = sessionDao.getAllSessions()
    val completedSessionCount: LiveData<Int> = sessionDao.getCompletedSessionCount()

    // Achievements
    val allAchievements: LiveData<List<Achievement>> = achievementDao.getAllAchievements()
    val unlockedAchievementCount: LiveData<Int> = achievementDao.getUnlockedCount()

    suspend fun insertWord(word: Word): Long = wordDao.insertWord(word)
    suspend fun updateWord(word: Word) = wordDao.updateWord(word)
    suspend fun deleteWord(word: Word) = wordDao.deleteWord(word)
    suspend fun getWordById(id: Long) = wordDao.getWordById(id)
    fun searchWords(query: String) = wordDao.searchWords(query)
    fun getWordsByCategory(category: String) = wordDao.getWordsByCategory(category)
    suspend fun getWordsForReview(limit: Int = 20) = wordDao.getWordsForReview(limit = limit)
    suspend fun getAllWordsList() = wordDao.getAllWordsList()

    suspend fun insertSession(session: PracticeSession): Long = sessionDao.insertSession(session)
    suspend fun updateSession(session: PracticeSession) = sessionDao.updateSession(session)
    suspend fun getRecentSessions() = sessionDao.getRecentSessions()
    suspend fun getTodaySession(today: Long) = sessionDao.getTodaySession(today)
    suspend fun getWordsAddedToday(start: Long, end: Long) = wordDao.getWordsAddedToday(start, end)

    suspend fun updateAchievement(achievement: Achievement) = achievementDao.updateAchievement(achievement)
    suspend fun getUnlockedAchievements() = achievementDao.getUnlockedAchievements()
}
