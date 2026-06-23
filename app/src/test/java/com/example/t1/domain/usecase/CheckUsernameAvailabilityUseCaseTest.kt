package com.example.t1.domain.usecase

import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckUsernameAvailabilityUseCaseTest {

    private val fakeUserRepository = object : UserRepository {
        var existingUsernames = mutableListOf<String>()

        override val userProfileFlow: Flow<UserProfile?> = flowOf(null)
        override val onboardingCompleted: Flow<Boolean> = flowOf(false)
        override val cachedFocusScore: Flow<Int> = flowOf(50)

        override suspend fun checkUsernameAvailable(username: String): Result<Boolean> {
            val usernameLower = username.trim().lowercase()
            if (usernameLower.length < 2) {
                return Result.failure(IllegalArgumentException("Username must be at least 2 characters"))
            }
            val exists = existingUsernames.any { it.lowercase() == usernameLower }
            return Result.success(!exists)
        }

        override suspend fun saveOnboardingProfile(
            userId: String,
            username: String,
            displayName: String?,
            focusScore: Int
        ): Result<Unit> = Result.success(Unit)

        override suspend fun getUserProfile(userId: String, forceRefresh: Boolean): Result<UserProfile?> = Result.success(null)
        override suspend fun saveFocusSession(durationSeconds: Long): Result<Unit> = Result.success(Unit)
        override suspend fun syncPendingEdits(): Result<Unit> = Result.success(Unit)
    }

    private val useCase = CheckUsernameAvailabilityUseCase(fakeUserRepository)

    @Test
    fun `invoke with available username returns success true`() = runTest {
        fakeUserRepository.existingUsernames = mutableListOf("existing_user")
        
        val result = useCase("new_user")
        
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `invoke with taken username returns success false`() = runTest {
        fakeUserRepository.existingUsernames = mutableListOf("arnav")
        
        val result = useCase("arnav")
        
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
    }

    @Test
    fun `invoke with taken username case-insensitive returns success false`() = runTest {
        fakeUserRepository.existingUsernames = mutableListOf("Arnav")
        
        val result = useCase("arnav")
        
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
    }

    @Test
    fun `invoke with another variation of taken username case-insensitive returns success false`() = runTest {
        fakeUserRepository.existingUsernames = mutableListOf("arnav")
        
        val result = useCase("ArNaV")
        
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
    }

    @Test
    fun `invoke with short username returns failure`() = runTest {
        val result = useCase("a")
        
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
}
