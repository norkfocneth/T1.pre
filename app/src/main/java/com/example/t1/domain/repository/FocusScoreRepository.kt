package com.example.t1.domain.repository

import com.example.t1.data.database.entity.DailyFocusScoreEntity
import kotlinx.coroutines.flow.Flow

interface FocusScoreRepository {
    suspend fun calculateAndSaveFocusScore(dateStr: String): Result<DailyFocusScoreEntity>
    suspend fun getScoreForDate(dateStr: String): DailyFocusScoreEntity?
    suspend fun getScoreHistory(): List<DailyFocusScoreEntity>
    fun getScoreHistoryFlow(userId: String): Flow<List<DailyFocusScoreEntity>>
    suspend fun getUnsyncedScores(): List<DailyFocusScoreEntity>
    suspend fun markSynced(dateStr: String)
    suspend fun syncWithCloud(): Result<Unit>
    suspend fun clearCache()
}
