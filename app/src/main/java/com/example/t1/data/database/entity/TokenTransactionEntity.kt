package com.example.t1.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "token_transactions",
    indices = [Index(value = ["userId"])]
)
data class TokenTransactionEntity(
    @PrimaryKey
    val transactionId: String,
    val userId: String,
    val amount: Int,
    val type: String,
    val reason: String,
    val timestamp: Long,
    val status: String,
    val source: String,
    val hashPlaceholder: String = "",
    val chainStatus: String = "",
    val walletVersion: Int = 1
)
