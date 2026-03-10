package com.vocabdaily.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vocabdaily.data.model.Word

@Dao
interface WordDao {

    @Query("SELECT * FROM words ORDER BY dateAdded DESC")
    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT * FROM words ORDER BY dateAdded DESC")
    suspend fun getAllWordsList(): List<Word>

    @Query("SELECT * FROM words WHERE category = :category ORDER BY dateAdded DESC")
    fun getWordsByCategory(category: String): LiveData<List<Word>>

    @Query("SELECT * FROM words WHERE nextReview <= :currentTime AND isMastered = 0 ORDER BY nextReview ASC LIMIT :limit")
    suspend fun getWordsForReview(currentTime: Long = System.currentTimeMillis(), limit: Int = 20): List<Word>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?

    @Query("SELECT DISTINCT category FROM words ORDER BY category ASC")
    fun getAllCategories(): LiveData<List<String>>

    @Query("SELECT COUNT(*) FROM words")
    fun getTotalWordCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isMastered = 1")
    fun getMasteredWordCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM words WHERE nextReview <= :currentTime AND isMastered = 0")
    fun getDueWordCount(currentTime: Long = System.currentTimeMillis()): LiveData<Int>

    @Query("SELECT * FROM words WHERE word LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%'")
    fun searchWords(query: String): LiveData<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("SELECT COUNT(*) FROM words WHERE dateAdded >= :startOfDay AND dateAdded <= :endOfDay")
    suspend fun getWordsAddedToday(startOfDay: Long, endOfDay: Long): Int
}
