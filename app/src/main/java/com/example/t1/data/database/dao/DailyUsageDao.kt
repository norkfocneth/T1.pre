package com.example.t1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.t1.data.database.entity.DailyUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyUsageDao {
    @Query("SELECT * FROM daily_usage WHERE userId = :userId AND date = :date LIMIT 1")
    fun getForDateFlow(userId: String, date: String): Flow<DailyUsageEntity?>

    @Query("SELECT * FROM daily_usage WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getForDate(userId: String, date: String): DailyUsageEntity?

    @Query("SELECT * FROM daily_usage WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getForRange(userId: String, startDate: String, endDate: String): List<DailyUsageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DailyUsageEntity)

    @Query("DELETE FROM daily_usage WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("DELETE FROM daily_usage")
    suspend fun clearAll()
}

