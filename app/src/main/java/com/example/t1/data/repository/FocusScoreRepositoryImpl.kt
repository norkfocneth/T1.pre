package com.example.t1.data.repository

import com.example.t1.data.database.dao.DailyBehaviourDao
import com.example.t1.data.database.dao.DailyBehaviourScoreDao
import com.example.t1.data.database.dao.DailyFocusScoreDao
import com.example.t1.data.database.dao.FocusSessionDao
import com.example.t1.data.database.entity.DailyFocusScoreEntity
import com.example.t1.data.remote.SupabaseService
import com.example.t1.data.remote.model.DailyFocusScoreDto
import com.example.t1.domain.focus.FocusScoreEngine
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.BehaviourScoreRepository
import com.example.t1.domain.repository.FocusScoreRepository
import com.example.t1.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusScoreRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val behaviourScoreRepository: BehaviourScoreRepository,
    private val dailyBehaviourDao: DailyBehaviourDao,
    private val dailyBehaviourScoreDao: DailyBehaviourScoreDao,
    private val dailyFocusScoreDao: DailyFocusScoreDao,
    private val focusSessionDao: FocusSessionDao,
    private val supabaseService: SupabaseService
) : FocusScoreRepository {

    override suspend fun calculateAndSaveFocusScore(dateStr: String): Result<DailyFocusScoreEntity> = withContext(Dispatchers.IO) {
        try {
            val userId = authRepository.currentUserIdSync
                ?: return@withContext Result.failure(Exception("User is not authenticated"))

            // 1. Get Questionnaire Score (from local Profile)
            val profile = userRepository.getLocalProfile(userId)
                ?: return@withContext Result.failure(Exception("User profile not found locally"))

            // Security Validation Check: profile.id == auth.uid()
            if (profile.id != userId) {
                authRepository.signOut()
                return@withContext Result.failure(SecurityException("Security violation: profile.id != auth.uid()"))
            }

            val questionnaireScore = profile.focusScore

            // 2. Fetch or Calculate Behaviour Score for today
            var behaviourScore = 0
            var confidence = 0

            val behaviourScoreResult = behaviourScoreRepository.calculateAndSaveScore(dateStr)
            if (behaviourScoreResult.isSuccess) {
                val scoreEntity = behaviourScoreResult.getOrThrow()
                behaviourScore = scoreEntity.behaviourScore
                confidence = scoreEntity.confidence
            } else {
                // Behaviour Freeze Rule: If behaviour data unavailable -> Reuse last verified Behaviour Score.
                val lastVerifiedScore = dailyBehaviourScoreDao.getScoreHistory(userId)
                    .lastOrNull { it.verified }
                if (lastVerifiedScore != null) {
                    behaviourScore = lastVerifiedScore.behaviourScore
                    confidence = lastVerifiedScore.confidence
                } else {
                    behaviourScore = 0
                    confidence = 0
                }
            }

            // 3. Load past Focus Scores (last 3 days)
            val allFocusHistory = dailyFocusScoreDao.getScoreHistory(userId)
            val pastFocusScores = allFocusHistory
                .filter { it.date < dateStr }
                .takeLast(3)
                .map { it.finalFocusScore }

            val yesterdayFocusScore = allFocusHistory
                .lastOrNull { it.date < dateStr }
                ?.finalFocusScore

            // 4. Compute Focus Score on Dispatchers.Default
            val focusResult = withContext(Dispatchers.Default) {
                FocusScoreEngine.calculateFocusScore(
                    questionnaireScore = questionnaireScore,
                    behaviourScore = behaviourScore,
                    confidence = confidence,
                    yesterdayFocusScore = yesterdayFocusScore,
                    pastFocusScores = pastFocusScores
                )
            }

            // 5. Calculate Time Saved (Yesterday screen time - Today screen time)
            val todayBehaviour = dailyBehaviourDao.getForDate(userId, dateStr)
            val yesterdayDateStr = LocalDate.parse(dateStr).minusDays(1).toString()
            val yesterdayBehaviour = dailyBehaviourDao.getForDate(userId, yesterdayDateStr)

            val timeSaved = if (todayBehaviour != null && yesterdayBehaviour != null) {
                yesterdayBehaviour.totalScreenTimeMs - todayBehaviour.totalScreenTimeMs
            } else {
                0L
            }

            // 6. Create & Save Local Room Entity
            val focusEntity = DailyFocusScoreEntity(
                userId = userId,
                date = dateStr,
                questionnaireScore = questionnaireScore,
                behaviourScore = behaviourScore,
                confidence = confidence,
                finalFocusScore = focusResult.finalScore,
                trend = focusResult.trend,
                timeSaved = timeSaved,
                generatedAt = System.currentTimeMillis(),
                version = FocusScoreEngine.ENGINE_VERSION,
                verified = true,
                synced = false
            )

            dailyFocusScoreDao.insertOrReplace(focusEntity)

            // Update User Profile with latest daily stats
            val totalNonUtility = if (todayBehaviour != null) {
                todayBehaviour.productiveTimeMs + todayBehaviour.educationTimeMs +
                todayBehaviour.entertainmentTimeMs + todayBehaviour.socialTimeMs
            } else 0L

            val currentSocialRatio = if (totalNonUtility > 0 && todayBehaviour != null) {
                todayBehaviour.socialTimeMs.toDouble() / totalNonUtility
            } else 0.0

            val currentProductivityRatio = if (totalNonUtility > 0 && todayBehaviour != null) {
                todayBehaviour.productiveTimeMs.toDouble() / totalNonUtility
            } else 0.0

            val totalSessions = focusSessionDao.getTotalFocusSessionCount(userId)

            val updatedProfile = profile.copy(
                focusScore = focusResult.finalScore,
                behaviourScore = behaviourScore,
                socialRatio = currentSocialRatio,
                productivityRatio = currentProductivityRatio,
                totalFocusSessions = totalSessions
            )
            // Save & Sync updated profile
            userRepository.saveProfile(updatedProfile)

            // 7. Perform Intelligent Sync & Conflict Resolution with Cloud
            val syncResult = syncSingleScore(focusEntity)
            if (syncResult.isSuccess) {
                dailyFocusScoreDao.insertOrReplace(focusEntity.copy(synced = true))
            }

            Result.success(focusEntity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getScoreForDate(dateStr: String): DailyFocusScoreEntity? = withContext(Dispatchers.IO) {
        val userId = authRepository.currentUserIdSync ?: return@withContext null
        dailyFocusScoreDao.getScoreForDate(userId, dateStr)
    }

    override suspend fun getScoreHistory(): List<DailyFocusScoreEntity> = withContext(Dispatchers.IO) {
        val userId = authRepository.currentUserIdSync ?: return@withContext emptyList()
        dailyFocusScoreDao.getScoreHistory(userId)
    }

    override fun getScoreHistoryFlow(userId: String): Flow<List<DailyFocusScoreEntity>> {
        return dailyFocusScoreDao.getScoreHistoryFlow(userId)
    }

    override suspend fun getUnsyncedScores(): List<DailyFocusScoreEntity> = withContext(Dispatchers.IO) {
        val userId = authRepository.currentUserIdSync ?: return@withContext emptyList()
        dailyFocusScoreDao.getUnsyncedScores(userId)
    }

    override suspend fun markSynced(dateStr: String) = withContext(Dispatchers.IO) {
        val userId = authRepository.currentUserIdSync ?: return@withContext
        dailyFocusScoreDao.markSynced(userId, dateStr)
    }

    override suspend fun syncWithCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = authRepository.currentUserIdSync
                ?: return@withContext Result.failure(Exception("User not authenticated"))

            val unsynced = dailyFocusScoreDao.getUnsyncedScores(userId)
            for (score in unsynced) {
                val res = syncSingleScore(score)
                if (res.isSuccess) {
                    dailyFocusScoreDao.markSynced(userId, score.date)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCache() = withContext(Dispatchers.IO) {
        val userId = authRepository.currentUserIdSync ?: return@withContext
        dailyFocusScoreDao.clearCache(userId)
    }

    private suspend fun syncSingleScore(local: DailyFocusScoreEntity): Result<Unit> {
        val serverDto = supabaseService.getDailyFocusScore(local.userId, local.date)
            ?: return if (uploadToCloud(local)) Result.success(Unit) else Result.failure(Exception("Upload failed"))

        // Intelligent Conflict Resolution
        // 1. Verified Status
        if (serverDto.verified && !local.verified) {
            overwriteLocalWithServer(serverDto)
            return Result.success(Unit)
        }
        if (!serverDto.verified && local.verified) {
            return if (uploadToCloud(local)) Result.success(Unit) else Result.failure(Exception("Upload failed"))
        }

        // 2. Newest Timestamp (generatedAt)
        val serverGeneratedAt = try {
            java.time.Instant.parse(serverDto.generatedAt).toEpochMilli()
        } catch (e: Exception) {
            try {
                serverDto.generatedAt.toLong()
            } catch (ex: Exception) {
                0L
            }
        }
        if (serverGeneratedAt > local.generatedAt) {
            overwriteLocalWithServer(serverDto)
            return Result.success(Unit)
        }
        if (local.generatedAt > serverGeneratedAt) {
            return if (uploadToCloud(local)) Result.success(Unit) else Result.failure(Exception("Upload failed"))
        }

        // 3. Engine Version
        val versionCompare = local.version.compareTo(serverDto.engineVersion)
        if (versionCompare < 0) { // Server has newer version
            overwriteLocalWithServer(serverDto)
            return Result.success(Unit)
        }
        if (versionCompare > 0) { // Local has newer version
            return if (uploadToCloud(local)) Result.success(Unit) else Result.failure(Exception("Upload failed"))
        }

        // 4. Server Wins (identical stats or fallback tie-breaker)
        overwriteLocalWithServer(serverDto)
        return Result.success(Unit)
    }

    private suspend fun uploadToCloud(local: DailyFocusScoreEntity): Boolean {
        val dto = DailyFocusScoreDto(
            userId = local.userId,
            date = local.date,
            questionnaireScore = local.questionnaireScore,
            behaviourScore = local.behaviourScore,
            confidence = local.confidence,
            finalFocusScore = local.finalFocusScore,
            trend = local.trend,
            timeSaved = local.timeSaved,
            generatedAt = try { java.time.Instant.ofEpochMilli(local.generatedAt).toString() } catch (e: Exception) { local.generatedAt.toString() },
            engineVersion = local.version,
            verified = local.verified
        )
        return supabaseService.upsertDailyFocusScore(dto)
    }

    private suspend fun overwriteLocalWithServer(server: DailyFocusScoreDto) {
        val localGeneratedAt = try {
            java.time.Instant.parse(server.generatedAt).toEpochMilli()
        } catch (e: Exception) {
            try {
                server.generatedAt.toLong()
            } catch (ex: Exception) {
                System.currentTimeMillis()
            }
        }
        val entity = DailyFocusScoreEntity(
            userId = server.userId,
            date = server.date,
            questionnaireScore = server.questionnaireScore,
            behaviourScore = server.behaviourScore,
            confidence = server.confidence,
            finalFocusScore = server.finalFocusScore,
            trend = server.trend,
            timeSaved = server.timeSaved,
            generatedAt = localGeneratedAt,
            version = server.engineVersion,
            verified = server.verified,
            synced = true
        )
        dailyFocusScoreDao.insertOrReplace(entity)
    }
}
