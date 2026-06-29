package com.example.t1.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t1.domain.SessionManager
import com.example.t1.domain.model.LeaderboardEntry
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.permission.UsagePermissionManager
import com.example.t1.domain.permission.UsagePermissionState
import com.example.t1.domain.repository.LeaderboardRepository
import com.example.t1.domain.repository.UserRepository
import com.example.t1.domain.repository.BehaviourRepository
import com.example.t1.domain.repository.FocusScoreRepository
import com.example.t1.domain.repository.ResearchBenchmarkRepository
import com.example.t1.domain.repository.AppCategoryRepository
import com.example.t1.domain.repository.EngineCategory
import com.example.t1.util.T1Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MainViewModel bridging current cached profile state, scores, and dashboard triggers.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val usagePermissionManager: UsagePermissionManager,
    private val behaviourRepository: BehaviourRepository,
    private val focusScoreRepository: FocusScoreRepository,
    private val researchBenchmarkRepository: ResearchBenchmarkRepository,
    private val appCategoryRepository: AppCategoryRepository,
    private val leaderboardRepository: LeaderboardRepository
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

    private val _dashboardState = MutableStateFlow(DashboardUiState())
    val dashboardState: StateFlow<DashboardUiState> = _dashboardState.asStateFlow()

    init {
        viewModelScope.launch {
            // Observe permission state reactively
            usagePermissionManager.observePermissionState(context).collectLatest { permState ->
                _dashboardState.update { it.copy(permissionState = permState) }
                if (permState is UsagePermissionState.Granted) {
                    refreshBehaviourData()
                } else {
                    _dashboardState.update { it.copy(collectionStatus = CollectionStatus.Idle) }
                }
            }
        }

        viewModelScope.launch {
            userProfile.collectLatest { profile ->
                if (profile != null) {
                    loadLatestCalculatedScore(profile.focusScore)
                    refreshLeaderboard(forceRefresh = false)
                }
            }
        }
    }

    private fun loadLatestCalculatedScore(defaultScore: Int) {
        viewModelScope.launch {
            val todayStr = java.time.LocalDate.now().toString()
            val scoreEntity = focusScoreRepository.getScoreForDate(todayStr)
                ?: focusScoreRepository.getScoreHistory().lastOrNull()
            
            if (scoreEntity != null) {
                val score = scoreEntity.finalFocusScore
                val pct = researchBenchmarkRepository.getPercentile(score)
                _dashboardState.update {
                    it.copy(
                        currentFocusScore = score,
                        behaviourScore = scoreEntity.behaviourScore,
                        confidence = scoreEntity.confidence,
                        trend = scoreEntity.trend,
                        timeSaved = scoreEntity.timeSaved,
                        percentile = pct
                    )
                }
            } else {
                val pct = researchBenchmarkRepository.getPercentile(defaultScore)
                _dashboardState.update {
                    it.copy(
                        currentFocusScore = defaultScore,
                        behaviourScore = 0,
                        confidence = 0,
                        trend = "Stable",
                        timeSaved = 0L,
                        percentile = pct
                    )
                }
            }
        }
    }

    fun refreshBehaviourData() {
        viewModelScope.launch {
            val perm = _dashboardState.value.permissionState
            if (perm !is UsagePermissionState.Granted) {
                _dashboardState.update { it.copy(collectionStatus = CollectionStatus.Idle) }
                return@launch
            }
            _dashboardState.update { it.copy(isLoading = true, collectionStatus = CollectionStatus.Collecting) }
            val result = behaviourRepository.getTodayBehaviour()
            if (result.isSuccess) {
                val behaviour = result.getOrNull()

                // Calculate category times dynamically using AppCategoryRepository
                val categoryTimes = mutableMapOf(
                    "Productivity" to 0L,
                    "Education" to 0L,
                    "Social" to 0L,
                    "Entertainment" to 0L,
                    "Utility" to 0L
                )
                behaviour?.topUsedApps?.forEach { app ->
                    val cat = appCategoryRepository.getCategory(app.packageName)
                    val catName = when (cat) {
                        EngineCategory.PRODUCTIVITY -> "Productivity"
                        EngineCategory.EDUCATION -> "Education"
                        EngineCategory.SOCIAL -> "Social"
                        EngineCategory.ENTERTAINMENT -> "Entertainment"
                        EngineCategory.UTILITY -> "Utility"
                    }
                    categoryTimes[catName] = (categoryTimes[catName] ?: 0L) + app.usageDurationMs
                }

                // Calculate today's focus score
                val todayStr = java.time.LocalDate.now().toString()
                val scoreCalcResult = focusScoreRepository.calculateAndSaveFocusScore(todayStr)
                val currentScore = scoreCalcResult.getOrNull()
                val score = currentScore?.finalFocusScore ?: _dashboardState.value.currentFocusScore
                val pct = researchBenchmarkRepository.getPercentile(score)

                _dashboardState.update {
                    it.copy(
                        isLoading = false,
                        collectionStatus = CollectionStatus.Success,
                        todayBehaviour = behaviour,
                        lastUpdated = System.currentTimeMillis(),
                        error = null,
                        currentFocusScore = score,
                        behaviourScore = currentScore?.behaviourScore ?: it.behaviourScore,
                        confidence = currentScore?.confidence ?: it.confidence,
                        trend = currentScore?.trend ?: it.trend,
                        timeSaved = currentScore?.timeSaved ?: it.timeSaved,
                        percentile = pct,
                        categoryTimes = categoryTimes
                    )
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to collect behavior stats"
                _dashboardState.update {
                    it.copy(
                        isLoading = false,
                        collectionStatus = CollectionStatus.Failed(errorMsg),
                        error = errorMsg
                    )
                }
            }
        }
    }

    fun openPermissionSettings() {
        usagePermissionManager.openPermissionSettings(context)
    }

    private val _leaderboardState = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardState: StateFlow<List<LeaderboardEntry>> = _leaderboardState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refreshLeaderboard(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isRefreshing.value = true
            val result = leaderboardRepository.getLeaderboard(forceRefresh)
            if (result.isSuccess) {
                _leaderboardState.value = result.getOrDefault(emptyList())
            } else {
                T1Logger.e("Failed to refresh leaderboard", result.exceptionOrNull())
            }
            _isRefreshing.value = false
        }
    }

    private val _temporaryRank = MutableStateFlow<Int?>(null)
    val temporaryRank: StateFlow<Int?> = _temporaryRank.asStateFlow()

    fun loadTemporaryRank() {
        viewModelScope.launch {
            val profile = userProfile.value
            if (profile != null) {
                val result = leaderboardRepository.getTemporaryRank(profile.id, profile.focusScore)
                if (result.isSuccess) {
                    _temporaryRank.value = result.getOrNull()
                }
            }
        }
    }

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

