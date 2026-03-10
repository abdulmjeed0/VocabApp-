package com.vocabdaily.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vocab_prefs")

object PreferenceKeys {
    val STREAK_COUNT = intPreferencesKey("streak_count")
    val LAST_PRACTICE_DAY = longPreferencesKey("last_practice_day")
    val TOTAL_PRACTICE_DAYS = intPreferencesKey("total_practice_days")
    val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
    val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    val DAILY_GOAL = intPreferencesKey("daily_goal")
    val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    val NOTIFICATION_PERMISSION_REQUESTED = booleanPreferencesKey("notification_permission_requested")
}

class UserPreferences(private val context: Context) {

    val streakCount: Flow<Int> = context.dataStore.data.map { it[PreferenceKeys.STREAK_COUNT] ?: 0 }
    val lastPracticeDay: Flow<Long> = context.dataStore.data.map { it[PreferenceKeys.LAST_PRACTICE_DAY] ?: 0L }
    val totalPracticeDays: Flow<Int> = context.dataStore.data.map { it[PreferenceKeys.TOTAL_PRACTICE_DAYS] ?: 0 }
    val reminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferenceKeys.REMINDER_ENABLED] ?: true }
    val reminderHour: Flow<Int> = context.dataStore.data.map { it[PreferenceKeys.REMINDER_HOUR] ?: 20 }
    val reminderMinute: Flow<Int> = context.dataStore.data.map { it[PreferenceKeys.REMINDER_MINUTE] ?: 0 }
    val dailyGoal: Flow<Int> = context.dataStore.data.map { it[PreferenceKeys.DAILY_GOAL] ?: 10 }

    suspend fun updateStreak(practiceCompleted: Boolean) {
        val today = getDayTimestamp()
        context.dataStore.edit { prefs ->
            val lastDay = prefs[PreferenceKeys.LAST_PRACTICE_DAY] ?: 0L
            val currentStreak = prefs[PreferenceKeys.STREAK_COUNT] ?: 0

            if (practiceCompleted) {
                val isConsecutive = (today - lastDay) <= DAY_MILLIS
                val isToday = lastDay == today

                when {
                    isToday -> { /* already counted */ }
                    isConsecutive -> {
                        prefs[PreferenceKeys.STREAK_COUNT] = currentStreak + 1
                        prefs[PreferenceKeys.TOTAL_PRACTICE_DAYS] = (prefs[PreferenceKeys.TOTAL_PRACTICE_DAYS] ?: 0) + 1
                        prefs[PreferenceKeys.LAST_PRACTICE_DAY] = today
                    }
                    else -> {
                        prefs[PreferenceKeys.STREAK_COUNT] = 1
                        prefs[PreferenceKeys.TOTAL_PRACTICE_DAYS] = (prefs[PreferenceKeys.TOTAL_PRACTICE_DAYS] ?: 0) + 1
                        prefs[PreferenceKeys.LAST_PRACTICE_DAY] = today
                    }
                }
            }
        }
    }

    suspend fun setReminderTime(hour: Int, minute: Int, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.REMINDER_HOUR] = hour
            prefs[PreferenceKeys.REMINDER_MINUTE] = minute
            prefs[PreferenceKeys.REMINDER_ENABLED] = enabled
        }
    }

    suspend fun setDailyGoal(goal: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.DAILY_GOAL] = goal
        }
    }

    suspend fun setOnboardingComplete() {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.ONBOARDING_COMPLETE] = true
        }
    }

    companion object {
        const val DAY_MILLIS = 24 * 60 * 60 * 1000L

        fun getDayTimestamp(): Long {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return cal.timeInMillis
        }

        fun getStartOfDay(): Long = getDayTimestamp()
        fun getEndOfDay(): Long = getDayTimestamp() + DAY_MILLIS - 1
    }
}
