package com.example.t1.domain.repository

import com.example.t1.data.database.entity.TokenWalletEntity
import com.example.t1.data.database.entity.TokenTransactionEntity
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    fun getWalletFlow(userId: String): Flow<TokenWalletEntity?>
    suspend fun getWallet(userId: String): TokenWalletEntity?
    fun getTransactionsFlow(userId: String): Flow<List<TokenTransactionEntity>>
    suspend fun getTransactions(userId: String): List<TokenTransactionEntity>
    suspend fun addTransaction(userId: String, amount: Int, type: String, reason: String): Result<Unit>
    suspend fun syncWallet(userId: String): Result<Unit>
    suspend fun syncPendingTransactions(userId: String): Result<Unit>
    suspend fun clearCache()
}
