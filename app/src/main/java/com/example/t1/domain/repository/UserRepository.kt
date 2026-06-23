package com.example.t1.domain.repository

import com.example.t1.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userProfileFlow: Flow<UserProfile?>
    val onboardingCompleted: Flow<Boolean>
    val cachedFocusScore: Flow<Int>

    suspend fun checkUsernameAvailable(username: String): Result<Boolean>
    suspend fun saveOnboardingProfile(userId: String, username: String, displayName: String?, focusScore: Int): Result<Unit>
    suspend fun getUserProfile(userId: String, forceRefresh: Boolean = false): Result<UserProfile?>
    suspend fun saveFocusSession(durationSeconds: Long): Result<Unit>
    suspend fun syncPendingEdits(): Result<Unit>
}
