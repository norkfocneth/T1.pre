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
    const val ENGINE_VERSION: Int = 5

    // ── Max Growth Category Daily Cap (4 hours = 14,400,000 ms) ──
    const val MAX_GROWTH_CAP_MS: Long = 4 * 3600000L

    // ── Configurable Score Bands (Calibrated for Diminishing Returns) ──
    val SCORE_BANDS: List<ScoreBandConfig> = listOf(
        ScoreBandConfig(minScore =  0, maxScore = 50, positiveMultiplier = 2.0f, negativeMultiplier = 0.8f, maxDailyGain = 4.0f, maxDailyLoss = 4.0f, streakMultiplier = 1.10f),
        ScoreBandConfig(minScore = 51, maxScore = 76, positiveMultiplier = 1.5f, negativeMultiplier = 1.0f, maxDailyGain = 3.0f, maxDailyLoss = 3.0f, streakMultiplier = 1.08f),
        ScoreBandConfig(minScore = 77, maxScore = 86, positiveMultiplier = 1.0f, negativeMultiplier = 1.4f, maxDailyGain = 2.0f, maxDailyLoss = 2.5f, streakMultiplier = 1.05f),
        ScoreBandConfig(minScore = 87, maxScore = 99, positiveMultiplier = 0.5f, negativeMultiplier = 2.0f, maxDailyGain = 1.0f, maxDailyLoss = 2.0f, streakMultiplier = 1.02f)
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
        pastFocusScores: List<Int> = emptyList(),
        defaultBaseScore: Int = 50
    ): FocusScoreResult {
        val isGrowthGoal = goalCategory.uppercase() == "PRODUCTIVITY" || goalCategory.uppercase() == "EDUCATION"
        val isReductionGoal = goalCategory.uppercase() == "SOCIAL" || goalCategory.uppercase() == "ENTERTAINMENT" || goalCategory.uppercase() == "COMMUNICATION"

        // 1. Calculate relative delta raw value based on specific goal evaluation rules
        var rawDelta = 0f

        if (isGrowthGoal) {
            // Productivity Goal: If productive time increased vs baseline (+30% -> +1 pt, +60% -> +2 pts, +90%+ -> +3 pts max)
            if (todayCategoryTimeMs > baselineCategoryTimeMs) {
                val pctIncrease = if (baselineCategoryTimeMs > 0) {
                    (todayCategoryTimeMs - baselineCategoryTimeMs).toFloat() / baselineCategoryTimeMs.toFloat()
                } else {
                    1.0f // If baseline was 0, any usage is treated as 100%+ increase
                }
                rawDelta = when {
                    pctIncrease >= 0.90f -> 3.0f
                    pctIncrease >= 0.60f -> 2.0f
                    pctIncrease >= 0.30f -> 1.0f
                    else -> 0.5f
                }
            } else if (todayCategoryTimeMs < baselineCategoryTimeMs) {
                // If decreased -> score drops proportionally (up to -3 pts)
                val pctDecrease = if (baselineCategoryTimeMs > 0) {
                    (baselineCategoryTimeMs - todayCategoryTimeMs).toFloat() / baselineCategoryTimeMs.toFloat()
                } else {
                    0.0f
                }
                rawDelta = -3.0f * pctDecrease
            }
        } else if (isReductionGoal) {
            // Social Control Goal: If social/entertainment time decreased vs baseline (1 min less -> +1 pt, 30 min less -> +2 pts, 60+ min less -> +3 pts max)
            if (todayCategoryTimeMs < baselineCategoryTimeMs) {
                val minsLess = (baselineCategoryTimeMs - todayCategoryTimeMs).toFloat() / 60000f
                rawDelta = when {
                    minsLess >= 60f -> 3.0f
                    minsLess >= 30f -> 2.0f
                    minsLess >= 1f -> 1.0f
                    else -> 0.5f
                }
            } else if (todayCategoryTimeMs > baselineCategoryTimeMs) {
                // If used more than baseline -> score drops proportionally (up to -3 pts for +60 mins)
                val minsMore = (todayCategoryTimeMs - baselineCategoryTimeMs).toFloat() / 60000f
                rawDelta = -(minsMore / 20f).coerceAtMost(3.0f)
            }
        }

        // 2. Base score baseline
        val previousScore = yesterdayFocusScore
            ?: pastFocusScores.lastOrNull()
            ?: defaultBaseScore

        // 3. Lookup current band config
        val band = getBand(previousScore)

        // 4. Apply progressive multiplier from band config
        val scaledDelta = if (rawDelta >= 0) {
            rawDelta * band.positiveMultiplier
        } else {
            rawDelta * band.negativeMultiplier
        }

        // 5. Apply stability cap for loss
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

        // 8. Total positive daily progression cap (diminishing returns logic)
        val totalChange = streakAdjustedDelta + sessionBonus
        val cappedChange = if (totalChange > 0f) {
            totalChange.coerceAtMost(band.maxDailyGain)
        } else {
            totalChange
        }

        // 9. New raw score
        val newRaw = previousScore + cappedChange

        // 10. Rolling average smoothing (80% today, 20% past days)
        val finalScore = if (pastFocusScores.isNotEmpty()) {
            val avgPast = pastFocusScores.average().toFloat()
            (newRaw * 0.80f + avgPast * 0.20f).roundToInt()
        } else {
            newRaw.roundToInt()
        }

        // 11. Internal Trend calculation
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

    /**
     * Calculates the initial baseline Focus Score from the user's past 7 days of actual screen time.
     * Guaranteed to return a baseline score between 36 (worst case) and 77 (best case).
     */
    fun calculateInitialScoreFrom7Days(
        dailySummaries: List<com.example.t1.domain.model.behaviour.DailyBehaviourSummary>,
        goalCategory: String = "PRODUCTIVITY"
    ): Int {
        if (dailySummaries.isEmpty()) return 50

        val avgProductive = dailySummaries.map { it.productiveTimeMs }.average().toFloat()
        val avgEducation = dailySummaries.map { it.educationTimeMs }.average().toFloat()
        val avgCommunication = dailySummaries.map { it.communicationTimeMs }.average().toFloat()
        val avgEntertainment = dailySummaries.map { it.entertainmentTimeMs }.average().toFloat()
        val avgSocial = dailySummaries.map { it.socialTimeMs }.average().toFloat()

        val totalNonUtility = avgProductive + avgEducation + avgCommunication + avgEntertainment + avgSocial
        if (totalNonUtility <= 0f) return 50

        val prodRatio = (avgProductive + avgEducation) / totalNonUtility
        val distractRatio = (avgSocial + avgEntertainment + avgCommunication * 0.5f) / totalNonUtility

        val isGrowth = goalCategory.uppercase() == "PRODUCTIVITY" || goalCategory.uppercase() == "EDUCATION"
        val weightedRatio = if (isGrowth) {
            prodRatio * 0.7f + (1f - distractRatio) * 0.3f
        } else {
            prodRatio * 0.3f + (1f - distractRatio) * 0.7f
        }

        // Map weightedRatio linearly to [36, 77]
        val initialScore = 36f + (77f - 36f) * weightedRatio.coerceIn(0f, 1f)
        return initialScore.roundToInt()
    }
}
