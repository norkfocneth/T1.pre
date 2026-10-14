package com.example.t1.domain.token

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * TokenRewardEngine encapsulates the gamification and tokenomics algorithm for T1.
 *
 * Core Economy Anchor:
 * - T1 Signature Oversized Merchandise costs 6,000 T1 Tokens.
 * - Calibrated so that a dedicated, disciplined power user takes ~21 to 25 days to earn it.
 * - An average consistent user takes ~30 to 35 days.
 * - Prevents cheesing or rapid farming while creating an engaging, habit-forming loop.
 */
object TokenRewardEngine {

    const val MERCHANDISE_TARGET_TOKENS = 6000
    const val MAX_DAILY_FOCUS_SESSION_TOKENS = 70

    data class DailyLoginReward(
        val baseTokens: Int,
        val streakMilestoneBonus: Int,
        val milestoneLabel: String?,
        val streakDay: Int,
        val totalTokens: Int
    )

    data class FocusScoreDeltaReward(
        val delta: Int,
        val tokens: Int,
        val evaluationTier: String,
        val notificationTitle: String,
        val notificationMessage: String,
        val isBounceBackTriggered: Boolean = false,
        val isBounceBackRewarded: Boolean = false
    )

    data class MerchPaceStats(
        val currentTokens: Int,
        val targetTokens: Int = MERCHANDISE_TARGET_TOKENS,
        val percentComplete: Float,
        val tokensRemaining: Int,
        val estimatedDaysLeft: Int
    )

    /**
     * Calculates graduated Daily Login tokens and compounding streak milestones.
     * Streak 1 to 7 scales from +10 to +40 base tokens.
     * Special milestone bonuses:
     * - Day 7: +150 Tokens
     * - Day 14: +350 Tokens
     * - Day 21: +600 Tokens (Key habit formation milestone)
     * - Day 30: +1000 Tokens (Mastery streak milestone)
     */
    fun calculateDailyLoginReward(streak: Int): DailyLoginReward {
        val sanitizedStreak = max(1, streak)
        val baseTokens = when {
            sanitizedStreak <= 1 -> 10
            sanitizedStreak == 2 -> 15
            sanitizedStreak == 3 -> 20
            sanitizedStreak == 4 -> 25
            sanitizedStreak == 5 -> 30
            sanitizedStreak == 6 -> 35
            else -> 40
        }

        val (milestoneBonus, milestoneLabel) = when (sanitizedStreak) {
            7 -> 150 to "7-Day Streak Milestone! 🔥"
            14 -> 350 to "14-Day Streak Master! ⚡"
            21 -> 600 to "21-Day Habit Milestone! 🏆"
            30 -> 1000 to "30-Day Legend Streak! 💎"
            else -> 0 to null
        }

        return DailyLoginReward(
            baseTokens = baseTokens,
            streakMilestoneBonus = milestoneBonus,
            milestoneLabel = milestoneLabel,
            streakDay = sanitizedStreak,
            totalTokens = baseTokens + milestoneBonus
        )
    }

