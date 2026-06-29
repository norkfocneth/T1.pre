package com.example.t1.domain.behaviour

import com.example.t1.domain.model.behaviour.AppUsageSummary
import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import com.example.t1.domain.repository.AppCategoryRepository
import com.example.t1.domain.repository.EngineCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BehaviourScoreEngineTest {

    private val mockCategoryRepository = object : AppCategoryRepository {
        override fun getCategory(packageName: String): EngineCategory {
            return when (packageName) {
                "com.chatgpt" -> EngineCategory.PRODUCTIVITY
                "com.udemy" -> EngineCategory.EDUCATION
                "com.youtube" -> EngineCategory.ENTERTAINMENT
                "com.instagram" -> EngineCategory.SOCIAL
                else -> EngineCategory.UTILITY
            }
        }
    }

    private val baseSummary = DailyBehaviourSummary(
        date = LocalDate.of(2026, 6, 27),
        totalScreenTimeMs = 3600000L * 4, // 4 hours
        socialTimeMs = 3600000L * 1,
        entertainmentTimeMs = 3600000L * 1,
        productiveTimeMs = 3600000L * 1,
        educationTimeMs = 3600000L * 1,
        communicationTimeMs = 0L,
        financeTimeMs = 0L,
        healthTimeMs = 0L,
        developmentTimeMs = 0L,
        otherTimeMs = 0L,
        unlockCount = 15,
        appOpenCount = 40,
        foregroundSessionCount = 10,
        topUsedApps = listOf(
            AppUsageSummary("com.chatgpt", "ChatGPT", 3600000L, com.example.t1.domain.model.behaviour.AppCategory.PRODUCTIVITY),
            AppUsageSummary("com.udemy", "Udemy", 3600000L, com.example.t1.domain.model.behaviour.AppCategory.EDUCATION),
            AppUsageSummary("com.youtube", "YouTube", 3600000L, com.example.t1.domain.model.behaviour.AppCategory.ENTERTAINMENT),
            AppUsageSummary("com.instagram", "Instagram", 3600000L, com.example.t1.domain.model.behaviour.AppCategory.SOCIAL)
        ),
        collectionTimestamp = System.currentTimeMillis()
    )

    @Test
    fun testConfidenceSteps() {
        assertEquals(0, BehaviourScoreEngine.calculateConfidence(1))
        assertEquals(30, BehaviourScoreEngine.calculateConfidence(2))
        assertEquals(50, BehaviourScoreEngine.calculateConfidence(3))
        assertEquals(65, BehaviourScoreEngine.calculateConfidence(4))
        assertEquals(80, BehaviourScoreEngine.calculateConfidence(5))
        assertEquals(90, BehaviourScoreEngine.calculateConfidence(6))
        assertEquals(100, BehaviourScoreEngine.calculateConfidence(7))
        assertEquals(100, BehaviourScoreEngine.calculateConfidence(10))
    }

    @Test
    fun testValidationNegativeDuration() {
        val todayCorrupt = baseSummary.copy(totalScreenTimeMs = -100L)
        val result = BehaviourScoreEngine.calculateBehaviourScore(
            today = todayCorrupt,
            completedFocusSessionsCount = 0,
            verifiedDaysCount = 3,
            appCategoryRepository = mockCategoryRepository
        )
        assertTrue(result is BehaviourEngineResult.Error)
        assertEquals("Negative duration or counts detected.", (result as BehaviourEngineResult.Error).reason)
    }

    @Test
    fun testScoringCalculationCorrectness() {
        // Non-utility screen time:
        // com.chatgpt (Productivity): 1h (3600000 Ms) -> Ratio = 0.25
        // com.udemy (Education): 1h (3600000 Ms) -> Ratio = 0.25
        // com.youtube (Entertainment): 1h (3600000 Ms) -> Ratio = 0.25
        // com.instagram (Social): 1h (3600000 Ms) -> Ratio = 0.25
        // Total screen time = 4 hours.
        // WeightedIndex = (0.25 * 0.40) + (0.25 * 0.20) - (0.25 * 0.15) - (0.25 * 0.25)
        // WeightedIndex = 0.10 + 0.05 - 0.0375 - 0.0625 = 0.05
        // Base = 50. InitialScore = 50 + 125 * 0.05 = 56.25
        // Penalties:
        // Total Screen Time = 4.0h -> 0 penalty.
        // Unlocks = 15 -> 0 penalty.
        // Opens = 40 -> 0 penalty.
        // Focus Sessions = 2 -> +2 bonus.
        // Expected score = 56.25 + 2 = 58.25 -> rounded = 58.
        val result = BehaviourScoreEngine.calculateBehaviourScore(
            today = baseSummary,
            completedFocusSessionsCount = 2,
            verifiedDaysCount = 3,
            appCategoryRepository = mockCategoryRepository
        )

        assertTrue(result is BehaviourEngineResult.Success)
        val success = result as BehaviourEngineResult.Success
        assertEquals(58, success.score)
        assertEquals(50, success.confidence)
    }
}
