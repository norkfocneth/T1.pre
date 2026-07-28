package com.example.t1.domain.model

data class UserProfile(
    val id: String,
    val username: String,
    val usernameLower: String,
    val displayName: String?,
    val focusScore: Int,
    val onboardingCompleted: Boolean,
    val createdAt: Long,
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
    val onboardingCompletedV2: Boolean = false
)
