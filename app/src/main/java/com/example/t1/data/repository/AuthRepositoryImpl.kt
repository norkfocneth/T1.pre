package com.example.t1.data.repository

import com.example.t1.data.preferences.UserPreferences
import com.example.t1.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val userPreferences: UserPreferences
) : AuthRepository {

    private val auth = supabaseClient.auth

    override val isSignedIn: Flow<Boolean> = userPreferences.isSignedInFlow

    override val currentUserId: Flow<String?> = userPreferences.userIdFlow

    override suspend fun signUpWithEmail(email: String, password: String): Result<String> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val currentUser = auth.currentUserOrNull()
            if (currentUser != null) {
                userPreferences.saveUserSession(currentUser.id, null, null)
                Result.success(currentUser.id)
            } else {
                Result.failure(Exception("Registration succeeded, please check your email for verification."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val currentUser = auth.currentUserOrNull()
                ?: throw Exception("Failed to retrieve user after sign in")
            
            // Check if profile exists in Supabase
            // (The profiles table might already contain onboarding Completed true/false)
            userPreferences.saveUserSession(currentUser.id, null, null)
            Result.success(currentUser.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            userPreferences.clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            // Even if network sign out fails, we clear local preferences to log the user out
            userPreferences.clearSession()
            Result.success(Unit)
        }
    }
}
