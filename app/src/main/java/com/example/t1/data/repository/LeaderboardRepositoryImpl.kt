package com.example.t1.data.repository

import com.example.t1.data.remote.SupabaseService
import com.example.t1.domain.model.LeaderboardEntry
import com.example.t1.domain.repository.LeaderboardRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepositoryImpl @Inject constructor(
    private val supabaseService: SupabaseService
) : LeaderboardRepository {

    private var cachedLeaderboard: List<LeaderboardEntry> = emptyList()

    override suspend fun getLeaderboard(forceRefresh: Boolean): Result<List<LeaderboardEntry>> {
        if (cachedLeaderboard.isNotEmpty() && !forceRefresh) {
            return Result.success(cachedLeaderboard)
        }
        return try {
            val list = supabaseService.getLeaderboard().mapIndexed { index, dto ->
                LeaderboardEntry(
                    username = dto.username,
                    displayName = dto.displayName,
                    focusScore = dto.focusScore,
                    streak = dto.streak,
                    rank = index + 1
                )
            }.sortedByDescending { it.focusScore }
             .mapIndexed { idx, entry -> entry.copy(rank = idx + 1) }

            cachedLeaderboard = list
            Result.success(list)
        } catch (e: Exception) {
            if (cachedLeaderboard.isNotEmpty()) {
                Result.success(cachedLeaderboard)
            } else {
                Result.failure(e)
            }
        }
    }
}
