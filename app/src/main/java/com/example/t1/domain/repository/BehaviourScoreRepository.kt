package com.example.t1.domain.repository

import com.example.t1.data.database.entity.DailyBehaviourScoreEntity

interface BehaviourScoreRepository {
    suspend fun calculateAndSaveScore(dateStr: String): Result<DailyBehaviourScoreEntity>
    suspend fun getScoreForDate(dateStr: String): DailyBehaviourScoreEntity?
    suspend fun getScoreHistory(): List<DailyBehaviourScoreEntity>
    suspend fun clearCache()
}
