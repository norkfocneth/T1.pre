package com.example.t1.domain.repository

interface PerformanceBadgeRepository {
    fun resolveBadge(percentile: Int): String
    fun resolveDescription(percentile: Int): String
    fun resolveColor(percentile: Int): Long
}
