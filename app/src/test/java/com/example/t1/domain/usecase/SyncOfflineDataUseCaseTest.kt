package com.example.t1.domain.usecase

import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncOfflineDataUseCaseTest {

    private val fakeUserRepository = object : UserRepository {
        var syncCalled = false
        var syncResult: Result<Unit> = Result.success(Unit)

        override val userProfileFlow: Flow<UserProfile?> = flowOf(null)
        override val onboardingCompleted: Flow<Boolean> = flowOf(false)
        override val cachedFocusScore: Flow<Int> = flowOf(50)

        override suspend fun checkUsernameAvailable(username: String): Result<Boolean> = Result.success(true)
        override suspend fun saveOnboardingProfile(
            userId: String,
            username: String,
            displayName: String?,
            focusScore: Int
        ): Result<Unit> = Result.success(Unit)

        override suspend fun getUserProfile(userId: String, forceRefresh: Boolean): Result<UserProfile?> = Result.success(null)
        override suspend fun saveFocusSession(durationSeconds: Long): Result<Unit> = Result.success(Unit)
        
        override suspend fun syncPendingEdits(): Result<Unit> {
            syncCalled = true
            return syncResult
        }
    }

    private val useCase = SyncOfflineDataUseCase(fakeUserRepository)

    @Test
    fun `invoke calls syncPendingEdits on repository`() = runTest {
        val result = useCase()
        
        assertTrue(fakeUserRepository.syncCalled)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke propagates failure on repository error`() = runTest {
        val expectedException = Exception("Network failure")
        fakeUserRepository.syncResult = Result.failure(expectedException)
        
        val result = useCase()
        
        assertTrue(fakeUserRepository.syncCalled)
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}
