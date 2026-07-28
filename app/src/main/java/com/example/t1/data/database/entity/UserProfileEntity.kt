package com.example.t1.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val usernameLower: String,
    val displayName: String?,
    val focusScore: Int,
    val onboardingCompleted: Boolean,
    val createdAt: Long,
    val synced: Boolean,
    val streak: Int = 0,
    val lastActiveDate: String? = null,
    val behaviourScore: Int = 0,
    val socialRatio: Double = 0.0,
    val productivityRatio: Double = 0.0,
    val totalFocusSessions: Int = 0,
    val age: Int? = null,
    val gender: String? = null,          // Demographic only. NEVER used for score calculation.
    val occupation: String? = null,
    val primaryGoal: String? = null,
    val focusScoreVersion: Int = 1,
    val onboardingCompletedV2: Boolean = false,
    val email: String? = null,
    val avatarUrl: String? = null,
    val onboardingVersion: Int = 1,
    val performanceTier: String = "Bronze",
    val behaviourProfile: String = "Balanced",
    val initialPercentile: Float = 50.0f,
    val bestStreak: Int = 0,
    val timeSaved: Long = 0L
)
