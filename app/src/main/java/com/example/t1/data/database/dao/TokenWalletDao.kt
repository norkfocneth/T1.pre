package com.example.t1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.t1.data.database.entity.TokenWalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenWalletDao {
    @Query("SELECT * FROM token_wallets WHERE userId = :userId LIMIT 1")
    fun getWalletFlow(userId: String): Flow<TokenWalletEntity?>

    @Query("SELECT * FROM token_wallets WHERE userId = :userId LIMIT 1")
    suspend fun getWallet(userId: String): TokenWalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: TokenWalletEntity)

    @Query("DELETE FROM token_wallets")
    suspend fun clearAll()
}
