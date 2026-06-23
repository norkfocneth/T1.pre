package com.example.t1.domain.repository

import com.example.t1.domain.model.LeaderboardEntry

interface LeaderboardRepository {
    suspend fun getLeaderboard(forceRefresh: Boolean): Result<List<LeaderboardEntry>>
}
