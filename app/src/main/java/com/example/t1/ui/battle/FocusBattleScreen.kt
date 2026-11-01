package com.example.t1.ui.battle

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.domain.model.battle.BattleHistoryRecord
import com.example.t1.domain.model.battle.BattleStep
import com.example.t1.domain.model.battle.BattleUser
import com.example.t1.domain.model.battle.ParticipantStatus
import com.example.t1.theme.*
import com.example.t1.util.Haptics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

// =========================================================================
// OBSIDIAN CHARCOAL THEME SYSTEM (Aligned with Home, Leaderboard & Profile)
// =========================================================================
object BattleGradients {
    val screenBackground = Brush.verticalGradient(
        listOf(
            BgTop,    // #0F0F0F
            BgBottom  // #0A0A0A
        )
    )

    // Subtle Duel Clash aura: Emerald focus vs Crimson intensity
    val duelClashAura = Brush.horizontalGradient(
        listOf(
            Color(0xFF10B981).copy(alpha = 0.16f),
            Card,
            Color(0xFFEF4444).copy(alpha = 0.16f)
        )
    )
    val duelBorderGradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF34D399).copy(alpha = 0.40f),
            Border,
            Color(0xFFF43F5E).copy(alpha = 0.40f)
        )
    )

    // Obsidian card fill (#131316 / #101014)
    val cardFill = Brush.verticalGradient(
        listOf(
            Card,
            Color(0xFF101014)
        )
    )

    // Subtle border (#222230)
    val glassBorder = Brush.verticalGradient(
        listOf(
            Border,
            Color(0xFF1A1A24)
        )
    )

    val primaryButtonBrush = Brush.verticalGradient(
        listOf(
            Color.White,
            Color(0xFFE2E8F0)
        )
    )
    val primaryButtonBorder = Brush.verticalGradient(
        listOf(
            Color.White,
            Color(0xFFCBD5E1).copy(alpha = 0.6f)
        )
    )

    val secondaryButtonBrush = Brush.verticalGradient(
        listOf(
            Secondary,
            Color(0xFF181820)
        )
    )
    val secondaryButtonBorder = Brush.verticalGradient(
        listOf(
            Border,
            Color(0xFF1C1C26)
        )
    )

    // Selected duration chip: Obsidian charcoal with crisp white border
    val selectedDurationBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF22222C),
            Color(0xFF181822)
        )
    )
    val selectedDurationBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.85f),
            Color(0xFF888899).copy(alpha = 0.50f)
        )
    )

    // Selected wager chip: Dark amber with gold border
    val selectedWagerBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF281E0E),
            Color(0xFF1C150A)
        )
    )
    val selectedWagerBorder = Brush.verticalGradient(
        listOf(
            Color(0xFFFBBF24),
            Color(0xFFD97706).copy(alpha = 0.60f)
        )
    )

    val arenaFocusedCard = Brush.verticalGradient(
        listOf(
            Color(0xFF131318),
            Color(0xFF0D0D12)
        )
    )
    val arenaFocusedBorder = Brush.verticalGradient(
        listOf(
            Color(0xFF34D399).copy(alpha = 0.45f),
            Border
        )
    )

    val arenaPausedCard = Brush.verticalGradient(
        listOf(
            Color(0xFF1C160E),
            Color(0xFF14100A)
        )
    )
    val arenaPausedBorder = Brush.verticalGradient(
        listOf(
            Color(0xFFF59E0B).copy(alpha = 0.50f),
            Border
        )
    )

    val timerFocusedSweep = Brush.sweepGradient(
        listOf(
            Color(0xFF10B981),
            Color(0xFF34D399),
            Color(0xFF059669),
            Color(0xFF10B981)
        )
    )
    val timerPausedSweep = Brush.sweepGradient(
        listOf(
            Color(0xFFFBBF24),
            Color(0xFFF87171),
            Color(0xFFFBBF24)
        )
    )

    val wonBadgeBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF064E3B).copy(alpha = 0.70f),
            Color(0xFF022C22).copy(alpha = 0.85f)
        )
    )
    val wonBadgeBorder = Brush.verticalGradient(
        listOf(
            Color(0xFF34D399).copy(alpha = 0.50f),
            Color(0xFF059669).copy(alpha = 0.20f)
        )
    )

    val lostBadgeBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF4C0519).copy(alpha = 0.70f),
            Color(0xFF2B020D).copy(alpha = 0.85f)
        )
    )
    val lostBadgeBorder = Brush.verticalGradient(
        listOf(
            Color(0xFFF43F5E).copy(alpha = 0.50f),
            Color(0xFFBE123C).copy(alpha = 0.20f)
        )
    )

    val goldCrownBrush = Brush.linearGradient(
        listOf(
            Color(0xFFFFFBEB),
            Color(0xFFFBBF24),
            Color(0xFFD97706)
        )
    )
    val rewardCardBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF1C170E),
            Color(0xFF120F08)
        )
    )
    val rewardCardBorder = Brush.verticalGradient(
        listOf(
            Color(0xFFFBBF24).copy(alpha = 0.40f),
            Border
        )
    )
}

@Composable
fun T1TokenImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = com.example.t1.R.drawable.t1_token),
        contentDescription = "T1 Token",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
