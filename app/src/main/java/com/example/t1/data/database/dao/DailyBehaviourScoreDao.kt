package com.example.t1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.t1.data.database.entity.DailyBehaviourScoreEntity

@Dao
interface DailyBehaviourScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: DailyBehaviourScoreEntity)

    @Query("SELECT * FROM daily_behaviour_score WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getScoreForDate(userId: String, date: String): DailyBehaviourScoreEntity?

    @Query("SELECT * FROM daily_behaviour_score WHERE userId = :userId ORDER BY date ASC")
    suspend fun getScoreHistory(userId: String): List<DailyBehaviourScoreEntity>

    @Query("DELETE FROM daily_behaviour_score WHERE userId = :userId")
    suspend fun clearCache(userId: String)

    @Query("DELETE FROM daily_behaviour_score")
    suspend fun clearAll()
}
