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
    private val t1NotificationManager: T1NotificationManager
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
            while (_uiState.value.currentStep == BattleStep.WAITING_ACCEPTANCE) {
                delay(2000)
                try {
                    val remoteFight = supabaseService.getFightById(fightId)
                    if (remoteFight != null) {
                        when (remoteFight.status) {
                            "ACCEPTED" -> {
                                T1Logger.i("Opponent ACCEPTED the focus fight! Starting countdown...")
                                startCountdown()
                                break
                            }
                            "DECLINED" -> {
                                T1Logger.i("Opponent DECLINED the focus fight.")
                                _uiState.update { it.copy(currentStep = BattleStep.DISCOVERY) }
                                break
                            }
                            "CANCELLED" -> {
                                _uiState.update { it.copy(currentStep = BattleStep.DISCOVERY) }
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
                supabaseService.updateFightStatus(fightId, "CANCELLED")
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
                        if (pendingFight != null && pendingFight.id != _uiState.value.incomingFight?.id) {
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

                            // Post real high-priority system notification
                            t1NotificationManager.postNotification(
                                id = (pendingFight.id ?: "").hashCode(),
                                title = "⚔️ Focus Fight Challenge!",
                                text = "@${challengerUser.username} challenged you to a ${pendingFight.durationMinutes}m focus duel!",
                                channelId = T1NotificationManager.CHANNEL_CRITICAL,
                                type = "focus_fight_challenge"
                            )
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
            viewModelScope.launch {
                supabaseService.updateFightStatus(fightId, "ACCEPTED")
            }
            startCountdown()
        } else {
            startCountdown()
        }
    }

    fun declineChallenge() {
        val fight = _uiState.value.incomingFight
        if (fight?.id != null) {
            viewModelScope.launch {
                supabaseService.updateFightStatus(fight.id, "DECLINED")
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
        if (_uiState.value.currentStep == BattleStep.LIVE_ARENA) {
            T1Logger.i("Focus Battle: App minimized. Pausing user focus timer.")
            _uiState.update { it.copy(myStatus = ParticipantStatus.PAUSED) }
        }
    }

    fun onAppResumed() {
        if (_uiState.value.currentStep == BattleStep.LIVE_ARENA) {
            T1Logger.i("Focus Battle: App foregrounded. Resuming user focus timer.")
            _uiState.update { it.copy(myStatus = ParticipantStatus.FOCUSED) }
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

        battleTimerJob?.cancel()
        battleTimerJob = viewModelScope.launch {
            var mySecs = totalSecs
            val activeId = _uiState.value.activeFightId
            val isChallenger = _uiState.value.isChallenger
            var syncTick = 0

            while (mySecs > 0) {
                delay(1000)
                syncTick++

                // Decrement only if user is actively focused (not minimized/paused)
                if (_uiState.value.myStatus == ParticipantStatus.FOCUSED) {
                    mySecs -= 1
                    _uiState.update { it.copy(myRemainingSeconds = mySecs) }
                }

                // Sync with Supabase every 3 seconds if real fight
                if (activeId != null && syncTick % 3 == 0) {
                    val elapsed = totalSecs - mySecs
                    supabaseService.updateFightProgress(activeId, isChallenger, elapsed)

                    val remoteFight = supabaseService.getFightById(activeId)
                    if (remoteFight != null) {
                        if (remoteFight.status == "SURRENDERED") {
                            T1Logger.i("Opponent surrendered! Winning battle.")
                            endBattleWithVictory()
                            break
                        }

                        // Check opponent elapsed progress
                        val oppElapsed = if (isChallenger) remoteFight.opponentSeconds else remoteFight.challengerSeconds
                        val oppRemaining = (totalSecs - oppElapsed).coerceAtLeast(0)
                        _uiState.update { it.copy(opponentRemainingSeconds = oppRemaining) }
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
        val elapsedSeconds = (_uiState.value.myTotalSeconds - _uiState.value.myRemainingSeconds).toLong()
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
                supabaseService.updateFightStatus(
                    fightId = activeId,
                    status = "SURRENDERED",
                    winnerId = opponent.id
                )
            }
        }

        // Even on surrender, if user focused for >= 30 seconds, credit the focus time!
        if (elapsedSeconds >= 30) {
            viewModelScope.launch {
                val user = currentProfile ?: return@launch
                try {
                    val session = FocusSessionEntity(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        durationSeconds = elapsedSeconds,
                        timestamp = System.currentTimeMillis(),
                        synced = false
                    )
                    focusSessionDao.insertSession(session)
                    val updatedCount = focusSessionDao.getTotalFocusSessionCount(user.id)
                    userRepository.saveProfile(user.copy(totalFocusSessions = updatedCount))
                    T1Logger.i("Focus Battle surrender: recorded $elapsedSeconds focus seconds.")
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
        val durationSeconds = (durationMins * 60).toLong()
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
            try {
                val session = FocusSessionEntity(
                    id = UUID.randomUUID().toString(),
                    userId = user.id,
                    durationSeconds = durationSeconds,
                    timestamp = System.currentTimeMillis(),
                    synced = false
                )
                focusSessionDao.insertSession(session)

                val updatedCount = focusSessionDao.getTotalFocusSessionCount(user.id)
                val updatedProfile = user.copy(totalFocusSessions = updatedCount)
                userRepository.saveProfile(updatedProfile)
                T1Logger.i("Focus Battle session recorded: $durationSeconds seconds. Total sessions: $updatedCount")
            } catch (e: Exception) {
                T1Logger.e("Error recording Focus Battle session: ${e.message}", e)
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

            // Update Supabase focus_fights
            try {
                if (activeId != null) {
                    supabaseService.updateFightStatus(
                        fightId = activeId,
                        status = "COMPLETED",
                        winnerId = user.id,
                        challengerSeconds = durationMins * 60,
                        opponentSeconds = (durationMins * 60) - 10
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
                            challengerSeconds = durationMins * 60,
                            opponentSeconds = (durationMins * 60) - 10
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
