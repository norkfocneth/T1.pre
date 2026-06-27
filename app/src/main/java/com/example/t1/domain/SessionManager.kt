package com.example.t1.domain

import android.content.Context
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.UserRepository
import com.example.t1.util.T1Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Coordinates auth sessions, token refreshes, and local Room cache synchronization.
 * Serves as the single coordinator for authentication state changes and security validation.
 */
@Singleton
open class SessionManager @Inject constructor(
    private val supabaseClient: SupabaseClient?,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        // Automatically clear local cache if session becomes invalid or expired
        if (supabaseClient != null) {
            scope.launch {
                supabaseClient.auth.sessionStatus.collectLatest { status ->
                    T1Logger.i("Supabase session status changed to: ${status::class.simpleName}")
                    if (status is SessionStatus.NotAuthenticated) {
                        T1Logger.i("Session invalid or expired. Cleaning Room cache to prevent session leakage.")
                        userRepository.clearCache()
                    }
                }
            }
        }
    }

    /**
     * Attempts to restore active user session from memory/storage and sync the profile.
     */
    open suspend fun restoreSessionAndProfile(): Result<UserProfile?> {
        T1Logger.i("Starting session and profile restore flow")
        val restoreResult = authRepository.restoreSession()
        if (restoreResult.isFailure) {
            T1Logger.w("No session restored: ${restoreResult.exceptionOrNull()?.message}")
            userRepository.clearCache()
            return Result.failure(restoreResult.exceptionOrNull() ?: Exception("No session"))
        }

        val userId = restoreResult.getOrThrow()
        T1Logger.i("Session restored successfully for user ID. Fetching profile.")

        return syncProfileAndCache(userId)
    }

    /**
     * Downloads user profile, runs security validation checks, and saves to Room.
     */
    open suspend fun syncProfileAndCache(userId: String): Result<UserProfile?> {
        val fetchResult = userRepository.fetchProfile(userId)
        if (fetchResult.isFailure) {
            val error = fetchResult.exceptionOrNull()
            T1Logger.e("Failed to sync profile from cloud", error)
            return Result.failure(error ?: Exception("Profile fetch failed"))
        }

        val profile = fetchResult.getOrNull()
        if (profile == null) {
            // Profile does not exist yet (New User flow)
            T1Logger.i("Profile does not exist for authenticated user.")
            userRepository.clearCache()
            return Result.success(null)
        }

        // Validate identity to prevent session leakage
        val currentUid = supabaseClient?.auth?.currentUserOrNull()?.id
        if (profile.id != userId || profile.id != currentUid) {
            T1Logger.e("SECURITY FAILURE: Downloaded profile ID mismatch! Initiating emergency logout.")
            // Emergency clear and logout
            userRepository.clearCache()
            authRepository.signOut()
            return Result.failure(SecurityException("Security Validation Failed: profile.id != auth.uid()"))
        }

        // Complete Cache Replacement
        userRepository.saveCachedProfile(profile)
        T1Logger.i("Profile synchronized and cached successfully in Room.")
        return Result.success(profile)
    }

    /**
     * Clean Sign-Out of session and cache.
     */
    open suspend fun performSignOut(): Result<Unit> {
        T1Logger.i("Performing clean sign-out")
        val signOutResult = authRepository.signOut()
        userRepository.clearCache()
        return signOutResult
    }
}
