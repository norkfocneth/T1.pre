package com.example.t1.domain.behaviour

import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BehaviourScoreEngineTest {

    private val baseSummary = DailyBehaviourSummary(
        date = LocalDate.of(2026, 6, 27),
        totalScreenTimeMs = 3600000L * 3, // 3 hours
        socialTimeMs = 3600000L * 1, // 1 hour
        entertainmentTimeMs = 3600000L * 1, // 1 hour
        productiveTimeMs = 3600000L * 1, // 1 hour
        educationTimeMs = 0L,
        communicationTimeMs = 0L,
        financeTimeMs = 0L,
        healthTimeMs = 0L,
        developmentTimeMs = 0L,
        otherTimeMs = 0L,
        unlockCount = 15,
        appOpenCount = 40,
        foregroundSessionCount = 10,
        topUsedApps = emptyList(),
        collectionTimestamp = System.currentTimeMillis()
    )

    @Test
    fun testConfidenceSteps() {
        assertEquals(0, BehaviourScoreEngine.calculateConfidence(1))
        assertEquals(35, BehaviourScoreEngine.calculateConfidence(2))
        assertEquals(50, BehaviourScoreEngine.calculateConfidence(3))
        assertEquals(60, BehaviourScoreEngine.calculateConfidence(4))
        assertEquals(70, BehaviourScoreEngine.calculateConfidence(5))
        assertEquals(70, BehaviourScoreEngine.calculateConfidence(6))
        assertEquals(100, BehaviourScoreEngine.calculateConfidence(7))
        assertEquals(100, BehaviourScoreEngine.calculateConfidence(10))
    }

    @Test
    fun testValidationLess2Days() {
        val result = BehaviourScoreEngine.calculateBehaviourScore(
            today = baseSummary,
            yesterday = baseSummary,
            verifiedDaysCount = 1
        )
        assertTrue(result is BehaviourEngineResult.Error)
        assertEquals("Less than 2 days of verified behaviour history.", (result as BehaviourEngineResult.Error).reason)
    }

    @Test
    fun testValidationNegativeDuration() {
        val todayCorrupt = baseSummary.copy(totalScreenTimeMs = -100L)
        val result = BehaviourScoreEngine.calculateBehaviourScore(
            today = todayCorrupt,
            yesterday = baseSummary.copy(date = LocalDate.of(2026, 6, 26)),
            verifiedDaysCount = 3
        )
        assertTrue(result is BehaviourEngineResult.Error)
        assertEquals("Negative duration detected.", (result as BehaviourEngineResult.Error).reason)
    }

    @Test
    fun testScoringCalculationCorrectness() {
        // Today Screen Time = 2h. Productive = 1.5h, Social = 0.5h. Unlocks = 20, Opens = 50. Sessions = 5.
        // Base = 80
        // Productive ratio = 1.5 / 2.0 = 0.75 -> + 20 * 0.75 = +15 -> 95
        // Social ratio = 0.5 / 2.0 = 0.25 -> -35 * 0.25 = -8.75 -> 86.25
        // Unlocks <= 30 -> no deduction
        // Opens <= 80 -> no deduction
        // Avg Session = 2h / 5 = 24 mins <= 30 mins -> no deduction
        // Screen time <= 4h -> no deduction
        // Expected = 86
        val today = baseSummary.copy(
            date = LocalDate.of(2026, 6, 27),
            totalScreenTimeMs = 3600000L * 2,
            productiveTimeMs = (3600000L * 1.5).toLong(),
            socialTimeMs = (3600000L * 0.5).toLong(),
            entertainmentTimeMs = 0L,
            unlockCount = 20,
            appOpenCount = 50,
            foregroundSessionCount = 5,
            collectionTimestamp = System.currentTimeMillis()
        )

        val yesterday = baseSummary.copy(
            date = LocalDate.of(2026, 6, 26),
            totalScreenTimeMs = 3600000L * 3
        )

        val result = BehaviourScoreEngine.calculateBehaviourScore(
            today = today,
            yesterday = yesterday,
            verifiedDaysCount = 3
        )

        assertTrue(result is BehaviourEngineResult.Success)
        val success = result as BehaviourEngineResult.Success
        assertEquals(86, success.score)
        assertEquals(50, success.confidence)
    }

    @Test
    fun testDeductionsExceededLimits() {
        // Screen Time = 6h (2h exceeded) -> -10 points
        // Unlocks = 40 (10 exceeded) -> -5 points
        // App Opens = 100 (20 exceeded) -> -4 points
        // Avg Session = 6h / 10 = 36 mins (>30 mins) -> -10 points
        // Start Base = 80. Ratios: Prod = 0, Social = 1.0 -> -35 points
        // Expected = 80 - 35 - 10 - 5 - 4 - 10 = 16
        val today = baseSummary.copy(
            date = LocalDate.of(2026, 6, 27),
            totalScreenTimeMs = 3600000L * 6,
            productiveTimeMs = 0L,
            socialTimeMs = 3600000L * 6,
            entertainmentTimeMs = 0L,
            unlockCount = 40,
            appOpenCount = 100,
            foregroundSessionCount = 10,
            collectionTimestamp = System.currentTimeMillis()
        )

        val yesterday = baseSummary.copy(
            date = LocalDate.of(2026, 6, 26),
            totalScreenTimeMs = 3600000L * 3
        )

        val result = BehaviourScoreEngine.calculateBehaviourScore(
            today = today,
            yesterday = yesterday,
            verifiedDaysCount = 7
        )

        assertTrue(result is BehaviourEngineResult.Success)
        val success = result as BehaviourEngineResult.Success
        assertEquals(16, success.score)
        assertEquals(100, success.confidence)
    }
}
