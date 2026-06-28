package com.example.t1.domain.repository

import com.example.t1.data.database.entity.DailyFocusScoreEntity

interface FocusScoreRepository {
    suspend fun calculateAndSaveFocusScore(dateStr: String): Result<DailyFocusScoreEntity>
    suspend fun getScoreForDate(dateStr: String): DailyFocusScoreEntity?
    suspend fun getScoreHistory(): List<DailyFocusScoreEntity>
    suspend fun getUnsyncedScores(): List<DailyFocusScoreEntity>
    suspend fun markSynced(dateStr: String)
    suspend fun syncWithCloud(): Result<Unit>
    suspend fun clearCache()
}
