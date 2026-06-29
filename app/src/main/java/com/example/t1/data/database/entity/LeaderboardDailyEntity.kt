package com.example.t1.data.database.entity

import androidx.room.Entity

@Entity(tableName = "leaderboard_daily", primaryKeys = ["snapshotDate", "userId"])
data class LeaderboardDailyEntity(
    val snapshotDate: String,
    val userId: String,
    val username: String,
    val displayName: String?,
    val focusScore: Int,
    val behaviourScore: Int,
    val percentile: Int,
    val rank: Int,
    val rankMovement: String,
    val badge: String,
    val rankReason: String,
    val streak: Int,
    val createdAt: Long
)
