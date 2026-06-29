package com.example.t1.data.repository

import com.example.t1.data.database.dao.DailyBehaviourDao
import com.example.t1.data.database.dao.DailyBehaviourScoreDao
import com.example.t1.data.database.dao.DailyFocusScoreDao
import com.example.t1.data.database.dao.FocusSessionDao
import com.example.t1.data.database.entity.DailyBehaviourScoreEntity
import com.example.t1.data.database.entity.DailyFocusScoreEntity
import com.example.t1.data.remote.SupabaseService
import com.example.t1.data.remote.model.DailyFocusScoreDto
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.BehaviourScoreRepository
import com.example.t1.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class FocusScoreRepositoryTest {

    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var mockUserRepository: UserRepository
    private lateinit var mockBehaviourScoreRepository: BehaviourScoreRepository
    private lateinit var mockDailyBehaviourDao: DailyBehaviourDao
    private lateinit var mockDailyBehaviourScoreDao: DailyBehaviourScoreDao
    private lateinit var mockDailyFocusScoreDao: DailyFocusScoreDao
    private lateinit var mockFocusSessionDao: FocusSessionDao
    private lateinit var mockSupabaseService: SupabaseService

    private lateinit var repository: FocusScoreRepositoryImpl

    private val testUserId = "user-123"
    private val testDate = "2026-06-28"

    private val dummyDto = DailyFocusScoreDto("", "", 0, 0, 0, 0, "", 0L, "2026-06-29T12:00:00Z", "", false)
    private val dummyEntity = DailyFocusScoreEntity("", "", 0, 0, 0, 0, "", 0L, 0L, "", false, false)

    @Before
    fun setUp() {
        mockAuthRepository = Mockito.mock(AuthRepository::class.java)
        mockUserRepository = Mockito.mock(UserRepository::class.java)
        mockBehaviourScoreRepository = Mockito.mock(BehaviourScoreRepository::class.java)
        mockDailyBehaviourDao = Mockito.mock(DailyBehaviourDao::class.java)
        mockDailyBehaviourScoreDao = Mockito.mock(DailyBehaviourScoreDao::class.java)
        mockDailyFocusScoreDao = Mockito.mock(DailyFocusScoreDao::class.java)
        mockFocusSessionDao = Mockito.mock(FocusSessionDao::class.java)
        mockSupabaseService = Mockito.mock(SupabaseService::class.java)

        repository = FocusScoreRepositoryImpl(
            authRepository = mockAuthRepository,
            userRepository = mockUserRepository,
            behaviourScoreRepository = mockBehaviourScoreRepository,
            dailyBehaviourDao = mockDailyBehaviourDao,
            dailyBehaviourScoreDao = mockDailyBehaviourScoreDao,
            dailyFocusScoreDao = mockDailyFocusScoreDao,
            focusSessionDao = mockFocusSessionDao,
            supabaseService = mockSupabaseService
        )

        kotlinx.coroutines.runBlocking {
            Mockito.`when`(mockUserRepository.saveProfile(anyNonNull(UserProfile::class.java)))
                .thenReturn(Result.success(Unit))
            Mockito.`when`(mockFocusSessionDao.getTotalFocusSessionCount(Mockito.anyString()))
                .thenReturn(0)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyNonNull(clazz: Class<T>): T {
        Mockito.any(clazz)
        return when (clazz) {
            DailyFocusScoreDto::class.java -> dummyDto as T
            DailyFocusScoreEntity::class.java -> dummyEntity as T
            UserProfile::class.java -> UserProfile("test", "test", "test", null, 50, true, 1000L) as T
            else -> null as T
        }
    }

    @Test
    fun testCalculateAndSaveFocusScoreSuccess() = runTest {
        Mockito.`when`(mockAuthRepository.currentUserIdSync).thenReturn(testUserId)

        val profile = UserProfile(
            id = testUserId,
            username = "testuser",
            usernameLower = "testuser",
            displayName = "Test User",
            focusScore = 80,
            onboardingCompleted = true,
            createdAt = 1000L
        )
        Mockito.`when`(mockUserRepository.getLocalProfile(testUserId)).thenReturn(profile)

        val behaviourScoreEntity = DailyBehaviourScoreEntity(
            userId = testUserId,
            date = testDate,
            behaviourScore = 90,
            confidence = 50,
            generatedAt = System.currentTimeMillis(),
            sourceVersion = "ENGINE_V1",
            verified = true
        )
        Mockito.`when`(mockBehaviourScoreRepository.calculateAndSaveScore(testDate))
            .thenReturn(Result.success(behaviourScoreEntity))

        Mockito.`when`(mockDailyFocusScoreDao.getScoreHistory(testUserId)).thenReturn(emptyList())
        Mockito.`when`(mockDailyBehaviourScoreDao.getScoreHistory(testUserId)).thenReturn(emptyList())
        Mockito.`when`(mockDailyBehaviourDao.getForDate(testUserId, testDate)).thenReturn(null)
        Mockito.`when`(mockDailyBehaviourDao.getForDate(testUserId, "2026-06-27")).thenReturn(null)
        Mockito.`when`(mockSupabaseService.getDailyFocusScore(testUserId, testDate)).thenReturn(null)
        Mockito.`when`(mockSupabaseService.upsertDailyFocusScore(anyNonNull(DailyFocusScoreDto::class.java))).thenReturn(true)

        val result = repository.calculateAndSaveFocusScore(testDate)

        assertTrue(result.isSuccess)
        val entity = result.getOrThrow()
        assertEquals(testUserId, entity.userId)
        assertEquals(testDate, entity.date)
        assertEquals(90, entity.behaviourScore)
        assertEquals(50, entity.confidence)

        Mockito.verify(mockDailyFocusScoreDao, Mockito.atLeastOnce()).insertOrReplace(anyNonNull(DailyFocusScoreEntity::class.java))
        Mockito.verify(mockSupabaseService, Mockito.atLeastOnce()).upsertDailyFocusScore(anyNonNull(DailyFocusScoreDto::class.java))
    }

    @Test
    fun testConflictResolutionServerWinsVerified() = runTest {
        Mockito.`when`(mockAuthRepository.currentUserIdSync).thenReturn(testUserId)

        val profile = UserProfile(
            id = testUserId,
            username = "testuser",
            usernameLower = "testuser",
            displayName = "Test User",
            focusScore = 80,
            onboardingCompleted = true,
            createdAt = 1000L
        )
        Mockito.`when`(mockUserRepository.getLocalProfile(testUserId)).thenReturn(profile)

        val behaviourScoreEntity = DailyBehaviourScoreEntity(
            userId = testUserId,
            date = testDate,
            behaviourScore = 90,
            confidence = 50,
            generatedAt = System.currentTimeMillis(),
            sourceVersion = "ENGINE_V1",
            verified = true
        )
        Mockito.`when`(mockBehaviourScoreRepository.calculateAndSaveScore(testDate))
            .thenReturn(Result.success(behaviourScoreEntity))

        Mockito.`when`(mockDailyFocusScoreDao.getScoreHistory(testUserId)).thenReturn(emptyList())
        Mockito.`when`(mockDailyBehaviourScoreDao.getScoreHistory(testUserId)).thenReturn(emptyList())
        Mockito.`when`(mockDailyBehaviourDao.getForDate(testUserId, testDate)).thenReturn(null)
        Mockito.`when`(mockDailyBehaviourDao.getForDate(testUserId, "2026-06-27")).thenReturn(null)

        val serverDto = DailyFocusScoreDto(
            userId = testUserId,
            date = testDate,
            questionnaireScore = 80,
            behaviourScore = 95,
            confidence = 100,
            finalFocusScore = 88,
            trend = "Improving",
            timeSaved = 0L,
            generatedAt = java.time.Instant.now().plusSeconds(10).toString(),
            engineVersion = "ENGINE_V1",
            verified = true
        )
        Mockito.`when`(mockSupabaseService.getDailyFocusScore(testUserId, testDate)).thenReturn(serverDto)

        val result = repository.calculateAndSaveFocusScore(testDate)

        assertTrue(result.isSuccess)
        Mockito.verify(mockDailyFocusScoreDao, Mockito.atLeastOnce()).insertOrReplace(anyNonNull(DailyFocusScoreEntity::class.java))
        Mockito.verify(mockSupabaseService, Mockito.never()).upsertDailyFocusScore(anyNonNull(DailyFocusScoreDto::class.java))
    }
}
