package com.example.t1.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntryDto(
    @SerialName("username")
    val username: String,
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("focus_score")
    val focusScore: Int,
    @SerialName("streak")
    val streak: Int = 1,
    @SerialName("rank")
    val rank: Int = 0
)
