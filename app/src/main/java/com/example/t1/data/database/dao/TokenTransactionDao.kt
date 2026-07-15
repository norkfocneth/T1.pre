package com.example.t1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.t1.data.database.entity.TokenTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TokenTransactionEntity)

    @Query("DELETE FROM token_transactions WHERE transactionId = :txId")
    suspend fun deleteTransaction(txId: String)

    @Query("SELECT * FROM token_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsFlow(userId: String): Flow<List<TokenTransactionEntity>>

    @Query("SELECT * FROM token_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getTransactions(userId: String): List<TokenTransactionEntity>

    @Query("SELECT * FROM token_transactions WHERE userId = :userId AND status = 'PENDING'")
    suspend fun getPendingTransactions(userId: String): List<TokenTransactionEntity>

    @Query("SELECT COUNT(*) FROM token_transactions WHERE userId = :userId AND reason = :reason AND timestamp >= :startTimestamp AND timestamp <= :endTimestamp")
    suspend fun getTransactionCount(userId: String, reason: String, startTimestamp: Long, endTimestamp: Long): Int

    @Query("DELETE FROM token_transactions")
    suspend fun clearAll()
}
