package com.example.t1.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("id")
    val id: String,
    @SerialName("username")
    val username: String,
    @SerialName("username_lower")
    val usernameLower: String,
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("focus_score")
    val focusScore: Int,
    @SerialName("onboarding_completed")
    val onboardingCompleted: Boolean,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("current_streak")
    val streak: Int = 0,
    @SerialName("last_active_date")
    val lastActiveDate: String? = null,
    @SerialName("behaviour_score")
    val behaviourScore: Int = 0,
    @SerialName("social_ratio")
    val socialRatio: Double = 0.0,
    @SerialName("productivity_ratio")
    val productivityRatio: Double = 0.0,
    @SerialName("total_focus_sessions")
    val totalFocusSessions: Int = 0,
    @SerialName("age")
    val age: Int? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("occupation")
    val occupation: String? = null,
    @SerialName("primary_goal")
    val primaryGoal: String? = null,
    @SerialName("focus_score_version")
    val focusScoreVersion: Int = 1,
    @SerialName("onboarding_completed_v2")
    val onboardingCompletedV2: Boolean = false,
    @SerialName("email")
    val email: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("onboarding_version")
    val onboardingVersion: Int = 1,
    @SerialName("performance_tier")
    val performanceTier: String = "Bronze",
    @SerialName("behaviour_profile")
    val behaviourProfile: String = "Balanced",
    @SerialName("initial_percentile")
    val initialPercentile: Float = 50.0f,
    @SerialName("best_streak")
    val bestStreak: Int = 0,
    @SerialName("time_saved")
    val timeSaved: Long = 0L
)
