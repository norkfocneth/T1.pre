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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
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
// GLOSSY OBSIDIAN LUXURY GRADIENT SYSTEM
// =========================================================================
object BattleGradients {
    val screenBackground = Brush.verticalGradient(
        listOf(
            Color(0xFF0C101A),
            Color(0xFF06080D),
            Color(0xFF030406)
        )
    )

    // Green & Red duel clash theme (Clash between Emerald Focus & Crimson Intensity)
    val duelClashAura = Brush.horizontalGradient(
        listOf(
            Color(0xFF10B981).copy(alpha = 0.22f),
            Color(0xFF060910).copy(alpha = 0.05f),
            Color(0xFFEF4444).copy(alpha = 0.22f)
        )
    )
    val duelBorderGradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF34D399).copy(alpha = 0.60f),
            Color(0xFF38BDF8).copy(alpha = 0.30f),
            Color(0xFFF43F5E).copy(alpha = 0.60f)
        )
    )
    val duelAmbientSweep = Brush.sweepGradient(
        listOf(
            Color(0xFF10B981),
            Color(0xFF38BDF8),
            Color(0xFFEF4444),
            Color(0xFF10B981)
        )
    )

    val cardFill = Brush.verticalGradient(
        listOf(
            Color(0xFF161B28).copy(alpha = 0.88f),
            Color(0xFF0B0E17).copy(alpha = 0.96f)
        )
    )

    val glassBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.20f),
            Color.White.copy(alpha = 0.03f)
        )
    )

    val primaryButtonBrush = Brush.verticalGradient(
        listOf(
            Color(0xFFFFFFFF),
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
            Color(0xFF181E2B).copy(alpha = 0.85f),
            Color(0xFF0E121B).copy(alpha = 0.85f)
        )
    )
    val secondaryButtonBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.22f),
            Color.White.copy(alpha = 0.06f)
        )
    )

    val selectedDurationBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF17253D),
            Color(0xFF0D1525)
        )
    )
    val selectedDurationBorder = Brush.verticalGradient(
        listOf(
            Color(0xFF60A5FA),
            Color(0xFF2563EB).copy(alpha = 0.40f)
        )
    )

    val selectedWagerBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF2C2210),
            Color(0xFF171106)
        )
    )
    val selectedWagerBorder = Brush.verticalGradient(
        listOf(
            Color(0xFFFBBF24),
            Color(0xFFD97706).copy(alpha = 0.40f)
        )
    )

    val arenaFocusedCard = Brush.verticalGradient(
        listOf(
            Color(0xFF131B2B).copy(alpha = 0.92f),
            Color(0xFF080D16).copy(alpha = 0.98f)
        )
    )
    val arenaFocusedBorder = Brush.verticalGradient(
        listOf(
            Color(0xFF38BDF8).copy(alpha = 0.45f),
            Color.White.copy(alpha = 0.06f)
        )
    )

    val arenaPausedCard = Brush.verticalGradient(
        listOf(
            Color(0xFF1C1612).copy(alpha = 0.92f),
            Color(0xFF0F0B08).copy(alpha = 0.98f)
        )
    )
    val arenaPausedBorder = Brush.verticalGradient(
        listOf(
            Color(0xFFF59E0B).copy(alpha = 0.45f),
            Color.White.copy(alpha = 0.06f)
        )
    )

    val timerFocusedSweep = Brush.sweepGradient(
        listOf(
            Color(0xFF38BDF8),
            Color(0xFFFFFFFF),
            Color(0xFF818CF8),
            Color(0xFF38BDF8)
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
            Color(0xFF064E3B).copy(alpha = 0.80f),
            Color(0xFF022C22).copy(alpha = 0.90f)
        )
    )
    val wonBadgeBorder = Brush.verticalGradient(
        listOf(
            Color(0xFF34D399).copy(alpha = 0.65f),
            Color(0xFF059669).copy(alpha = 0.20f)
        )
    )

    val lostBadgeBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF4C0519).copy(alpha = 0.80f),
            Color(0xFF2B020D).copy(alpha = 0.90f)
        )
    )
    val lostBadgeBorder = Brush.verticalGradient(
        listOf(
            Color(0xFFF43F5E).copy(alpha = 0.65f),
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
            Color(0xFF1F1A0F).copy(alpha = 0.92f),
            Color(0xFF0E0C06).copy(alpha = 0.96f)
        )
    )
    val rewardCardBorder = Brush.verticalGradient(
        listOf(
            Color(0xFFFBBF24).copy(alpha = 0.50f),
            Color(0xFFD97706).copy(alpha = 0.15f)
        )
    )
}

