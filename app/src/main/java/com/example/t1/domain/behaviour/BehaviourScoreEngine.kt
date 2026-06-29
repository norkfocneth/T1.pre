package com.example.t1.domain.behaviour

import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import com.example.t1.domain.repository.AppCategoryRepository
import com.example.t1.domain.repository.EngineCategory
import kotlin.math.roundToInt

sealed interface BehaviourEngineResult {
    data class Success(val score: Int, val confidence: Int) : BehaviourEngineResult
    data class Error(val reason: String) : BehaviourEngineResult
}

object BehaviourScoreEngine {

    const val ENGINE_VERSION = "ENGINE_V2"

    fun calculateConfidence(verifiedDaysCount: Int): Int {
        return when {
            verifiedDaysCount <= 1 -> 0
            verifiedDaysCount == 2 -> 30
            verifiedDaysCount == 3 -> 50
            verifiedDaysCount == 4 -> 65
            verifiedDaysCount == 5 -> 80
            verifiedDaysCount == 6 -> 90
            else -> 100
        }
    }

    fun calculateBehaviourScore(
        today: DailyBehaviourSummary,
        completedFocusSessionsCount: Int,
        verifiedDaysCount: Int,
        appCategoryRepository: AppCategoryRepository
    ): BehaviourEngineResult {
        // 1. Anti-Cheat & Validation checks
        if (today.totalScreenTimeMs < 0 || today.unlockCount < 0 || today.appOpenCount < 0) {
            return BehaviourEngineResult.Error("Negative duration or counts detected.")
        }
        if (today.totalScreenTimeMs > 86400000L) {
            return BehaviourEngineResult.Error("Screen time exceeds 24 hours.")
        }
        if (today.collectionTimestamp > System.currentTimeMillis() + 600000L) { // Allow 10 min clock skew
            return BehaviourEngineResult.Error("Future timestamp detected (clock tampering).")
        }
        if (today.unlockCount > 1000 || today.appOpenCount > 3000) {
            return BehaviourEngineResult.Error("Impossible unlock count or app opens detected.")
        }

        // 2. Classify usage into 5 categories using AppCategoryRepository
        var productivityTime = 0L
        var educationTime = 0L
        var entertainmentTime = 0L
        var socialTime = 0L
        var utilityTime = 0L

        // Iterate through all tracked app summaries to group them
        for (app in today.topUsedApps) {
            val engineCat = appCategoryRepository.getCategory(app.packageName)
            val duration = app.usageDurationMs
            when (engineCat) {
                EngineCategory.PRODUCTIVITY -> productivityTime += duration
                EngineCategory.EDUCATION -> educationTime += duration
                EngineCategory.ENTERTAINMENT -> entertainmentTime += duration
                EngineCategory.SOCIAL -> socialTime += duration
                EngineCategory.UTILITY -> utilityTime += duration
            }
        }

        // 3. Compute Non-Utility Screen Time
        val nonUtilityTime = productivityTime + educationTime + entertainmentTime + socialTime

        // 4. Calculate Ratios & Weighted Index
        var weightedIndex = 0f
        var productivityRatio = 0f
        var educationRatio = 0f
        if (nonUtilityTime > 0) {
            productivityRatio = productivityTime.toFloat() / nonUtilityTime.toFloat()
            educationRatio = educationTime.toFloat() / nonUtilityTime.toFloat()
            val entertainmentRatio = entertainmentTime.toFloat() / nonUtilityTime.toFloat()
            val socialRatio = socialTime.toFloat() / nonUtilityTime.toFloat()

            weightedIndex = (productivityRatio * 0.40f) + (educationRatio * 0.20f) - (entertainmentRatio * 0.15f) - (socialRatio * 0.25f)
        }

        // 5. Initial Base Score (Base = 50)
        val initialScore = 50f + 125f * weightedIndex

        // 6. Modifiers
        // a. Screen Time Penalty
        val totalScreenTimeHours = today.totalScreenTimeMs.toFloat() / 3600000f
        var screenTimePenalty = when {
            totalScreenTimeHours <= 4f -> 0f
            totalScreenTimeHours <= 6f -> 2f
            totalScreenTimeHours <= 8f -> 5f
            totalScreenTimeHours <= 10f -> 10f
            totalScreenTimeHours <= 12f -> 15f
            else -> 20f
        }
        // Reduction: 50% penalty if productivity + education >= 70% of non-utility time
        if (productivityRatio + educationRatio >= 0.70f) {
            screenTimePenalty *= 0.5f
        }

        // b. Unlock Penalty
        val unlockPenalty = when {
            today.unlockCount <= 30 -> 0f
            today.unlockCount <= 60 -> 3f
            today.unlockCount <= 100 -> 7f
            else -> 12f
        }

        // c. App Opens Penalty
        val appOpensPenalty = when {
            today.appOpenCount <= 80 -> 0f
            today.appOpenCount <= 150 -> 3f
            today.appOpenCount <= 250 -> 6f
            else -> 10f
        }

        // d. Focus Session Bonus (+1 per session, max +10)
        val focusSessionBonus = minOf(completedFocusSessionsCount, 10).toFloat()

        // 7. Final Behaviour Score Calculation
        val finalScore = (initialScore - screenTimePenalty - unlockPenalty - appOpensPenalty + focusSessionBonus).roundToInt().coerceIn(0, 100)

        // 8. Confidence
        val confidence = calculateConfidence(verifiedDaysCount)

        return BehaviourEngineResult.Success(
            score = finalScore,
            confidence = confidence
        )
    }
}
