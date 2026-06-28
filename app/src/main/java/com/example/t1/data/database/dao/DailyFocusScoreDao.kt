package com.example.t1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.t1.data.database.entity.DailyFocusScoreEntity

@Dao
interface DailyFocusScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: DailyFocusScoreEntity)

    @Query("SELECT * FROM daily_focus_score WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getScoreForDate(userId: String, date: String): DailyFocusScoreEntity?

    @Query("SELECT * FROM daily_focus_score WHERE userId = :userId ORDER BY date ASC")
    suspend fun getScoreHistory(userId: String): List<DailyFocusScoreEntity>

    @Query("SELECT * FROM daily_focus_score WHERE userId = :userId AND synced = 0")
    suspend fun getUnsyncedScores(userId: String): List<DailyFocusScoreEntity>

    @Query("UPDATE daily_focus_score SET synced = 1 WHERE userId = :userId AND date = :date")
    suspend fun markSynced(userId: String, date: String)

    @Query("DELETE FROM daily_focus_score WHERE userId = :userId")
    suspend fun clearCache(userId: String)

    @Query("DELETE FROM daily_focus_score")
    suspend fun clearAll()
}
