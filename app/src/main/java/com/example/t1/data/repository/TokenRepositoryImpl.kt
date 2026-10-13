package com.example.t1.data.repository

import com.example.t1.data.database.dao.TokenWalletDao
import com.example.t1.data.database.dao.TokenTransactionDao
import com.example.t1.data.database.entity.TokenWalletEntity
import com.example.t1.data.database.entity.TokenTransactionEntity
import com.example.t1.data.remote.SupabaseService
import com.example.t1.data.remote.model.TokenWalletDto
import com.example.t1.data.remote.model.TokenTransactionDto
import com.example.t1.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRepositoryImpl @Inject constructor(
    private val walletDao: TokenWalletDao,
    private val tokenTransactionDao: TokenTransactionDao,
    private val supabaseService: SupabaseService
) : TokenRepository {

    override fun getWalletFlow(userId: String): Flow<TokenWalletEntity?> {
        return walletDao.getWalletFlow(userId)
    }

    override suspend fun getWallet(userId: String): TokenWalletEntity? {
        return walletDao.getWallet(userId)
    }

    override fun getTransactionsFlow(userId: String): Flow<List<TokenTransactionEntity>> {
        return tokenTransactionDao.getTransactionsFlow(userId)
    }

    override suspend fun getTransactions(userId: String): List<TokenTransactionEntity> {
        return tokenTransactionDao.getTransactions(userId)
    }

    override suspend fun addTransaction(
        userId: String,
        amount: Int,
        type: String,
        reason: String
    ): Result<Unit> {
        val now = System.currentTimeMillis()
        
        // Cooldown and Daily/Weekly/Monthly limits check
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1

        when (reason) {
            "DAILY_LOGIN" -> {
                val existing = tokenTransactionDao.getTransactionCount(userId, "DAILY_LOGIN", startOfDay, endOfDay)
                if (existing > 0) return Result.failure(Exception("Daily login reward already claimed today."))
            }
            "DAILY_MISSION" -> {
                val existing = tokenTransactionDao.getTransactionCount(userId, "DAILY_MISSION", startOfDay, endOfDay)
                if (existing > 0) return Result.failure(Exception("Daily mission reward already claimed today."))
            }
            "FOCUS_SESSION" -> {
                val existing = tokenTransactionDao.getTransactionCount(userId, "FOCUS_SESSION", startOfDay, endOfDay)
                if (existing >= 10) return Result.failure(Exception("Focus session daily reward limit reached (max 10)."))
            }
            "WEEKLY_MISSION" -> {
                // Determine start of current week
                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val daysToSubtract = if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - Calendar.MONDAY
                val startOfWeek = startOfDay - daysToSubtract * 24 * 60 * 60 * 1000L
                val endOfWeek = startOfWeek + 7 * 24 * 60 * 60 * 1000L - 1
                val existing = tokenTransactionDao.getTransactionCount(userId, "WEEKLY_MISSION", startOfWeek, endOfWeek)
                if (existing > 0) return Result.failure(Exception("Weekly mission reward already claimed this week."))
            }
            "MONTHLY_CHALLENGE" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startOfMonth = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                val endOfMonth = calendar.timeInMillis - 1
                val existing = tokenTransactionDao.getTransactionCount(userId, "MONTHLY_CHALLENGE", startOfMonth, endOfMonth)
                if (existing > 0) return Result.failure(Exception("Monthly challenge reward already claimed this month."))
            }
        }

        // Create transaction
        val txId = UUID.randomUUID().toString()
        val localTx = TokenTransactionEntity(
            transactionId = txId,
            userId = userId,
            amount = amount,
            type = type,
            reason = reason,
            timestamp = now,
            status = "PENDING",
            source = "OFFLINE"
        )

        // Insert locally
        tokenTransactionDao.insertTransaction(localTx)
        recalculateLocalWallet(userId)

        // Try syncing to Supabase
        val remoteDto = TokenTransactionDto(
            transactionId = txId,
            userId = userId,
            amount = amount,
            type = type,
            reason = reason,
            timestamp = now,
            status = "SYNCED",
            source = "ONLINE"
        )

        try {
            val syncSuccess = supabaseService.insertTokenTransaction(remoteDto)
            if (syncSuccess) {
                // Mark as SYNCED locally
                tokenTransactionDao.insertTransaction(localTx.copy(status = "SYNCED", source = "ONLINE"))
                return Result.success(Unit)
            } else {
                // Network was reachable but DB execution failed (RLS, validation, duplicate PK, etc.)
                // Rollback local transaction to keep local and remote authoritative
                tokenTransactionDao.deleteTransaction(txId)
                recalculateLocalWallet(userId)
                return Result.failure(Exception("Server validation failed. Transaction rolled back."))
            }
        } catch (e: Exception) {
            // Offline or general network timeout - keep transaction as PENDING locally
            android.util.Log.w("TokenRepository", "Network offline. Transaction cached as pending: ${e.message}")
            return Result.success(Unit)
        }
    }

    override suspend fun syncWallet(userId: String): Result<Unit> {
        try {
            // First, push any pending local transactions
            syncPendingTransactions(userId)

            // Download remote wallet and transactions (or initialize 0-balance wallet if none exists)
            val remoteWallet = supabaseService.getTokenWallet(userId) ?: run {
                val now = System.currentTimeMillis()
                val newWalletDto = TokenWalletDto(
                    userId = userId,
                    balance = 0,
                    lifetimeEarned = 0,
                    lifetimeSpent = 0,
                    lastUpdated = now,
                    createdAt = now
                )
                supabaseService.upsertTokenWallet(newWalletDto)
                newWalletDto
            }
            val remoteTxs = supabaseService.getTokenTransactions(userId)

            // Sync transactions locally (clear existing cached transactions first to prevent ghost transactions)
            tokenTransactionDao.clearAll()
            remoteTxs.forEach { remoteTx ->
                val localTx = TokenTransactionEntity(
                    transactionId = remoteTx.transactionId,
                    userId = remoteTx.userId,
                    amount = remoteTx.amount,
                    type = remoteTx.type,
                    reason = remoteTx.reason,
                    timestamp = remoteTx.timestamp,
                    status = "SYNCED",
                    source = "ONLINE"
                )
                tokenTransactionDao.insertTransaction(localTx)
            }

            // Sync wallet locally
            val localWallet = TokenWalletEntity(
                userId = remoteWallet.userId,
                balance = remoteWallet.balance,
                lifetimeEarned = remoteWallet.lifetimeEarned,
                lifetimeSpent = remoteWallet.lifetimeSpent,
                lastUpdated = remoteWallet.lastUpdated,
                createdAt = remoteWallet.createdAt
            )
            walletDao.insertWallet(localWallet)

            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun syncPendingTransactions(userId: String): Result<Unit> {
        return try {
            val pendings = tokenTransactionDao.getPendingTransactions(userId)
            if (pendings.isEmpty()) return Result.success(Unit)

            val dtos = pendings.map { tx ->
                TokenTransactionDto(
                    transactionId = tx.transactionId,
                    userId = tx.userId,
                    amount = tx.amount,
                    type = tx.type,
                    reason = tx.reason,
                    timestamp = tx.timestamp,
                    status = "SYNCED",
                    source = "ONLINE"
                )
            }

            val syncSuccess = supabaseService.insertTokenTransactionsBatch(dtos)
            if (syncSuccess) {
                pendings.forEach { tx ->
                    tokenTransactionDao.insertTransaction(tx.copy(status = "SYNCED", source = "ONLINE"))
                }
                recalculateLocalWallet(userId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to batch sync pending transactions"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCache() {
        walletDao.clearAll()
        tokenTransactionDao.clearAll()
    }

    private suspend fun recalculateLocalWallet(userId: String) {
        val txs = tokenTransactionDao.getTransactions(userId)
        val balance = txs.sumOf { it.amount }
        val lifetimeEarned = txs.filter { it.amount > 0 }.sumOf { it.amount }
        val lifetimeSpent = txs.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }

        val currentWallet = walletDao.getWallet(userId)
        val wallet = TokenWalletEntity(
            userId = userId,
            balance = if (balance < 0) 0 else balance,
            lifetimeEarned = lifetimeEarned,
            lifetimeSpent = lifetimeSpent,
            lastUpdated = System.currentTimeMillis(),
            createdAt = currentWallet?.createdAt ?: System.currentTimeMillis()
        )
        walletDao.insertWallet(wallet)
    }
}
