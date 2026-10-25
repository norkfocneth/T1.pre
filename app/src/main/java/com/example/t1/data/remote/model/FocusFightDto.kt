package com.example.t1.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FocusFightDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("challenger_id")
    val challengerId: String,
    @SerialName("opponent_id")
    val opponentId: String,
    @SerialName("duration_minutes")
    val durationMinutes: Int,
    @SerialName("wager_tokens")
    val wagerTokens: Int,
    @SerialName("status")
    val status: String,
    @SerialName("winner_id")
    val winnerId: String? = null,
    @SerialName("challenger_seconds")
    val challengerSeconds: Int = 0,
    @SerialName("opponent_seconds")
    val opponentSeconds: Int = 0,
    @SerialName("created_at")
    val createdAt: String? = null
)
