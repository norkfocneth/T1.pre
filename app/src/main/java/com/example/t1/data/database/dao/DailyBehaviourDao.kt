package com.example.t1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.t1.data.database.entity.DailyBehaviourEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyBehaviourDao {
    @Query("SELECT * FROM daily_behaviour WHERE userId = :userId AND date = :date LIMIT 1")
    fun getForDateFlow(userId: String, date: String): Flow<DailyBehaviourEntity?>

    @Query("SELECT * FROM daily_behaviour WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getForDate(userId: String, date: String): DailyBehaviourEntity?

    @Query("SELECT * FROM daily_behaviour WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getForRange(userId: String, startDate: String, endDate: String): List<DailyBehaviourEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DailyBehaviourEntity)

    @Query("SELECT COUNT(DISTINCT date) FROM daily_behaviour WHERE userId = :userId")
    suspend fun getVerifiedDaysCount(userId: String): Int

    @Query("DELETE FROM daily_behaviour WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("DELETE FROM daily_behaviour")
    suspend fun clearAll()
}

