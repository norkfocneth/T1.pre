package com.example.t1.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenTransactionDto(
    @SerialName("transaction_id") val transactionId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("amount") val amount: Int,
    @SerialName("type") val type: String,
    @SerialName("reason") val reason: String,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("status") val status: String,
    @SerialName("source") val source: String,
    @SerialName("hash_placeholder") val hashPlaceholder: String = "",
    @SerialName("chain_status") val chainStatus: String = "",
    @SerialName("wallet_version") val walletVersion: Int = 1
)
