package com.vocabdaily.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val meaning: String,
    val pronunciation: String = "",
    val partOfSpeech: String = "",
    val exampleSentence: String = "",
    val userSentence: String = "",
    val notes: String = "",
    val category: String = "General",
    val dateAdded: Long = System.currentTimeMillis(),
    val lastReviewed: Long = 0L,
    val nextReview: Long = System.currentTimeMillis(),
    val memoryStrength: Int = 0, // 0-5 (SM-2 algorithm ease factor index)
    val reviewCount: Int = 0,
    val interval: Int = 1, // Days until next review
    val easeFactor: Float = 2.5f, // SM-2 ease factor
    val isMastered: Boolean = false
)
