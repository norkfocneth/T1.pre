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
    const val ENGINE_VERSION: Int = 3

    // ── Configurable Score Bands ──
    // To tune difficulty: only modify this list. Engine logic stays untouched.
    val SCORE_BANDS: List<ScoreBandConfig> = listOf(
        ScoreBandConfig(minScore =  0, maxScore = 40, positiveMultiplier = 1.6f, negativeMultiplier = 0.8f, maxDailyGain = 5.0f, maxDailyLoss = 4.0f, streakMultiplier = 1.15f),
        ScoreBandConfig(minScore = 41, maxScore = 69, positiveMultiplier = 1.2f, negativeMultiplier = 1.0f, maxDailyGain = 3.5f, maxDailyLoss = 3.5f, streakMultiplier = 1.10f),
        ScoreBandConfig(minScore = 70, maxScore = 78, positiveMultiplier = 1.0f, negativeMultiplier = 1.1f, maxDailyGain = 2.5f, maxDailyLoss = 2.8f, streakMultiplier = 1.08f),
        ScoreBandConfig(minScore = 79, maxScore = 89, positiveMultiplier = 0.8f, negativeMultiplier = 1.3f, maxDailyGain = 1.5f, maxDailyLoss = 2.0f, streakMultiplier = 1.06f),
        ScoreBandConfig(minScore = 90, maxScore = 96, positiveMultiplier = 0.5f, negativeMultiplier = 1.5f, maxDailyGain = 1.0f, maxDailyLoss = 1.5f, streakMultiplier = 1.04f),
        ScoreBandConfig(minScore = 97, maxScore = 99, positiveMultiplier = 0.2f, negativeMultiplier = 1.8f, maxDailyGain = 0.5f, maxDailyLoss = 1.0f, streakMultiplier = 1.02f),
    )

    /**
     * Lookup the band config for a given score.
     * Engine logic uses this — never raw numbers.
     */
    private fun getBand(score: Int): ScoreBandConfig {
        return SCORE_BANDS.firstOrNull { score in it.minScore..it.maxScore }
            ?: SCORE_BANDS.last()
    }

    fun calculateFocusScore(
        questionnaireScore: Int,
        behaviourScore: Int,
        confidence: Int,
        yesterdayFocusScore: Int?,
        pastFocusScores: List<Int> // Last 3 days of final Focus Scores
    ): FocusScoreResult {

        // 1. Adaptive Blend (unchanged from V1)
        val bw = confidence.toFloat() / 100f
        val qw = 1f - bw
        val rawBlended = (questionnaireScore * qw) + (behaviourScore * bw)

        // 2. Determine previous score baseline
        val previousScore = yesterdayFocusScore
            ?: pastFocusScores.lastOrNull()
            ?: rawBlended.roundToInt()

        // 3. Lookup current band config
        val band = getBand(previousScore)

        // 4. Calculate raw delta
        val rawDelta = rawBlended - previousScore

        // 5. Apply progressive multiplier from band config
        val scaledDelta = if (rawDelta >= 0) {
            rawDelta * band.positiveMultiplier
        } else {
            rawDelta * band.negativeMultiplier
        }

        // 6. Apply band-specific stability cap (asymmetric gain/loss)
        val cappedDelta = scaledDelta.coerceIn(-band.maxDailyLoss, band.maxDailyGain)

        // 7. Streak multiplier: if last 3 days were ALL improving,
        //    multiply today's positive delta by the band's streak multiplier.
        //    This rewards consistent effort proportionally to difficulty.
        val streakAdjustedDelta = if (
            cappedDelta > 0 &&
            pastFocusScores.size >= 3 &&
            pastFocusScores.zipWithNext().all { (a, b) -> b >= a }
        ) {
            (cappedDelta * band.streakMultiplier).coerceAtMost(band.maxDailyGain)
        } else {
            cappedDelta
        }

        // 8. New raw score
        val newRaw = previousScore + streakAdjustedDelta

        // 9. Rolling average smoothing (70% today, 30% past 3 days)
        val finalScore = if (pastFocusScores.isNotEmpty()) {
            val avgPast = pastFocusScores.average()
            (newRaw * 0.7f + avgPast.toFloat() * 0.3f).roundToInt()
        } else {
            newRaw.roundToInt()
        }

        // 10. Trend
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
