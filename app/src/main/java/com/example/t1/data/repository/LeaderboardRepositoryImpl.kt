package com.example.t1.data.repository

import com.example.t1.data.database.dao.LeaderboardDailyDao
import com.example.t1.data.database.entity.LeaderboardDailyEntity
import com.example.t1.data.remote.SupabaseService
import com.example.t1.data.remote.model.LeaderboardDailyDto
import com.example.t1.domain.model.LeaderboardEntry
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.LeaderboardRepository
import com.example.t1.domain.repository.PerformanceBadgeRepository
import com.example.t1.domain.repository.ResearchBenchmarkRepository
import com.example.t1.util.T1Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class LeaderboardRepositoryImpl @Inject constructor(
    private val supabaseService: SupabaseService,
    private val leaderboardDailyDao: LeaderboardDailyDao,
    private val authRepository: AuthRepository,
    private val researchBenchmarkRepository: ResearchBenchmarkRepository,
    private val performanceBadgeRepository: PerformanceBadgeRepository
) : LeaderboardRepository {

    private var cachedLeaderboard: List<LeaderboardEntry> = emptyList()
    private var cachedDate: String? = null

    override suspend fun getLeaderboard(forceRefresh: Boolean): Result<List<LeaderboardEntry>> = withContext(Dispatchers.IO) {
        val todayStr = LocalDate.now().toString()
        
        if (cachedLeaderboard.isNotEmpty() && cachedDate == todayStr && !forceRefresh) {
            return@withContext Result.success(cachedLeaderboard)
        }

        try {
            // 1. Fetch locally first
            val localList = leaderboardDailyDao.getLeaderboardForDate(todayStr)
            if (localList.isNotEmpty() && !forceRefresh) {
                val mapped = localList.map { it.toDomain() }
                cachedLeaderboard = mapped
                cachedDate = todayStr
                return@withContext Result.success(mapped)
            }

            // 2. Fetch from Supabase
            val remoteList = supabaseService.getDailyLeaderboard(todayStr)
            if (remoteList.isNotEmpty()) {
                val entities = remoteList.map { dto ->
                    LeaderboardDailyEntity(
                        snapshotDate = dto.snapshotDate,
                        userId = dto.userId,
                        username = dto.username,
                        displayName = dto.displayName,
                        focusScore = dto.focusScore,
                        behaviourScore = dto.behaviourScore,
                        percentile = dto.percentile,
                        rank = dto.rank,
                        rankMovement = dto.rankMovement,
                        badge = dto.badge,
                        rankReason = dto.rankReason,
                        streak = dto.streak,
                        createdAt = try { java.time.Instant.parse(dto.createdAt).toEpochMilli() } catch (e: Exception) { System.currentTimeMillis() }
                    )
                }
                leaderboardDailyDao.deleteForDate(todayStr)
                leaderboardDailyDao.insertOrReplace(entities)
                
                val mapped = entities.map { it.toDomain() }
                cachedLeaderboard = mapped
                cachedDate = todayStr
                return@withContext Result.success(mapped)
            }

            // 3. Fallback: Load yesterday's snapshot if today's is missing
            val yesterdayStr = LocalDate.now().minusDays(1).toString()
            val yesterdayList = leaderboardDailyDao.getLeaderboardForDate(yesterdayStr)
            if (yesterdayList.isNotEmpty()) {
                val mapped = yesterdayList.map { it.toDomain() }
                return@withContext Result.success(mapped)
            }

            // If absolutely nothing exists, return empty list
            Result.success(emptyList())
        } catch (e: Exception) {
            T1Logger.e("Exception loading leaderboard", e)
            if (cachedLeaderboard.isNotEmpty()) {
                Result.success(cachedLeaderboard)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun generateDailySnapshot(date: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            T1Logger.i("Starting daily leaderboard snapshot generation for date: $date")
            
            // 1. Fetch all user profiles from Supabase
            val profiles = supabaseService.getAllProfiles()
            if (profiles.isEmpty()) {
                T1Logger.w("No profiles found to generate leaderboard snapshot.")
                return@withContext Result.success(Unit)
            }

            // 2. Load yesterday's ranks to calculate movement
            val yesterdayStr = LocalDate.parse(date).minusDays(1).toString()
            val yesterdayEntries = leaderboardDailyDao.getLeaderboardForDate(yesterdayStr)
                .associateBy { it.userId }

            // 3. Sort profiles according to Tie-Breaking Rules:
            // Focus Score Desc -> Behaviour Score Desc -> Social Ratio Asc -> Productivity Ratio Desc -> Focus Sessions Desc -> Created At Asc -> User ID Asc
            val sortedProfiles = profiles.sortedWith(
                compareByDescending<com.example.t1.data.remote.model.ProfileDto> { it.focusScore }
                    .thenByDescending { it.behaviourScore }
                    .thenBy { it.socialRatio }
                    .thenByDescending { it.productivityRatio }
                    .thenByDescending { it.totalFocusSessions }
                    .thenBy {
                        try { java.time.Instant.parse(it.createdAt).toEpochMilli() } catch (e: Exception) { 0L }
                    }
                    .thenBy { it.id }
            )

            // 4. Create snapshot records
            val dtos = sortedProfiles.mapIndexed { index, profile ->
                val rank = index + 1
                val yesterdayEntry = yesterdayEntries[profile.id]
                
                // Rank Movement
                val movement = if (yesterdayEntry != null) {
                    val diff = yesterdayEntry.rank - rank
                    when {
                        diff > 0 -> "▲$diff"
                        diff < 0 -> "▼${abs(diff)}"
                        else -> "—"
                    }
                } else {
                    "NEW"
                }

                // Percentile and Badge
                val pct = researchBenchmarkRepository.getPercentile(profile.focusScore)
                val badge = performanceBadgeRepository.resolveBadge(pct)

                // Rank Reason
                val reason = when {
                    yesterdayEntry == null -> "NEW_USER"
                    profile.focusScore > yesterdayEntry.focusScore -> "FOCUS_SCORE_INCREASE"
                    profile.focusScore < yesterdayEntry.focusScore -> "FOCUS_SCORE_DECREASE"
                    else -> "NO_CHANGE"
                }

                LeaderboardDailyDto(
                    snapshotDate = date,
                    userId = profile.id,
                    username = profile.username,
                    displayName = profile.displayName,
                    focusScore = profile.focusScore,
                    behaviourScore = profile.behaviourScore,
                    percentile = pct,
                    rank = rank,
                    rankMovement = movement,
                    badge = badge,
                    rankReason = reason,
                    streak = profile.streak,
                    createdAt = profile.createdAt
                )
            }

            // 5. Save remotely to Supabase
            val uploadSuccess = supabaseService.upsertDailyLeaderboard(dtos)
            if (!uploadSuccess) {
                T1Logger.e("Failed to upload leaderboard snapshot to Supabase.")
                return@withContext Result.failure(Exception("Failed to upload leaderboard to Supabase"))
            }

            // 6. Save locally to Room
            val entities = dtos.map { dto ->
                LeaderboardDailyEntity(
                    snapshotDate = dto.snapshotDate,
                    userId = dto.userId,
                    username = dto.username,
                    displayName = dto.displayName,
                    focusScore = dto.focusScore,
                    behaviourScore = dto.behaviourScore,
                    percentile = dto.percentile,
                    rank = dto.rank,
                    rankMovement = dto.rankMovement,
                    badge = dto.badge,
                    rankReason = dto.rankReason,
                    streak = dto.streak,
                    createdAt = try { java.time.Instant.parse(dto.createdAt).toEpochMilli() } catch (e: Exception) { System.currentTimeMillis() }
                )
            }
            leaderboardDailyDao.deleteForDate(date)
            leaderboardDailyDao.insertOrReplace(entities)

            T1Logger.i("Daily leaderboard snapshot generation succeeded for date: $date with ${entities.size} users.")
            Result.success(Unit)
        } catch (e: Exception) {
            T1Logger.e("Exception during daily leaderboard generation", e)
            Result.failure(e)
        }
    }

    override suspend fun getTemporaryRank(userId: String, focusScore: Int): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val todayStr = LocalDate.now().toString()
            val count = leaderboardDailyDao.countUsersWithHigherScore(todayStr, focusScore)
            Result.success(count + 1)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper mapping Room Entity -> Domain Model
    private fun LeaderboardDailyEntity.toDomain(): LeaderboardEntry {
        return LeaderboardEntry(
            username = username,
            displayName = displayName,
            focusScore = focusScore,
            streak = streak,
            rank = rank,
            percentile = percentile,
            badge = badge,
            rankMovement = rankMovement
        )
    }
}
