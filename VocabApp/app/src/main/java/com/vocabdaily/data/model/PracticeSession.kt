package com.vocabdaily.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_sessions")
data class PracticeSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val wordsReviewed: Int = 0,
    val correctAnswers: Int = 0,
    val durationSeconds: Int = 0,
    val isCompleted: Boolean = false
)
