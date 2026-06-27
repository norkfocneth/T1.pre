package com.example.t1.ui.viewmodel

import android.content.Context
import com.example.t1.domain.SessionManager
import com.example.t1.domain.model.AuthError
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.UserRepository
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    // Fakes
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeSupabaseClient: SupabaseClient
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeAuthRepository = FakeAuthRepository()
        fakeUserRepository = FakeUserRepository()
        fakeSupabaseClient = mock(SupabaseClient::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLaunchRestoreSuccessDashboard() = testScope.runTest {
        // Setup existing session & profile
        fakeAuthRepository.restoreUserId = "user-123"
        fakeUserRepository.remoteProfiles["user-123"] = UserProfile(
            id = "user-123",
            username = "testuser",
            usernameLower = "testuser",
            displayName = "Test User",
            focusScore = 80,
            onboardingCompleted = true,
            createdAt = 1000L
        )

        // Initialize SessionManager with our fakes and null SupabaseClient
        val customSessionManager = object : SessionManager(null, fakeAuthRepository, fakeUserRepository) {
            override suspend fun restoreSessionAndProfile(): Result<UserProfile?> {
                val uid = fakeAuthRepository.restoreSession().getOrNull() ?: return Result.failure(Exception("No session"))
                return Result.success(fakeUserRepository.remoteProfiles[uid])
            }
        }

        viewModel = AuthViewModel(fakeAuthRepository, customSessionManager)

        // Trigger restore session check (runs in init)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify destination state is Dashboard
        val finalState = viewModel.authState.value
        assertTrue("Expected Dashboard state but got: $finalState", finalState is AuthState.Dashboard)
        assertEquals("user-123", (finalState as AuthState.Dashboard).profile.id)
    }

    @Test
    fun testLaunchRestoreNoProfileOnboarding() = testScope.runTest {
        // Setup session but NO profile
        fakeAuthRepository.restoreUserId = "user-456"

        val customSessionManager = object : SessionManager(null, fakeAuthRepository, fakeUserRepository) {
            override suspend fun restoreSessionAndProfile(): Result<UserProfile?> {
                return Result.success(null) // no profile
            }
        }

        viewModel = AuthViewModel(fakeAuthRepository, customSessionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = viewModel.authState.value
        assertTrue("Expected NavigateToOnboarding but got: $finalState", finalState is AuthState.NavigateToOnboarding)
    }

    @Test
    fun testGoogleLoginNewUser() = testScope.runTest {
        fakeAuthRepository.loginUserId = "new-user-789"
        // Remote returns null profile

        val customSessionManager = object : SessionManager(null, fakeAuthRepository, fakeUserRepository) {
            override suspend fun syncProfileAndCache(userId: String): Result<UserProfile?> {
                return Result.success(null) // new user
            }
        }

        viewModel = AuthViewModel(fakeAuthRepository, customSessionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Clear checking state to unauthenticated first
        viewModel.resetErrorState()
        
        val context = mock(Context::class.java)
        viewModel.signInWithGoogle(context)
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = viewModel.authState.value
        assertTrue("Expected NavigateToOnboarding but got: $finalState", finalState is AuthState.NavigateToOnboarding)
    }

    @Test
    fun testSecurityValidationFailure() = testScope.runTest {
        fakeAuthRepository.loginUserId = "legit-user"

        val customSessionManager = object : SessionManager(null, fakeAuthRepository, fakeUserRepository) {
            override suspend fun syncProfileAndCache(userId: String): Result<UserProfile?> {
                // Return mismatched ID profile to trigger security failure
                return Result.failure(SecurityException("SECURITY FAILURE: ID mismatch"))
            }
        }

        viewModel = AuthViewModel(fakeAuthRepository, customSessionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.resetErrorState()
        val context = mock(Context::class.java)
        viewModel.signInWithGoogle(context)
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = viewModel.authState.value
        assertTrue("Expected Error state but got: $finalState", finalState is AuthState.Error)
        assertEquals(AuthError.SECURITY_FAILURE, (finalState as AuthState.Error).errorType)
    }

    @Test
    fun testLogoutDropsCache() = testScope.runTest {
        val customSessionManager = object : SessionManager(null, fakeAuthRepository, fakeUserRepository) {
            override suspend fun performSignOut(): Result<Unit> {
                fakeUserRepository.clearCache()
                return Result.success(Unit)
            }
        }

        viewModel = AuthViewModel(fakeAuthRepository, customSessionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.signOut()
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = viewModel.authState.value
        assertTrue("Expected Unauthenticated but got: $finalState", finalState is AuthState.Unauthenticated)
        assertTrue(fakeUserRepository.cacheCleared)
    }

    // --- Fakes ---

    private class FakeAuthRepository : AuthRepository {
        var restoreUserId: String? = null
        var loginUserId: String? = null

        override val isSignedIn: Flow<Boolean> = flowOf(false)
        private val _currentUserId = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
        override val currentUserId: Flow<String?> = _currentUserId

        override suspend fun signInWithGoogle(context: Context): Result<String> {
            return loginUserId?.let {
                _currentUserId.value = it
                Result.success(it)
            } ?: Result.failure(Exception("Login failed"))
        }

        override suspend fun restoreSession(): Result<String> {
            return restoreUserId?.let { Result.success(it) } ?: Result.failure(Exception("No session"))
        }

        override suspend fun signOut(): Result<Unit> {
            _currentUserId.value = null
            return Result.success(Unit)
        }
    }

    private class FakeUserRepository : UserRepository {
        val remoteProfiles = mutableMapOf<String, UserProfile>()
        var cachedProfile: UserProfile? = null
        var cacheCleared = false

        override val cachedProfileFlow: Flow<UserProfile?> = flowOf(null)

        override suspend fun fetchProfile(userId: String): Result<UserProfile?> {
            return Result.success(remoteProfiles[userId])
        }

        override suspend fun saveCachedProfile(profile: UserProfile) {
            cachedProfile = profile
        }

        override suspend fun clearCache() {
            cachedProfile = null
            cacheCleared = true
        }
    }
}
