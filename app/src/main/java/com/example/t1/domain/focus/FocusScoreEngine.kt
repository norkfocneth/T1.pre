package com.example.t1.domain.focus

import kotlin.math.roundToInt

data class FocusScoreResult(
    val finalScore: Int,
    val trend: String
)

object FocusScoreEngine {

    const val ENGINE_VERSION = "ENGINE_V1"

    fun calculateWeights(confidence: Int): Pair<Float, Float> {
        val behaviourWeight = when {
            confidence <= 0 -> 0.0f
            confidence == 35 -> 0.3f
            confidence == 50 -> 0.4f
            confidence >= 100 -> 0.5f
            else -> {
                if (confidence < 35) {
                    (confidence.toFloat() / 35.0f) * 0.3f
                } else if (confidence < 50) {
                    0.3f + ((confidence - 35).toFloat() / 15.0f) * 0.1f
                } else {
                    0.4f + ((confidence - 50).toFloat() / 50.0f) * 0.1f
                }
            }
        }
        val questionnaireWeight = 1.0f - behaviourWeight
        return Pair(questionnaireWeight, behaviourWeight)
    }

    fun calculateFocusScore(
        questionnaireScore: Int,
        behaviourScore: Int,
        confidence: Int,
        yesterdayFocusScore: Int?,
        pastFocusScores: List<Int>, // Last 6 days of final Focus Scores
        pastBehaviourScores: List<Int> // Last 3 days of Behaviour Scores
    ): FocusScoreResult {
        // 1. Calculate Adaptive Blend
        val (qw, bw) = calculateWeights(confidence)
        val rawBlended = (questionnaireScore * qw) + (behaviourScore * bw)

        // 2. Apply Stability Modifier (Gradual increase/decrease)
        var smoothedScore = rawBlended
        if (yesterdayFocusScore != null) {
            val delta = rawBlended - yesterdayFocusScore.toFloat()
            smoothedScore = if (delta > 0f) {
                // Suddenly improves -> gradual climb (30% weight to improvement)
                yesterdayFocusScore + (delta * 0.3f)
            } else {
                // Suddenly drops -> gradual decline (50% weight to drop)
                yesterdayFocusScore + (delta * 0.5f)
            }
        }

        // 3. Maintain 7-Day Rolling Average
        val targetScoreInt = smoothedScore.roundToInt().coerceIn(0, 100)
        val rollingAverageScores = (pastFocusScores + targetScoreInt).takeLast(7)
        val finalFocusScore = if (rollingAverageScores.isNotEmpty()) {
            rollingAverageScores.average().roundToInt().coerceIn(0, 100)
        } else {
            targetScoreInt
        }

        // 4. Generate Daily Trend (based purely on behaviour history comparison)
        val trend = if (pastBehaviourScores.isNotEmpty()) {
            val avgPastBehaviour = pastBehaviourScores.average()
            val diff = behaviourScore - avgPastBehaviour
            when {
                diff > 5.0 -> "Improving"
                diff < -5.0 -> "Declining"
                else -> "Stable"
            }
        } else {
            "Stable"
        }

        return FocusScoreResult(
            finalScore = finalFocusScore,
            trend = trend
        )
    }
}
