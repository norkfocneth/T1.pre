package com.example.t1.data.repository

import com.example.t1.domain.repository.ResearchBenchmarkRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class ResearchBenchmarkRepositoryImpl @Inject constructor() : ResearchBenchmarkRepository {

    companion object {
        const val BENCHMARK_VERSION = "BENCHMARK_V1"
    }

    private val percentileLookup = IntArray(101)

    init {
        val keyPoints = listOf(
            Pair(0, 2f),
            Pair(5, 4f),
            Pair(10, 7f),
            Pair(15, 11f),
            Pair(20, 16f),
            Pair(25, 20f),
            Pair(30, 25f),
            Pair(35, 31f),
            Pair(40, 36f),
            Pair(45, 41f),
            Pair(50, 46f),
            Pair(55, 51f),
            Pair(60, 56f),
            Pair(65, 61f),
            Pair(70, 65f),
            Pair(74, 69f),
            Pair(75, 71f),
            Pair(80, 77f),
            Pair(85, 83f),
            Pair(90, 90f),
            Pair(95, 96f),
            Pair(100, 99f)
        )

        // Fill lookup table by linear interpolation between consecutive points
        for (i in 0 until keyPoints.size - 1) {
            val (x0, y0) = keyPoints[i]
            val (x1, y1) = keyPoints[i + 1]
            for (x in x0..x1) {
                val fraction = (x - x0).toFloat() / (x1 - x0).toFloat()
                val interpolatedValue = y0 + fraction * (y1 - y0)
                percentileLookup[x] = interpolatedValue.roundToInt().coerceIn(0, 100)
            }
        }
    }

    override fun getPercentile(focusScore: Int): Int {
        val score = focusScore.coerceIn(0, 100)
        return percentileLookup[score]
    }

    override fun getBenchmarkVersion(): String {
        return BENCHMARK_VERSION
    }
}
