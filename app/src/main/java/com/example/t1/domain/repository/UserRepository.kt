package com.example.t1.domain.repository

import com.example.t1.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user profile operations.
 * Isolates presentation layer from Supabase and Room cache.
 */
interface UserRepository {
    /**
     * Flow emitting the currently cached user profile.
     */
    val cachedProfileFlow: Flow<UserProfile?>

    /**
     * Fetches the user profile from the remote Supabase database.
     * @param userId The unique user authenticated ID (auth.uid()).
     * @return Result containing UserProfile if it exists, null if it does not, or failure.
     */
    suspend fun fetchProfile(userId: String): Result<UserProfile?>

    /**
     * Caches the user profile in local Room database, replacing any existing entry.
     */
    suspend fun saveCachedProfile(profile: UserProfile)

    /**
     * Clears all cached user profile data from local Room storage.
     */
    suspend fun clearCache()
}
