package com.example.t1.data.repository

import com.example.t1.data.database.dao.DailyUsageDao
import com.example.t1.data.database.entity.DailyUsageEntity
import com.example.t1.data.usage.UsageStatsDataSource
import com.example.t1.domain.model.usage.DailyUsageData
import com.example.t1.domain.model.usage.ForegroundSession
import com.example.t1.domain.repository.UsageRepository
import com.example.t1.domain.usage.UsageDataValidator
import com.example.t1.domain.usage.ValidationResult
import com.example.t1.util.T1Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val usageStatsDataSource: UsageStatsDataSource,
    private val dailyUsageDao: DailyUsageDao
) : UsageRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private fun getUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }

    override suspend fun getTodayUsage(): Result<DailyUsageData?> {
        val today = LocalDate.now()
        return getUsageForDate(today)
    }

    override suspend fun getYesterdayUsage(): Result<DailyUsageData?> {
        val yesterday = LocalDate.now().minusDays(1)
        return getUsageForDate(yesterday)
    }

    override suspend fun getUsageForDate(date: LocalDate): Result<DailyUsageData?> = withContext(Dispatchers.IO) {
        val userId = getUserId() ?: return@withContext Result.failure(IllegalStateException("User is not authenticated"))
        val dateStr = date.format(dateFormatter)
        val startTime = System.currentTimeMillis()

        try {
            val isToday = date == LocalDate.now()

            // If not today, try to fetch from Room first
            if (!isToday) {
                val cached = dailyUsageDao.getForDate(userId, dateStr)
                if (cached != null && cached.isVerified) {
                    T1Logger.i("Found cached daily usage stats for date: $dateStr")
                    return@withContext Result.success(cached.toDomain())
                }
            }

            // Otherwise (is today OR cache miss), query Android framework via DataSource
            T1Logger.i("Querying usage data from Android framework for date: $dateStr")
            com.example.t1.util.UsageLogger.logCollectionStarted(date)
            val rawData = withContext(Dispatchers.Default) {
                usageStatsDataSource.queryDailyUsage(date)
            }

            // Validate data
            when (val validation = UsageDataValidator.validateUsage(rawData)) {
                is ValidationResult.Invalid -> {
                    T1Logger.e("Usage data validation failed for date $dateStr: ${validation.reason}")
                    com.example.t1.util.UsageLogger.logCollectionFailed(date, validation.reason)
                    return@withContext Result.success(null)
                }
                is ValidationResult.Valid -> {
                    if (rawData.isVerified) {
                        val entity = rawData.toEntity(userId)
                        dailyUsageDao.insert(entity)
                        T1Logger.i("Saved usage stats to Room cache for date: $dateStr")
                    }
                    val elapsed = System.currentTimeMillis() - startTime
                    com.example.t1.util.UsageLogger.logCollectionCompleted(date, elapsed, rawData.foregroundSessions.size)
                    Result.success(rawData)
                }
            }
        } catch (e: Exception) {
            T1Logger.e("Failed to get usage stats for date: $dateStr", e)
            com.example.t1.util.UsageLogger.logCollectionFailed(date, e.message ?: "Unknown Exception")
            Result.failure(e)
        }
    }

    override suspend fun getUsageForRange(start: LocalDate, end: LocalDate): Result<List<DailyUsageData>> = withContext(Dispatchers.IO) {
        val results = mutableListOf<DailyUsageData>()
        var current = start
        while (!current.isAfter(end)) {
            val result = getUsageForDate(current)
            if (result.isSuccess) {
                result.getOrNull()?.let { results.add(it) }
            } else {
                return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Unknown error querying usage range"))
            }
            current = current.plusDays(1)
        }
        Result.success(results)
    }

    private fun DailyUsageEntity.toDomain(): DailyUsageData {
        val sessions = try {
            json.decodeFromString<List<ForegroundSession>>(foregroundSessionsJson)
        } catch (e: Exception) {
            T1Logger.e("Failed to deserialize foreground sessions for date $date", e)
            emptyList()
        }

        return DailyUsageData(
            date = LocalDate.parse(date, dateFormatter),
            totalScreenTimeMs = totalScreenTimeMs,
            unlockCount = unlockCount,
            appOpenCount = appOpenCount,
            foregroundSessions = sessions,
            firstAppOpenTime = firstAppOpenTime,
            lastAppCloseTime = lastAppCloseTime,
            collectionTimestamp = collectionTimestamp,
            isVerified = isVerified
        )
    }

    private fun DailyUsageData.toEntity(userId: String): DailyUsageEntity {
        val sessionsJson = json.encodeToString(foregroundSessions)
        return DailyUsageEntity(
            userId = userId,
            date = date.format(dateFormatter),
            totalScreenTimeMs = totalScreenTimeMs,
            unlockCount = unlockCount,
            appOpenCount = appOpenCount,
            foregroundSessionsJson = sessionsJson,
            firstAppOpenTime = firstAppOpenTime,
            lastAppCloseTime = lastAppCloseTime,
            collectionTimestamp = collectionTimestamp,
            isVerified = isVerified
        )
    }
}
