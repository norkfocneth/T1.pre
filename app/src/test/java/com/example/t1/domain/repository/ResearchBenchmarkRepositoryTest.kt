package com.example.t1.domain.repository

import com.example.t1.data.repository.ResearchBenchmarkRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Test

class ResearchBenchmarkRepositoryTest {

    private val repository = ResearchBenchmarkRepositoryImpl()

    @Test
    fun testKeyDataPoints() {
        assertEquals(2, repository.getPercentile(0))
        assertEquals(4, repository.getPercentile(5))
        assertEquals(7, repository.getPercentile(10))
        assertEquals(11, repository.getPercentile(15))
        assertEquals(16, repository.getPercentile(20))
        assertEquals(20, repository.getPercentile(25))
        assertEquals(25, repository.getPercentile(30))
        assertEquals(31, repository.getPercentile(35))
        assertEquals(36, repository.getPercentile(40))
        assertEquals(41, repository.getPercentile(45))
        assertEquals(46, repository.getPercentile(50))
        assertEquals(51, repository.getPercentile(55))
        assertEquals(56, repository.getPercentile(60))
        assertEquals(61, repository.getPercentile(65))
        assertEquals(65, repository.getPercentile(70))
        assertEquals(69, repository.getPercentile(74))
        assertEquals(71, repository.getPercentile(75))
        assertEquals(77, repository.getPercentile(80))
        assertEquals(83, repository.getPercentile(85))
        assertEquals(90, repository.getPercentile(90))
        assertEquals(96, repository.getPercentile(95))
        assertEquals(99, repository.getPercentile(100))
    }

    @Test
    fun testInterpolation() {
        // Between 50 and 55:
        // 50 -> 46
        // 55 -> 51
        // 51 should be round(46 + 1/5 * 5) = 47
        assertEquals(47, repository.getPercentile(51))
        assertEquals(48, repository.getPercentile(52))
        assertEquals(49, repository.getPercentile(53))
        assertEquals(50, repository.getPercentile(54))
    }
}
