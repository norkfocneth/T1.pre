package com.example.t1.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t1.domain.SessionManager
import com.example.t1.domain.model.AuthError
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.util.T1Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * States representing the deterministic authentication machine.
 */
sealed interface AuthState {
    object CheckingSession : AuthState
    object Unauthenticated : AuthState
    object Authenticating : AuthState
    object Authenticated : AuthState
    object LoadingProfile : AuthState
    data class Dashboard(val profile: UserProfile) : AuthState
    object NavigateToOnboarding : AuthState
    object SigningOut : AuthState
    data class Error(val errorType: AuthError, val message: String) : AuthState
}

/**
 * ViewModel orchestrating authentication state machine, duplicate login prevention, and retries.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.CheckingSession)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Flag ensuring no concurrent authentication flows are triggered
    private var isAuthenticating = false

    init {
        restoreSession()
        
        // Listen to currentUserId changes to route users when they authenticate via Google OAuth redirect
        viewModelScope.launch {
            authRepository.currentUserId.collect { userId ->
                T1Logger.i("AuthViewModel observed currentUserId: $userId")
                if (userId != null) {
                    val currentState = _authState.value
                    T1Logger.i("Current AuthState is: $currentState")
                    if (currentState is AuthState.Unauthenticated || currentState is AuthState.Authenticating) {
                        T1Logger.i("Google OAuth authenticated successfully. Syncing profile for user ID: $userId")
                        _authState.value = AuthState.LoadingProfile
                        val syncResult = syncProfileWithRetry(userId)
                        if (syncResult.isSuccess) {
                            val profile = syncResult.getOrNull()
                            if (profile != null) {
                                T1Logger.i("OAuth profile sync success. Routing to Dashboard.")
                                _authState.value = AuthState.Dashboard(profile)
                            } else {
                                T1Logger.i("OAuth profile sync success but profile is null. Routing to Onboarding.")
                                _authState.value = AuthState.NavigateToOnboarding
                            }
                        } else {
                            val error = syncResult.exceptionOrNull()
                            T1Logger.e("Profile sync failed after OAuth redirect", error)
                            if (error is SecurityException) {
                                _authState.value = AuthState.Error(AuthError.SECURITY_FAILURE, error.message ?: "Security validation failed")
                            } else {
                                _authState.value = AuthState.Error(AuthError.TIMEOUT, "Unable to sync profile. Check connection.")
                            }
                        }
                    } else {
                        T1Logger.i("Ignoring userId emit because currentState $currentState is not Unauthenticated or Authenticating")
                    }
                }
            }
        }
    }

    /**
     * Attempts session restore on launch.
     */
    fun restoreSession() {
        _authState.value = AuthState.CheckingSession
        viewModelScope.launch {
            T1Logger.i("Application launch: Restoring session and profile")
            val result = sessionManager.restoreSessionAndProfile()
            if (result.isSuccess) {
                val profile = result.getOrNull()
                if (profile != null) {
                    T1Logger.i("Session and profile successfully restored. Routing to Dashboard.")
                    _authState.value = AuthState.Dashboard(profile)
                } else {
                    T1Logger.i("Active session found but user profile is missing. Routing to Onboarding.")
                    _authState.value = AuthState.NavigateToOnboarding
                }
            } else {
                val error = result.exceptionOrNull()
                T1Logger.w("Restore session failed: ${error?.message}")
                if (error is SecurityException) {
                    _authState.value = AuthState.Error(AuthError.SECURITY_FAILURE, error.message ?: "Security validation failed")
                } else {
                    // Check if it's due to no active session (normal unauthenticated state)
                    if (error?.message?.contains("No session", ignoreCase = true) == true) {
                        _authState.value = AuthState.Unauthenticated
                    } else {
                        _authState.value = AuthState.Error(AuthError.NETWORK_ERROR, error?.message ?: "Network error during restore")
                    }
                }
            }
        }
    }

    /**
     * Initiates Google authentication, exchanging tokens with Supabase and downloading profile.
     */
    fun signInWithGoogle(context: Context) {
        if (isAuthenticating) {
            T1Logger.w("Google OAuth flow call ignored: authentication is already in progress.")
            return
        }
        isAuthenticating = true
        _authState.value = AuthState.Authenticating

        viewModelScope.launch {
            try {
                T1Logger.i("Initiating Google OAuth browser flow")
                val loginResult = authRepository.signInWithGoogle(context)
                if (loginResult.isFailure) {
                    val exception = loginResult.exceptionOrNull()
                    T1Logger.e("Failed to launch Google OAuth browser flow", exception)
                    _authState.value = AuthState.Error(AuthError.SUPABASE_ERROR, exception?.message ?: "Sign-in failed")
                } else {
                    // Browser launched successfully. We return state to Unauthenticated so if the user 
                    // cancels the browser flow, they are not permanently stuck on the loading screen.
                    _authState.value = AuthState.Unauthenticated
                }
            } finally {
                isAuthenticating = false
            }
        }
    }

    /**
     * Retries profile download with a deterministic timeout and backoff.
     */
    private suspend fun syncProfileWithRetry(userId: String): Result<UserProfile?> {
        return try {
            withTimeout(15000L) { // strict 15 seconds timeout
                var lastError: Throwable? = null
                val retryDelays = listOf(0L, 3000L, 8000L) // Immediate, 3s, 8s

                for ((index, delay) in retryDelays.withIndex()) {
                    if (delay > 0) {
                        T1Logger.i("Retrying profile sync in ${delay / 1000} seconds (Attempt ${index + 1}/3)...")
                        kotlinx.coroutines.delay(delay)
                    }
                    val result = sessionManager.syncProfileAndCache(userId)
                    if (result.isSuccess) {
                        return@withTimeout result
                    }
                    lastError = result.exceptionOrNull()
                    T1Logger.w("Profile sync attempt ${index + 1} failed: ${lastError?.message}")
                }
                Result.failure(lastError ?: Exception("Profile sync failed after retries"))
            }
        } catch (e: Exception) {
            T1Logger.e("Profile sync timed out or cancelled", e)
            Result.failure(e)
        }
    }

    /**
     * Executes clean sign-out.
     */
    fun signOut() {
        _authState.value = AuthState.SigningOut
        viewModelScope.launch {
            T1Logger.i("Sign-out process started")
            sessionManager.performSignOut()
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Resets error state back to login screen.
     */
    fun resetErrorState() {
        _authState.value = AuthState.Unauthenticated
    }
}
