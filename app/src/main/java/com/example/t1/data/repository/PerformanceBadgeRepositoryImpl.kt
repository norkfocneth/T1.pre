package com.example.t1.data.repository

import com.example.t1.domain.repository.PerformanceBadgeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceBadgeRepositoryImpl @Inject constructor() : PerformanceBadgeRepository {
    override fun resolveBadge(percentile: Int): String {
        return when {
            percentile >= 99 -> "Apex Predator"
            percentile >= 95 -> "Elite Focus"
            percentile >= 90 -> "Deep Worker"
            percentile >= 80 -> "Top Performer"
            percentile >= 65 -> "Consistent Builder"
            percentile >= 50 -> "Balanced Performer"
            else -> "Getting Started"
        }
    }

    override fun resolveDescription(percentile: Int): String {
        return when {
            percentile >= 99 -> "Top 1% of performers worldwide"
            percentile >= 95 -> "Top 5% focus consistency"
            percentile >= 90 -> "Top 10% deep work champions"
            percentile >= 80 -> "Top 20% high performance group"
            percentile >= 65 -> "Top 35% consistent builders"
            percentile >= 50 -> "Top 50% balanced work and life"
            else -> "Improving focus habits day by day"
        }
    }

    override fun resolveColor(percentile: Int): Long {
        return when {
            percentile >= 99 -> 0xFFFF2D55 // Red
            percentile >= 95 -> 0xFFFF9500 // Orange
            percentile >= 90 -> 0xFF5856D6 // Indigo
            percentile >= 80 -> 0xFF5AC8FA // Light Blue
            percentile >= 65 -> 0xFF007AFF // Blue
            percentile >= 50 -> 0xFF34C759 // Green
            else -> 0xFF8E8E93 // Grey
        }
    }
}
