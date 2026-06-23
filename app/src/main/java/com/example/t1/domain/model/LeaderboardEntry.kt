package com.example.t1.domain.model

data class LeaderboardEntry(
    val username: String,
    val displayName: String?,
    val focusScore: Int,
    val streak: Int,
    val rank: Int
)
