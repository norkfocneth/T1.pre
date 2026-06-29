package com.example.t1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.t1.data.database.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getAllSessionsFlow(): Flow<List<FocusSessionEntity>>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE userId = :userId AND durationSeconds >= 30 AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getFocusSessionCountForDay(userId: String, startTime: Long, endTime: Long): Int

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE userId = :userId AND durationSeconds >= 30")
    suspend fun getTotalFocusSessionCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE userId = :userId AND durationSeconds >= 30")
    fun getTotalFocusSessionCountFlow(userId: String): Flow<Int>

    @Query("SELECT SUM(durationSeconds) FROM focus_sessions WHERE userId = :userId")
    fun getTotalFocusDurationFlow(userId: String): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions WHERE synced = 0")
    suspend fun getUnsyncedSessions(): List<FocusSessionEntity>

    @Update
    suspend fun updateSessions(sessions: List<FocusSessionEntity>)

    @Query("DELETE FROM focus_sessions")
    suspend fun clearSessions()
}
