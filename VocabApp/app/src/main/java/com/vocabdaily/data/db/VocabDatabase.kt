package com.vocabdaily.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vocabdaily.data.model.Achievement
import com.vocabdaily.data.model.AchievementType
import com.vocabdaily.data.model.PracticeSession
import com.vocabdaily.data.model.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Word::class, PracticeSession::class, Achievement::class],
    version = 1,
    exportSchema = false
)
abstract class VocabDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun practiceSessionDao(): PracticeSessionDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: VocabDatabase? = null

        fun getDatabase(context: Context): VocabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabDatabase::class.java,
                    "vocab_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateAchievements(database.achievementDao())
                    populateSampleWords(database.wordDao())
                }
            }
        }

        suspend fun populateAchievements(dao: AchievementDao) {
            val achievements = listOf(
                Achievement(title = "First Word!", description = "Save your very first word", iconName = "ic_star", threshold = 1, type = AchievementType.WORDS_LEARNED),
                Achievement(title = "Word Collector", description = "Save 10 words", iconName = "ic_book", threshold = 10, type = AchievementType.WORDS_LEARNED),
                Achievement(title = "Vocabulary Builder", description = "Save 50 words", iconName = "ic_trophy", threshold = 50, type = AchievementType.WORDS_LEARNED),
                Achievement(title = "Word Master", description = "Save 100 words", iconName = "ic_crown", threshold = 100, type = AchievementType.WORDS_LEARNED),
                Achievement(title = "3-Day Streak", description = "Practice 3 days in a row", iconName = "ic_fire", threshold = 3, type = AchievementType.STREAK),
                Achievement(title = "Week Warrior", description = "Practice 7 days in a row", iconName = "ic_flame", threshold = 7, type = AchievementType.STREAK),
                Achievement(title = "Monthly Master", description = "Practice 30 days in a row", iconName = "ic_medal", threshold = 30, type = AchievementType.STREAK),
                Achievement(title = "First Practice", description = "Complete your first practice session", iconName = "ic_check", threshold = 1, type = AchievementType.PRACTICE_SESSIONS),
                Achievement(title = "Dedicated Learner", description = "Complete 10 practice sessions", iconName = "ic_brain", threshold = 10, type = AchievementType.PRACTICE_SESSIONS),
                Achievement(title = "Perfect Score", description = "Get 100% on a practice session", iconName = "ic_perfect", threshold = 1, type = AchievementType.PERFECT_RECALL)
            )
            dao.insertAll(achievements)
        }

        suspend fun populateSampleWords(dao: WordDao) {
            val sampleWords = listOf(
                Word(
                    word = "Serendipity",
                    meaning = "The occurrence of events by chance in a happy or beneficial way",
                    partOfSpeech = "noun",
                    pronunciation = "ser-en-DIP-i-tee",
                    exampleSentence = "It was pure serendipity that we found the perfect venue on our first visit.",
                    category = "General",
                    memoryStrength = 0
                ),
                Word(
                    word = "Ephemeral",
                    meaning = "Lasting for a very short time; transitory",
                    partOfSpeech = "adjective",
                    pronunciation = "ih-FEM-er-ul",
                    exampleSentence = "Cherry blossoms are ephemeral, blooming only for a brief moment in spring.",
                    category = "General",
                    memoryStrength = 0
                ),
                Word(
                    word = "Resilient",
                    meaning = "Able to recover quickly from difficult conditions",
                    partOfSpeech = "adjective",
                    pronunciation = "rih-ZIL-yent",
                    exampleSentence = "She proved to be resilient, bouncing back after each setback.",
                    category = "General",
                    memoryStrength = 0
                )
            )
            sampleWords.forEach { dao.insertWord(it) }
        }
    }
}
