package com.example.t1.data.database.entity

import androidx.room.Entity

@Entity(tableName = "daily_focus_score", primaryKeys = ["userId", "date"])
data class DailyFocusScoreEntity(
    val userId: String,
    val date: String, // ISO-8601 date string e.g. "2026-06-27"
    val questionnaireScore: Int,
    val behaviourScore: Int,
    val confidence: Int,
    val finalFocusScore: Int,
    val trend: String,
    val timeSaved: Long,
    val generatedAt: Long,
    val version: String,
    val verified: Boolean,
    val synced: Boolean
)
