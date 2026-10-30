package com.example.t1.ui.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t1.data.database.dao.FocusSessionDao
import com.example.t1.data.database.entity.FocusSessionEntity
import com.example.t1.data.remote.SupabaseService
import com.example.t1.data.remote.model.FocusFightDto
import com.example.t1.data.remote.model.ProfileDto
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.model.battle.BattleChallengeData
import com.example.t1.domain.model.battle.BattleHistoryRecord
import com.example.t1.domain.model.battle.BattleStep
import com.example.t1.domain.model.battle.BattleUser
import com.example.t1.domain.model.battle.ParticipantStatus
import com.example.t1.domain.repository.TokenRepository
import com.example.t1.domain.repository.UserRepository
import com.example.t1.notification.T1NotificationManager
import com.example.t1.util.T1Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class BattleUiState(
    val currentStep: BattleStep = BattleStep.DISCOVERY,
    val searchQuery: String = "",
    val isBatteryWhitelisted: Boolean = true,
    val oemManufacturer: String = "",
    val currentUser: BattleUser = BattleUser(
        id = "current_user",
        username = "you",
        displayName = "You",
        battlesCount = 0,
        winsCount = 0,
        lossesCount = 0,
        winRatePercent = 0,
        totalTokensWon = 0,
        bio = ""
    ),
    val allCommunityUsers: List<BattleUser> = emptyList(),
    val recentUsers: List<BattleUser> = emptyList(),
    val suggestedUsers: List<BattleUser> = emptyList(),
    val selectedUser: BattleUser? = null,
    val selectedUserFights: List<BattleHistoryRecord> = emptyList(),
    val selectedUserFightsLoading: Boolean = false,
    val incomingFight: FocusFightDto? = null,
    val incomingChallenger: BattleUser? = null,
    val activeFightId: String? = null,
    val isChallenger: Boolean = true,
    val selectedDurationMinutes: Int = 25,
    val selectedWagerTokens: Int = 25,
    val activeChallenge: BattleChallengeData? = null,
    val countdownNumber: Int = 3,
    val myRemainingSeconds: Int = 25 * 60,
    val myTotalSeconds: Int = 25 * 60,
    val myStatus: ParticipantStatus = ParticipantStatus.FOCUSED,
    val opponentRemainingSeconds: Int = 25 * 60,
    val opponentTotalSeconds: Int = 25 * 60,
    val opponentStatus: ParticipantStatus = ParticipantStatus.FOCUSED,
    val opponentPausedReason: String = "App minimized\nTimer paused",
    val isVictory: Boolean = true,
    val tokensWonAmount: Int = 50,
    val battleHistory: List<BattleHistoryRecord> = emptyList()
)

