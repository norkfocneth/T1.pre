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
    val totalFocusSessions: Int = 0
)
