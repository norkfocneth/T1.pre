package com.example.t1.data.repository

import com.example.t1.data.database.dao.DailyBehaviourDao
import com.example.t1.data.database.entity.DailyBehaviourEntity
import com.example.t1.domain.behaviour.BehaviourProcessor
import com.example.t1.domain.model.behaviour.AppUsageSummary
import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import com.example.t1.domain.repository.BehaviourRepository
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
class BehaviourRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val usageRepository: UsageRepository,
    private val behaviourProcessor: BehaviourProcessor,
    private val dailyBehaviourDao: DailyBehaviourDao
) : BehaviourRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private fun getUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }

    override suspend fun getTodayBehaviour(): Result<DailyBehaviourSummary?> {
        val today = LocalDate.now()
        return getBehaviourForDate(today)
    }

    override suspend fun getYesterdayBehaviour(): Result<DailyBehaviourSummary?> {
        val yesterday = LocalDate.now().minusDays(1)
        return getBehaviourForDate(yesterday)
    }

    override suspend fun getBehaviourForDate(date: LocalDate): Result<DailyBehaviourSummary?> = withContext(Dispatchers.IO) {
        val userId = getUserId() ?: return@withContext Result.failure(IllegalStateException("User is not authenticated"))
        val dateStr = date.format(dateFormatter)

        try {
            val isToday = date == LocalDate.now()

            // Cache-first for past days
            if (!isToday) {
                val cached = dailyBehaviourDao.getForDate(userId, dateStr)
                if (cached != null) {
                    T1Logger.i("Found cached daily behaviour stats for date: $dateStr")
                    return@withContext Result.success(cached.toDomain())
                }
            }

            // Fetch daily usage (either from Room or Android)
            val usageResult = usageRepository.getUsageForDate(date)
            if (usageResult.isFailure) {
                com.example.t1.util.BehaviourLogger.logCollectionFailed("Usage fetch failed for date: $dateStr")
                return@withContext Result.failure(usageResult.exceptionOrNull() ?: Exception("Usage fetch failed"))
            }

            val usageData = usageResult.getOrNull()
            if (usageData == null) {
                T1Logger.w("No usage data available to process behaviour for date: $dateStr")
                return@withContext Result.success(null)
            }

            // Process daily usage to daily behaviour
            com.example.t1.util.BehaviourLogger.logCollectionStarted()
            val behaviourSummary = behaviourProcessor.process(usageData)

            // Validate behaviour data
            when (val validation = UsageDataValidator.validateBehaviour(behaviourSummary)) {
                is ValidationResult.Invalid -> {
                    T1Logger.e("Behaviour data validation failed for date $dateStr: ${validation.reason}")
                    com.example.t1.util.BehaviourLogger.logCollectionFailed(validation.reason)
                    return@withContext Result.success(null)
                }
                is ValidationResult.Valid -> {
                    val entity = behaviourSummary.toEntity(userId)
                    dailyBehaviourDao.insert(entity)
                    T1Logger.i("Saved daily behaviour to Room cache for date: $dateStr")
                    com.example.t1.util.BehaviourLogger.logRoomSaveCompleted(date)
                    com.example.t1.util.BehaviourLogger.logCollectionFinished(date, behaviourSummary.topUsedApps.size)
                    Result.success(behaviourSummary)
                }
            }
        } catch (e: Exception) {
            T1Logger.e("Failed to get behaviour stats for date: $dateStr", e)
            com.example.t1.util.BehaviourLogger.logCollectionFailed(e.message ?: "Unknown Exception")
            Result.failure(e)
        }
    }

    override suspend fun getWeeklySummary(): Result<List<DailyBehaviourSummary>> {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(6)
        return getBehaviourForRange(startOfWeek, today)
    }

    override suspend fun getMonthlySummary(): Result<List<DailyBehaviourSummary>> {
        val today = LocalDate.now()
        val startOfMonth = today.minusDays(29)
        return getBehaviourForRange(startOfMonth, today)
    }

    override suspend fun clearBehaviourCache(): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = getUserId() ?: return@withContext Result.failure(IllegalStateException("User is not authenticated"))
        try {
            dailyBehaviourDao.deleteAllForUser(userId)
            T1Logger.i("Daily behaviour cache cleared for user: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            T1Logger.e("Failed to clear behaviour cache", e)
            Result.failure(e)
        }
    }

    private suspend fun getBehaviourForRange(start: LocalDate, end: LocalDate): Result<List<DailyBehaviourSummary>> = withContext(Dispatchers.IO) {
        val results = mutableListOf<DailyBehaviourSummary>()
        var current = start
        while (!current.isAfter(end)) {
            val result = getBehaviourForDate(current)
            if (result.isSuccess) {
                result.getOrNull()?.let { results.add(it) }
            } else {
                return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Unknown error querying behaviour range"))
            }
            current = current.plusDays(1)
        }
        Result.success(results)
    }

    private fun DailyBehaviourEntity.toDomain(): DailyBehaviourSummary {
        val topApps = try {
            json.decodeFromString<List<AppUsageSummary>>(topUsedAppsJson)
        } catch (e: Exception) {
            T1Logger.e("Failed to deserialize top apps for date $date", e)
            emptyList()
        }

        return DailyBehaviourSummary(
            date = LocalDate.parse(date, dateFormatter),
            totalScreenTimeMs = totalScreenTimeMs,
            socialTimeMs = socialTimeMs,
            entertainmentTimeMs = entertainmentTimeMs,
            productiveTimeMs = productiveTimeMs,
            educationTimeMs = educationTimeMs,
            communicationTimeMs = communicationTimeMs,
            financeTimeMs = financeTimeMs,
            healthTimeMs = healthTimeMs,
            developmentTimeMs = developmentTimeMs,
            otherTimeMs = otherTimeMs,
            unlockCount = unlockCount,
            appOpenCount = appOpenCount,
            foregroundSessionCount = foregroundSessionCount,
            topUsedApps = topApps,
            collectionTimestamp = collectionTimestamp
        )
    }

    private fun DailyBehaviourSummary.toEntity(userId: String): DailyBehaviourEntity {
        val topAppsJson = json.encodeToString(topUsedApps)
        return DailyBehaviourEntity(
            userId = userId,
            date = date.format(dateFormatter),
            totalScreenTimeMs = totalScreenTimeMs,
            socialTimeMs = socialTimeMs,
            entertainmentTimeMs = entertainmentTimeMs,
            productiveTimeMs = productiveTimeMs,
            educationTimeMs = educationTimeMs,
            communicationTimeMs = communicationTimeMs,
            financeTimeMs = financeTimeMs,
            healthTimeMs = healthTimeMs,
            developmentTimeMs = developmentTimeMs,
            otherTimeMs = otherTimeMs,
            unlockCount = unlockCount,
            appOpenCount = appOpenCount,
            foregroundSessionCount = foregroundSessionCount,
            topUsedAppsJson = topAppsJson,
            collectionTimestamp = collectionTimestamp
        )
    }
}