fun UserBattleHeroCard(
    user: BattleUser,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(BattleGradients.cardFill)
            .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(22.dp))
            .drawBehind {
                drawRect(
                    brush = BattleGradients.duelClashAura,
                    size = Size(size.width, 3.dp.toPx())
                )
            }
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Secondary, CircleShape)
                            .border(1.5.dp, Border, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.displayName.take(1).uppercase(),
                            style = TextStyle(
                                fontFamily = SpaceGrotesk,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.displayName,
                                style = TextStyle(
                                    fontFamily = SpaceGrotesk,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(BattleGradients.wonBadgeBrush)
                                    .border(1.dp, BattleGradients.wonBadgeBorder, RoundedCornerShape(100.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "YOU",
                                    style = TextStyle(
                                        fontFamily = Inter,
                                        color = Emerald400,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Text(
                            text = "@${user.username}",
                            style = TextStyle(
                                fontFamily = Inter,
                                color = MutedForeground,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                // Streak / Consistency Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFF1E170A))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.45f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    val streakText = if (user.bio.contains("Streak:")) "🔥 ${user.bio.substringAfter("Streak: ").substringBefore(" •")}" else "🔥 Top Focus"
                    Text(
                        text = streakText,
                        style = TextStyle(
                            fontFamily = Inter,
                            color = Color(0xFFFDE68A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Stats Grid: Fights, Wins, Losses, Win Rate
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Secondary.copy(alpha = 0.5f))
                    .border(1.dp, Border.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(vertical = 12.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${user.battlesCount}",
                        style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    )
                    Text(
                        text = "Fights",
                        style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp)
                    )
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Border))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${user.winsCount}",
                        style = TextStyle(fontFamily = SpaceGrotesk, color = Emerald400, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    )
                    Text(
                        text = "Wins",
                        style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp)
                    )
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Border))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${user.lossesCount}",
                        style = TextStyle(fontFamily = SpaceGrotesk, color = Red400, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    )
                    Text(
                        text = "Losses",
                        style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp)
                    )
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Border))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${user.winRatePercent}%",
                        style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    )
                    Text(
                        text = "Win Rate",
                        style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun FocusBattleScreen(
    viewModel: FocusBattleViewModel,
    modifier: Modifier = Modifier,
    onBackToHome: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current
    val context = androidx.compose.ui.platform.LocalContext.current

    // Minimization detection: Pause focus timer if user exits or minimizes app during live arena
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, uiState.currentStep) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    if (uiState.currentStep == BattleStep.LIVE_ARENA) {
                        viewModel.onAppMinimized()
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.refreshBatteryOptimizationStatus()
                    if (uiState.currentStep == BattleStep.LIVE_ARENA) {
                        viewModel.onAppResumed()
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BattleGradients.screenBackground)
    ) {
        // Subtle ambient obsidian glow
        Canvas(modifier = Modifier.fillMaxWidth().height(360.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E1E26).copy(alpha = 0.20f),
                        Color(0xFF121216).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2f, -40f),
                    radius = size.width * 0.9f
                )
            )
        }
        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                (fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(350), initialOffsetX = { 200 }))
                    .togetherWith(fadeOut(animationSpec = tween(200)) + slideOutHorizontally(animationSpec = tween(250), targetOffsetX = { -200 }))
            },
            label = "battle_step_transition"
        ) { step ->
            when (step) {
                BattleStep.DISCOVERY -> {
                    BattleDiscoveryView(
                        currentUser = uiState.currentUser,
                        searchQuery = uiState.searchQuery,
                        allCommunityUsers = uiState.allCommunityUsers,
                        recentUsers = uiState.recentUsers,
                        suggestedUsers = uiState.suggestedUsers,
                        incomingFight = uiState.incomingFight,
                        incomingChallenger = uiState.incomingChallenger,
                        isBatteryWhitelisted = uiState.isBatteryWhitelisted,
                        onEnableBackgroundAlerts = { viewModel.openBatterySettings(context) },
                        onSearchChange = { viewModel.onSearchQueryChanged(it) },
                        onSelectUser = {
                            Haptics.playLight(view)
                            viewModel.selectUser(it)
                        },
                        onIncomingInviteClick = {
                            Haptics.playMedium(view)
                            viewModel.openIncomingInviteView()
                        }
                    )
                }
                BattleStep.USER_DETAIL -> {
                    val fallbackUser = uiState.recentUsers.firstOrNull() ?: uiState.suggestedUsers.firstOrNull() ?: uiState.currentUser
                    BattleUserDetailView(
                        user = uiState.selectedUser ?: fallbackUser,
                        userFights = uiState.selectedUserFights,
                        userFightsLoading = uiState.selectedUserFightsLoading,
                        onBack = {
                            Haptics.playLight(view)
                            viewModel.resetToDiscovery()
                        },
                        onChallengeClick = {
                            Haptics.playMedium(view)
                            viewModel.openChallengeConfig()
                        }
                    )
                }
                BattleStep.CHALLENGE_CONFIG -> {
                    val fallbackUser = uiState.recentUsers.firstOrNull() ?: uiState.suggestedUsers.firstOrNull() ?: uiState.currentUser
                    BattleChallengeConfigView(
                        opponent = uiState.selectedUser ?: fallbackUser,
                        selectedDuration = uiState.selectedDurationMinutes,
                        selectedWager = uiState.selectedWagerTokens,
                        onDurationSelect = {
                            Haptics.playLight(view)
                            viewModel.setDuration(it)
                        },
                        onWagerSelect = {
                            Haptics.playLight(view)
                            viewModel.setWager(it)
                        },
                        onBack = {
                            Haptics.playLight(view)
                            viewModel.selectUser(uiState.selectedUser ?: fallbackUser)
                        },
                        onSendChallenge = {
                            Haptics.playSuccess(context)
                            viewModel.sendChallenge()
                        }
                    )
                }
                BattleStep.WAITING_ACCEPTANCE -> {
                    val fallbackUser = uiState.recentUsers.firstOrNull() ?: uiState.suggestedUsers.firstOrNull() ?: uiState.currentUser
                    BattleWaitingView(
                        opponent = uiState.selectedUser ?: fallbackUser,
                        durationMinutes = uiState.selectedDurationMinutes,
                        wagerTokens = uiState.selectedWagerTokens,
                        potentialWinTokens = uiState.selectedWagerTokens * 2,
                        onCancel = {
                            Haptics.playLight(view)
                            viewModel.cancelChallenge()
                        }
                    )
                }
                BattleStep.INCOMING_INVITE -> {
                    val fallbackUser = uiState.recentUsers.firstOrNull() ?: uiState.suggestedUsers.firstOrNull() ?: uiState.currentUser
                    val challenger = uiState.incomingChallenger ?: uiState.selectedUser ?: fallbackUser
                    BattleIncomingInviteView(
                        challenger = challenger,
                        durationMinutes = uiState.selectedDurationMinutes,
                        wagerTokens = uiState.selectedWagerTokens,
                        potentialWin = uiState.selectedWagerTokens * 2,
                        onAccept = {
                            Haptics.playSuccess(context)
                            viewModel.acceptChallenge()
                        },
                        onDecline = {
                            Haptics.playLight(view)
                            viewModel.declineChallenge()
                        }
                    )
                }
                BattleStep.COUNTDOWN -> {
                    val opponentUsername = uiState.selectedUser?.username ?: "opponent"
                    BattleCountdownView(
                        countdownNumber = uiState.countdownNumber,
                        challenger = uiState.currentUser.username,
                        opponent = opponentUsername
                    )
                }
                BattleStep.LIVE_ARENA -> {
                    val opponentUsername = uiState.selectedUser?.username ?: "opponent"
                    val opponentDisplayName = uiState.selectedUser?.displayName ?: "Opponent"
                    BattleLiveArenaView(
                        mySeconds = uiState.myRemainingSeconds,
                        myTotalSeconds = uiState.myTotalSeconds,
                        myStatus = uiState.myStatus,
                        myUsername = uiState.currentUser.username,
                        myDisplayName = uiState.currentUser.displayName,
                        opponentSeconds = uiState.opponentRemainingSeconds,
                        opponentTotalSeconds = uiState.opponentTotalSeconds,
                        opponentStatus = uiState.opponentStatus,
                        opponentPausedReason = uiState.opponentPausedReason,
                        opponentUsername = opponentUsername,
                        opponentDisplayName = opponentDisplayName,
                        isLocalPaused = uiState.isLocalPaused,
                        surrenderCountdownSeconds = uiState.surrenderCountdownSeconds,
                        opponentReconnectedAlert = uiState.opponentReconnectedAlert,
                        onResumeFight = {
                            Haptics.playHeavy(view)
                            viewModel.manualResumeFight()
                        },
                        onSurrender = {
                            Haptics.playMedium(view)
                            viewModel.surrender()
                        }
                    )
                }
                BattleStep.RESULT -> {
                    val opponentUsername = uiState.selectedUser?.username ?: "opponent"
                    val myElapsed = (uiState.myTotalSeconds - uiState.myRemainingSeconds).coerceAtLeast(0)
                    val compSecFormatted = String.format("%02d:%02d", myElapsed / 60, myElapsed % 60)
                    val oppElapsed = (uiState.opponentTotalSeconds - uiState.opponentRemainingSeconds).coerceAtLeast(0)
                    val oppSecFormatted = String.format("%02d:%02d", oppElapsed / 60, oppElapsed % 60)
                    BattleResultView(
                        isVictory = uiState.isVictory,
                        myUsername = uiState.currentUser.username,
                        opponentUsername = opponentUsername,
                        completedTime = compSecFormatted,
                        opponentTime = oppSecFormatted,
                        tokensWon = uiState.tokensWonAmount,
                        onFindNextFight = {
                            Haptics.playLight(view)
                            viewModel.resetToDiscovery()
                        },
                        onBackHome = {
                            Haptics.playSuccess(context)
                            viewModel.resetToDiscovery()
                            onBackToHome()
                        }
                    )
                }
            }
        }
    }
}

