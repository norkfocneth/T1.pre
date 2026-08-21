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
            percentile >= 76 -> "Top Performer"
            percentile >= 58 -> "Consistent Builder"
            percentile >= 36 -> "Balanced Performer"
            percentile >= 15 -> "Grit Builder"
            else -> "Getting Started"
        }
    }

    override fun resolveDescription(percentile: Int): String {
        return when {
            percentile >= 99 -> "Top 1% of performers worldwide"
            percentile >= 95 -> "Top 5% focus consistency"
            percentile >= 90 -> "Top 10% deep work champions"
            percentile >= 76 -> "Top 24% high performance group"
            percentile >= 58 -> "Top 42% consistent builders"
            percentile >= 36 -> "Top 64% balanced performers"
            percentile >= 15 -> "Top 85% grit and habit builders"
            else -> "Improving focus habits day by day"
        }
    }

    override fun resolveColor(percentile: Int): Long {
        return when {
            percentile >= 99 -> 0xFFFF2D55 // Red
            percentile >= 95 -> 0xFFFF9500 // Orange
            percentile >= 90 -> 0xFF5856D6 // Indigo
            percentile >= 76 -> 0xFF5AC8FA // Light Blue
            percentile >= 58 -> 0xFF007AFF // Blue
            percentile >= 36 -> 0xFF34C759 // Green
            percentile >= 15 -> 0xFFFFCC00 // Yellow/Amber
            else -> 0xFF8E8E93 // Grey
        }
    }
}
