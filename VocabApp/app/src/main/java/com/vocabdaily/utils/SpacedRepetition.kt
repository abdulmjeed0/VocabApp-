package com.vocabdaily.utils

import com.vocabdaily.data.model.Word
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Implementation of the SuperMemo SM-2 spaced repetition algorithm.
 * Quality ratings: 0-5
 *  5 = perfect response
 *  4 = correct response after a hesitation
 *  3 = correct response recalled with serious difficulty
 *  2 = incorrect response; where the correct one seemed easy to recall
 *  1 = incorrect response; the correct one remembered
 *  0 = complete blackout
 */
object SpacedRepetition {

    private const val MIN_EASE_FACTOR = 1.3f
    private const val DAY_MILLIS = 24 * 60 * 60 * 1000L

    /**
     * Apply SM-2 algorithm and return updated word.
     * @param word The word being reviewed
     * @param quality Rating 0-5
     */
    fun calculateNextReview(word: Word, quality: Int): Word {
        val q = quality.coerceIn(0, 5)

        // Calculate new ease factor
        val newEaseFactor = max(
            MIN_EASE_FACTOR,
            word.easeFactor + (0.1f - (5 - q) * (0.08f + (5 - q) * 0.02f))
        )

        // Calculate new interval
        val newInterval = when {
            q < 3 -> 1 // Failed recall - reset to 1 day
            word.reviewCount == 0 -> 1
            word.reviewCount == 1 -> 6
            else -> (word.interval * newEaseFactor).roundToInt()
        }

        val newReviewCount = if (q >= 3) word.reviewCount + 1 else 0
        val newMemoryStrength = calculateMemoryStrength(q, newInterval)
        val isMastered = newInterval >= 21 && q >= 4

        return word.copy(
            easeFactor = newEaseFactor,
            interval = newInterval,
            nextReview = System.currentTimeMillis() + (newInterval * DAY_MILLIS),
            lastReviewed = System.currentTimeMillis(),
            reviewCount = newReviewCount,
            memoryStrength = newMemoryStrength,
            isMastered = isMastered
        )
    }

    /**
     * Maps algorithm state to a 0-5 visual memory strength bar.
     */
    private fun calculateMemoryStrength(quality: Int, interval: Int): Int {
        return when {
            quality < 3 -> 0
            interval <= 1 -> 1
            interval <= 3 -> 2
            interval <= 7 -> 3
            interval <= 14 -> 4
            else -> 5
        }
    }

    /**
     * Converts a tap on "Easy/Good/Hard/Again" to a quality score.
     */
    fun buttonToQuality(button: ReviewButton): Int = when (button) {
        ReviewButton.AGAIN -> 0
        ReviewButton.HARD -> 2
        ReviewButton.GOOD -> 4
        ReviewButton.EASY -> 5
    }

    enum class ReviewButton { AGAIN, HARD, GOOD, EASY }
}
