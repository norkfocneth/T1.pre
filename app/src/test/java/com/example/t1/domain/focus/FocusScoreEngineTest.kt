package com.example.t1.domain.focus

import org.junit.Assert.assertEquals
import org.junit.Test

class FocusScoreEngineTest {

    @Test
    fun testFocusScoreCalculationWithoutHistory() {
        // No yesterday score, no history.
        // Blended = 80 * 0.5 + 90 * 0.5 = 40 + 45 = 85. (confidence = 50 -> bw = 0.5, qw = 0.5)
        // Expect final score = 85, trend = Stable
        val result = FocusScoreEngine.calculateFocusScore(
            questionnaireScore = 80,
            behaviourScore = 90,
            confidence = 50,
            yesterdayFocusScore = null,
            pastFocusScores = emptyList()
        )
        assertEquals(85, result.finalScore)
        assertEquals("Stable", result.trend)
    }

    @Test
    fun testFocusScoreCalculationWithImprovementAndStability() {
        // Yesterday = 70.
        // Blended = 80 * 0.5 + 90 * 0.5 = 85.
        // Delta = 85 - 70 = 15.
        // Capped movement is +5 -> today's capped score = 75.
        // Past scores = [70, 72].
        // Rolling average = 75 * 0.7 + (70 + 72)/2.0 * 0.3 = 52.5 + 71 * 0.3 = 52.5 + 21.3 = 73.8 -> rounded = 74.
        // Expected = 74, trend = Improving
        val result = FocusScoreEngine.calculateFocusScore(
            questionnaireScore = 80,
            behaviourScore = 90,
            confidence = 50,
            yesterdayFocusScore = 70,
            pastFocusScores = listOf(70, 72)
        )
        assertEquals(74, result.finalScore)
        assertEquals("Improving", result.trend)
    }

    @Test
    fun testFocusScoreCalculationWithDeclineAndStability() {
        // Yesterday = 90.
        // Blended = 80 * 0.5 + 50 * 0.5 = 65.
        // Delta = 65 - 90 = -25.
        // Capped movement is -5 -> today's capped score = 85.
        // Past scores = [88, 90].
        // Rolling average = 85 * 0.7 + (88 + 90)/2.0 * 0.3 = 59.5 + 89 * 0.3 = 59.5 + 26.7 = 86.2 -> rounded = 86.
        // Expected = 86, trend = Declining
        val result = FocusScoreEngine.calculateFocusScore(
            questionnaireScore = 80,
            behaviourScore = 50,
            confidence = 50,
            yesterdayFocusScore = 90,
            pastFocusScores = listOf(88, 90)
        )
        assertEquals(86, result.finalScore)
        assertEquals("Declining", result.trend)
    }
}
