package com.example.t1.domain.model.usage

import java.time.LocalDate
import kotlinx.serialization.Serializable

data class DailyUsageData(
    val date: LocalDate,
    val totalScreenTimeMs: Long,
    val unlockCount: Int,
    val appOpenCount: Int,
    val foregroundSessions: List<ForegroundSession>,
    val firstAppOpenTime: Long?,
    val lastAppCloseTime: Long?,
    val collectionTimestamp: Long,
    val isVerified: Boolean
)

@Serializable
data class ForegroundSession(
    val packageName: String,
    val startTime: Long,
    val endTime: Long,
    val durationMs: Long
)
