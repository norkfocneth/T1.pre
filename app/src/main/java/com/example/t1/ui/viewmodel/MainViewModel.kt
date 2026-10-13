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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val focusSessionDao: FocusSessionDao,
    private val userPreferences: com.example.t1.data.preferences.UserPreferences,
    private val t1NotificationManager: com.example.t1.notification.T1NotificationManager,
    private val tokenRepository: com.example.t1.domain.repository.TokenRepository
) : ViewModel() {

    val notificationsEnabled: StateFlow<Boolean> = userPreferences.notificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val focusRemindersEnabled: StateFlow<Boolean> = userPreferences.focusRemindersEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val tutorialCompleted: StateFlow<Boolean> = userPreferences.tutorialCompletedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val tutorialVersion: StateFlow<Int> = userPreferences.tutorialVersionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val selectedExam: StateFlow<String> = userPreferences.selectedExamFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.t1.data.preferences.UserPreferences.DEFAULT_EXAM)

    val examChangeCount: StateFlow<Int> = userPreferences.examChangeCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val examLastChangedEpoch: StateFlow<Long> = userPreferences.examLastChangedEpochFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val privacyGhostMode: StateFlow<Boolean> = userPreferences.privacyGhostModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val goalLastChangedEpoch: StateFlow<Long> = userPreferences.goalLastChangedEpochFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.saveNotificationsEnabled(enabled)
        }
    }

    fun setFocusRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.saveFocusRemindersEnabled(enabled)
            if (enabled) {
                com.example.t1.notification.T1NotificationScheduler.schedule(context)
            }
        }
    }

    fun setPrivacyGhostMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.savePrivacyGhostMode(enabled)
        }
    }

    fun updatePrimaryGoal(newGoal: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val sevenDaysMs = 7 * 24 * 60 * 60 * 1000L
            val lastEpoch = goalLastChangedEpoch.value

            if (lastEpoch > 0L && now - lastEpoch < sevenDaysMs) {
                val remainingMs = (lastEpoch + sevenDaysMs) - now
                val remainingDays = kotlin.math.max(1, (remainingMs / (24 * 60 * 60 * 1000L)).toInt())
                onResult(false, "Primary goal is locked. You can switch again in $remainingDays day${if (remainingDays > 1) "s" else ""}.")
                return@launch
            }

            val currentProfile = userProfile.value
            if (currentProfile != null) {
                val updatedProfile = currentProfile.copy(primaryGoal = newGoal)
                val result = userRepository.saveProfile(updatedProfile)
                if (result.isSuccess) {
                    userPreferences.saveGoalLastChangedEpoch(now)
                    refreshBehaviourData()
                    onResult(true, "Switched primary goal to ${newGoal.lowercase().replaceFirstChar { it.uppercase() }}.")
                } else {
                    onResult(false, "Could not update primary goal.")
                }
            } else {
                onResult(false, "User profile not found.")
            }
        }
    }

    fun selectExamTrack(exam: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val lastEpoch = examLastChangedEpoch.value
            val sevenDaysMs = 7 * 24 * 60 * 60 * 1000L
            val currentCount = if (now - lastEpoch > sevenDaysMs) 0 else examChangeCount.value

            if (currentCount >= 2) {
                val remainingMs = (lastEpoch + sevenDaysMs) - now
                val remainingDays = kotlin.math.max(1, (remainingMs / (24 * 60 * 60 * 1000L)).toInt())
                onResult(false, "Exam track locked. You can switch in $remainingDays day${if (remainingDays > 1) "s" else ""}.")
            } else {
                val ok = userPreferences.saveSelectedExam(exam)
                if (ok) {
                    onResult(true, "Switched to $exam track.")
                } else {
                    onResult(false, "Could not switch exam track.")
                }
            }
        }
    }

    fun completeTutorial(version: Int = com.example.t1.data.preferences.UserPreferences.CURRENT_TUTORIAL_VERSION) {
        viewModelScope.launch {
            userPreferences.saveTutorialCompleted(true, version)
        }
    }

    fun resetTutorial() {
        viewModelScope.launch {
            userPreferences.resetTutorial()
        }
    }

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

    private val _tokenWallet = MutableStateFlow<com.example.t1.data.database.entity.TokenWalletEntity?>(null)
    val tokenWallet: StateFlow<com.example.t1.data.database.entity.TokenWalletEntity?> = _tokenWallet.asStateFlow()

    private val _tokenTransactions = MutableStateFlow<List<com.example.t1.data.database.entity.TokenTransactionEntity>>(emptyList())
    val tokenTransactions: StateFlow<List<com.example.t1.data.database.entity.TokenTransactionEntity>> = _tokenTransactions.asStateFlow()

    private val _tokenEarnedEvent = MutableSharedFlow<Int>()
    val tokenEarnedEvent: SharedFlow<Int> = _tokenEarnedEvent.asSharedFlow()

    private val _weeklyTrend = MutableStateFlow<List<Int>>(List(7) { 0 })
    val weeklyTrend: StateFlow<List<Int>> = _weeklyTrend.asStateFlow()

    private val _dashboardState = MutableStateFlow(DashboardUiState())
    val dashboardState: StateFlow<DashboardUiState> = _dashboardState.asStateFlow()

    init {
        viewModelScope.launch {
            // Setup dynamic wallet sync on profile changes
            userProfile.collectLatest { profile ->
                if (profile != null) {
                    // Sync wallet with Supabase
                    launch {
                        tokenRepository.syncWallet(profile.id)
                    }

                    // Observe wallet cache
                    launch {
                        tokenRepository.getWalletFlow(profile.id).collect { wallet ->
                            _tokenWallet.value = wallet
                        }
                    }

                    // Observe transactions cache
                    launch {
                        tokenRepository.getTransactionsFlow(profile.id).collect { txs ->
                            _tokenTransactions.value = txs
                        }
                    }

                    // Trigger daily login reward check
                    launch {
                        triggerDailyLoginReward(profile.id)
                    }

                    // Silent migration to Focus Score V2 in the background
                    if (profile.focusScoreVersion < com.example.t1.domain.focus.FocusScoreEngine.ENGINE_VERSION || !profile.onboardingCompletedV2) {
                        launch {
                            val updatedProfile = profile.copy(
                                focusScoreVersion = com.example.t1.domain.focus.FocusScoreEngine.ENGINE_VERSION,
                                onboardingCompletedV2 = true
                            )
                            userRepository.saveProfile(updatedProfile)
                            refreshBehaviourData()
                        }
                    }
                } else {
                    _tokenWallet.value = null
                    _tokenTransactions.value = emptyList()
                }
            }
        }
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
            val yesterdayResult = behaviourRepository.getYesterdayBehaviour()
            val yesterdayBehaviour = yesterdayResult.getOrNull()
            if (result.isSuccess) {
                val behaviour = result.getOrNull()

                // Calculate category times dynamically using behaviour category totals and AppCategoryRepository
                val categoryTimes = mutableMapOf(
                    "Productivity" to (behaviour?.productiveTimeMs ?: 0L),
                    "Education" to (behaviour?.educationTimeMs ?: 0L),
                    "Communication" to (behaviour?.communicationTimeMs ?: 0L),
                    "Social" to (behaviour?.socialTimeMs ?: 0L),
                    "Entertainment" to (behaviour?.entertainmentTimeMs ?: 0L),
                    "Utility" to (behaviour?.otherTimeMs ?: 0L)
                )
                behaviour?.topUsedApps?.forEach { app ->
                    val cat = appCategoryRepository.getCategory(app.packageName)
                    val catName = when (cat) {
                        EngineCategory.PRODUCTIVITY -> "Productivity"
                        EngineCategory.EDUCATION -> "Education"
                        EngineCategory.COMMUNICATION -> "Communication"
                        EngineCategory.SOCIAL -> "Social"
                        EngineCategory.ENTERTAINMENT -> "Entertainment"
                        EngineCategory.UTILITY -> "Utility"
                    }
                    if ((categoryTimes[catName] ?: 0L) == 0L) {
                        categoryTimes[catName] = (categoryTimes[catName] ?: 0L) + app.usageDurationMs
                    }
                }

                // Calculate yesterday's category times dynamically for accurate Today vs Yesterday baseline comparison
                val yesterdayCategoryTimes = mutableMapOf(
                    "Productivity" to (yesterdayBehaviour?.productiveTimeMs ?: 0L),
                    "Education" to (yesterdayBehaviour?.educationTimeMs ?: 0L),
                    "Communication" to (yesterdayBehaviour?.communicationTimeMs ?: 0L),
                    "Social" to (yesterdayBehaviour?.socialTimeMs ?: 0L),
                    "Entertainment" to (yesterdayBehaviour?.entertainmentTimeMs ?: 0L),
                    "Utility" to (yesterdayBehaviour?.otherTimeMs ?: 0L)
                )
                yesterdayBehaviour?.topUsedApps?.forEach { app ->
                    val cat = appCategoryRepository.getCategory(app.packageName)
                    val catName = when (cat) {
                        EngineCategory.PRODUCTIVITY -> "Productivity"
                        EngineCategory.EDUCATION -> "Education"
                        EngineCategory.COMMUNICATION -> "Communication"
                        EngineCategory.SOCIAL -> "Social"
                        EngineCategory.ENTERTAINMENT -> "Entertainment"
                        EngineCategory.UTILITY -> "Utility"
                    }
                    if ((yesterdayCategoryTimes[catName] ?: 0L) == 0L) {
                        yesterdayCategoryTimes[catName] = (yesterdayCategoryTimes[catName] ?: 0L) + app.usageDurationMs
                    }
                }

                val primaryGoal = userProfile.value?.primaryGoal ?: "PRODUCTIVITY"
                val baselineMs = when (primaryGoal.uppercase()) {
                    "PRODUCTIVITY" -> yesterdayCategoryTimes["Productivity"] ?: 0L
                    "EDUCATION" -> yesterdayCategoryTimes["Education"] ?: 0L
                    "SOCIAL" -> yesterdayCategoryTimes["Social"] ?: 0L
                    "ENTERTAINMENT" -> yesterdayCategoryTimes["Entertainment"] ?: 0L
                    "COMMUNICATION" -> yesterdayCategoryTimes["Communication"] ?: 0L
                    else -> yesterdayCategoryTimes["Productivity"] ?: 0L
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
                        yesterdayBehaviour = yesterdayBehaviour,
                        lastUpdated = System.currentTimeMillis(),
                        error = null,
                        currentFocusScore = score,
                        behaviourScore = currentScore?.behaviourScore ?: it.behaviourScore,
                        confidence = currentScore?.confidence ?: it.confidence,
                        trend = currentScore?.trend ?: it.trend,
                        timeSaved = currentScore?.timeSaved ?: it.timeSaved,
                        percentile = pct,
                        categoryTimes = categoryTimes,
                        yesterdayCategoryTimes = yesterdayCategoryTimes,
                        baselineGoalTimeMs = baselineMs
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
            val oldLeaderboard = _leaderboardState.value
            val result = leaderboardRepository.getLeaderboard(forceRefresh)
            if (result.isSuccess) {
                val newLeaderboard = result.getOrDefault(emptyList())
                _leaderboardState.value = newLeaderboard

                val profileVal = userProfile.value
                if (profileVal != null && oldLeaderboard.isNotEmpty()) {
                    val oldIndex = oldLeaderboard.indexOfFirst { it.username == profileVal.username }
                    val newIndex = newLeaderboard.indexOfFirst { it.username == profileVal.username }
                    if (oldIndex != -1 && newIndex != -1 && oldIndex != newIndex) {
                        val oldRank = oldIndex + 1
                        val newRank = newIndex + 1
                        if (newRank < oldRank) {
                            t1NotificationManager.postNotification(
                                id = 3001,
                                title = "Rank Improved",
                                text = "You moved up ${oldRank - newRank} places on the leaderboard. Keep going.",
                                channelId = com.example.t1.notification.T1NotificationManager.CHANNEL_IMPORTANT,
                                type = "rank_improved"
                            )
                        } else if (newRank > oldRank) {
                            t1NotificationManager.postNotification(
                                id = 3002,
                                title = "Rank Dropped",
                                text = "You dropped ${newRank - oldRank} places today. One good day can recover it.",
                                channelId = com.example.t1.notification.T1NotificationManager.CHANNEL_IMPORTANT,
                                type = "rank_down"
                            )
                        }
                    }
                }
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

                // 4. Trigger token earned and milestone notifications
                val earnResult = tokenRepository.addTransaction(
                    userId = profile.id,
                    amount = 5,
                    type = "EARN",
                    reason = "FOCUS_SESSION"
                )
                if (earnResult.isSuccess) {
                    _tokenEarnedEvent.emit(5)
                    t1NotificationManager.postNotification(
                        id = 3003,
                        title = "Tokens Earned",
                        text = "+5 T1 Tokens earned. Reward added to your wallet.",
                        channelId = com.example.t1.notification.T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "token_reward"
                    )
                } else {
                    T1Logger.w("Focus session token reward skipped: ${earnResult.exceptionOrNull()?.message}")
                }

                if (totalSessions == 10 || totalSessions == 25 || totalSessions == 50 || totalSessions == 100) {
                    t1NotificationManager.postNotification(
                        id = 3004,
                        title = "Milestone Achieved",
                        text = "Congratulations. $totalSessions Focus Sessions completed.",
                        channelId = com.example.t1.notification.T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "milestone"
                    )
                }
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

    /**
     * Deletes the user account and signs out.
     */
    fun deleteUserAccount(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            val profile = userProfile.value
            if (profile == null) {
                onFailure("No active user session found.")
                return@launch
            }
            T1Logger.i("Delete user account triggered for user ID: ${profile.id}")
            val deleteResult = userRepository.deleteProfile(profile.id)
            if (deleteResult.isSuccess) {
                tokenRepository.clearCache()
                _tokenWallet.value = null
                _tokenTransactions.value = emptyList()
                T1Logger.i("Profile and tokens deleted successfully. Performing final sign-out.")
                val signOutResult = sessionManager.performSignOut()
                if (signOutResult.isSuccess) {
                    onSuccess()
                } else {
                    onFailure(signOutResult.exceptionOrNull()?.message ?: "Failed to sign out after deletion.")
                }
            } else {
                val errorMsg = deleteResult.exceptionOrNull()?.message ?: "Failed to delete user profile from server."
                T1Logger.e(errorMsg)
                onFailure(errorMsg)
            }
        }
    }

    private suspend fun triggerDailyLoginReward(userId: String) {
        val result = tokenRepository.addTransaction(
            userId = userId,
            amount = 5,
            type = "EARN",
            reason = "DAILY_LOGIN"
        )
        if (result.isSuccess) {
            _tokenEarnedEvent.emit(5)
            t1NotificationManager.postNotification(
                id = 3005,
                title = "Daily Login Reward",
                text = "You earned +5 T1 Tokens for logging in today!",
                channelId = com.example.t1.notification.T1NotificationManager.CHANNEL_IMPORTANT,
                type = "token_reward"
            )
        }
    }

    // Reward Simulation Functions for Developer/User testing
    fun earnDailyMission() {
        triggerSimulatedReward("DAILY_MISSION", 15)
    }

    fun earnWeeklyMission() {
        triggerSimulatedReward("WEEKLY_MISSION", 100)
    }

    fun earnMonthlyChallenge() {
        triggerSimulatedReward("MONTHLY_CHALLENGE", 500)
    }

    fun earnPersonalBest() {
        triggerSimulatedReward("PERSONAL_BEST", 30)
    }

    fun earnBeatYesterday() {
        triggerSimulatedReward("BEAT_YESTERDAY", 10)
    }

    fun earnWeeklyAverage() {
        triggerSimulatedReward("WEEKLY_AVERAGE", 40)
    }

    fun earnStreak7() {
        triggerSimulatedReward("STREAK_7", 70)
    }

    fun earnStreak30() {
        triggerSimulatedReward("STREAK_30", 300)
    }

    fun earnHiddenAchievement() {
        triggerSimulatedReward("HIDDEN_ACHIEVEMENT", 100)
    }

    fun earnBadgeUnlock() {
        triggerSimulatedReward("BADGE_UNLOCK", 50)
    }

    fun earnLeaderboardPromotion() {
        triggerSimulatedReward("LEADERBOARD_PROMOTION", 25)
    }

    fun earnCollegeTop10() {
        triggerSimulatedReward("COLLEGE_TOP_10", 200)
    }

    fun earnGlobalTop100() {
        triggerSimulatedReward("GLOBAL_TOP_100", 1000)
    }

    fun triggerSimulatedReward(reason: String, amount: Int) {
        viewModelScope.launch {
            val profile = userProfile.value
            if (profile != null) {
                val result = tokenRepository.addTransaction(
                    userId = profile.id,
                    amount = amount,
                    type = "EARN",
                    reason = reason
                )
                if (result.isSuccess) {
                    _tokenEarnedEvent.emit(amount)
                    t1NotificationManager.postNotification(
                        id = 3006,
                        title = "Tokens Earned",
                        text = "+$amount T1 Tokens earned for $reason!",
                        channelId = com.example.t1.notification.T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "token_reward"
                    )
                } else {
                    T1Logger.w("Simulated reward skipped: ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }
}


