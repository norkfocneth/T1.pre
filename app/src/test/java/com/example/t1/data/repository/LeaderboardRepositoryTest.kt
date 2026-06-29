package com.example.t1.data.repository

import com.example.t1.data.database.dao.LeaderboardDailyDao
import com.example.t1.data.database.entity.LeaderboardDailyEntity
import com.example.t1.data.remote.SupabaseService
import com.example.t1.data.remote.model.ProfileDto
import com.example.t1.data.remote.model.LeaderboardDailyDto
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.PerformanceBadgeRepository
import com.example.t1.domain.repository.ResearchBenchmarkRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify

class LeaderboardRepositoryTest {

    private lateinit var mockSupabaseService: SupabaseService
    private lateinit var mockLeaderboardDailyDao: LeaderboardDailyDao
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var mockResearchBenchmarkRepository: ResearchBenchmarkRepository
    private lateinit var mockPerformanceBadgeRepository: PerformanceBadgeRepository
    private lateinit var repository: LeaderboardRepositoryImpl

    @Before
    fun setUp() {
        mockSupabaseService = mock(SupabaseService::class.java)
        mockLeaderboardDailyDao = mock(LeaderboardDailyDao::class.java)
        mockAuthRepository = mock(AuthRepository::class.java)
        mockResearchBenchmarkRepository = mock(ResearchBenchmarkRepository::class.java)
        mockPerformanceBadgeRepository = mock(PerformanceBadgeRepository::class.java)

        repository = LeaderboardRepositoryImpl(
            mockSupabaseService,
            mockLeaderboardDailyDao,
            mockAuthRepository,
            mockResearchBenchmarkRepository,
            mockPerformanceBadgeRepository
        )
    }

    @Test
    fun testTieBreakingAndSnapshot() = runTest {
        val testDate = "2026-06-29"

        // Create user profiles that have ties
        val user1 = ProfileDto(
            id = "user1-uuid",
            username = "user1",
            usernameLower = "user1",
            displayName = "User 1",
            focusScore = 80, // Same Focus Score
            onboardingCompleted = true,
            createdAt = "2026-06-01T00:00:00Z",
            streak = 5,
            lastActiveDate = testDate,
            behaviourScore = 75, // Lower behaviour score than user 2
            socialRatio = 0.1,
            productivityRatio = 0.8,
            totalFocusSessions = 5
        )

        val user2 = ProfileDto(
            id = "user2-uuid",
            username = "user2",
            usernameLower = "user2",
            displayName = "User 2",
            focusScore = 80, // Same Focus Score
            onboardingCompleted = true,
            createdAt = "2026-06-02T00:00:00Z",
            streak = 6,
            lastActiveDate = testDate,
            behaviourScore = 85, // Higher behaviour score -> should rank higher than user 1
            socialRatio = 0.2,
            productivityRatio = 0.7,
            totalFocusSessions = 4
        )

        `when`(mockSupabaseService.getAllProfiles()).thenReturn(listOf(user1, user2))
        `when`(mockLeaderboardDailyDao.getLeaderboardForDate("2026-06-28")).thenReturn(emptyList()) // No yesterday entries
        `when`(mockResearchBenchmarkRepository.getPercentile(80)).thenReturn(85)
        `when`(mockPerformanceBadgeRepository.resolveBadge(85)).thenReturn("Top Performer")
        var uploadedDtos: List<LeaderboardDailyDto>? = null
        `when`(mockSupabaseService.upsertDailyLeaderboard(Mockito.anyList())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            uploadedDtos = invocation.arguments[0] as List<LeaderboardDailyDto>
            true
        }

        val result = repository.generateDailySnapshot(testDate)
        assertEquals(true, result.isSuccess)

        // Capture/Verify: user 2 must rank higher (rank 1) than user 1 (rank 2) because behaviourScore is higher (85 > 75)
        val entries = uploadedDtos
        assertEquals(true, entries != null)
        assertEquals(2, entries!!.size)
        
        val firstEntry = entries.first()
        val secondEntry = entries.last()

        assertEquals("user2-uuid", firstEntry.userId)
        assertEquals(1, firstEntry.rank)

        assertEquals("user1-uuid", secondEntry.userId)
        assertEquals(2, secondEntry.rank)
    }
}
