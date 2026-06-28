package com.example.t1.data.database.entity

import androidx.room.Entity

@Entity(tableName = "daily_behaviour_score", primaryKeys = ["userId", "date"])
data class DailyBehaviourScoreEntity(
    val userId: String,
    val date: String, // ISO-8601 date string e.g. "2026-06-27"
    val behaviourScore: Int,
    val confidence: Int,
    val generatedAt: Long,
    val sourceVersion: String,
    val verified: Boolean
)
