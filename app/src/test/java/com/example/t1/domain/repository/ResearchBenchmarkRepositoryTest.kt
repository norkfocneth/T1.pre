package com.example.t1.domain.repository

import com.example.t1.data.repository.ResearchBenchmarkRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Test

class ResearchBenchmarkRepositoryTest {

    private val repository = ResearchBenchmarkRepositoryImpl()

    @Test
    fun testKeyDataPoints() {
        // Top percentiles
        assertEquals(95, repository.getTopPercentile(0))
        assertEquals(85, repository.getTopPercentile(20))
        assertEquals(80, repository.getTopPercentile(35))
        assertEquals(74, repository.getTopPercentile(40))
        assertEquals(64, repository.getTopPercentile(45))
        assertEquals(59, repository.getTopPercentile(50))
        assertEquals(52, repository.getTopPercentile(55))
        assertEquals(42, repository.getTopPercentile(60))
        assertEquals(34, repository.getTopPercentile(65))
        assertEquals(29, repository.getTopPercentile(70))
        assertEquals(24, repository.getTopPercentile(76))
        assertEquals(15, repository.getTopPercentile(80))
        assertEquals(10, repository.getTopPercentile(85))
        assertEquals(5, repository.getTopPercentile(90))
        assertEquals(1, repository.getTopPercentile(95))
        assertEquals(1, repository.getTopPercentile(100))

        // Ahead of X% users (100 - topPercentile)
        assertEquals(5, repository.getPercentile(0))
        assertEquals(41, repository.getPercentile(50))
        assertEquals(85, repository.getPercentile(80))
        assertEquals(99, repository.getPercentile(95))
        assertEquals(99, repository.getPercentile(100))
    }

    @Test
    fun testTierRanges() {
        assertEquals(59, repository.getTopPercentile(51))
        assertEquals(59, repository.getTopPercentile(52))
        assertEquals(59, repository.getTopPercentile(53))
        assertEquals(59, repository.getTopPercentile(54))
    }
}
