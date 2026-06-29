package com.example.t1.data.repository

import com.example.t1.data.database.dao.DailyBehaviourDao
import com.example.t1.data.database.dao.DailyBehaviourScoreDao
import com.example.t1.data.database.entity.DailyBehaviourScoreEntity
import com.example.t1.domain.behaviour.BehaviourEngineResult
import com.example.t1.domain.behaviour.BehaviourScoreEngine
import com.example.t1.domain.model.behaviour.AppUsageSummary
import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.BehaviourScoreRepository
import com.example.t1.domain.repository.AppCategoryRepository
import com.example.t1.data.database.dao.FocusSessionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BehaviourScoreRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val dailyBehaviourDao: DailyBehaviourDao,
    private val dailyBehaviourScoreDao: DailyBehaviourScoreDao,
    private val focusSessionDao: FocusSessionDao,
    private val appCategoryRepository: AppCategoryRepository
) : BehaviourScoreRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun calculateAndSaveScore(dateStr: String): Result<DailyBehaviourScoreEntity> = withContext(Dispatchers.IO) {
        try {
            val userId = authRepository.currentUserIdSync
                ?: return@withContext Result.failure(Exception("User is not authenticated"))

            // Fetch today's behaviour summary
            val todayEntity = dailyBehaviourDao.getForDate(userId, dateStr)
                ?: return@withContext Result.failure(Exception("No behaviour summary found for date: $dateStr"))

            val todaySummary = mapEntityToDomain(todayEntity)

            // Fetch yesterday's behaviour summary
            val yesterdayDateStr = LocalDate.parse(dateStr).minusDays(1).toString()
            val yesterdayEntity = dailyBehaviourDao.getForDate(userId, yesterdayDateStr)

            val yesterdaySummary = yesterdayEntity?.let { mapEntityToDomain(it) }

            // Fetch verified days count
            val verifiedDaysCount = dailyBehaviourDao.getVerifiedDaysCount(userId)

            // Fetch completed focus sessions count for the day
            val startOfDay = LocalDate.parse(dateStr).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = LocalDate.parse(dateStr).plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
            val completedFocusSessions = focusSessionDao.getFocusSessionCountForDay(userId, startOfDay, endOfDay)

            // Compute Behaviour Score on Dispatchers.Default
            val scoreResult = withContext(Dispatchers.Default) {
                BehaviourScoreEngine.calculateBehaviourScore(
                    today = todaySummary,
                    completedFocusSessionsCount = completedFocusSessions,
                    verifiedDaysCount = verifiedDaysCount,
                    appCategoryRepository = appCategoryRepository
                )
            }

            when (scoreResult) {
                is BehaviourEngineResult.Success -> {
                    val scoreEntity = DailyBehaviourScoreEntity(
                        userId = userId,
                        date = dateStr,
                        behaviourScore = scoreResult.score,
                        confidence = scoreResult.confidence,
                        generatedAt = System.currentTimeMillis(),
                        sourceVersion = BehaviourScoreEngine.ENGINE_VERSION,
                        verified = true
                    )
                    dailyBehaviourScoreDao.insertOrReplace(scoreEntity)
                    Result.success(scoreEntity)
                }
                is BehaviourEngineResult.Error -> {
                    Result.failure(Exception(scoreResult.reason))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getScoreForDate(dateStr: String): DailyBehaviourScoreEntity? = withContext(Dispatchers.IO) {
        val userId = authRepository.currentUserIdSync ?: return@withContext null
        dailyBehaviourScoreDao.getScoreForDate(userId, dateStr)
    }

    override suspend fun getScoreHistory(): List<DailyBehaviourScoreEntity> = withContext(Dispatchers.IO) {
        val userId = authRepository.currentUserIdSync ?: return@withContext emptyList()
        dailyBehaviourScoreDao.getScoreHistory(userId)
    }

    override suspend fun clearCache() = withContext(Dispatchers.IO) {
        val userId = authRepository.currentUserIdSync ?: return@withContext
        dailyBehaviourScoreDao.clearCache(userId)
    }

    private fun mapEntityToDomain(entity: com.example.t1.data.database.entity.DailyBehaviourEntity): DailyBehaviourSummary {
        val topUsedApps = try {
            json.decodeFromString<List<AppUsageSummary>>(entity.topUsedAppsJson)
        } catch (e: Exception) {
            emptyList()
        }
        return DailyBehaviourSummary(
            date = LocalDate.parse(entity.date),
            totalScreenTimeMs = entity.totalScreenTimeMs,
            socialTimeMs = entity.socialTimeMs,
            entertainmentTimeMs = entity.entertainmentTimeMs,
            productiveTimeMs = entity.productiveTimeMs,
            educationTimeMs = entity.educationTimeMs,
            communicationTimeMs = entity.communicationTimeMs,
            financeTimeMs = entity.financeTimeMs,
            healthTimeMs = entity.healthTimeMs,
            developmentTimeMs = entity.developmentTimeMs,
            otherTimeMs = entity.otherTimeMs,
            unlockCount = entity.unlockCount,
            appOpenCount = entity.appOpenCount,
            foregroundSessionCount = entity.foregroundSessionCount,
            topUsedApps = topUsedApps,
            collectionTimestamp = entity.collectionTimestamp
        )
    }
}
