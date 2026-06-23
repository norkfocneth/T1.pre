package com.example.t1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t1.domain.model.LeaderboardEntry
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.LeaderboardRepository
import com.example.t1.domain.repository.UserRepository
import com.example.t1.domain.usecase.SignOutUseCase
import com.example.t1.domain.usecase.SyncOfflineDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val syncOfflineDataUseCase: SyncOfflineDataUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    val isSignedIn: StateFlow<Boolean> = authRepository.isSignedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentUserId: StateFlow<String?> = authRepository.currentUserId
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val onboardingCompleted: StateFlow<Boolean> = userRepository.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userProfile: StateFlow<UserProfile?> = userRepository.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val cachedFocusScore: StateFlow<Int> = userRepository.cachedFocusScore
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 50)

    private val _leaderboardState = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardState: StateFlow<List<LeaderboardEntry>> = _leaderboardState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        // Automatically sync pending edits when viewmodel initializes (e.g. app launch)
        syncOfflineData()
        loadLeaderboard(forceRefresh = false)

        viewModelScope.launch {
            authRepository.currentUserId.collect { userId ->
                if (userId != null) {
                    userRepository.getUserProfile(userId, forceRefresh = false)
                }
            }
        }
    }

    fun syncOfflineData() {
        viewModelScope.launch {
            syncOfflineDataUseCase()
            // If sync succeeds, refresh local profile & leaderboard
            val userId = authRepository.currentUserId.first()
            if (userId != null) {
                userRepository.getUserProfile(userId, forceRefresh = true)
            }
            loadLeaderboard(forceRefresh = true)
        }
    }

    fun logFocusSession(durationSeconds: Long) {
        viewModelScope.launch {
            userRepository.saveFocusSession(durationSeconds)
            // Trigger a sync flush in the background
            syncOfflineDataUseCase()
        }
    }

    fun loadLeaderboard(forceRefresh: Boolean) {
        _isRefreshing.value = true
        viewModelScope.launch {
            leaderboardRepository.getLeaderboard(forceRefresh)
                .onSuccess { entries ->
                    _leaderboardState.value = entries
                }
            _isRefreshing.value = false
        }
    }

    fun updateDisplayName(displayName: String) {
        viewModelScope.launch {
            val profileVal = userProfile.value
            if (profileVal != null) {
                userRepository.saveOnboardingProfile(
                    userId = profileVal.id,
                    username = profileVal.username,
                    displayName = displayName,
                    focusScore = profileVal.focusScore
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }
}
