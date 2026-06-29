package com.example.t1.domain.focus

import kotlin.math.roundToInt

data class FocusScoreResult(
    val finalScore: Int,
    val trend: String
)

object FocusScoreEngine {

    const val ENGINE_VERSION = "ENGINE_V2"

    fun calculateFocusScore(
        questionnaireScore: Int,
        behaviourScore: Int,
        confidence: Int,
        yesterdayFocusScore: Int?,
        pastFocusScores: List<Int> // Last 3 days of final Focus Scores
    ): FocusScoreResult {
        // 1. Calculate Adaptive Blend (Step 4)
        val bw = confidence.toFloat() / 100.0f
        val qw = 1.0f - bw
        val rawBlended = (questionnaireScore * qw) + (behaviourScore * bw)

        // 2. Apply Stability Layer (Step 5 - Daily movement cap +5 or -5)
        val stabilityCapped = if (yesterdayFocusScore != null) {
            val maxChange = 5f
            val diff = rawBlended - yesterdayFocusScore.toFloat()
            val cappedDiff = diff.coerceIn(-maxChange, maxChange)
            yesterdayFocusScore + cappedDiff
        } else {
            rawBlended
        }

        // 3. Rolling Average (Step 6 - Today's Score * 70% + Last 3 Days Average * 30%)
        val finalFocusScore = if (pastFocusScores.isNotEmpty()) {
            val avgPast = pastFocusScores.average()
            val todayComponent = stabilityCapped * 0.7f
            val pastComponent = avgPast * 0.3f
            (todayComponent + pastComponent).roundToInt()
        } else {
            stabilityCapped.roundToInt()
        }

        // 4. Generate Daily Trend (based on comparison with yesterday's focus score)
        val trend = if (yesterdayFocusScore != null) {
            val diff = finalFocusScore - yesterdayFocusScore
            when {
                diff > 0 -> "Improving"
                diff < 0 -> "Declining"
                else -> "Stable"
            }
        } else {
            "Stable"
        }

        return FocusScoreResult(
            finalScore = finalFocusScore.coerceIn(0, 100),
            trend = trend
        )
    }
}
