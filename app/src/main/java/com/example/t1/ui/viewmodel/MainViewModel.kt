package com.example.t1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t1.domain.SessionManager
import com.example.t1.domain.model.LeaderboardEntry
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.UserRepository
import com.example.t1.util.T1Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MainViewModel bridging current cached profile state, scores, and dashboard triggers.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    /**
     * Flow of the currently logged-in user profile, sourced from Room.
     */
    val userProfile: StateFlow<UserProfile?> = userRepository.cachedProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * Flow of the current user's cached focus score.
     */
    val cachedFocusScore: StateFlow<Int> = userProfile.map { profile ->
        profile?.focusScore ?: 50
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 50)

    // Stubs for future Phase 2 leaderboard support
    private val _leaderboardState = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardState: StateFlow<List<LeaderboardEntry>> = _leaderboardState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    /**
     * Updates display name in local cache and remote (if online).
     */
    fun updateDisplayName(displayName: String) {
        viewModelScope.launch {
            val currentProfile = userProfile.value
            if (currentProfile != null) {
                val updatedProfile = currentProfile.copy(displayName = displayName)
                userRepository.saveCachedProfile(updatedProfile)
                T1Logger.i("Updated display name to: $displayName")
            }
        }
    }

    /**
     * Helper to log focus sessions (placeholder for future phases).
     */
    fun logFocusSession(durationSeconds: Long) {
        T1Logger.i("Focus session logged (duration: $durationSeconds s) - (Phase 2 feature)")
    }

    /**
     * Logs out using SessionManager.
     */
    fun signOut() {
        viewModelScope.launch {
            T1Logger.i("Sign-out triggered from dashboard")
            sessionManager.performSignOut()
        }
    }
}
