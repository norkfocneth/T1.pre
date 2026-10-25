package com.example.t1.domain.model.battle

enum class BattleStep {
    DISCOVERY,          // Search friend, recent & suggested list
    USER_DETAIL,        // View selected user profile
    CHALLENGE_CONFIG,   // Choose duration & token wager
    WAITING_ACCEPTANCE, // Waiting for opponent to accept
    INCOMING_INVITE,    // Opponent sent you a challenge
    COUNTDOWN,          // 3.. 2.. 1.. pre-battle countdown
    LIVE_ARENA,         // Live duel with dual split-screen timers
    RESULT              // Victory or Defeat result screen
}

enum class ParticipantStatus {
    FOCUSED,
    PAUSED,     // App minimized / phone switched
    FORFEITED
}

data class BattleUser(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val battlesCount: Int = 0,
    val winsCount: Int = 0,
    val lossesCount: Int = 0,
    val winRatePercent: Int = 0,
    val totalTokensWon: Int = 0,
    val bio: String = ""
)

data class BattleChallengeData(
    val id: String,
    val challenger: BattleUser,
    val opponent: BattleUser,
    val durationMinutes: Int = 25,
    val wagerTokens: Int = 25,
    val potentialWinTokens: Int = 50,
    val quote: String = "Let's build a more focused version of ourselves."
)

data class BattleHistoryRecord(
    val id: String,
    val opponentUsername: String,
    val opponentDisplayName: String,
    val durationMinutes: Int,
    val tokensDelta: Int,
    val isWin: Boolean,
    val timeAgo: String
)