    /**
     * Evaluates Focus Score Delta (Today vs Yesterday).
     *
     * Rewards:
     * - Jump >= 6 points or >= 10% gain: +90 Tokens (God Mode)
     * - Jump 3 to 5 points: +50 Tokens (Solid Gain)
     * - Jump 1 to 2 points: +25 Tokens (Focus Gain)
     * - Maintained (delta 0 or -1):
     *     - If score >= 75: +35 Tokens (Mastery Consistency)
     *     - If score < 75: +10 Tokens (Flat Maintenance)
     * - Dropped (delta < -1): 0 tokens + triggers Revenge Quest (1.5x multiplier on recovery)
     */
    fun calculateFocusScoreDeltaReward(
        todayScore: Int,
        yesterdayScore: Int,
        hasActiveBounceBack: Boolean = false
    ): FocusScoreDeltaReward {
        val delta = todayScore - yesterdayScore
        val isPercentJump = yesterdayScore > 0 && (delta.toFloat() / yesterdayScore) >= 0.10f

        return when {
            delta >= 6 || isPercentJump -> {
                val base = 90
                val finalTokens = if (hasActiveBounceBack) (base * 1.5).toInt() else base
                FocusScoreDeltaReward(
                    delta = delta,
                    tokens = finalTokens,
                    evaluationTier = "GOD_MODE",
                    notificationTitle = "🔥 God Mode Focus Jump!",
                    notificationMessage = "+$finalTokens T1 Tokens! Focus Score jumped by +$delta points.",
                    isBounceBackRewarded = hasActiveBounceBack
                )
            }
            delta in 3..5 -> {
                val base = 50
                val finalTokens = if (hasActiveBounceBack) (base * 1.5).toInt() else base
                FocusScoreDeltaReward(
                    delta = delta,
                    tokens = finalTokens,
                    evaluationTier = "SOLID_GAIN",
                    notificationTitle = "⚡ Impressive Focus Gain!",
                    notificationMessage = "+$finalTokens T1 Tokens! Focus Score up by +$delta points.",
                    isBounceBackRewarded = hasActiveBounceBack
                )
            }
            delta in 1..2 -> {
                val base = 25
                val finalTokens = if (hasActiveBounceBack) (base * 1.5).toInt() else base
                FocusScoreDeltaReward(
                    delta = delta,
                    tokens = finalTokens,
                    evaluationTier = "FOCUS_GAIN",
                    notificationTitle = "📈 Focus Score Up!",
                    notificationMessage = "+$finalTokens T1 Tokens! +$delta points vs yesterday.",
                    isBounceBackRewarded = hasActiveBounceBack
                )
            }
            delta in -1..0 -> {
                if (todayScore >= 75) {
                    FocusScoreDeltaReward(
                        delta = delta,
                        tokens = 35,
                        evaluationTier = "MASTERY_CONSISTENCY",
                        notificationTitle = "🛡️ High Mastery Consistency",
                        notificationMessage = "+35 T1 Tokens! Maintained elite focus score ($todayScore).",
                        isBounceBackRewarded = false
                    )
                } else {
                    FocusScoreDeltaReward(
                        delta = delta,
                        tokens = 10,
                        evaluationTier = "FLAT_MAINTENANCE",
                        notificationTitle = "⚖️ Focus Steady",
                        notificationMessage = "+10 T1 Tokens. Push for a higher score tomorrow!",
                        isBounceBackRewarded = false
                    )
                }
            }
            else -> {
                FocusScoreDeltaReward(
                    delta = delta,
                    tokens = 0,
                    evaluationTier = "REVENGE_QUEST",
                    notificationTitle = "⚠️ Bounce-Back Challenge Active!",
                    notificationMessage = "Focus Score dipped by ${abs(delta)}. Reclaim it tomorrow for a 1.5x token multiplier!",
                    isBounceBackTriggered = true
                )
            }
        }
    }

    /**
     * Calculates tokens awarded for a completed focus session.
     * Scaled by session duration and capped at MAX_DAILY_FOCUS_SESSION_TOKENS (70) per day.
     */
    fun calculateFocusSessionReward(
        durationMinutes: Int,
        sessionTokensEarnedToday: Int = 0
    ): Int {
        if (sessionTokensEarnedToday >= MAX_DAILY_FOCUS_SESSION_TOKENS) return 0

        val nominalReward = when {
            durationMinutes >= 50 -> 35
            durationMinutes >= 25 -> 15
            durationMinutes >= 15 -> 10
            else -> 5
        }

        return min(nominalReward, MAX_DAILY_FOCUS_SESSION_TOKENS - sessionTokensEarnedToday)
    }

    /**
     * Screen time reduction reward when user saves 30+ or 15+ minutes vs yesterday.
     */
    fun calculateTimeSavedReward(timeSavedMinutes: Int): Int {
        return when {
            timeSavedMinutes >= 30 -> 30
            timeSavedMinutes >= 15 -> 15
            else -> 0
        }
    }

    /**
     * Variable dopamine mystery loot crate upon completing all daily quests.
     * Delivers between 30 and 60 tokens randomly.
     */
    fun rollMysteryChestReward(): Int {
        return Random.nextInt(30, 61)
    }

    /**
     * Computes real-time pace stats toward the 6,000 Token Merchandise goal.
     */
    fun calculateMerchPace(
        currentTokens: Int,
        avgDailyTokens: Int = 200
    ): MerchPaceStats {
        val tokensRemaining = max(0, MERCHANDISE_TARGET_TOKENS - currentTokens)
        val percent = (currentTokens.toFloat() / MERCHANDISE_TARGET_TOKENS).coerceIn(0f, 1f)
        val safeDailyRate = max(1, avgDailyTokens)
        val daysLeft = if (tokensRemaining == 0) 0 else (tokensRemaining + safeDailyRate - 1) / safeDailyRate

        return MerchPaceStats(
            currentTokens = currentTokens,
            targetTokens = MERCHANDISE_TARGET_TOKENS,
            percentComplete = percent,
            tokensRemaining = tokensRemaining,
            estimatedDaysLeft = daysLeft
        )
    }
}
