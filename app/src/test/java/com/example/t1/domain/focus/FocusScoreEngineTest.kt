package com.example.t1.domain.focus

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusScoreEngineTest {

    @Test
    fun testFocusScoreCalculationWithoutHistory() {
        // Growth goal: Productivity.
        // Today = 2 hours, Baseline = 2 hours.
        // Delta = 0 min.
        // Base score = 50 -> Final score = 50, trend = Stable
        val result = FocusScoreEngine.calculateFocusScore(
            goalCategory = "PRODUCTIVITY",
            todayCategoryTimeMs = 2 * 3600000L,
            baselineCategoryTimeMs = 2 * 3600000L,
            completedFocusSessionsCount = 0,
            yesterdayFocusScore = null,
            pastFocusScores = emptyList()
        )
        assertEquals(50, result.finalScore)
        assertEquals("Stable", result.trend)
    }

    @Test
    fun testFocusScoreCalculationWithImprovementAndStability() {
        // Growth goal: Productivity.
        // Yesterday = 70.
        // Today = 3 hours (180 min), Baseline = 1 hour (60 min). Delta = +120 min.
        // Higher usage than baseline improves score.
        val result = FocusScoreEngine.calculateFocusScore(
            goalCategory = "PRODUCTIVITY",
            todayCategoryTimeMs = 3 * 3600000L,
            baselineCategoryTimeMs = 1 * 3600000L,
            completedFocusSessionsCount = 1,
            yesterdayFocusScore = 70,
            pastFocusScores = listOf(68, 69, 70)
        )
        assertTrue(result.finalScore > 70)
        assertEquals("Improving", result.trend)
    }

    @Test
    fun testFocusScoreCalculationWithDecline() {
        // Reduction goal: Social.
        // Yesterday = 80.
        // Today = 4 hours (240 min), Baseline = 1 hour (60 min). Delta = +180 min (much more social than baseline).
        // For reduction goal, higher usage reduces score.
        val result = FocusScoreEngine.calculateFocusScore(
            goalCategory = "SOCIAL",
            todayCategoryTimeMs = 4 * 3600000L,
            baselineCategoryTimeMs = 1 * 3600000L,
            completedFocusSessionsCount = 0,
            yesterdayFocusScore = 80,
            pastFocusScores = listOf(82, 81, 80)
        )
        assertTrue(result.finalScore < 80)
        assertEquals("Declining", result.trend)
    }
}