// =========================================================================
// SCREEN 1: DISCOVERY & SEARCH
// =========================================================================
@Composable
fun BattleDiscoveryView(
    currentUser: BattleUser,
    searchQuery: String,
    allCommunityUsers: List<BattleUser> = emptyList(),
    recentUsers: List<BattleUser>,
    suggestedUsers: List<BattleUser>,
    incomingFight: com.example.t1.data.remote.model.FocusFightDto? = null,
    incomingChallenger: BattleUser? = null,
    isBatteryWhitelisted: Boolean = true,
    onEnableBackgroundAlerts: () -> Unit = {},
    onSearchChange: (String) -> Unit,
    onSelectUser: (BattleUser) -> Unit,
    onIncomingInviteClick: () -> Unit
) {
    val allSearchPool = remember(allCommunityUsers, recentUsers, suggestedUsers, currentUser) {
        val pool = (allCommunityUsers + recentUsers + suggestedUsers).distinctBy { it.id }
        pool.filter { it.id != currentUser.id }
    }
    val searchMatches = remember(searchQuery, allSearchPool) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val cleanQuery = searchQuery.trim().removePrefix("@")
            allSearchPool.filter {
                it.username.contains(cleanQuery, ignoreCase = true) ||
                it.displayName.contains(cleanQuery, ignoreCase = true)
            }
        }
    }

    val hasIncoming = incomingFight != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Row: "Focus Fight" with Fight Invites Notification Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Focus Fight",
                    style = TextStyle(
                        fontFamily = SpaceGrotesk,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Live focus duels with your circle",
                    style = TextStyle(
                        fontFamily = Inter,
                        color = MutedForeground,
                        fontSize = 14.sp
                    )
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(BattleGradients.secondaryButtonBrush, CircleShape)
                    .border(1.dp, BattleGradients.secondaryButtonBorder, CircleShape)
                    .clickable(onClick = onIncomingInviteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsMma,
                    contentDescription = "Fight Invites",
                    tint = if (hasIncoming) Emerald400 else MutedForeground,
                    modifier = Modifier.size(20.dp)
                )
                if (hasIncoming) {
                    val infiniteTransition = rememberInfiniteTransition(label = "fight_dot_pulse")
                    val dotScale by infiniteTransition.animateFloat(
                        initialValue = 0.9f,
                        targetValue = 1.35f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(900, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "fight_dot_scale"
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-3).dp, y = (3).dp)
                            .size(8.dp)
                            .scale(dotScale)
                            .background(Emerald400, CircleShape)
                            .border(1.5.dp, Background, CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Unrestricted Battery Guidance Banner for Android OEMs (Xiaomi, Samsung, OnePlus, Vivo)
        if (!isBatteryWhitelisted) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E1E26))
                    .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable(onClick = onEnableBackgroundAlerts)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Alerts Whitelist",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Enable Closed-App Fight Alerts",
                            style = TextStyle(
                                fontFamily = SpaceGrotesk,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Allow duels when phone is locked or closed",
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 11.sp,
                                color = MutedForeground
                            )
                        )
                    }
                }
                Text(
                    text = "Enable",
                    style = TextStyle(
                        fontFamily = SpaceGrotesk,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24)
                    ),
                    modifier = Modifier
                        .background(Color(0xFFF59E0B).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // User's Own Battle Profile Card
        UserBattleHeroCard(user = currentUser)

        // Incoming Real Challenge Banner if available
        if (incomingFight != null && incomingChallenger != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Emerald500.copy(alpha = 0.20f),
                                Card
                            )
                        )
                    )
                    .border(1.dp, Emerald400.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                    .clickable(onClick = onIncomingInviteClick)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Emerald500.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsMma,
                                contentDescription = null,
                                tint = Emerald400,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "⚔️ Focus Fight Challenge!",
                                style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "@${incomingChallenger.username} • ${incomingFight.durationMinutes}m • ${incomingFight.wagerTokens} tokens",
                                style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 12.sp)
                            )
                        }
                    }
                    Text(
                        text = "Review ›",
                        style = TextStyle(fontFamily = Inter, color = Emerald400, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Obsidian Pill-Shaped Search Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(25.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MutedForeground,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        fontFamily = Inter,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search fighter by username...",
                                style = TextStyle(
                                    fontFamily = Inter,
                                    color = MutedForeground,
                                    fontSize = 14.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MutedForeground,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            if (searchQuery.isNotBlank()) {
                item {
                    Text(
                        text = "Search Results (${searchMatches.size})",
                        style = TextStyle(
                            fontFamily = SpaceGrotesk,
                            color = MutedForeground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                if (searchMatches.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Card)
                                .border(1.dp, Border, RoundedCornerShape(16.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🥊 No fighters found matching \"$searchQuery\"",
                                style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 14.sp)
                            )
                        }
                    }
                } else {
                    items(searchMatches) { user ->
                        UserSearchRow(user = user, onClick = { onSelectUser(user) })
                    }
                }
            } else {
                if (recentUsers.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recent Opponents",
                            style = TextStyle(
                                fontFamily = SpaceGrotesk,
                                color = MutedForeground,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    items(recentUsers) { user ->
                        UserSearchRow(user = user, onClick = { onSelectUser(user) })
                    }
                }

                if (suggestedUsers.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Suggested Fighters",
                            style = TextStyle(
                                fontFamily = SpaceGrotesk,
                                color = MutedForeground,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    items(suggestedUsers) { user ->
                        UserSearchRow(user = user, onClick = { onSelectUser(user) })
                    }
                }

                if (allCommunityUsers.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "All Community Fighters (${allCommunityUsers.size})",
                            style = TextStyle(
                                fontFamily = SpaceGrotesk,
                                color = MutedForeground,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    items(allCommunityUsers) { user ->
                        UserSearchRow(user = user, onClick = { onSelectUser(user) })
                    }
                }
            }
        }
    }
}

@Composable
fun UserSearchRow(user: BattleUser, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BattleGradients.cardFill)
            .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Secondary, CircleShape)
                    .border(1.dp, Border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.displayName.take(1).uppercase(),
                    style = TextStyle(
                        fontFamily = SpaceGrotesk,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "@${user.username}",
                    style = TextStyle(
                        fontFamily = SpaceGrotesk,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "${user.battlesCount} fights • ${user.winRatePercent}% win",
                    style = TextStyle(
                        fontFamily = Inter,
                        color = MutedForeground,
                        fontSize = 12.sp
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(BattleGradients.secondaryButtonBrush, CircleShape)
                .border(1.dp, BattleGradients.secondaryButtonBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsMma,
                contentDescription = "Fight",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// =========================================================================
// SCREEN 2: SELECTED USER PROFILE & CHALLENGE
// =========================================================================
@Composable
fun BattleUserDetailView(
    user: BattleUser,
    userFights: List<BattleHistoryRecord> = emptyList(),
    userFightsLoading: Boolean = false,
    onBack: () -> Unit,
    onChallengeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(BattleGradients.secondaryButtonBrush, CircleShape)
                    .border(1.dp, BattleGradients.secondaryButtonBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Fighter Profile",
                style = TextStyle(
                    fontFamily = SpaceGrotesk,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Center Profile Card
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .background(Secondary, CircleShape)
                    .border(1.5.dp, Border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.displayName.take(1).uppercase(),
                    style = TextStyle(
                        fontFamily = SpaceGrotesk,
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "@${user.username}",
                style = TextStyle(
                    fontFamily = SpaceGrotesk,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = user.displayName,
                style = TextStyle(
                    fontFamily = Inter,
                    color = MutedForeground,
                    fontSize = 14.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3 Stats Card Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(18.dp))
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${user.battlesCount}",
                    style = TextStyle(
                        fontFamily = SpaceGrotesk,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Fights",
                    style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp)
                )
            }
            Box(modifier = Modifier.width(1.dp).height(32.dp).background(Border))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${user.winRatePercent}%",
                    style = TextStyle(
                        fontFamily = SpaceGrotesk,
                        color = Emerald400,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Win Rate",
                    style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp)
                )
            }
            Box(modifier = Modifier.width(1.dp).height(32.dp).background(Border))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T1TokenImage(modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${user.totalTokensWon}",
                        style = TextStyle(
                            fontFamily = SpaceGrotesk,
                            color = Color(0xFFFBBF24),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Text(
                    text = "Tokens",
                    style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Bio Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = user.bio.ifBlank { "T1 Focus Fighter • Ready for live duels." },
                style = TextStyle(
                    fontFamily = Inter,
                    color = MutedForeground,
                    fontSize = 13.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Recent Activity Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                style = TextStyle(
                    fontFamily = SpaceGrotesk,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (userFightsLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BattleGradients.cardFill)
                    .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading fights...",
                    style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 13.sp)
                )
            }
        } else if (userFights.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BattleGradients.cardFill)
                    .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No recent fights on record",
                    style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 13.sp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userFights.take(4)) { record ->
                    RecentActivityRow(
                        opponent = record.opponentUsername,
                        duration = "${record.durationMinutes}m",
                        tokens = "${record.tokensDelta}",
                        isWin = record.isWin
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Start Focus Fight Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(BattleGradients.primaryButtonBrush)
                .border(1.dp, BattleGradients.primaryButtonBorder, RoundedCornerShape(28.dp))
                .clickable(onClick = onChallengeClick),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SportsMma,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Start Focus Fight",
                    style = TextStyle(
                        fontFamily = Inter,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

@Composable
fun RecentActivityRow(
    opponent: String,
    duration: String,
    tokens: String,
    isWin: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BattleGradients.cardFill)
            .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Secondary, CircleShape)
                    .border(1.dp, Border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opponent.take(1).uppercase(),
                    style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "vs @$opponent",
                    style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$duration • ",
                        style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp)
                    )
                    T1TokenImage(modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$tokens tokens",
                        style = TextStyle(fontFamily = Inter, color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(if (isWin) BattleGradients.wonBadgeBrush else BattleGradients.lostBadgeBrush)
                .border(1.dp, if (isWin) BattleGradients.wonBadgeBorder else BattleGradients.lostBadgeBorder, RoundedCornerShape(100.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isWin) "Won" else "Lost",
                style = TextStyle(
                    fontFamily = Inter,
                    color = if (isWin) Emerald400 else Red400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// =========================================================================
// SCREEN 3: FOCUS FIGHT CONFIGURATION (Duration & Wager)
// =========================================================================
@Composable
fun BattleChallengeConfigView(
    opponent: BattleUser,
    selectedDuration: Int,
    selectedWager: Int,
    onDurationSelect: (Int) -> Unit,
    onWagerSelect: (Int) -> Unit,
    onBack: () -> Unit,
    onSendChallenge: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(BattleGradients.secondaryButtonBrush, CircleShape)
                    .border(1.dp, BattleGradients.secondaryButtonBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Configure Focus Fight",
                style = TextStyle(
                    fontFamily = SpaceGrotesk,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Opponent Snippet
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Secondary, CircleShape)
                    .border(1.dp, Border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opponent.displayName.take(1).uppercase(),
                    style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Fight vs @${opponent.username}",
                    style = TextStyle(
                        fontFamily = SpaceGrotesk,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = opponent.displayName,
                    style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 13.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Duration Chips
        Text(
            text = "Duration",
            style = TextStyle(
                fontFamily = SpaceGrotesk,
                color = MutedForeground,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(15, 25, 45).forEach { mins ->
                val isSelected = selectedDuration == mins
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) BattleGradients.selectedDurationBrush else BattleGradients.cardFill)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            brush = if (isSelected) BattleGradients.selectedDurationBorder else BattleGradients.glassBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onDurationSelect(mins) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$mins",
                            style = TextStyle(
                                fontFamily = SpaceGrotesk,
                                color = Color.White,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "min",
                            style = TextStyle(
                                fontFamily = Inter,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MutedForeground,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Wager Chips
        Text(
            text = "Wager (T1 Tokens)",
            style = TextStyle(
                fontFamily = SpaceGrotesk,
                color = MutedForeground,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(10, 25, 50, 100).forEach { tokens ->
                val isSelected = selectedWager == tokens
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) BattleGradients.selectedWagerBrush else BattleGradients.cardFill)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            brush = if (isSelected) BattleGradients.selectedWagerBorder else BattleGradients.glassBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onWagerSelect(tokens) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$tokens",
                        style = TextStyle(
                            fontFamily = SpaceGrotesk,
                            color = if (isSelected) Color(0xFFFBBF24) else Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reward Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.duelBorderGradient, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "You'll wager",
                        style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 14.sp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    T1TokenImage(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$selectedWager tokens",
                        style = TextStyle(fontFamily = SpaceGrotesk, color = Color(0xFFFBBF24), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "If you win, you get",
                        style = TextStyle(fontFamily = Inter, color = Emerald400, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    T1TokenImage(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${selectedWager * 2} tokens",
                        style = TextStyle(fontFamily = SpaceGrotesk, color = Emerald400, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Start Focus Fight Capsule Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(BattleGradients.primaryButtonBrush)
                .border(1.dp, BattleGradients.primaryButtonBorder, RoundedCornerShape(28.dp))
                .clickable(onClick = onSendChallenge),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SportsMma,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Start Focus Fight",
                    style = TextStyle(
                        fontFamily = Inter,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

// =========================================================================
// SCREEN 4: WAITING / SENT CHALLENGE
// =========================================================================
@Composable
fun BattleWaitingView(
    opponent: BattleUser,
    durationMinutes: Int,
    wagerTokens: Int,
    potentialWinTokens: Int,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar with pulse ring
        Box(
            modifier = Modifier
                .size(130.dp)
                .scale(pulseScale)
                .drawBehind {
                    drawCircle(
                        color = Emerald400.copy(alpha = 0.15f),
                        radius = size.minDimension / 1.5f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .background(Secondary, CircleShape)
                    .border(2.dp, Border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opponent.displayName.take(1).uppercase(),
                    style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "@${opponent.username}",
            style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = opponent.displayName,
            style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 14.sp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Fight Request Sent",
            style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Waiting for @${opponent.username} to accept fight...",
            style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 14.sp),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Info Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(18.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⏱ Duration", style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 14.sp))
                Text(text = "$durationMinutes minutes", style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontWeight = FontWeight.Bold))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T1TokenImage(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Wager", style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 14.sp))
                }
                Text(text = "$wagerTokens tokens", style = TextStyle(fontFamily = SpaceGrotesk, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T1TokenImage(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Potential Win", style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 14.sp))
                }
                Text(text = "$potentialWinTokens tokens", style = TextStyle(fontFamily = SpaceGrotesk, color = Emerald400, fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Cancel Fight Request Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(BattleGradients.secondaryButtonBrush)
                .border(1.dp, BattleGradients.secondaryButtonBorder, RoundedCornerShape(27.dp))
                .clickable(onClick = onCancel),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Cancel Fight Request",
                style = TextStyle(fontFamily = Inter, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

// =========================================================================
// SCREEN 5: INCOMING FIGHT INVITE
// =========================================================================
@Composable
fun BattleIncomingInviteView(
    challenger: BattleUser,
    durationMinutes: Int,
    wagerTokens: Int,
    potentialWin: Int,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .background(Secondary, CircleShape)
                .border(2.dp, Border, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = challenger.displayName.take(1).uppercase(),
                style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "@${challenger.username}",
            style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "wants to duel in Focus Fight",
            style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 15.sp),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 3 Column Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(18.dp))
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$durationMinutes min", style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp))
                Text(text = "Duration", style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp))
            }
            Box(modifier = Modifier.width(1.dp).height(28.dp).background(Border))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T1TokenImage(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$wagerTokens", style = TextStyle(fontFamily = SpaceGrotesk, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 18.sp))
                }
                Text(text = "T1 Tokens", style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp))
            }
            Box(modifier = Modifier.width(1.dp).height(28.dp).background(Border))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T1TokenImage(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$potentialWin", style = TextStyle(fontFamily = SpaceGrotesk, color = Emerald400, fontWeight = FontWeight.Bold, fontSize = 18.sp))
                }
                Text(text = "Potential Win", style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 11.sp))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\"Discipline is the new currency. Welcome to the duel.\"",
                style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 13.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(BattleGradients.secondaryButtonBrush)
                    .border(1.dp, BattleGradients.secondaryButtonBorder, RoundedCornerShape(27.dp))
                    .clickable(onClick = onDecline),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Decline", style = TextStyle(fontFamily = Inter, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(BattleGradients.primaryButtonBrush)
                    .border(1.dp, BattleGradients.primaryButtonBorder, RoundedCornerShape(27.dp))
                    .clickable(onClick = onAccept),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SportsMma,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Accept Fight", style = TextStyle(fontFamily = Inter, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp))
                }
            }
        }

        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

// =========================================================================
// SCREEN 6: COUNTDOWN BEFORE FIGHT
// =========================================================================
@Composable
fun BattleCountdownView(
    countdownNumber: Int,
    challenger: String,
    opponent: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Focus Fight begins in...",
            style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 15.sp),
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Big Animated Countdown Circle
        Box(
            modifier = Modifier
                .size(180.dp)
                .drawBehind {
                    drawCircle(
                        color = Secondary,
                        radius = size.minDimension / 2,
                        style = Stroke(width = 6.dp.toPx())
                    )
                    drawArc(
                        brush = BattleGradients.timerFocusedSweep,
                        startAngle = -90f,
                        sweepAngle = (countdownNumber / 3f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$countdownNumber",
                style = TextStyle(
                    fontFamily = SpaceGrotesk,
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(56.dp))

        // Matchup Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.duelBorderGradient, RoundedCornerShape(18.dp))
                .padding(vertical = 14.dp, horizontal = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Emerald400, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "@$challenger", style = TextStyle(fontFamily = SpaceGrotesk, color = Emerald400, fontWeight = FontWeight.Bold, fontSize = 14.sp))
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(Secondary)
                    .border(1.dp, Border, RoundedCornerShape(100.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = "VS", style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "@$opponent", style = TextStyle(fontFamily = SpaceGrotesk, color = Red400, fontWeight = FontWeight.Bold, fontSize = 14.sp))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(8.dp).background(Red400, CircleShape))
            }
        }

        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

// =========================================================================
// SCREEN 7: LIVE DUAL BATTLE ARENA (Split View)
// =========================================================================
@Composable
fun BattleLiveArenaView(
    mySeconds: Int,
    myTotalSeconds: Int,
    myStatus: ParticipantStatus,
    myUsername: String = "you",
    myDisplayName: String = "You",
    opponentSeconds: Int,
    opponentTotalSeconds: Int,
    opponentStatus: ParticipantStatus,
    opponentPausedReason: String,
    opponentUsername: String,
    opponentDisplayName: String,
    isLocalPaused: Boolean = false,
    surrenderCountdownSeconds: Int = 15,
    opponentReconnectedAlert: Boolean = false,
    onResumeFight: () -> Unit = {},
    onSurrender: () -> Unit
) {
    val view = LocalView.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSurrender,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BattleGradients.cardFill)
                    .border(1.dp, BattleGradients.glassBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Focus Fight",
                style = TextStyle(
                    fontFamily = SpaceGrotesk,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BattleGradients.cardFill)
                    .border(1.dp, BattleGradients.glassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsMma,
                    contentDescription = "Fight Arena",
                    tint = Emerald400,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Floating Alert: Rival returned to arena
        AnimatedVisibility(
            visible = opponentReconnectedAlert,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF064E3B))
                    .border(1.dp, Emerald400.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Emerald400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🟢 Rival returned to arena • Duel resumed!",
                        style = TextStyle(
                            fontFamily = Inter,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }

        // Top Card: You (Emerald Green theme)
        DualPlayerCard(
            displayName = if (myDisplayName.isNotBlank()) myDisplayName else "You",
            username = myUsername,
            status = myStatus,
            remainingSeconds = mySeconds,
            totalSeconds = myTotalSeconds,
            isOpponent = false,
            pausedReason = if (myStatus == ParticipantStatus.PAUSED) "App minimized • Timer paused" else ""
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Bottom Card: Opponent (Crimson Red theme)
        DualPlayerCard(
            displayName = opponentDisplayName,
            username = opponentUsername,
            status = opponentStatus,
            remainingSeconds = opponentSeconds,
            totalSeconds = opponentTotalSeconds,
            isOpponent = true,
            pausedReason = opponentPausedReason
        )

        // When local user is paused: Prominent Resume Action Card
        if (myStatus == ParticipantStatus.PAUSED || isLocalPaused) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF261A13),
                                Color(0xFF16100C)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFF59E0B),
                                Color(0xFFEF4444)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PauseCircle,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FOCUS DUEL PAUSED",
                            style = TextStyle(
                                fontFamily = SpaceGrotesk,
                                color = Color(0xFFFDE68A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "App was minimized. Tap Resume to jump back in!",
                        style = TextStyle(
                            fontFamily = Inter,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Forfeit countdown timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFF3F1D1D))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Red400,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Auto-forfeit penalty in: ${surrenderCountdownSeconds}s",
                            style = TextStyle(
                                fontFamily = SpaceGrotesk,
                                color = Red400,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Big Glowing RESUME Button
                    Button(
                        onClick = {
                            Haptics.playHeavy(view)
                            onResumeFight()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Emerald400,
                                            Color(0xFF059669)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RESUME FOCUS FIGHT",
                                    style = TextStyle(
                                        fontFamily = SpaceGrotesk,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Surrender / End Fight (Capsule Shape)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(BattleGradients.secondaryButtonBrush)
                .border(1.dp, BattleGradients.secondaryButtonBorder, RoundedCornerShape(27.dp))
                .clickable(onClick = onSurrender),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Red400, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Surrender Fight", style = TextStyle(fontFamily = Inter, color = Red400, fontWeight = FontWeight.SemiBold, fontSize = 15.sp))
            }
        }

        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

@Composable
fun DualPlayerCard(
    displayName: String,
    username: String,
    status: ParticipantStatus,
    remainingSeconds: Int,
    totalSeconds: Int,
    isOpponent: Boolean,
    pausedReason: String
) {
    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f
    val minutes = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, secs)
    val totalFormatted = String.format("%02d:00", totalSeconds / 60)

    val isPaused = status == ParticipantStatus.PAUSED

    val cardBorder = when {
        isPaused -> BattleGradients.arenaPausedBorder
        !isOpponent -> Brush.linearGradient(
            listOf(
                Emerald400.copy(alpha = 0.50f),
                Border
            )
        )
        else -> Brush.linearGradient(
            listOf(
                Red400.copy(alpha = 0.50f),
                Border
            )
        )
    }

    val cardBg = when {
        isPaused -> BattleGradients.arenaPausedCard
        !isOpponent -> Brush.verticalGradient(
            listOf(
                Color(0xFF0F1E16),
                Card
            )
        )
        else -> Brush.verticalGradient(
            listOf(
                Color(0xFF1E0F12),
                Card
            )
        )
    }

    val arcSweepBrush = when {
        isPaused -> BattleGradients.timerPausedSweep
        !isOpponent -> BattleGradients.timerFocusedSweep
        else -> Brush.sweepGradient(
            listOf(
                Color(0xFF991B1B),
                Red500,
                Red400,
                Color(0xFF991B1B)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Column {
            // Header Row: Avatar, Username, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (!isOpponent) Color(0xFF0D3325) else Color(0xFF3B1218),
                                CircleShape
                            )
                            .border(
                                1.5.dp,
                                if (!isOpponent) Emerald400.copy(alpha = 0.7f) else Red400.copy(alpha = 0.7f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.take(1).uppercase(),
                            style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = displayName,
                            style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        )
                        Text(
                            text = "@$username",
                            style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 12.sp)
                        )
                    }
                }

                // Status Badge
                if (isPaused) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFF2E1C14))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.45f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Pause, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "PAUSED", style = TextStyle(fontFamily = Inter, color = Color(0xFFFDE68A), fontSize = 10.sp, fontWeight = FontWeight.Bold))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(
                                if (!isOpponent) BattleGradients.wonBadgeBrush
                                else Brush.verticalGradient(listOf(Color(0xFF381016), Color(0xFF22080D)))
                            )
                            .border(
                                1.dp,
                                if (!isOpponent) BattleGradients.wonBadgeBorder
                                else BattleGradients.lostBadgeBorder,
                                RoundedCornerShape(100.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(if (!isOpponent) Emerald400 else Red400, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "FOCUSED",
                                style = TextStyle(
                                    fontFamily = Inter,
                                    color = if (!isOpponent) Emerald400 else Red400,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Big Circular Progress Ring Centerpiece
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .drawBehind {
                            drawCircle(
                                color = Secondary,
                                radius = size.minDimension / 2 - 8.dp.toPx(),
                                style = Stroke(width = 10.dp.toPx())
                            )
                            drawArc(
                                brush = arcSweepBrush,
                                startAngle = -90f,
                                sweepAngle = progress * 360f,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeFormatted,
                            style = TextStyle(
                                fontFamily = SpaceGrotesk,
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "/ $totalFormatted",
                            style = TextStyle(
                                fontFamily = Inter,
                                color = MutedForeground,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            if (isPaused) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PauseCircle,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (pausedReason.isNotBlank()) pausedReason.replace("\n", " • ") else "App minimized • Timer paused",
                        style = TextStyle(
                            fontFamily = Inter,
                            color = Color(0xFFFDE68A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MinimalistCrown(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(36.dp, 26.dp)) {
        val w = size.width
        val h = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.08f, h * 0.88f)
            lineTo(w * 0.16f, h * 0.28f)
            lineTo(w * 0.38f, h * 0.58f)
            lineTo(w * 0.50f, h * 0.12f)
            lineTo(w * 0.62f, h * 0.58f)
            lineTo(w * 0.84f, h * 0.28f)
            lineTo(w * 0.92f, h * 0.88f)
            close()
        }
        drawPath(path, brush = BattleGradients.goldCrownBrush)
        drawCircle(brush = BattleGradients.goldCrownBrush, radius = 2.5.dp.toPx(), center = Offset(w * 0.16f, h * 0.28f))
        drawCircle(brush = BattleGradients.goldCrownBrush, radius = 3.dp.toPx(), center = Offset(w * 0.50f, h * 0.12f))
        drawCircle(brush = BattleGradients.goldCrownBrush, radius = 2.5.dp.toPx(), center = Offset(w * 0.84f, h * 0.28f))
    }
}

// =========================================================================
// SCREEN 8: VICTORY & FIGHT RESULT
// =========================================================================
@Composable
fun BattleResultView(
    isVictory: Boolean,
    myUsername: String,
    opponentUsername: String,
    completedTime: String,
    opponentTime: String,
    tokensWon: Int,
    onFindNextFight: () -> Unit,
    onBackHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Winner Avatar with Ripple Circles and Minimalist Crown
        Box(
            modifier = Modifier.size(190.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .border(
                        1.dp,
                        if (isVictory) Emerald400.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .border(
                        1.dp,
                        if (isVictory) Emerald400.copy(alpha = 0.30f) else Color.White.copy(alpha = 0.08f),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(106.dp)
                    .border(
                        1.5.dp,
                        if (isVictory) Emerald400.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.15f),
                        CircleShape
                    )
            )

            // Center Winner Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        if (isVictory) Color(0xFF0F3826) else Secondary,
                        CircleShape
                    )
                    .border(
                        2.dp,
                        if (isVictory) Emerald400 else Border,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isVictory) myUsername.take(1).uppercase() else opponentUsername.take(1).uppercase(),
                    style = TextStyle(
                        fontFamily = SpaceGrotesk,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            if (isVictory) {
                MinimalistCrown(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isVictory) "Victory!" else "Fight Concluded",
            style = TextStyle(
                fontFamily = SpaceGrotesk,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = if (isVictory) "You held your focus and dominated the duel." else "Opponent lasted longer in flow state.",
            style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 14.sp),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Side-by-side comparison card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.duelBorderGradient, RoundedCornerShape(18.dp))
                .padding(vertical = 18.dp, horizontal = 22.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF0D3325), CircleShape)
                        .border(1.dp, Emerald400.copy(alpha = 0.70f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = myUsername.take(1).uppercase(),
                        style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "@$myUsername",
                    style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = completedTime,
                    style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = (-0.5).sp)
                )
                Text(
                    text = "Focused",
                    style = TextStyle(fontFamily = Inter, color = Emerald400, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                )
            }

            Box(modifier = Modifier.width(1.dp).height(74.dp).background(Border))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF3B1218), CircleShape)
                        .border(1.dp, Red400.copy(alpha = 0.70f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opponentUsername.take(1).uppercase(),
                        style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "@$opponentUsername",
                    style = TextStyle(fontFamily = Inter, color = MutedForeground, fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = opponentTime,
                    style = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = (-0.5).sp)
                )
                Text(
                    text = "Opponent",
                    style = TextStyle(fontFamily = Inter, color = Red400, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Token Reward Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BattleGradients.rewardCardBrush)
                .border(1.dp, BattleGradients.duelBorderGradient, RoundedCornerShape(16.dp))
                .padding(vertical = 16.dp, horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    T1TokenImage(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+ $tokensWon tokens",
                        style = TextStyle(
                            fontFamily = SpaceGrotesk,
                            color = Color(0xFFFEF3C7),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "T1 Tokens added to your vault balance.",
                    style = TextStyle(
                        fontFamily = Inter,
                        color = MutedForeground,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Action 1: Find Next Fight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Emerald500,
                            Color(0xFF059669)
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(27.dp))
                .clickable(onClick = onFindNextFight),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SportsMma,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Find Next Fight",
                    style = TextStyle(
                        fontFamily = Inter,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action 2: Back to Home
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(26.dp))
                .clickable(onClick = onBackHome),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back to Home",
                    style = TextStyle(
                        fontFamily = Inter,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}
