package com.example.t1.domain.repository

import com.example.t1.domain.model.LeaderboardEntry

interface LeaderboardRepository {
    suspend fun getLeaderboard(forceRefresh: Boolean): Result<List<LeaderboardEntry>>
    suspend fun generateDailySnapshot(date: String): Result<Unit>
    suspend fun getTemporaryRank(userId: String, focusScore: Int): Result<Int>
}
