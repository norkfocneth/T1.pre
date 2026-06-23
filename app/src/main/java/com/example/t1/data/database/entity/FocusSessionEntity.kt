package com.example.t1.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val durationSeconds: Long,
    val timestamp: Long,
    val synced: Boolean
)
