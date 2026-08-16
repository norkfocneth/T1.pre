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

    @Test
    fun testFocusScoreWithCustomDefaultBaseScore() {
        val result = FocusScoreEngine.calculateFocusScore(
            goalCategory = "PRODUCTIVITY",
            todayCategoryTimeMs = 2 * 3600000L,
            baselineCategoryTimeMs = 2 * 3600000L,
            completedFocusSessionsCount = 0,
            yesterdayFocusScore = null,
            pastFocusScores = emptyList(),
            defaultBaseScore = 65
        )
        assertEquals(65, result.finalScore)
        assertEquals("Stable", result.trend)
    }

    @Test
    fun testInitialScoreFrom7Days() {
        val emptySummaries = emptyList<com.example.t1.domain.model.behaviour.DailyBehaviourSummary>()
        assertEquals(50, FocusScoreEngine.calculateInitialScoreFrom7Days(emptySummaries))

        val productiveDays = (1..7).map {
            com.example.t1.domain.model.behaviour.DailyBehaviourSummary(
                date = java.time.LocalDate.now().minusDays(it.toLong()),
                totalScreenTimeMs = 4 * 3600000L,
                socialTimeMs = 5 * 60000L,
                entertainmentTimeMs = 10 * 60000L,
                productiveTimeMs = 3 * 3600000L,
                educationTimeMs = 30 * 60000L,
                communicationTimeMs = 15 * 60000L,
                financeTimeMs = 0L,
                healthTimeMs = 0L,
                developmentTimeMs = 0L,
                otherTimeMs = 0L,
                unlockCount = 20,
                appOpenCount = 40,
                foregroundSessionCount = 30,
                topUsedApps = emptyList(),
                collectionTimestamp = System.currentTimeMillis()
            )
        }
        val highProdScore = FocusScoreEngine.calculateInitialScoreFrom7Days(productiveDays, "PRODUCTIVITY")
        assertTrue(highProdScore > 50)
    }
}
