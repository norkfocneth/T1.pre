package com.example.t1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.t1.data.database.entity.LeaderboardDailyEntity

@Dao
interface LeaderboardDailyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entries: List<LeaderboardDailyEntity>)

    @Query("SELECT * FROM leaderboard_daily WHERE snapshotDate = :date ORDER BY rank ASC")
    suspend fun getLeaderboardForDate(date: String): List<LeaderboardDailyEntity>

    @Query("SELECT * FROM leaderboard_daily WHERE snapshotDate = :date AND userId = :userId LIMIT 1")
    suspend fun getEntryForUser(date: String, userId: String): LeaderboardDailyEntity?

    @Query("SELECT COUNT(*) FROM leaderboard_daily WHERE snapshotDate = :date AND focusScore > :score")
    suspend fun countUsersWithHigherScore(date: String, score: Int): Int

    @Query("DELETE FROM leaderboard_daily WHERE snapshotDate = :date")
    suspend fun deleteForDate(date: String)
}
