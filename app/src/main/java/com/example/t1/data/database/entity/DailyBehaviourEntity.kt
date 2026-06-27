package com.example.t1.data.database.entity

import androidx.room.Entity

@Entity(tableName = "daily_behaviour", primaryKeys = ["userId", "date"])
data class DailyBehaviourEntity(
    val userId: String,
    val date: String, // ISO-8601 date string e.g. "2026-06-27"
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
    val topUsedAppsJson: String, // Serialized JSON list of AppUsageSummary
    val collectionTimestamp: Long
)
