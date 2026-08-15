package com.example.t1.domain.focus

import kotlin.math.roundToInt

data class FocusScoreResult(
    val finalScore: Int,
    val trend: String
)

/**
 * Configurable score band definition.
 * All tuning happens here — the engine logic itself never uses raw numbers.
 */
data class ScoreBandConfig(
    val minScore: Int,
    val maxScore: Int,
    val positiveMultiplier: Float,
    val negativeMultiplier: Float,
    val maxDailyGain: Float,
    val maxDailyLoss: Float,
    val streakMultiplier: Float  // Applied when 3+ consecutive improving days
)

object FocusScoreEngine {

    // ── Version as integer ──
    const val ENGINE_VERSION: Int = 4

    // ── Max Growth Category Daily Cap (4 hours = 14,400,000 ms) ──
    const val MAX_GROWTH_CAP_MS: Long = 4 * 3600000L

    // ── Configurable Score Bands ──
    val SCORE_BANDS: List<ScoreBandConfig> = listOf(
        ScoreBandConfig(minScore =  0, maxScore = 40, positiveMultiplier = 2.0f, negativeMultiplier = 0.8f, maxDailyGain = 6.0f, maxDailyLoss = 4.0f, streakMultiplier = 1.15f),
        ScoreBandConfig(minScore = 41, maxScore = 69, positiveMultiplier = 1.8f, negativeMultiplier = 1.0f, maxDailyGain = 4.5f, maxDailyLoss = 3.5f, streakMultiplier = 1.10f),
        ScoreBandConfig(minScore = 70, maxScore = 78, positiveMultiplier = 1.4f, negativeMultiplier = 1.2f, maxDailyGain = 3.5f, maxDailyLoss = 3.0f, streakMultiplier = 1.08f),
        ScoreBandConfig(minScore = 79, maxScore = 89, positiveMultiplier = 1.0f, negativeMultiplier = 1.4f, maxDailyGain = 2.5f, maxDailyLoss = 2.5f, streakMultiplier = 1.06f),
        ScoreBandConfig(minScore = 90, maxScore = 96, positiveMultiplier = 0.6f, negativeMultiplier = 1.6f, maxDailyGain = 1.5f, maxDailyLoss = 2.0f, streakMultiplier = 1.04f),
        ScoreBandConfig(minScore = 97, maxScore = 99, positiveMultiplier = 0.3f, negativeMultiplier = 2.0f, maxDailyGain = 0.8f, maxDailyLoss = 1.5f, streakMultiplier = 1.02f),
    )

    private fun getBand(score: Int): ScoreBandConfig {
        return SCORE_BANDS.firstOrNull { score in it.minScore..it.maxScore }
            ?: SCORE_BANDS.last()
    }

    /**
     * Calculates the daily Focus Score (0-99) purely from actual user behavior
     * relative to their personal 7-day category baseline and primary goal.
     */
    fun calculateFocusScore(
        goalCategory: String,
        todayCategoryTimeMs: Long,
        baselineCategoryTimeMs: Long,
        completedFocusSessionsCount: Int = 0,
        yesterdayFocusScore: Int? = null,
        pastFocusScores: List<Int> = emptyList()
    ): FocusScoreResult {
        val isGrowthGoal = goalCategory.uppercase() == "PRODUCTIVITY" || goalCategory.uppercase() == "EDUCATION"
        val isReductionGoal = goalCategory.uppercase() == "SOCIAL" || goalCategory.uppercase() == "ENTERTAINMENT" || goalCategory.uppercase() == "COMMUNICATION"

        // 1. Calculate relative delta vs baseline
        val deltaMs = if (isGrowthGoal) {
            // Capped at 4h max to prevent idling/farming
            val cappedToday = todayCategoryTimeMs.coerceAtMost(MAX_GROWTH_CAP_MS)
            val cappedBaseline = baselineCategoryTimeMs.coerceAtMost(MAX_GROWTH_CAP_MS)
            cappedToday - cappedBaseline
        } else if (isReductionGoal) {
            // For reduction goals: less usage than baseline is positive
            baselineCategoryTimeMs - todayCategoryTimeMs
        } else {
            todayCategoryTimeMs - baselineCategoryTimeMs
        }

        // Convert delta to minutes for intuitive scaling (30 min shift = ~1.0 raw shift)
        val deltaMinutes = deltaMs.toFloat() / 60000f
        val rawDelta = (deltaMinutes / 30f).coerceIn(-15f, 15f)

        // 2. Base score baseline
        val previousScore = yesterdayFocusScore
            ?: pastFocusScores.lastOrNull()
            ?: 50

        // 3. Lookup current band config
        val band = getBand(previousScore)

        // 4. Apply progressive multiplier from band config
        val scaledDelta = if (rawDelta >= 0) {
            rawDelta * band.positiveMultiplier
        } else {
            rawDelta * band.negativeMultiplier
        }

        // 5. Apply stability cap
        val cappedDelta = scaledDelta.coerceIn(-band.maxDailyLoss, band.maxDailyGain)

        // 6. Streak multiplier: if last 3 days were improving
        val streakAdjustedDelta = if (
            cappedDelta > 0 &&
            pastFocusScores.size >= 3 &&
            pastFocusScores.zipWithNext().all { (a, b) -> b >= a }
        ) {
            (cappedDelta * band.streakMultiplier).coerceAtMost(band.maxDailyGain)
        } else {
            cappedDelta
        }

        // 7. Focus Session Bonus: +1 point per completed session (capped at +3 points)
        val sessionBonus = (completedFocusSessionsCount * 1.0f).coerceAtMost(3.0f)

        // 8. New raw score
        val newRaw = previousScore + streakAdjustedDelta + sessionBonus

        // 9. Rolling average smoothing (80% today, 20% past days)
        val finalScore = if (pastFocusScores.isNotEmpty()) {
            val avgPast = pastFocusScores.average().toFloat()
            (newRaw * 0.80f + avgPast * 0.20f).roundToInt()
        } else {
            newRaw.roundToInt()
        }

        // 10. Internal Trend calculation
        val trend = when {
            finalScore > previousScore -> "Improving"
            finalScore < previousScore -> "Declining"
            else -> "Stable"
        }

        return FocusScoreResult(
            finalScore = finalScore.coerceIn(0, 99),
            trend = trend
        )
    }
}
