package com.example.t1.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenWalletDto(
    @SerialName("user_id") val userId: String,
    @SerialName("balance") val balance: Int,
    @SerialName("lifetime_earned") val lifetimeEarned: Int,
    @SerialName("lifetime_spent") val lifetimeSpent: Int,
    @SerialName("last_updated") val lastUpdated: Long,
    @SerialName("created_at") val createdAt: Long
)
