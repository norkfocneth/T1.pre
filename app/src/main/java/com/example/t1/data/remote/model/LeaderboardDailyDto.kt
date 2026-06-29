package com.example.t1.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardDailyDto(
    @SerialName("snapshot_date")
    val snapshotDate: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("username")
    val username: String,
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("focus_score")
    val focusScore: Int,
    @SerialName("behaviour_score")
    val behaviourScore: Int,
    @SerialName("percentile")
    val percentile: Int,
    @SerialName("rank")
    val rank: Int,
    @SerialName("rank_movement")
    val rankMovement: String,
    @SerialName("badge")
    val badge: String,
    @SerialName("rank_reason")
    val rankReason: String,
    @SerialName("streak")
    val streak: Int,
    @SerialName("created_at")
    val createdAt: String
)
