package com.example.t1.domain.repository

interface ResearchBenchmarkRepository {
    fun getPercentile(focusScore: Int): Int
    fun getBenchmarkVersion(): String
}
