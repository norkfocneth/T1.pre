package com.example.t1.data.repository

import com.example.t1.data.database.dao.UserProfileDao
import com.example.t1.data.database.entity.UserProfileEntity
import com.example.t1.data.remote.SupabaseService
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.UserRepository
import com.example.t1.util.T1Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of UserRepository.
 * Coordinates remote Supabase operations and local Room caching.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val supabaseService: SupabaseService,
    private val userProfileDao: UserProfileDao
) : UserRepository {

    override val cachedProfileFlow: Flow<UserProfile?> = userProfileDao.getProfileFlow().map { entity ->
        entity?.toDomain()
    }

    override suspend fun fetchProfile(userId: String): Result<UserProfile?> = withContext(Dispatchers.IO) {
        try {
            T1Logger.i("Fetching remote profile for user ID")
            val profileDto = supabaseService.getProfile(userId)
            if (profileDto == null) {
                T1Logger.i("No remote profile found for user ID")
                return@withContext Result.success(null)
            }

            // Identity Validation Check
            val authenticatedUid = supabaseClient.auth.currentUserOrNull()?.id
            if (profileDto.id != authenticatedUid || profileDto.id != userId) {
                val errorMsg = "Security Validation Failure: Profile ID mismatch (Downloaded=${profileDto.id}, Authenticated=$authenticatedUid)"
                T1Logger.e(errorMsg)
                return@withContext Result.failure(SecurityException(errorMsg))
            }

            T1Logger.i("Remote profile fetched and validated successfully")
            Result.success(profileDto.toDomain())
        } catch (e: Exception) {
            T1Logger.e("Failed to fetch remote user profile", e)
            Result.failure(e)
        }
    }

    override suspend fun saveCachedProfile(profile: UserProfile): Unit = withContext(Dispatchers.IO) {
        T1Logger.i("Caching fresh user profile in Room")
        // Enforce replacement rules: clear any old cache and write new profile
        userProfileDao.clearProfile()
        userProfileDao.insertProfile(profile.toEntity())
        T1Logger.i("Room cache updated successfully")
    }

    override suspend fun clearCache(): Unit = withContext(Dispatchers.IO) {
        T1Logger.i("Clearing local user profile cache from Room")
        userProfileDao.clearProfile()
        T1Logger.i("Local cache cleared successfully")
    }

    // --- Mappers ---

    private fun com.example.t1.data.remote.model.ProfileDto.toDomain(): UserProfile {
        // Parse ISO 8601 created_at string or fallback to current time
        val epochMillis = try {
            java.time.Instant.parse(createdAt).toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
        return UserProfile(
            id = id,
            username = username,
            usernameLower = usernameLower,
            displayName = displayName,
            focusScore = focusScore,
            onboardingCompleted = onboardingCompleted,
            createdAt = epochMillis
        )
    }

    private fun UserProfileEntity.toDomain(): UserProfile {
        return UserProfile(
            id = id,
            username = username,
            usernameLower = usernameLower,
            displayName = displayName,
            focusScore = focusScore,
            onboardingCompleted = onboardingCompleted,
            createdAt = createdAt
        )
    }

    private fun UserProfile.toEntity(): UserProfileEntity {
        return UserProfileEntity(
            id = id,
            username = username,
            usernameLower = usernameLower,
            displayName = displayName,
            focusScore = focusScore,
            onboardingCompleted = onboardingCompleted,
            createdAt = createdAt,
            synced = true // Always synced since it comes directly from cloud
        )
    }
}
