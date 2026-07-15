package com.example.t1.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token_wallets")
data class TokenWalletEntity(
    @PrimaryKey
    val userId: String,
    val balance: Int,
    val lifetimeEarned: Int,
    val lifetimeSpent: Int,
    val lastUpdated: Long,
    val createdAt: Long
)
