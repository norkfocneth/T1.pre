package com.example.t1.data.repository

import com.example.t1.domain.repository.ResearchBenchmarkRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class ResearchBenchmarkRepositoryImpl @Inject constructor() : ResearchBenchmarkRepository {

    companion object {
        const val BENCHMARK_VERSION = "BENCHMARK_V2"
    }

    /**
     * Calculates the top percentage tier based on Focus Score:
     * - 95..100 -> Top 1%
     * - 90..94  -> Top 5%
     * - 85..89  -> Top 10%
     * - 80..84  -> Top 15%
     * - 76..79  -> Top 24%
     * - 70..75  -> Top 29%
     * - 65..69  -> Top 34%
     * - 60..64  -> Top 42%
     * - 55..59  -> Top 52%
     * - 50..54  -> Top 59%
     * - 45..49  -> Top 64%
     * - 40..44  -> Top 74%
     * - 35..39  -> Top 80%
     * - 20..34  -> Top 85%
     * - 0..19   -> Top 95%
     */
    override fun getTopPercentile(focusScore: Int): Int {
        val score = focusScore.coerceIn(0, 100)
        return when {
            score >= 95 -> 1
            score >= 90 -> 5
            score >= 85 -> 10
            score >= 80 -> 15
            score >= 76 -> 24
            score >= 70 -> 29
            score >= 65 -> 34
            score >= 60 -> 42
            score >= 55 -> 52
            score >= 50 -> 59
            score >= 45 -> 64
            score >= 40 -> 74
            score >= 35 -> 80
            score >= 20 -> 85
            else -> 95
        }
    }

    /**
     * Returns "Ahead of X% of users" (100 - topPercentile).
     */
    override fun getPercentile(focusScore: Int): Int {
        val topPct = getTopPercentile(focusScore)
        return (100 - topPct).coerceIn(1, 99)
    }

    override fun getBenchmarkVersion(): String {
        return BENCHMARK_VERSION
    }
}
