package com.example.t1.domain.behaviour

import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import java.lang.Math.abs
import kotlin.math.max

sealed interface BehaviourEngineResult {
    data class Success(val score: Int, val confidence: Int, val stability: Float) : BehaviourEngineResult
    data class Error(val reason: String) : BehaviourEngineResult
}

object BehaviourScoreEngine {

    const val ENGINE_VERSION = "ENGINE_V1"

    fun calculateConfidence(verifiedDaysCount: Int): Int {
        return when {
            verifiedDaysCount <= 1 -> 0
            verifiedDaysCount == 2 -> 35
            verifiedDaysCount == 3 -> 50
            verifiedDaysCount == 4 -> 60
            verifiedDaysCount in 5..6 -> 70
            else -> 100
        }
    }

    fun calculateBehaviourScore(
        today: DailyBehaviourSummary,
        yesterday: DailyBehaviourSummary?,
        verifiedDaysCount: Int
    ): BehaviourEngineResult {
        // 1. Data Validation
        if (verifiedDaysCount < 2) {
            return BehaviourEngineResult.Error("Less than 2 days of verified behaviour history.")
        }
        if (yesterday == null) {
            return BehaviourEngineResult.Error("Yesterday's behaviour summary is missing.")
        }
        if (today.date == yesterday.date) {
            return BehaviourEngineResult.Error("Duplicate summaries detected for date: ${today.date}")
        }
        if (today.totalScreenTimeMs < 0 || today.socialTimeMs < 0 || today.entertainmentTimeMs < 0 ||
            today.productiveTimeMs < 0 || today.educationTimeMs < 0 || today.communicationTimeMs < 0 ||
            today.financeTimeMs < 0 || today.healthTimeMs < 0 || today.developmentTimeMs < 0 ||
            today.otherTimeMs < 0
        ) {
            return BehaviourEngineResult.Error("Negative duration detected.")
        }
        if (today.totalScreenTimeMs > 86400000L || yesterday.totalScreenTimeMs > 86400000L) {
            return BehaviourEngineResult.Error("Corrupted behaviour summary: screen time exceeds 24 hours.")
        }
        if (today.collectionTimestamp > System.currentTimeMillis() + 600000L) { // Allow 10 min clock skew
            return BehaviourEngineResult.Error("Future timestamp detected.")
        }

        // 2. Calculate Confidence
        val confidence = calculateConfidence(verifiedDaysCount)

        // 3. Calculate Stability (compares screen time consistency)
        val diff = abs(today.totalScreenTimeMs - yesterday.totalScreenTimeMs).toFloat()
        val maxVal = max(today.totalScreenTimeMs, yesterday.totalScreenTimeMs).toFloat()
        val stability = if (maxVal > 0f) 1.0f - (diff / maxVal) else 1.0f

        // 4. Scoring Logic (starts at base 80)
        var scoreFloat = 80.0f
        val totalMs = today.totalScreenTimeMs.toFloat()

        if (totalMs > 0f) {
            val socialRatio = today.socialTimeMs.toFloat() / totalMs
            val entertainmentRatio = today.entertainmentTimeMs.toFloat() / totalMs
            val productiveRatio = today.productiveTimeMs.toFloat() / totalMs
            val educationRatio = today.educationTimeMs.toFloat() / totalMs
            val communicationRatio = today.communicationTimeMs.toFloat() / totalMs
            val devRatio = today.developmentTimeMs.toFloat() / totalMs

            // Bonuses
            scoreFloat += (20.0f * productiveRatio)
            scoreFloat += (15.0f * devRatio)
            scoreFloat += (10.0f * educationRatio)

            // Deductions
            scoreFloat -= (35.0f * socialRatio)
            scoreFloat -= (25.0f * entertainmentRatio)
        }

        // Screen time penalty (exceeding 4 hours / 14400000 ms)
        val fourHoursMs = 14400000L
        if (today.totalScreenTimeMs > fourHoursMs) {
            val exceededMs = today.totalScreenTimeMs - fourHoursMs
            val exceededHours = exceededMs.toFloat() / 3600000f
            scoreFloat -= (5.0f * exceededHours)
        }

        // Unlocks penalty (> 30)
        if (today.unlockCount > 30) {
            val extraUnlocks = today.unlockCount - 30
            val penalty = extraUnlocks * 0.5f
            scoreFloat -= minOf(penalty, 15.0f)
        }

        // App opens penalty (> 80)
        if (today.appOpenCount > 80) {
            val extraOpens = today.appOpenCount - 80
            val penalty = extraOpens * 0.2f
            scoreFloat -= minOf(penalty, 15.0f)
        }

        // Average Session Length check (> 30 mins / 1800000 ms)
        if (today.foregroundSessionCount > 0) {
            val avgSessionMs = today.totalScreenTimeMs / today.foregroundSessionCount
            if (avgSessionMs > 1800000L) {
                scoreFloat -= 10.0f
            }
        }

        val finalScore = scoreFloat.toInt().coerceIn(0, 100)

        return BehaviourEngineResult.Success(
            score = finalScore,
            confidence = confidence,
            stability = stability
        )
    }
}