@Composable
fun T1TokenImage(
    modifier: Modifier = Modifier.size(20.dp)
) {
    Image(
        painter = painterResource(id = com.example.t1.R.drawable.t1_token),
        contentDescription = "T1 Token",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(CircleShape)
            .border(0.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
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
                            .background(
                                Brush.radialGradient(listOf(Color(0xFF1E293B), Color(0xFF0B0F19))),
                                CircleShape
                            )
                            .border(
                                1.5.dp,
                                Brush.sweepGradient(listOf(Color(0xFF10B981), Color(0xFF38BDF8), Color(0xFF10B981))),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.displayName.take(1).uppercase(),
                            style = TrackingNarrow.copy(
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
                                style = TrackingNarrow.copy(
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
                                    style = TrackingNarrow.copy(
                                        color = Color(0xFF34D399),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Text(
                            text = "@${user.username}",
                            style = TrackingNarrow.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                // Consistency / Streak Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1F180A), Color(0xFF140F04))
                            )
                        )
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔥 5d Streak",
                        style = TrackingNarrow.copy(
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
                    .background(Color(0xFF090C13).copy(alpha = 0.75f))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                    .padding(vertical = 12.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${user.battlesCount}",
                        style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                    Text(
                        text = "Fights",
                        style = TrackingNarrow.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                    )
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.08f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${user.winsCount}",
                        style = TrackingNarrow.copy(color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                    Text(
                        text = "Wins",
                        style = TrackingNarrow.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                    )
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.08f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${user.lossesCount}",
                        style = TrackingNarrow.copy(color = Color(0xFFFB7185), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                    Text(
                        text = "Losses",
                        style = TrackingNarrow.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                    )
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.08f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${user.winRatePercent}%",
                        style = TrackingNarrow.copy(color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                    Text(
                        text = "Win Rate",
                        style = TrackingNarrow.copy(color = Color(0xFF64748B), fontSize = 11.sp)
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
        if (uiState.currentStep == BattleStep.LIVE_ARENA) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                        viewModel.onAppMinimized()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        viewModel.onAppResumed()
                    }
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        } else {
            onDispose {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BattleGradients.screenBackground)
    ) {
        // Subtle ambient radial glow at top
        Canvas(modifier = Modifier.fillMaxWidth().height(360.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E293B).copy(alpha = 0.28f),
                        Color(0xFF0F172A).copy(alpha = 0.10f),
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
                        recentUsers = uiState.recentUsers,
                        suggestedUsers = uiState.suggestedUsers,
                        incomingFight = uiState.incomingFight,
                        incomingChallenger = uiState.incomingChallenger,
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
    recentUsers: List<BattleUser>,
    suggestedUsers: List<BattleUser>,
    incomingFight: com.example.t1.data.remote.model.FocusFightDto? = null,
    incomingChallenger: BattleUser? = null,
    onSearchChange: (String) -> Unit,
    onSelectUser: (BattleUser) -> Unit,
    onIncomingInviteClick: () -> Unit
) {
    val filteredRecent = remember(searchQuery, recentUsers) {
        if (searchQuery.isBlank()) recentUsers
        else recentUsers.filter { it.username.contains(searchQuery, ignoreCase = true) || it.displayName.contains(searchQuery, ignoreCase = true) }
    }
    val filteredSuggested = remember(searchQuery, suggestedUsers) {
        if (searchQuery.isBlank()) suggestedUsers
        else suggestedUsers.filter { it.username.contains(searchQuery, ignoreCase = true) || it.displayName.contains(searchQuery, ignoreCase = true) }
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
                    style = TrackingNarrow.copy(
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Live focus duels with your circle",
                    style = TrackingNarrow.copy(
                        color = Color(0xFF64748B),
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
                    tint = if (hasIncoming) Color(0xFF10B981) else Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
                if (hasIncoming) {
                    // Pulsing indicator dot for real incoming fight requests
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
                            .background(Color(0xFF10B981), CircleShape)
                            .border(1.5.dp, Color(0xFF0C101A), CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

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
                                Color(0xFF10B981).copy(alpha = 0.22f),
                                Color(0xFF0C101A)
                            )
                        )
                    )
                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.50f), RoundedCornerShape(16.dp))
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
                                .background(Color(0xFF10B981).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsMma,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "⚔️ Focus Fight Challenge!",
                                style = TrackingNarrow.copy(color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "@${incomingChallenger.username} • ${incomingFight.durationMinutes}m • ${incomingFight.wagerTokens} tokens",
                                style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                            )
                        }
                    }
                    Text(
                        text = "Review ›",
                        style = TrackingNarrow.copy(color = Color(0xFF34D399), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Pill-Shaped Curved Search Input Bar with Smooth Styling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(32.dp))
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    textStyle = TrackingNarrow.copy(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search fighter by username...",
                                style = TrackingNarrow.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 15.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchChange("") },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            if (filteredRecent.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent Opponents",
                        style = TrackingNarrow.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                items(filteredRecent) { user ->
                    UserSearchRow(user = user, onClick = { onSelectUser(user) })
                }
            }

            if (filteredSuggested.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Suggested Fighters",
                        style = TrackingNarrow.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                items(filteredSuggested) { user ->
                    UserSearchRow(user = user, onClick = { onSelectUser(user) })
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
            .clip(RoundedCornerShape(18.dp))
            .background(BattleGradients.cardFill)
            .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF263045), Color(0xFF131722))),
                        CircleShape
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.displayName.take(1).uppercase(),
                    style = TrackingNarrow.copy(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "@${user.username}",
                    style = TrackingNarrow.copy(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "${user.battlesCount} fights • ${user.winRatePercent}% win",
                    style = TrackingNarrow.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(BattleGradients.secondaryButtonBrush, CircleShape)
                .border(1.dp, BattleGradients.secondaryButtonBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsMma,
                contentDescription = "Fight",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(17.dp)
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
                style = TrackingNarrow.copy(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Center Profile Card
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF2C374E), Color(0xFF131723))),
                        CircleShape
                    )
                    .border(
                        2.dp,
                        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.40f), Color.White.copy(alpha = 0.08f))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.displayName.take(1).uppercase(),
                    style = TrackingNarrow.copy(
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "@${user.username}",
                style = TrackingNarrow.copy(
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = user.displayName,
                style = TrackingNarrow.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 15.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 3 Stats Card Row (Glossy Glass Card with Specular Rim)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(20.dp))
                .padding(vertical = 18.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${user.battlesCount}",
                    style = TrackingNarrow.copy(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Fights",
                    style = TrackingNarrow.copy(
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                )
            }
            Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFF1E2330)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${user.winRatePercent}%",
                    style = TrackingNarrow.copy(
                        color = Color(0xFF34D399),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Win Rate",
                    style = TrackingNarrow.copy(
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                )
            }
            Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFF1E2330)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T1TokenImage(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${user.totalTokensWon}",
                        style = TrackingNarrow.copy(
                            color = Color(0xFFFBBF24),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Text(
                    text = "Tokens",
                    style = TrackingNarrow.copy(
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bio Card (Glossy glass card)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = user.bio,
                style = TrackingNarrow.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Activity Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                style = TrackingNarrow.copy(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "See all",
                style = TrackingNarrow.copy(
                    color = Color(0xFF64748B),
                    fontSize = 13.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (userFightsLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BattleGradients.cardFill)
                    .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading fights...",
                    style = TrackingNarrow.copy(color = Color(0xFF64748B), fontSize = 13.sp)
                )
            }
        } else if (userFights.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BattleGradients.cardFill)
                    .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(14.dp))
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsMma,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No focus fights yet",
                        style = TrackingNarrow.copy(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Be the first to challenge @${user.username}!",
                        style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 12.sp)
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                userFights.take(5).forEach { record ->
                    RecentActivityRow(
                        opponent = record.opponentUsername,
                        duration = "${record.durationMinutes}m",
                        tokens = if (record.isWin) "+${record.tokensDelta}" else "-${record.tokensDelta}",
                        isWin = record.isWin
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Big Primary Fight Button (Glossy Titanium Capsule)
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
                    style = TrackingNarrow.copy(
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
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF263045), Color(0xFF131722))),
                        CircleShape
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opponent.take(1).uppercase(),
                    style = TrackingNarrow.copy(color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "vs @$opponent",
                    style = TrackingNarrow.copy(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$duration • ",
                        style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 12.sp)
                    )
                    T1TokenImage(modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$tokens tokens",
                        style = TrackingNarrow.copy(color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(if (isWin) BattleGradients.wonBadgeBrush else BattleGradients.lostBadgeBrush)
                .border(1.dp, if (isWin) BattleGradients.wonBadgeBorder else BattleGradients.lostBadgeBorder, RoundedCornerShape(100.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = if (isWin) "Won" else "Lost",
                style = TrackingNarrow.copy(
                    color = if (isWin) Color(0xFF6EE7B7) else Color(0xFFFDA4AF),
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
                style = TrackingNarrow.copy(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Opponent Snippet (Glossy glass card)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF263045), Color(0xFF131722))),
                        CircleShape
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opponent.displayName.take(1).uppercase(),
                    style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Fight vs @${opponent.username}",
                    style = TrackingNarrow.copy(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = opponent.displayName,
                    style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 13.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Duration Chips
        Text(
            text = "Duration",
            style = TrackingNarrow.copy(
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(15, 25, 45).forEach { mins ->
                val isSelected = selectedDuration == mins
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
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
                            style = TrackingNarrow.copy(
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "min",
                            style = TrackingNarrow.copy(
                                color = if (isSelected) Color(0xFF93C5FD) else Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Wager Chips
        Text(
            text = "Wager (T1 Tokens)",
            style = TrackingNarrow.copy(
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(10, 25, 50, 100).forEach { tokens ->
                val isSelected = selectedWager == tokens
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
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
                        style = TrackingNarrow.copy(
                            color = if (isSelected) Color(0xFFFBBF24) else Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Reward Summary Card (Glossy Glass Card with Green & Red Duel Border)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.duelBorderGradient, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "You'll wager",
                        style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 15.sp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    T1TokenImage(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$selectedWager tokens",
                        style = TrackingNarrow.copy(color = Color(0xFFFBBF24), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "If you win, you get",
                        style = TrackingNarrow.copy(color = Color(0xFF34D399), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    T1TokenImage(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${selectedWager * 2} tokens",
                        style = TrackingNarrow.copy(color = Color(0xFF34D399), fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                    style = TrackingNarrow.copy(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
        targetValue = 1.15f,
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

        // Avatar with animated glossy pulse ring
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(pulseScale)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.25f), Color.Transparent)
                        ),
                        radius = size.minDimension / 1.5f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF2C374E), Color(0xFF131723))),
                        CircleShape
                    )
                    .border(
                        2.dp,
                        Brush.sweepGradient(listOf(Color(0xFF38BDF8), Color.White, Color(0xFF38BDF8))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opponent.displayName.take(1).uppercase(),
                    style = TrackingNarrow.copy(color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "@${opponent.username}",
            style = TrackingNarrow.copy(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = opponent.displayName,
            style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 14.sp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Fight Request Sent",
            style = TrackingNarrow.copy(color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Waiting for @${opponent.username} to accept fight...",
            style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 15.sp),
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Info Card (Glossy Glass Card)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⏱ Duration", style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 15.sp))
                Text(text = "$durationMinutes minutes", style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.Bold))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T1TokenImage(modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Wager", style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 15.sp))
                }
                Text(text = "$wagerTokens tokens", style = TrackingNarrow.copy(color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T1TokenImage(modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Potential Win", style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 15.sp))
                }
                Text(text = "$potentialWinTokens tokens", style = TrackingNarrow.copy(color = Color(0xFF34D399), fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Cancel Fight Request Button (Glossy Glass Capsule)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(BattleGradients.secondaryButtonBrush)
                .border(1.dp, BattleGradients.secondaryButtonBorder, RoundedCornerShape(28.dp))
                .clickable(onClick = onCancel),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Cancel Fight Request", style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.SemiBold))
        }

        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

// =========================================================================
// INCOMING FOCUS FIGHT MODAL (Recipients View)
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
                .size(96.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0xFF2C374E), Color(0xFF131723))),
                    CircleShape
                )
                .border(
                    2.dp,
                    Brush.sweepGradient(listOf(Color(0xFF38BDF8), Color.White, Color(0xFF38BDF8))),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = challenger.displayName.take(1).uppercase(),
                style = TrackingNarrow.copy(color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "@${challenger.username}",
            style = TrackingNarrow.copy(color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "wants to duel in Focus Fight",
            style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 16.sp),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3 Column Stats (Glossy Glass Card with Specular Rim)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(20.dp))
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$durationMinutes min", style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp))
                Text(text = "Duration", style = TrackingNarrow.copy(color = Color(0xFF64748B), fontSize = 12.sp))
            }
            Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFF1E2330)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T1TokenImage(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$wagerTokens", style = TrackingNarrow.copy(color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 18.sp))
                }
                Text(text = "T1 Tokens", style = TrackingNarrow.copy(color = Color(0xFF64748B), fontSize = 12.sp))
            }
            Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFF1E2330)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    T1TokenImage(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$potentialWin", style = TrackingNarrow.copy(color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 18.sp))
                }
                Text(text = "Potential Win", style = TrackingNarrow.copy(color = Color(0xFF64748B), fontSize = 12.sp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                text = "\"Let's build a more focused version of ourselves.\"",
                style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 14.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(BattleGradients.secondaryButtonBrush)
                    .border(1.dp, BattleGradients.secondaryButtonBorder, RoundedCornerShape(28.dp))
                    .clickable(onClick = onDecline),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Decline", style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.SemiBold))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(BattleGradients.primaryButtonBrush)
                    .border(1.dp, BattleGradients.primaryButtonBorder, RoundedCornerShape(28.dp))
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
                    Text(text = "Accept Fight", style = TrackingNarrow.copy(color = Color.Black, fontWeight = FontWeight.Bold))
                }
            }
        }

        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

// =========================================================================
// SCREEN 5: 3-SECOND COUNTDOWN
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
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(BattleGradients.wonBadgeBrush)
                .border(1.5.dp, BattleGradients.wonBadgeBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsMma,
                contentDescription = "Accepted",
                tint = Color(0xFF34D399),
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Fight Accepted",
            style = TrackingNarrow.copy(color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Focus Fight begins in...",
            style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 16.sp),
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Big Animated Countdown Circle
        Box(
            modifier = Modifier
                .size(180.dp)
                .drawBehind {
                    drawCircle(
                        color = Color(0xFF141824),
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
                style = TrackingNarrow.copy(
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Matchup Footer with Green & Red Duel Gradient Clash Styling
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.duelBorderGradient, RoundedCornerShape(20.dp))
                .padding(vertical = 16.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF34D399), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "@$challenger", style = TrackingNarrow.copy(color = Color(0xFF34D399), fontWeight = FontWeight.Bold))
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF1E2433), Color(0xFF121620))))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(100.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = "VS", style = TrackingNarrow.copy(color = Color(0xFFE2E8F0), fontWeight = FontWeight.Bold, fontSize = 12.sp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "@$opponent", style = TrackingNarrow.copy(color = Color(0xFFFB7185), fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFFB7185), CircleShape))
            }
        }

        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

// =========================================================================
// SCREEN 6: LIVE DUAL BATTLE ARENA (Split View)
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
    onSurrender: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
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
                style = TrackingNarrow.copy(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            IconButton(
                onClick = { /* Options */ },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BattleGradients.cardFill)
                    .border(1.dp, BattleGradients.glassBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SportsMma,
                    contentDescription = "Fight Arena",
                    tint = Color(0xFF10B981)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Top Card: You (Emerald Green theme)
        DualPlayerCard(
            displayName = if (myDisplayName.isNotBlank()) myDisplayName else "You",
            username = myUsername,
            status = myStatus,
            remainingSeconds = mySeconds,
            totalSeconds = myTotalSeconds,
            isOpponent = false,
            pausedReason = if (myStatus == ParticipantStatus.PAUSED) "App minimized\nTimer paused" else ""
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Surrender / End Fight (Capsule Shape)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(BattleGradients.secondaryButtonBrush)
                .border(1.dp, BattleGradients.secondaryButtonBorder, RoundedCornerShape(28.dp))
                .clickable(onClick = onSurrender),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Surrender Fight", style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.SemiBold))
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
                Color(0xFF10B981).copy(alpha = 0.55f),
                Color(0xFF059669).copy(alpha = 0.20f),
                Color.White.copy(alpha = 0.05f)
            )
        )
        else -> Brush.linearGradient(
            listOf(
                Color(0xFFEF4444).copy(alpha = 0.55f),
                Color(0xFFB91C1C).copy(alpha = 0.20f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    }

    val cardBg = when {
        isPaused -> BattleGradients.arenaPausedCard
        !isOpponent -> Brush.verticalGradient(
            listOf(
                Color(0xFF091F18),
                Color(0xFF07120F),
                Color(0xFF06090D)
            )
        )
        else -> Brush.verticalGradient(
            listOf(
                Color(0xFF220C0F),
                Color(0xFF14070A),
                Color(0xFF06090D)
            )
        )
    }

    val arcSweepBrush = when {
        isPaused -> BattleGradients.timerPausedSweep
        !isOpponent -> Brush.sweepGradient(
            listOf(
                Color(0xFF059669),
                Color(0xFF10B981),
                Color(0xFF34D399),
                Color(0xFF6EE7B7)
            )
        )
        else -> Brush.sweepGradient(
            listOf(
                Color(0xFF991B1B),
                Color(0xFFEF4444),
                Color(0xFFF87171),
                Color(0xFFFCA5A5)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
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
                            .size(44.dp)
                            .background(
                                Brush.radialGradient(
                                    if (!isOpponent) listOf(Color(0xFF0D3325), Color(0xFF071A12))
                                    else listOf(Color(0xFF3B1218), Color(0xFF1E0A0E))
                                ),
                                CircleShape
                            )
                            .border(
                                1.5.dp,
                                if (!isOpponent) Color(0xFF10B981).copy(alpha = 0.7f)
                                else Color(0xFFEF4444).copy(alpha = 0.7f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.take(1).uppercase(),
                            style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = displayName,
                            style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        )
                        Text(
                            text = "@$username",
                            style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 13.sp)
                        )
                    }
                }

                // Status Badge (Capsule)
                if (isPaused) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF2E1C14), Color(0xFF1E110B))))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.45f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Pause, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "PAUSED", style = TrackingNarrow.copy(color = Color(0xFFFDE68A), fontSize = 11.sp, fontWeight = FontWeight.Bold))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(
                                if (!isOpponent) BattleGradients.wonBadgeBrush
                                else Brush.horizontalGradient(listOf(Color(0xFF381016), Color(0xFF22080D)))
                            )
                            .border(
                                1.dp,
                                if (!isOpponent) BattleGradients.wonBadgeBorder
                                else Brush.horizontalGradient(listOf(Color(0xFFEF4444).copy(alpha = 0.6f), Color(0xFFB91C1C).copy(alpha = 0.3f))),
                                RoundedCornerShape(100.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(if (!isOpponent) Color(0xFF34D399) else Color(0xFFEF4444), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FOCUSED",
                                style = TrackingNarrow.copy(
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Large Circular Progress Ring
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .drawBehind {
                            val strokeWidth = 5.dp.toPx()
                            if (isPaused) {
                                // Dashed ring for paused state
                                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 14f), 0f)
                                drawCircle(
                                    brush = BattleGradients.timerPausedSweep,
                                    radius = (size.minDimension - strokeWidth) / 2,
                                    style = Stroke(width = strokeWidth, pathEffect = pathEffect)
                                )
                            } else {
                                // Background circle
                                drawCircle(
                                    color = Color(0xFF141824),
                                    radius = (size.minDimension - strokeWidth) / 2,
                                    style = Stroke(width = strokeWidth)
                                )
                                // Active progress arc
                                drawArc(
                                    brush = arcSweepBrush,
                                    startAngle = -90f,
                                    sweepAngle = progress * 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeFormatted,
                            style = TrackingNarrow.copy(
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "/ $totalFormatted",
                            style = TrackingNarrow.copy(
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            if (isPaused) {
                Spacer(modifier = Modifier.height(10.dp))
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
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (pausedReason.isNotBlank()) pausedReason else "App minimized",
                            style = TrackingNarrow.copy(
                                color = Color(0xFFFDE68A),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Timer paused",
                        style = TrackingNarrow.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
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
// SCREEN 7: VICTORY & FIGHT RESULT
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
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ripple 3
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .border(
                        1.dp,
                        if (isVictory) Brush.radialGradient(listOf(Color(0xFF10B981).copy(alpha = 0.20f), Color(0xFF10B981).copy(alpha = 0.02f)))
                        else Brush.radialGradient(listOf(Color.White.copy(alpha = 0.06f), Color.White.copy(alpha = 0.01f))),
                        CircleShape
                    )
            )
            // Ripple 2
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .border(
                        1.dp,
                        if (isVictory) Brush.radialGradient(listOf(Color(0xFF10B981).copy(alpha = 0.35f), Color(0xFF10B981).copy(alpha = 0.04f)))
                        else Brush.radialGradient(listOf(Color.White.copy(alpha = 0.10f), Color.White.copy(alpha = 0.02f))),
                        CircleShape
                    )
            )
            // Ripple 1
            Box(
                modifier = Modifier
                    .size(114.dp)
                    .border(
                        1.5.dp,
                        if (isVictory) Brush.radialGradient(listOf(Color(0xFF34D399).copy(alpha = 0.55f), Color(0xFF10B981).copy(alpha = 0.08f)))
                        else Brush.radialGradient(listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.03f))),
                        CircleShape
                    )
            )

            // Center Winner Avatar
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .background(
                        if (isVictory) Brush.radialGradient(listOf(Color(0xFF0F3826), Color(0xFF061A11)))
                        else Brush.radialGradient(listOf(Color(0xFF242C3D), Color(0xFF0F131C))),
                        CircleShape
                    )
                    .border(
                        2.dp,
                        if (isVictory) Brush.linearGradient(listOf(Color(0xFF34D399), Color(0xFF10B981)))
                        else Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isVictory) myUsername.take(1).uppercase() else opponentUsername.take(1).uppercase(),
                    style = TrackingNarrow.copy(
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
            style = TrackingNarrow.copy(
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = if (isVictory) "You held your focus and dominated the duel." else "Opponent lasted longer in flow state.",
            style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 15.sp),
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Side-by-side comparison card (Green vs Red Duel Clash)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.duelBorderGradient, RoundedCornerShape(20.dp))
                .padding(vertical = 20.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Brush.radialGradient(listOf(Color(0xFF0D3325), Color(0xFF071A12))), CircleShape)
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.70f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = myUsername.take(1).uppercase(),
                        style = TrackingNarrow.copy(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "@$myUsername",
                    style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 13.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = completedTime,
                    style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                )
                Text(
                    text = "Completed",
                    style = TrackingNarrow.copy(color = Color(0xFF34D399), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                )
            }

            Box(modifier = Modifier.width(1.dp).height(80.dp).background(Color(0xFF1E2330)))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Brush.radialGradient(listOf(Color(0xFF3B1218), Color(0xFF1E0A0E))), CircleShape)
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.70f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opponentUsername.take(1).uppercase(),
                        style = TrackingNarrow.copy(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "@$opponentUsername",
                    style = TrackingNarrow.copy(color = Color(0xFF94A3B8), fontSize = 13.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = opponentTime,
                    style = TrackingNarrow.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                )
                Text(
                    text = "Paused",
                    style = TrackingNarrow.copy(color = Color(0xFFF87171), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Token Reward Card with Real T1 Token & Duel Clash Border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BattleGradients.rewardCardBrush)
                .border(1.dp, BattleGradients.duelBorderGradient, RoundedCornerShape(16.dp))
                .padding(vertical = 18.dp, horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    T1TokenImage(modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "+ $tokensWon tokens",
                        style = TrackingNarrow.copy(
                            color = Color(0xFFFEF3C7),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "T1 Tokens added to your vault balance.",
                    style = TrackingNarrow.copy(
                        color = Color(0xFFD1D5DB),
                        fontSize = 13.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Action 1: Find Next Fight (Primary Duel Button)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF10B981),
                            Color(0xFF059669)
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
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
                    style = TrackingNarrow.copy(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action 2: Back to Home (Secondary Outline Button)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(BattleGradients.cardFill)
                .border(1.dp, BattleGradients.glassBorder, RoundedCornerShape(28.dp))
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
                    style = TrackingNarrow.copy(
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
