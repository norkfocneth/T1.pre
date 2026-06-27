package com.example.t1.domain.model.behaviour

import java.time.LocalDate
import kotlinx.serialization.Serializable

data class DailyBehaviourSummary(
    val date: LocalDate,
    val totalScreenTimeMs: Long,
    val socialTimeMs: Long,
    val entertainmentTimeMs: Long,
    val productiveTimeMs: Long,
    val educationTimeMs: Long,
    val communicationTimeMs: Long,
    val financeTimeMs: Long,
    val healthTimeMs: Long,
    val developmentTimeMs: Long,
    val otherTimeMs: Long,
    val unlockCount: Int,
    val appOpenCount: Int,
    val foregroundSessionCount: Int,
    val topUsedApps: List<AppUsageSummary>,
    val collectionTimestamp: Long
)

@Serializable
data class AppUsageSummary(
    val packageName: String,
    val displayName: String,
    val usageDurationMs: Long,
    val category: AppCategory
)
