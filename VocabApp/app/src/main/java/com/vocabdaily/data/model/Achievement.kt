package com.vocabdaily.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val threshold: Int = 0,
    val type: AchievementType
)

enum class AchievementType {
    STREAK, WORDS_LEARNED, PRACTICE_SESSIONS, PERFECT_RECALL
}
