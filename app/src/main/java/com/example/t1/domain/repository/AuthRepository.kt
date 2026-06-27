package com.example.t1.domain.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining authentication operations.
 * Abstracts direct details of credential managers and Supabase clients.
 */
interface AuthRepository {
    /**
     * Flow emitting the current signed-in status of the user.
     */
    val isSignedIn: Flow<Boolean>

    /**
     * Flow emitting the current user's authenticated ID (auth.uid() equivalent) or null if unauthenticated.
     */
    val currentUserId: Flow<String?>

    /**
     * Attempts to sign in using Google Auth.
     * @param context Context needed for Android Credential Manager bottom sheet.
     * @return Result containing the unique authenticated ID (auth.uid) on success.
     */
    suspend fun signInWithGoogle(context: Context): Result<String>

    /**
     * Restores a previously active session on app launch.
     * @return Result containing the unique authenticated ID (auth.uid) on success.
     */
    suspend fun restoreSession(): Result<String>

    /**
     * Performs a clean logout from the active session.
     */
    suspend fun signOut(): Result<Unit>
}