@HiltViewModel
class FocusBattleViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val supabaseService: SupabaseService,
    private val focusSessionDao: FocusSessionDao,
    private val tokenRepository: TokenRepository,
    private val t1NotificationManager: T1NotificationManager,
    private val batteryOptimizationDetector: com.example.t1.data.compat.BatteryOptimizationDetector,
    private val oemCompatibilityLayer: com.example.t1.data.compat.OemCompatibilityLayer
) : ViewModel() {

    private var currentProfile: UserProfile? = null
    private var allRemoteProfiles: List<ProfileDto> = emptyList()
    private var allRemoteFights: List<FocusFightDto> = emptyList()

    private val _uiState = MutableStateFlow(BattleUiState())
    val uiState: StateFlow<BattleUiState> = _uiState.asStateFlow()

    private var battleTimerJob: Job? = null
    private var countdownJob: Job? = null
    private var waitingAcceptanceJob: Job? = null
    private var pendingChallengesPollerJob: Job? = null

    init {
        observeCurrentUserProfile()
        loadRealCommunityProfilesAndFights()
        startPendingChallengesPoller()
        refreshBatteryOptimizationStatus()
    }

    private fun observeCurrentUserProfile() {
        viewModelScope.launch {
            userRepository.cachedProfileFlow.collect { profile ->
                if (profile != null) {
                    currentProfile = profile
                    updateCurrentUserStats()
                    rebuildCommunityLists()
                }
            }
        }
    }

    private fun updateCurrentUserStats() {
        val profile = currentProfile ?: return
        val userFights = allRemoteFights.filter {
            (it.challengerId == profile.id || it.opponentId == profile.id) &&
                    (it.status == "COMPLETED" || it.status == "SURRENDERED")
        }
        val battles = userFights.size
        val wins = userFights.count { it.winnerId == profile.id }
        val losses = battles - wins
        val winRate = if (battles > 0) (wins * 100 / battles) else 0
        val tokensWon = userFights.filter { it.winnerId == profile.id }.sumOf { it.wagerTokens * 2 }
        val bioText = profile.primaryGoal?.let { "Goal: $it • Streak: ${profile.streak}d" }
            ?: "Consistency: ${profile.streak}d • Top Focus"

        val battleUser = BattleUser(
            id = profile.id,
            username = profile.username,
            displayName = profile.displayName?.takeIf { it.isNotBlank() } ?: profile.username,
            avatarUrl = profile.avatarUrl,
            battlesCount = battles,
            winsCount = wins,
            lossesCount = losses,
            winRatePercent = winRate,
            totalTokensWon = tokensWon,
            bio = bioText
        )
        _uiState.update { it.copy(currentUser = battleUser) }
    }

    private fun loadRealCommunityProfilesAndFights() {
        viewModelScope.launch {
            try {
                val fights = supabaseService.getAllFocusFights()
                allRemoteFights = fights

                val profiles = supabaseService.getAllProfiles()
                if (profiles.isNotEmpty()) {
                    allRemoteProfiles = profiles
                    rebuildCommunityLists()
                }
                updateCurrentUserStats()
            } catch (e: Exception) {
                T1Logger.w("Failed to load community profiles/fights: ${e.message}")
            }
        }
    }

    private fun rebuildCommunityLists() {
        val currentUserId = currentProfile?.id
        val otherProfiles = if (currentUserId != null) {
            allRemoteProfiles.filter { it.id != currentUserId }
        } else {
            allRemoteProfiles
        }

        if (otherProfiles.isNotEmpty()) {
            val mapped = otherProfiles.map { it.toBattleUser(allRemoteFights) }
            val suggested = mapped.sortedByDescending { it.totalTokensWon }.take(8)
            val recent = mapped.filterNot { suggested.contains(it) }.take(10)
                .ifEmpty { mapped.take(6) }

            _uiState.update {
                it.copy(
                    allCommunityUsers = mapped,
                    suggestedUsers = suggested,
                    recentUsers = recent
                )
            }
        }
    }

    private fun ProfileDto.toBattleUser(fights: List<FocusFightDto>): BattleUser {
        val userFights = fights.filter {
            (it.challengerId == id || it.opponentId == id) &&
                    (it.status == "COMPLETED" || it.status == "SURRENDERED")
        }
        val battles = userFights.size
        val wins = userFights.count { it.winnerId == id }
        val losses = battles - wins
        val winRate = if (battles > 0) (wins * 100 / battles) else 0
        val tokens = userFights.filter { it.winnerId == id }.sumOf { it.wagerTokens * 2 }
        val bioText = primaryGoal?.let { "Goal: $it • Streak: ${streak}d" }
            ?: "Streak: ${streak}d • Tier: $performanceTier"

        return BattleUser(
            id = id,
            username = username,
            displayName = displayName?.takeIf { it.isNotBlank() } ?: username,
            avatarUrl = avatarUrl,
            battlesCount = battles,
            winsCount = wins,
            lossesCount = losses,
            winRatePercent = winRate,
            totalTokensWon = tokens,
            bio = bioText
        )
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectUser(user: BattleUser) {
        _uiState.update {
            it.copy(
                selectedUser = user,
                selectedUserFights = emptyList(),
                selectedUserFightsLoading = true,
                currentStep = BattleStep.USER_DETAIL
            )
        }

        // Fetch real fight history for this specific user
        viewModelScope.launch {
            try {
                val fights = supabaseService.getUserFocusFights(user.id)
                val completed = fights.filter { it.status == "COMPLETED" || it.status == "SURRENDERED" }
                val history = completed.mapNotNull { fight ->
                    val opponentId = if (fight.challengerId == user.id) fight.opponentId else fight.challengerId
                    val opponentProfile = allRemoteProfiles.firstOrNull { it.id == opponentId }
                    val oppUsername = opponentProfile?.username ?: "fighter"
                    val oppDisplayName = opponentProfile?.displayName?.takeIf { it.isNotBlank() } ?: oppUsername
                    val isWin = fight.winnerId == user.id
                    val tokensDelta = if (isWin) fight.wagerTokens * 2 else fight.wagerTokens
                    BattleHistoryRecord(
                        id = fight.id ?: UUID.randomUUID().toString(),
                        opponentUsername = oppUsername,
                        opponentDisplayName = oppDisplayName,
                        durationMinutes = fight.durationMinutes,
                        tokensDelta = tokensDelta,
                        isWin = isWin,
                        timeAgo = "Recently"
                    )
                }
                _uiState.update {
                    it.copy(
                        selectedUserFights = history,
                        selectedUserFightsLoading = false
                    )
                }
            } catch (e: Exception) {
                T1Logger.w("Failed to load user focus fights: ${e.message}")
                _uiState.update { it.copy(selectedUserFightsLoading = false) }
            }
        }
    }

    fun openChallengeConfig() {
        _uiState.update { it.copy(currentStep = BattleStep.CHALLENGE_CONFIG) }
    }

    fun setDuration(minutes: Int) {
        _uiState.update {
            it.copy(
                selectedDurationMinutes = minutes,
                myTotalSeconds = minutes * 60,
                myRemainingSeconds = minutes * 60,
                opponentTotalSeconds = minutes * 60,
                opponentRemainingSeconds = minutes * 60
            )
        }
    }

    fun setWager(tokens: Int) {
        _uiState.update { it.copy(selectedWagerTokens = tokens) }
    }

    fun sendChallenge() {
        val opponent = _uiState.value.selectedUser
            ?: _uiState.value.recentUsers.firstOrNull()
            ?: _uiState.value.currentUser

        val currentUserId = currentProfile?.id ?: _uiState.value.currentUser.id

        val challenge = BattleChallengeData(
            id = "chal_${System.currentTimeMillis()}",
            challenger = _uiState.value.currentUser,
            opponent = opponent,
            durationMinutes = _uiState.value.selectedDurationMinutes,
            wagerTokens = _uiState.value.selectedWagerTokens,
            potentialWinTokens = _uiState.value.selectedWagerTokens * 2
        )
        _uiState.update {
            it.copy(
                activeChallenge = challenge,
                currentStep = BattleStep.WAITING_ACCEPTANCE,
                isChallenger = true
            )
        }

        // Insert real challenge into Supabase
        viewModelScope.launch {
            try {
                val created = supabaseService.createFocusFight(
                    FocusFightDto(
                        challengerId = currentUserId,
                        opponentId = opponent.id,
                        durationMinutes = _uiState.value.selectedDurationMinutes,
                        wagerTokens = _uiState.value.selectedWagerTokens,
                        status = "PENDING"
                    )
                )

                if (created?.id != null) {
                    val fightId = created.id
                    _uiState.update { it.copy(activeFightId = fightId) }
                    startWaitingAcceptancePoller(fightId)
                } else {
                    T1Logger.w("Focus fight creation returned null ID, continuing with local challenge.")
                }
            } catch (e: Exception) {
                T1Logger.e("Failed to create focus fight in Supabase: ${e.message}", e)
            }
        }
    }

    private fun startWaitingAcceptancePoller(fightId: String) {
        waitingAcceptanceJob?.cancel()
        waitingAcceptanceJob = viewModelScope.launch {
            var waitSeconds = 0
            while (_uiState.value.currentStep == BattleStep.WAITING_ACCEPTANCE) {
                delay(2000)
                waitSeconds += 2
                if (waitSeconds >= 180) { // 3 minutes timeout
                    T1Logger.i("Challenge waiting acceptance timed out after 3 minutes. Expiring challenge.")
                    try {
                        supabaseService.updateFightStatus(fightId, "EXPIRED")
                    } catch (_: Exception) {}
                    _uiState.update { it.copy(currentStep = BattleStep.DISCOVERY, activeFightId = null) }
                    break
                }
                try {
                    val remoteFight = supabaseService.getFightById(fightId)
                    if (remoteFight != null) {
                        when (remoteFight.status) {
                            "ACCEPTED" -> {
                                T1Logger.i("Opponent ACCEPTED the focus fight! Starting countdown...")
                                startCountdown()
                                break
                            }
                            "DECLINED", "CANCELLED", "EXPIRED" -> {
                                T1Logger.i("Opponent ${remoteFight.status} the focus fight.")
                                _uiState.update { it.copy(currentStep = BattleStep.DISCOVERY, activeFightId = null) }
                                break
                            }
                        }
                    }
                } catch (e: Exception) {
                    T1Logger.w("Error polling fight acceptance: ${e.message}")
                }
            }
        }
    }

    fun cancelChallenge() {
        val fightId = _uiState.value.activeFightId
        waitingAcceptanceJob?.cancel()
        if (fightId != null) {
            viewModelScope.launch {
                try {
                    supabaseService.updateFightStatus(fightId, "CANCELLED")
                } catch (e: Exception) {
                    T1Logger.w("Failed to cancel fight: ${e.message}")
                }
            }
        }
        _uiState.update { it.copy(currentStep = BattleStep.DISCOVERY, activeFightId = null) }
    }

    private fun startPendingChallengesPoller() {
        pendingChallengesPollerJob?.cancel()
        pendingChallengesPollerJob = viewModelScope.launch {
            while (true) {
                delay(3500)
                val currentUserId = currentProfile?.id ?: continue
                if (_uiState.value.currentStep == BattleStep.DISCOVERY) {
                    try {
                        val pendingFight = supabaseService.getPendingFightForUser(currentUserId)
                        if (pendingFight != null && pendingFight.id != null) {
                            val fightId = pendingFight.id
                            // Check expiration
                            if (supabaseService.isFightExpired(pendingFight.createdAt, maxAgeMinutes = 3)) {
                                try {
                                    supabaseService.updateFightStatus(fightId, "EXPIRED")
                                } catch (_: Exception) {}
                                continue
                            }

                            // Check if dismissed
                            if (t1NotificationManager.isFightDismissed(fightId)) {
                                continue
                            }

                            if (fightId != _uiState.value.incomingFight?.id) {
                                val challengerProfile = allRemoteProfiles.firstOrNull { it.id == pendingFight.challengerId }
                                val challengerUser = challengerProfile?.toBattleUser(allRemoteFights) ?: BattleUser(
                                    id = pendingFight.challengerId,
                                    username = "focus_mate",
                                    displayName = "Focus Challenger"
                                )

                                _uiState.update {
                                    it.copy(
                                        incomingFight = pendingFight,
                                        incomingChallenger = challengerUser
                                    )
                                }

                                // Post real high-priority system notification with fightId and direct navigation
                                t1NotificationManager.postNotification(
                                    id = fightId.hashCode(),
                                    title = "⚔️ Focus Fight Challenge!",
                                    text = "@${challengerUser.username} challenged you to a ${pendingFight.durationMinutes}m focus duel!",
                                    channelId = T1NotificationManager.CHANNEL_BATTLE_CHALLENGES,
                                    type = "focus_fight_challenge",
                                    navigateTab = "BATTLE",
                                    fightId = fightId
                                )
                            }
                        } else if (_uiState.value.incomingFight != null) {
                            // Challenger cancelled or fight expired: auto-dismiss prompt
                            _uiState.update { it.copy(incomingFight = null, incomingChallenger = null) }
                        }
                    } catch (e: Exception) {
                        T1Logger.w("Error checking pending challenges: ${e.message}")
                    }
                }
            }
        }
    }

    fun openIncomingInviteView() {
        val fight = _uiState.value.incomingFight
        val challenger = _uiState.value.incomingChallenger
        if (fight != null && challenger != null) {
            _uiState.update {
                it.copy(
                    selectedUser = challenger,
                    selectedDurationMinutes = fight.durationMinutes,
                    selectedWagerTokens = fight.wagerTokens,
                    currentStep = BattleStep.INCOMING_INVITE
                )
            }
        }
    }

    fun acceptChallenge() {
        val fight = _uiState.value.incomingFight
        if (fight?.id != null) {
            val fightId = fight.id
            viewModelScope.launch {
                try {
                    // Check remote fight status and expiry to avoid ghost battles
                    val remote = supabaseService.getFightById(fightId)
                    if (remote == null || remote.status != "PENDING" || supabaseService.isFightExpired(remote.createdAt, maxAgeMinutes = 3)) {
                        T1Logger.w("Challenge no longer pending or has expired. Cannot accept.")
                        if (remote != null && remote.status == "PENDING") {
                            supabaseService.updateFightStatus(fightId, "EXPIRED")
                        }
                        _uiState.update {
                            it.copy(
                                incomingFight = null,
                                incomingChallenger = null,
                                currentStep = BattleStep.DISCOVERY
                            )
                        }
                        return@launch
                    }

                    _uiState.update {
                        it.copy(
                            activeFightId = fightId,
                            isChallenger = false,
                            selectedDurationMinutes = fight.durationMinutes,
                            selectedWagerTokens = fight.wagerTokens,
                            selectedUser = _uiState.value.incomingChallenger,
                            incomingFight = null
                        )
                    }
                    supabaseService.updateFightStatus(fightId, "ACCEPTED")
                    startCountdown()
                } catch (e: Exception) {
                    T1Logger.e("Error accepting fight: ${e.message}", e)
                    _uiState.update { it.copy(incomingFight = null, currentStep = BattleStep.DISCOVERY) }
                }
            }
        } else {
            startCountdown()
        }
    }


    fun refreshBatteryOptimizationStatus() {
        val isWhitelisted = batteryOptimizationDetector.isIgnoringBatteryOptimizations()
        val oem = oemCompatibilityLayer.getOemProfile().manufacturer
        _uiState.update {
            it.copy(
                isBatteryWhitelisted = isWhitelisted,
                oemManufacturer = oem
            )
        }
    }

    fun openBatterySettings(context: android.content.Context) {
        oemCompatibilityLayer.openOemBackgroundSettings(context)
        refreshBatteryOptimizationStatus()
    }

    fun autoAcceptFight(fightId: String) {
        T1Logger.i("FocusBattleViewModel: autoAcceptFight called for fightId: $fightId")
        viewModelScope.launch {
            try {
                val remote = supabaseService.getFightById(fightId)
                if (remote != null && remote.status == "PENDING" && !supabaseService.isFightExpired(remote.createdAt, maxAgeMinutes = 3)) {
                    val allProfiles = supabaseService.getAllProfiles()
                    val challenger = allProfiles.firstOrNull { it.id == remote.challengerId }?.toBattleUser(allRemoteFights)
                    _uiState.update {
                        it.copy(
                            activeFightId = fightId,
                            isChallenger = false,
                            selectedDurationMinutes = remote.durationMinutes,
                            selectedWagerTokens = remote.wagerTokens,
                            selectedUser = challenger,
                            incomingFight = null,
                            incomingChallenger = null
                        )
                    }
                    supabaseService.updateFightStatus(fightId, "ACCEPTED")
                    startCountdown()
                } else {
                    T1Logger.w("Auto-accept failed: fight $fightId expired or no longer pending.")
                    _uiState.update { it.copy(currentStep = BattleStep.DISCOVERY) }
                }
            } catch (e: Exception) {
                T1Logger.e("Error auto-accepting fight $fightId: ${e.message}", e)
            }
        }
    }

    fun declineChallenge() {
        val fight = _uiState.value.incomingFight
        if (fight?.id != null) {
            val fightId = fight.id
            t1NotificationManager.markFightDismissed(fightId)
            t1NotificationManager.cancelNotification(fightId.hashCode())
            viewModelScope.launch {
                try {
                    supabaseService.updateFightStatus(fightId, "DECLINED")
                } catch (e: Exception) {
                    T1Logger.w("Failed to decline fight: ${e.message}")
                }
            }
        }
        _uiState.update {
            it.copy(
                incomingFight = null,
                incomingChallenger = null,
                currentStep = BattleStep.DISCOVERY
            )
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        waitingAcceptanceJob?.cancel()
        _uiState.update {
            it.copy(
                currentStep = BattleStep.COUNTDOWN,
                countdownNumber = 3
            )
        }
        countdownJob = viewModelScope.launch {
            for (i in 3 downTo 1) {
                _uiState.update { it.copy(countdownNumber = i) }
                delay(1000)
            }
            startActiveBattle()
        }
    }

    fun onAppMinimized() {
        if (_uiState.value.currentStep == BattleStep.LIVE_ARENA && _uiState.value.myStatus != ParticipantStatus.PAUSED) {
            T1Logger.i("Focus Battle: App minimized. Pausing user focus timer immediately.")
            _uiState.update { it.copy(myStatus = ParticipantStatus.PAUSED) }
            val activeId = _uiState.value.activeFightId
            val isChallenger = _uiState.value.isChallenger
            if (activeId != null) {
                viewModelScope.launch {
                    try {
                        val pauseStatus = if (isChallenger) "PAUSED_CHALLENGER" else "PAUSED_OPPONENT"
                        supabaseService.updateFightStatus(activeId, pauseStatus)
                    } catch (e: Exception) {
                        T1Logger.w("Failed to notify Supabase of app pause: ${e.message}")
                    }
                }
            }
        }
    }

    fun onAppResumed() {
        if (_uiState.value.currentStep == BattleStep.LIVE_ARENA && _uiState.value.myStatus != ParticipantStatus.FOCUSED) {
            T1Logger.i("Focus Battle: App foregrounded. Resuming user focus timer.")
            _uiState.update { it.copy(myStatus = ParticipantStatus.FOCUSED) }
            val activeId = _uiState.value.activeFightId
            if (activeId != null) {
                viewModelScope.launch {
                    try {
                        supabaseService.updateFightStatus(activeId, "IN_PROGRESS")
                    } catch (e: Exception) {
                        T1Logger.w("Failed to notify Supabase of app resume: ${e.message}")
                    }
                }
            }
        }
    }

    private fun startActiveBattle() {
        val totalSecs = _uiState.value.selectedDurationMinutes * 60
        _uiState.update {
            it.copy(
                currentStep = BattleStep.LIVE_ARENA,
                myTotalSeconds = totalSecs,
                myRemainingSeconds = totalSecs,
                myStatus = ParticipantStatus.FOCUSED,
                opponentTotalSeconds = totalSecs,
                opponentRemainingSeconds = totalSecs,
                opponentStatus = ParticipantStatus.FOCUSED
            )
        }

        val activeFightId = _uiState.value.activeFightId
        if (activeFightId != null) {
            viewModelScope.launch {
                try {
                    supabaseService.updateFightStatus(activeFightId, "IN_PROGRESS")
                } catch (_: Exception) {}
            }
        }

        battleTimerJob?.cancel()
        battleTimerJob = viewModelScope.launch {
            var mySecs = totalSecs
            var oppSecs = totalSecs
            val activeId = _uiState.value.activeFightId
            val isChallenger = _uiState.value.isChallenger
            var syncTick = 0

            while (mySecs > 0) {
                delay(1000)
                syncTick++

                // 1. Smoothly decrement local player's timer if actively focused
                if (_uiState.value.myStatus == ParticipantStatus.FOCUSED) {
                    mySecs -= 1
                    _uiState.update { it.copy(myRemainingSeconds = mySecs) }
                }

                // 2. Smoothly decrement opponent's timer second-by-second in realtime (client-side prediction)
                if (_uiState.value.opponentStatus == ParticipantStatus.FOCUSED && oppSecs > 0) {
                    oppSecs -= 1
                    _uiState.update { it.copy(opponentRemainingSeconds = oppSecs) }
                }

                // 3. Sync with Supabase every 2 seconds if real fight
                if (activeId != null && syncTick % 2 == 0) {
                    val myElapsed = totalSecs - mySecs
                    try {
                        supabaseService.updateFightProgress(activeId, isChallenger, myElapsed)

                        val remoteFight = supabaseService.getFightById(activeId)
                        if (remoteFight != null) {
                            if (remoteFight.status == "SURRENDERED") {
                                T1Logger.i("Opponent surrendered! Winning battle.")
                                endBattleWithVictory()
                                break
                            }

                            // Check remote status of opponent accurately
                            val opponentIsPaused = if (isChallenger) {
                                remoteFight.status == "PAUSED_OPPONENT"
                            } else {
                                remoteFight.status == "PAUSED_CHALLENGER"
                            } || (remoteFight.status == "PAUSED" && _uiState.value.myStatus == ParticipantStatus.FOCUSED)

                            if (opponentIsPaused) {
                                if (_uiState.value.opponentStatus != ParticipantStatus.PAUSED) {
                                    _uiState.update {
                                        it.copy(
                                            opponentStatus = ParticipantStatus.PAUSED,
                                            opponentPausedReason = "App minimized • Timer paused"
                                        )
                                    }
                                }
                            } else if (remoteFight.status == "IN_PROGRESS" || remoteFight.status == "ACCEPTED") {
                                if (_uiState.value.opponentStatus == ParticipantStatus.PAUSED) {
                                    _uiState.update { it.copy(opponentStatus = ParticipantStatus.FOCUSED) }
                                }
                            }

                            // Reconcile opponent elapsed seconds smoothly without 3-second jumping
                            val oppElapsedRemote = if (isChallenger) remoteFight.opponentSeconds else remoteFight.challengerSeconds
                            if (oppElapsedRemote > 0) {
                                val expectedRemaining = (totalSecs - oppElapsedRemote).coerceAtLeast(0)
                                if (Math.abs(oppSecs - expectedRemaining) > 5) {
                                    oppSecs = expectedRemaining
                                    _uiState.update { it.copy(opponentRemainingSeconds = oppSecs) }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        T1Logger.w("Battle sync error: ${e.message}")
                    }
                }

                if (mySecs <= 0) {
                    endBattleWithVictory()
                    break
                }
            }
        }
    }

    fun surrender() {
        battleTimerJob?.cancel()
        val opponent = _uiState.value.selectedUser
            ?: _uiState.value.recentUsers.firstOrNull()
            ?: _uiState.value.currentUser
        val durationMins = _uiState.value.selectedDurationMinutes
        // EXACT FOCUS TIME SPENT BEFORE SURRENDER:
        val actualElapsedSeconds = (_uiState.value.myTotalSeconds - _uiState.value.myRemainingSeconds).coerceAtLeast(0).toLong()
        val activeId = _uiState.value.activeFightId

        _uiState.update {
            it.copy(
                currentStep = BattleStep.RESULT,
                isVictory = false,
                tokensWonAmount = 0
            )
        }

        if (activeId != null) {
            viewModelScope.launch {
                val oppElapsed = (_uiState.value.opponentTotalSeconds - _uiState.value.opponentRemainingSeconds).coerceAtLeast(0)
                try {
                    supabaseService.updateFightStatus(
                        fightId = activeId,
                        status = "SURRENDERED",
                        winnerId = opponent.id,
                        challengerSeconds = actualElapsedSeconds.toInt(),
                        opponentSeconds = oppElapsed
                    )
                } catch (e: Exception) {
                    T1Logger.w("Failed to update fight status on surrender: ${e.message}")
                }
            }
        }

        // Record the EXACT elapsed focus time in profile even on surrender (if >= 10s)
        if (actualElapsedSeconds >= 10) {
            viewModelScope.launch {
                val user = currentProfile ?: return@launch
                try {
                    val session = FocusSessionEntity(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        durationSeconds = actualElapsedSeconds, // EXACT ELAPSED TIME!
                        timestamp = System.currentTimeMillis(),
                        synced = false
                    )
                    focusSessionDao.insertSession(session)
                    val updatedCount = focusSessionDao.getTotalFocusSessionCount(user.id)
                    userRepository.saveProfile(user.copy(totalFocusSessions = updatedCount))
                    T1Logger.i("Focus Battle surrender: recorded $actualElapsedSeconds exact focus seconds.")
                } catch (e: Exception) {
                    T1Logger.e("Error recording partial focus session on surrender: ${e.message}", e)
                }
            }
        }
    }

    fun endBattleWithVictory() {
        battleTimerJob?.cancel()
        val wagerTokens = _uiState.value.selectedWagerTokens
        val tokensWon = wagerTokens * 2
        val opponent = _uiState.value.selectedUser
            ?: _uiState.value.recentUsers.firstOrNull()
            ?: _uiState.value.currentUser
        val durationMins = _uiState.value.selectedDurationMinutes
        // EXACT FOCUS TIME SPENT BY THE USER:
        val actualElapsedSeconds = (_uiState.value.myTotalSeconds - _uiState.value.myRemainingSeconds).coerceAtLeast(0).toLong()
        val activeId = _uiState.value.activeFightId

        val newRecord = BattleHistoryRecord(
            id = "h_${System.currentTimeMillis()}",
            opponentUsername = opponent.username,
            opponentDisplayName = opponent.displayName,
            durationMinutes = durationMins,
            tokensDelta = tokensWon,
            isWin = true,
            timeAgo = "Just now"
        )
        _uiState.update {
            it.copy(
                currentStep = BattleStep.RESULT,
                isVictory = true,
                tokensWonAmount = tokensWon,
                battleHistory = listOf(newRecord) + it.battleHistory
            )
        }

        viewModelScope.launch {
            val user = currentProfile ?: return@launch
            // Record the EXACT elapsed focus duration (e.g. 1m 23s = 83 seconds, or full 1500s)
            if (actualElapsedSeconds >= 10) {
                try {
                    val session = FocusSessionEntity(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        durationSeconds = actualElapsedSeconds, // EXACT ELAPSED TIME!
                        timestamp = System.currentTimeMillis(),
                        synced = false
                    )
                    focusSessionDao.insertSession(session)

                    val updatedCount = focusSessionDao.getTotalFocusSessionCount(user.id)
                    val updatedProfile = user.copy(totalFocusSessions = updatedCount)
                    userRepository.saveProfile(updatedProfile)
                    T1Logger.i("Focus Battle session recorded: $actualElapsedSeconds seconds (Actual elapsed). Total sessions: $updatedCount")
                } catch (e: Exception) {
                    T1Logger.e("Error recording Focus Battle session: ${e.message}", e)
                }
            }

            try {
                tokenRepository.addTransaction(
                    userId = user.id,
                    amount = tokensWon,
                    type = "FOCUS_FIGHT_WIN",
                    reason = "Won Focus Fight vs @${opponent.username}"
                )
                T1Logger.i("Focus Battle win reward: +$tokensWon tokens credited")
            } catch (e: Exception) {
                T1Logger.e("Error crediting tokens for battle: ${e.message}", e)
            }

            // Update Supabase focus_fights with exact elapsed seconds
            try {
                val oppElapsed = (_uiState.value.opponentTotalSeconds - _uiState.value.opponentRemainingSeconds).coerceAtLeast(0)
                if (activeId != null) {
                    supabaseService.updateFightStatus(
                        fightId = activeId,
                        status = "COMPLETED",
                        winnerId = user.id,
                        challengerSeconds = actualElapsedSeconds.toInt(),
                        opponentSeconds = oppElapsed
                    )
                } else if (opponent.id.isNotBlank() && opponent.id != "current_user") {
                    supabaseService.recordFocusFight(
                        FocusFightDto(
                            challengerId = user.id,
                            opponentId = opponent.id,
                            durationMinutes = durationMins,
                            wagerTokens = wagerTokens,
                            status = "COMPLETED",
                            winnerId = user.id,
                            challengerSeconds = actualElapsedSeconds.toInt(),
                            opponentSeconds = oppElapsed
                        )
                    )
                }
            } catch (e: Exception) {
                T1Logger.w("Remote focus_fights recording: ${e.message}")
            }
        }
    }

    fun resetToDiscovery() {
        battleTimerJob?.cancel()
        countdownJob?.cancel()
        waitingAcceptanceJob?.cancel()
        _uiState.update {
            it.copy(
                currentStep = BattleStep.DISCOVERY,
                searchQuery = "",
                selectedUser = null,
                activeFightId = null
            )
        }
        loadRealCommunityProfilesAndFights()
    }

    override fun onCleared() {
        super.onCleared()
        battleTimerJob?.cancel()
        countdownJob?.cancel()
        waitingAcceptanceJob?.cancel()
        pendingChallengesPollerJob?.cancel()
    }
}
