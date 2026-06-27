package com.example.t1.domain.repository

import com.example.t1.domain.model.usage.DailyUsageData
import java.time.LocalDate

interface UsageRepository {
    suspend fun getTodayUsage(): Result<DailyUsageData?>
    suspend fun getYesterdayUsage(): Result<DailyUsageData?>
    suspend fun getUsageForDate(date: LocalDate): Result<DailyUsageData?>
    suspend fun getUsageForRange(start: LocalDate, end: LocalDate): Result<List<DailyUsageData>>
}
