package com.example.t1.data.database.entity

import androidx.room.Entity

@Entity(tableName = "daily_usage", primaryKeys = ["userId", "date"])
data class DailyUsageEntity(
    val userId: String,
    val date: String, // ISO-8601 date string e.g. "2026-06-27"
    val totalScreenTimeMs: Long,
    val unlockCount: Int,
    val appOpenCount: Int,
    val foregroundSessionsJson: String, // Serialized JSON list of ForegroundSession
    val firstAppOpenTime: Long?,
    val lastAppCloseTime: Long?,
    val collectionTimestamp: Long,
    val isVerified: Boolean
)
