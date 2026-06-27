package com.example.t1.data.repository

import android.content.Context
import com.example.t1.data.remote.AuthProvider
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.util.T1Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of AuthRepository.
 * Manages OAuth sessions via GoogleAuthProvider and maps Supabase session status flows.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val authProvider: AuthProvider
) : AuthRepository {

    private val auth = supabaseClient.auth

    override val isSignedIn: Flow<Boolean> = auth.sessionStatus.map { status ->
        status is SessionStatus.Authenticated
    }

    override val currentUserId: Flow<String?> = auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> status.session.user?.id
            else -> null
        }
    }

    override suspend fun signInWithGoogle(context: Context): Result<String> {
        T1Logger.i("Sign-In with Google triggered in AuthRepository")
        return authProvider.signIn(context)
    }

    override suspend fun restoreSession(): Result<String> {
        T1Logger.i("Restoring active user session from local Supabase memory")
        return try {
            // Attempt to load existing session from local storage or trigger a token refresh
            val currentSession = auth.currentSessionOrNull()
            if (currentSession != null) {
                val userId = currentSession.user?.id
                if (userId != null) {
                    T1Logger.i("Session successfully restored.")
                    Result.success(userId)
                } else {
                    T1Logger.e("Restored session has null user ID")
                    Result.failure(Exception("Session contains no user data"))
                }
            } else {
                T1Logger.i("No active session found during restore")
                Result.failure(Exception("No session available"))
            }
        } catch (e: Exception) {
            T1Logger.e("Error attempting to restore session", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        T1Logger.i("Sign-Out triggered in AuthRepository")
        return authProvider.signOut()
    }
}
