package com.example.t1.data.remote

import android.content.Context

/**
 * Abstraction layer for authentication providers (e.g. Google Sign-In, etc.).
 * Allows adding new authentication methods without changing domain repositories or ViewModels.
 */
interface AuthProvider {
    /**
     * Initiates authentication using the provider.
     * @param context Context needed to invoke Credential Manager.
     * @return Result containing the unique user ID (auth.uid) on success.
     */
    suspend fun signIn(context: Context): Result<String>

    /**
     * Performs sign-out from the provider.
     */
    suspend fun signOut(): Result<Unit>
}
