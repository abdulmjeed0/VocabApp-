package com.vocabdaily.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vocabdaily.data.model.Achievement

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, threshold ASC")
    fun getAllAchievements(): LiveData<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1")
    suspend fun getUnlockedAchievements(): List<Achievement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<Achievement>)

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedCount(): LiveData<Int>
}
