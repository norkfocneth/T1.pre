package com.example.t1.data.repository

import com.example.t1.data.database.dao.FocusSessionDao
import com.example.t1.data.database.dao.UserProfileDao
import com.example.t1.data.database.entity.FocusSessionEntity
import com.example.t1.data.database.entity.UserProfileEntity
import com.example.t1.data.preferences.UserPreferences
import com.example.t1.data.remote.SupabaseService
import com.example.t1.data.remote.model.ProfileDto
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.UserRepository
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val supabaseService: SupabaseService,
    private val userProfileDao: UserProfileDao,
    private val focusSessionDao: FocusSessionDao,
    private val userPreferences: UserPreferences
) : UserRepository {

    override val userProfileFlow: Flow<UserProfile?> = userProfileDao.getProfileFlow().map { entity ->
        entity?.let {
            UserProfile(
                id = it.id,
                username = it.username,
                usernameLower = it.usernameLower,
                displayName = it.displayName,
                focusScore = it.focusScore,
                onboardingCompleted = it.onboardingCompleted,
                createdAt = it.createdAt
            )
        }
    }

    override val onboardingCompleted: Flow<Boolean> = userPreferences.onboardingCompletedFlow
    override val cachedFocusScore: Flow<Int> = userPreferences.focusScoreFlow

    override suspend fun checkUsernameAvailable(username: String): Result<Boolean> {
        val usernameLower = username.trim().lowercase()
        if (usernameLower.length < 2) {
            return Result.failure(IllegalArgumentException("Username must be at least 2 characters"))
        }
        return try {
            val isTaken = supabaseService.isUsernameTaken(usernameLower)
            Result.success(!isTaken)
        } catch (e: Exception) {
            android.util.Log.e("UserRepositoryImpl", "Username check failed", e)
            Result.failure(e)
        }
    }

    override suspend fun saveOnboardingProfile(
        userId: String,
        username: String,
        displayName: String?,
        focusScore: Int
    ): Result<Unit> {
        val now = System.currentTimeMillis()
        val usernameLower = username.trim().lowercase()
        val displayNameClean = displayName?.trim()?.take(40)

        val localEntity = UserProfileEntity(
            id = userId,
            username = username,
            usernameLower = usernameLower,
            displayName = displayNameClean,
            focusScore = focusScore,
            onboardingCompleted = true,
            createdAt = now,
            synced = false
        )

        return try {
            // Save locally first
            userProfileDao.insertProfile(localEntity)
            userPreferences.saveUsernameAndDisplayName(username, displayNameClean)
            userPreferences.saveFocusScore(focusScore)
            userPreferences.saveOnboardingCompleted(true)

            // Try syncing with Supabase immediately
            val dto = ProfileDto(
                id = userId,
                username = username,
                usernameLower = usernameLower,
                displayName = displayNameClean,
                focusScore = focusScore,
                onboardingCompleted = true,
                createdAt = now.toString() // or format as ISO string
            )
            val success = supabaseService.upsertProfile(dto)
            if (success) {
                userProfileDao.insertProfile(localEntity.copy(synced = true))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            // Offline-first: even if remote upsert fails, we succeed locally
            Result.success(Unit)
        }
    }

    override suspend fun getUserProfile(userId: String, forceRefresh: Boolean): Result<UserProfile?> {
        val local = userProfileDao.getProfile()
        if (local != null && !forceRefresh) {
            return Result.success(
                UserProfile(
                    id = local.id,
                    username = local.username,
                    usernameLower = local.usernameLower,
                    displayName = local.displayName,
                    focusScore = local.focusScore,
                    onboardingCompleted = local.onboardingCompleted,
                    createdAt = local.createdAt
                )
            )
        }

        return try {
            val remoteDto = supabaseService.getProfile(userId)
            if (remoteDto != null) {
                val entity = UserProfileEntity(
                    id = remoteDto.id,
                    username = remoteDto.username,
                    usernameLower = remoteDto.usernameLower,
                    displayName = remoteDto.displayName,
                    focusScore = remoteDto.focusScore,
                    onboardingCompleted = remoteDto.onboardingCompleted,
                    createdAt = System.currentTimeMillis(), // fallback
                    synced = true
                )
                userProfileDao.insertProfile(entity)
                userPreferences.saveUsernameAndDisplayName(remoteDto.username, remoteDto.displayName)
                userPreferences.saveFocusScore(remoteDto.focusScore)
                userPreferences.saveOnboardingCompleted(remoteDto.onboardingCompleted)

                Result.success(
                    UserProfile(
                        id = entity.id,
                        username = entity.username,
                        usernameLower = entity.usernameLower,
                        displayName = entity.displayName,
                        focusScore = entity.focusScore,
                        onboardingCompleted = entity.onboardingCompleted,
                        createdAt = entity.createdAt
                    )
                )
            } else {
                if (local != null) {
                    Result.success(
                        UserProfile(
                            id = local.id,
                            username = local.username,
                            usernameLower = local.usernameLower,
                            displayName = local.displayName,
                            focusScore = local.focusScore,
                            onboardingCompleted = local.onboardingCompleted,
                            createdAt = local.createdAt
                        )
                    )
                } else {
                    Result.success(null)
                }
            }
        } catch (e: Exception) {
            if (local != null) {
                Result.success(
                    UserProfile(
                        id = local.id,
                        username = local.username,
                        usernameLower = local.usernameLower,
                        displayName = local.displayName,
                        focusScore = local.focusScore,
                        onboardingCompleted = local.onboardingCompleted,
                        createdAt = local.createdAt
                    )
                )
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun saveFocusSession(durationSeconds: Long): Result<Unit> {
        val userId = userPreferences.userIdFlow.first()
            ?: return Result.failure(Exception("User session not found"))

        val session = FocusSessionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            durationSeconds = durationSeconds,
            timestamp = System.currentTimeMillis(),
            synced = false
        )

        return try {
            focusSessionDao.insertSession(session)
            // Trigger sync in background (fire-and-forget or try)
            trySyncSession(session)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun trySyncSession(session: FocusSessionEntity) {
        try {
            // In Supabase, focus sessions will be inserted in a focus_sessions table
            supabaseService.client.postgrest.from("focus_sessions").insert(
                mapOf(
                    "id" to session.id,
                    "user_id" to session.userId,
                    "duration_seconds" to session.durationSeconds,
                    "timestamp" to session.timestamp
                )
            )
            focusSessionDao.insertSession(session.copy(synced = true))
        } catch (e: Exception) {
            // Handled silently for offline-first behavior
        }
    }

    override suspend fun syncPendingEdits(): Result<Unit> {
        return try {
            // 1. Sync Profile
            val unsyncedProfiles = userProfileDao.getUnsyncedProfiles()
            for (profile in unsyncedProfiles) {
                val dto = ProfileDto(
                    id = profile.id,
                    username = profile.username,
                    usernameLower = profile.usernameLower,
                    displayName = profile.displayName,
                    focusScore = profile.focusScore,
                    onboardingCompleted = profile.onboardingCompleted,
                    createdAt = profile.createdAt.toString()
                )
                val success = supabaseService.upsertProfile(dto)
                if (success) {
                    userProfileDao.insertProfile(profile.copy(synced = true))
                }
            }

            // 2. Sync Focus Sessions
            val unsyncedSessions = focusSessionDao.getUnsyncedSessions()
            for (session in unsyncedSessions) {
                try {
                    supabaseService.client.postgrest.from("focus_sessions").insert(
                        mapOf(
                            "id" to session.id,
                            "user_id" to session.userId,
                            "duration_seconds" to session.durationSeconds,
                            "timestamp" to session.timestamp
                        )
                    )
                    focusSessionDao.insertSession(session.copy(synced = true))
                } catch (e: Exception) {
                    // Stop syncing list if we hit network errors
                    break
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
