package com.vocabdaily.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vocabdaily.data.model.PracticeSession

@Dao
interface PracticeSessionDao {

    @Query("SELECT * FROM practice_sessions ORDER BY date DESC")
    fun getAllSessions(): LiveData<List<PracticeSession>>

    @Query("SELECT * FROM practice_sessions WHERE date >= :startTime AND date <= :endTime")
    suspend fun getSessionsInRange(startTime: Long, endTime: Long): List<PracticeSession>

    @Query("SELECT COUNT(*) FROM practice_sessions WHERE isCompleted = 1")
    fun getCompletedSessionCount(): LiveData<Int>

    @Query("SELECT * FROM practice_sessions WHERE date >= :today ORDER BY date DESC LIMIT 1")
    suspend fun getTodaySession(today: Long): PracticeSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSession): Long

    @Update
    suspend fun updateSession(session: PracticeSession)

    @Query("SELECT * FROM practice_sessions ORDER BY date DESC LIMIT 30")
    suspend fun getRecentSessions(): List<PracticeSession>
}
