package com.example.t1.domain.focus

import org.junit.Assert.assertEquals
import org.junit.Test

class FocusScoreEngineTest {

    @Test
    fun testWeightCalculations() {
        // confidence = 0 -> questionnaireWeight = 1.0f, behaviourWeight = 0.0f
        val (qw0, bw0) = FocusScoreEngine.calculateWeights(0)
        assertEquals(1.0f, qw0, 1e-5f)
        assertEquals(0.0f, bw0, 1e-5f)

        // confidence = 35 -> qw = 0.7f, bw = 0.3f
        val (qw35, bw35) = FocusScoreEngine.calculateWeights(35)
        assertEquals(0.7f, qw35, 1e-5f)
        assertEquals(0.3f, bw35, 1e-5f)

        // confidence = 50 -> qw = 0.6f, bw = 0.4f
        val (qw50, bw50) = FocusScoreEngine.calculateWeights(50)
        assertEquals(0.6f, qw50, 1e-5f)
        assertEquals(0.4f, bw50, 1e-5f)

        // confidence >= 100 -> qw = 0.5f, bw = 0.5f
        val (qw100, bw100) = FocusScoreEngine.calculateWeights(100)
        assertEquals(0.5f, qw100, 1e-5f)
        assertEquals(0.5f, bw100, 1e-5f)

        // Interpolation checks
        // confidence = 17.5 (half of 35) -> bw should be 0.15f
        val (_, bw17) = FocusScoreEngine.calculateWeights(17)
        assertEquals((17.0f / 35.0f) * 0.3f, bw17, 1e-5f)

        // confidence = 42.5 -> bw should be 0.35f
        val (_, bw42) = FocusScoreEngine.calculateWeights(42)
        assertEquals(0.3f + ((42 - 35).toFloat() / 15.0f) * 0.1f, bw42, 1e-5f)
    }

    @Test
    fun testFocusScoreCalculationWithoutHistory() {
        // No yesterday score, no history.
        // Blended = 80 * 0.6 + 90 * 0.4 = 48 + 36 = 84.
        // Expect final score = 84, trend = Stable
        val result = FocusScoreEngine.calculateFocusScore(
            questionnaireScore = 80,
            behaviourScore = 90,
            confidence = 50,
            yesterdayFocusScore = null,
            pastFocusScores = emptyList(),
            pastBehaviourScores = emptyList()
        )
        assertEquals(84, result.finalScore)
        assertEquals("Stable", result.trend)
    }

    @Test
    fun testFocusScoreCalculationWithImprovementAndStability() {
        // Yesterday = 70.
        // Blended = 80 * 0.6 + 90 * 0.4 = 84.
        // Delta = 84 - 70 = 14 (positive improvement).
        // Smoothed = 70 + (14 * 0.3) = 74.2 -> rounded = 74.
        // Past scores = [70, 72].
        // Rolling average = average of [70, 72, 74] = 72.
        // Past behaviour = [80, 82]. Avg past = 81. Today behaviour = 90.
        // Diff = 90 - 81 = 9 (> 5) -> "Improving"
        val result = FocusScoreEngine.calculateFocusScore(
            questionnaireScore = 80,
            behaviourScore = 90,
            confidence = 50,
            yesterdayFocusScore = 70,
            pastFocusScores = listOf(70, 72),
            pastBehaviourScores = listOf(80, 82)
        )
        assertEquals(72, result.finalScore)
        assertEquals("Improving", result.trend)
    }

    @Test
    fun testFocusScoreCalculationWithDeclineAndStability() {
        // Yesterday = 90.
        // Blended = 80 * 0.6 + 50 * 0.4 = 68.
        // Delta = 68 - 90 = -22 (negative drop).
        // Smoothed = 90 + (-22 * 0.5) = 79.
        // Past scores = [88, 90].
        // Rolling average = average of [88, 90, 79] = 257 / 3 = 86. Let's trace average precisely: (88+90+79)/3 = 85.66 -> 86.
        // Past behaviour = [90, 92]. Avg past = 91. Today behaviour = 50.
        // Diff = 50 - 91 = -41 (< -5) -> "Declining"
        val result = FocusScoreEngine.calculateFocusScore(
            questionnaireScore = 80,
            behaviourScore = 50,
            confidence = 50,
            yesterdayFocusScore = 90,
            pastFocusScores = listOf(88, 90),
            pastBehaviourScores = listOf(90, 92)
        )
        assertEquals(86, result.finalScore)
        assertEquals("Declining", result.trend)
    }
}
