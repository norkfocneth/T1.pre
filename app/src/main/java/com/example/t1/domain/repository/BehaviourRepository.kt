package com.example.t1.domain.repository

import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import java.time.LocalDate

interface BehaviourRepository {
    suspend fun getTodayBehaviour(): Result<DailyBehaviourSummary?>
    suspend fun getYesterdayBehaviour(): Result<DailyBehaviourSummary?>
    suspend fun getBehaviourForDate(date: LocalDate): Result<DailyBehaviourSummary?>
    suspend fun getWeeklySummary(): Result<List<DailyBehaviourSummary>>
    suspend fun getMonthlySummary(): Result<List<DailyBehaviourSummary>>
    suspend fun clearBehaviourCache(): Result<Unit>
}
