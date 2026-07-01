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
import com.example.t1.data.database.dao.FocusSessionDao
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
    private val leaderboardRepository: LeaderboardRepository,
    private val focusSessionDao: FocusSessionDao
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

    private val _totalFocusSessions = MutableStateFlow(0)
    val totalFocusSessions: StateFlow<Int> = _totalFocusSessions.asStateFlow()

    private val _totalFocusDuration = MutableStateFlow(0L)
    val totalFocusDuration: StateFlow<Long> = _totalFocusDuration.asStateFlow()

    private val _weeklyTrend = MutableStateFlow<List<Int>>(List(7) { 0 })
    val weeklyTrend: StateFlow<List<Int>> = _weeklyTrend.asStateFlow()

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
                    
                    launch {
                        focusScoreRepository.syncWithCloud()
                    }
                    launch {
                        focusSessionDao.getTotalFocusSessionCountFlow(profile.id).collect {
                            _totalFocusSessions.value = it
                        }
                    }
                    launch {
                        focusSessionDao.getTotalFocusDurationFlow(profile.id).collect {
                            _totalFocusDuration.value = it ?: 0L
                        }
                    }
                    launch {
                        focusScoreRepository.getScoreHistoryFlow(profile.id).collect { history ->
                            val today = java.time.LocalDate.now()
                            val monday = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                            val historyMap = history.associateBy { it.date }
                            
                            val trend = List(7) { i ->
                                val date = monday.plusDays(i.toLong())
                                if (date.isAfter(today)) {
                                    0
                                } else if (date == today) {
                                    profile.focusScore
                                } else {
                                    historyMap[date.toString()]?.finalFocusScore ?: 0
                                }
                            }
                            _weeklyTrend.value = trend
                        }
                    }
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
            userProfile.collectLatest { profile ->
                if (profile != null) {
                    val result = leaderboardRepository.getTemporaryRank(profile.id, profile.focusScore)
                    if (result.isSuccess) {
                        _temporaryRank.value = result.getOrNull()
                    }
                }
            }
        }
    }

    /**
     * Updates display name in local cache and remote (if online).
     */
    fun updateDisplayName(displayName: String, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val currentProfile = userProfile.value
            if (currentProfile != null) {
                val resolvedName = displayName.trim().takeIf { it.isNotEmpty() }
                val updatedProfile = currentProfile.copy(displayName = resolvedName)
                val result = userRepository.saveProfile(updatedProfile)
                if (result.isSuccess) {
                    T1Logger.i("Updated display name to: $resolvedName")
                    onResult?.invoke(true)
                } else {
                    T1Logger.e("Failed to sync updated display name remotely", result.exceptionOrNull())
                    onResult?.invoke(false)
                }
            } else {
                onResult?.invoke(false)
            }
        }
    }

    /**
     * Logs the completed focus session to the local database,
     * updates the profile's total sessions count, and upserts it.
     */
    fun logFocusSession(durationSeconds: Long) {
        viewModelScope.launch {
            val profile = userProfile.value
            if (profile != null) {
                T1Logger.i("Logging focus session: $durationSeconds seconds for user ${profile.id}")
                
                // 1. Insert session to local database
                val session = com.example.t1.data.database.entity.FocusSessionEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = profile.id,
                    durationSeconds = durationSeconds,
                    timestamp = System.currentTimeMillis(),
                    synced = false
                )
                focusSessionDao.insertSession(session)
                
                // 2. Fetch updated total session count (duration >= 60 seconds)
                val totalSessions = focusSessionDao.getTotalFocusSessionCount(profile.id)
                
                // 3. Update profile entity with new session count and save/sync
                val updatedProfile = profile.copy(totalFocusSessions = totalSessions)
                userRepository.saveProfile(updatedProfile)
                T1Logger.i("Focus session logged successfully. Total sessions: $totalSessions")
            }
        }
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

