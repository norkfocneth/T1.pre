package com.example.t1.domain.token

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenRewardEngineTest {

    @Test
    fun `test daily login rewards and milestone bonuses`() {
        val day1 = TokenRewardEngine.calculateDailyLoginReward(1)
        assertEquals(10, day1.baseTokens)
        assertEquals(0, day1.streakMilestoneBonus)
        assertNull(day1.milestoneLabel)
        assertEquals(10, day1.totalTokens)

        val day3 = TokenRewardEngine.calculateDailyLoginReward(3)
        assertEquals(20, day3.baseTokens)
        assertEquals(0, day3.streakMilestoneBonus)
        assertEquals(20, day3.totalTokens)

        val day7 = TokenRewardEngine.calculateDailyLoginReward(7)
        assertEquals(40, day7.baseTokens)
        assertEquals(150, day7.streakMilestoneBonus)
        assertNotNull(day7.milestoneLabel)
        assertEquals(190, day7.totalTokens)

        val day14 = TokenRewardEngine.calculateDailyLoginReward(14)
        assertEquals(40, day14.baseTokens)
        assertEquals(350, day14.streakMilestoneBonus)
        assertEquals(390, day14.totalTokens)

        val day21 = TokenRewardEngine.calculateDailyLoginReward(21)
        assertEquals(40, day21.baseTokens)
        assertEquals(600, day21.streakMilestoneBonus)
        assertEquals(640, day21.totalTokens)

        val day30 = TokenRewardEngine.calculateDailyLoginReward(30)
        assertEquals(40, day30.baseTokens)
        assertEquals(1000, day30.streakMilestoneBonus)
        assertEquals(1040, day30.totalTokens)
    }

    @Test
    fun `test focus score delta evaluation tiers`() {
        // God Mode / Jump >= 6 points
        val godMode = TokenRewardEngine.calculateFocusScoreDeltaReward(todayScore = 80, yesterdayScore = 72)
        assertEquals(90, godMode.tokens)
        assertEquals("GOD_MODE", godMode.evaluationTier)

        // Solid Gain (3 to 5 points)
        val solidGain = TokenRewardEngine.calculateFocusScoreDeltaReward(todayScore = 75, yesterdayScore = 71)
        assertEquals(50, solidGain.tokens)
        assertEquals("SOLID_GAIN", solidGain.evaluationTier)

        // Focus Gain (1 to 2 points)
        val focusGain = TokenRewardEngine.calculateFocusScoreDeltaReward(todayScore = 70, yesterdayScore = 69)
        assertEquals(25, focusGain.tokens)
        assertEquals("FOCUS_GAIN", focusGain.evaluationTier)

        // High Mastery Consistency (score >= 75 maintained)
        val mastery = TokenRewardEngine.calculateFocusScoreDeltaReward(todayScore = 85, yesterdayScore = 85)
        assertEquals(35, mastery.tokens)
        assertEquals("MASTERY_CONSISTENCY", mastery.evaluationTier)

        // Flat Maintenance (score < 75 maintained)
        val flat = TokenRewardEngine.calculateFocusScoreDeltaReward(todayScore = 60, yesterdayScore = 60)
        assertEquals(10, flat.tokens)
        assertEquals("FLAT_MAINTENANCE", flat.evaluationTier)

        // Score Dropped -> Revenge Quest
        val dropped = TokenRewardEngine.calculateFocusScoreDeltaReward(todayScore = 65, yesterdayScore = 70)
        assertEquals(0, dropped.tokens)
        assertEquals("REVENGE_QUEST", dropped.evaluationTier)
        assertTrue(dropped.isBounceBackTriggered)

        // Bounce-Back multiplier active on solid gain
        val bounceBackReward = TokenRewardEngine.calculateFocusScoreDeltaReward(
            todayScore = 75,
            yesterdayScore = 71,
            hasActiveBounceBack = true
        )
        assertEquals(75, bounceBackReward.tokens) // 50 * 1.5 = 75
        assertTrue(bounceBackReward.isBounceBackRewarded)
    }

    @Test
    fun `test focus session duration scaling and daily cap`() {
        // 25m Pomodoro
        val pomodoro = TokenRewardEngine.calculateFocusSessionReward(durationMinutes = 25, sessionTokensEarnedToday = 0)
        assertEquals(15, pomodoro)

        // 50m Deep Work
        val deepWork = TokenRewardEngine.calculateFocusSessionReward(durationMinutes = 50, sessionTokensEarnedToday = 0)
        assertEquals(35, deepWork)

        // Cap at 70 tokens max per day
        val capped = TokenRewardEngine.calculateFocusSessionReward(durationMinutes = 50, sessionTokensEarnedToday = 50)
        assertEquals(20, capped) // 70 - 50 = 20

        val maxedOut = TokenRewardEngine.calculateFocusSessionReward(durationMinutes = 50, sessionTokensEarnedToday = 70)
        assertEquals(0, maxedOut)
    }

    @Test
    fun `test merchandise 6000 token pace calculator`() {
        val startPace = TokenRewardEngine.calculateMerchPace(currentTokens = 0, avgDailyTokens = 200)
        assertEquals(6000, startPace.targetTokens)
        assertEquals(0f, startPace.percentComplete, 0.001f)
        assertEquals(6000, startPace.tokensRemaining)
        assertEquals(30, startPace.estimatedDaysLeft)

        val midPace = TokenRewardEngine.calculateMerchPace(currentTokens = 3000, avgDailyTokens = 200)
        assertEquals(0.5f, midPace.percentComplete, 0.001f)
        assertEquals(3000, midPace.tokensRemaining)
        assertEquals(15, midPace.estimatedDaysLeft)

        val completedPace = TokenRewardEngine.calculateMerchPace(currentTokens = 6000, avgDailyTokens = 200)
        assertEquals(1.0f, completedPace.percentComplete, 0.001f)
        assertEquals(0, completedPace.tokensRemaining)
        assertEquals(0, completedPace.estimatedDaysLeft)
    }
}
