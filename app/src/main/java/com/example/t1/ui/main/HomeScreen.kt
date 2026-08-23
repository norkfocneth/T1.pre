package com.example.t1.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Layers
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.offset
import com.example.t1.domain.model.behaviour.AppCategory
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.fadeOut
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.ui.components.FocusRing
import com.example.t1.ui.onboarding.OnboardingBackground
import com.example.t1.ui.viewmodel.DashboardUiState
import com.example.t1.ui.viewmodel.MainViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.t1.domain.permission.UsagePermissionState
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.geometry.Offset
import com.example.t1.domain.focus.AICoachEngine

@Composable
fun HomeScreen(
    username: String,
    onOpenTimer: () -> Unit,
    onOpenProfile: () -> Unit,
    dashboardState: DashboardUiState,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    focusScore: Int = 85,
    streak: Int = 12,
    totalFocusSessions: Int = 0,
    viewModel: MainViewModel = hiltViewModel()
) {
    val view = LocalView.current
    val context = LocalContext.current

    val tokenWallet by viewModel.tokenWallet.collectAsStateWithLifecycle()
    val tokenTransactions by viewModel.tokenTransactions.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showMerchCustomizer by remember { mutableStateOf(false) }
    var showWalletScreen by remember { mutableStateOf(false) }

    // Gamified Coin Fly Animation States
    var showFlyingCoin by remember { mutableStateOf(false) }
    val coinAnimY = remember { Animatable(0f) }
    val coinAlpha = remember { Animatable(0f) }
    var capsuleScaleState by remember { mutableStateOf(1f) }
    val capsuleScaleAnimated by animateFloatAsState(
        targetValue = capsuleScaleState,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    // Listen to token earned events to trigger coin fly animation
    LaunchedEffect(Unit) {
        viewModel.tokenEarnedEvent.collect { amount ->
            showFlyingCoin = true
            coinAlpha.snapTo(1f)
            coinAnimY.snapTo(0f)
            
            // Fly up (Vertical shift of -350dp)
            coinAnimY.animateTo(
                targetValue = -350f,
                animationSpec = tween(1200, easing = FastOutSlowInEasing)
            )
            
            // Dynamic Island scale punch
            capsuleScaleState = 1.25f
            Haptics.playMedium(view)
            
            // Hide coin and restore scale
            coinAlpha.animateTo(0f, animationSpec = tween(200))
            delay(200)
            capsuleScaleState = 1f
            showFlyingCoin = false
        }
    }

    val score = dashboardState.currentFocusScore
    val percentile = dashboardState.percentile
    var showBreakdown by remember { mutableStateOf(false) }
    var showAICoach by remember { mutableStateOf(false) }

    // Streak parameters
    val streakColor = when {
        streak >= 14 -> StreakIntense
        streak >= 7 -> StreakMedium
        else -> StreakSmall
    }
    val flameSize = when {
        streak >= 14 -> 24.dp
        streak >= 7 -> 20.dp
        else -> 16.dp
    }

    // Entrance Animation States
    var showPercentileNudge by remember { mutableStateOf(false) }
    var showTopPerformerNudge by remember { mutableStateOf(false) }
    var showTapHint by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showCTA by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showPercentileNudge = true
        delay(300)
        showStats = true
        delay(200)
        showCTA = true
        delay(200)
        showTopPerformerNudge = true
        delay(400)
        showTapHint = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground(modifier = modifier) {
            Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 180.dp) // Extra bottom padding for BottomNav and FAB
        ) {
            // 1. Header (Logo + Dynamic Token Island + Profile Button)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // T1. Logo (Start)
                Text(
                    text = "T1.",
                    style = HeadlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        brush = Brush.linearGradient(
                            colors = listOf(GradientTextStart, GradientTextEnd)
                        )
                    ),
                    letterSpacing = (-1).sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                // Dynamic Token Island (Center)
                val tokenIslandInteraction = remember { MutableInteractionSource() }
                val isTokenPressed by tokenIslandInteraction.collectIsPressedAsState()
                val tokenScale = if (isTokenPressed) 0.92f else 1f

                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .scale(tokenScale * capsuleScaleAnimated)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Card, SurfaceRaised)
                            )
                        )
                        .border(1.dp, Border.copy(alpha = 0.8f), CircleShape)
                        .clickable(
                            interactionSource = tokenIslandInteraction,
                            indication = null,
                            onClick = {
                                Haptics.playMedium(view)
                                showWalletScreen = true
                            }
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = com.example.t1.R.drawable.t1_token),
                        contentDescription = "T1 Token",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .border(0.5.dp, Border, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        val targetBalance = tokenWallet?.balance ?: 0
                        var displayedBalance by remember { mutableStateOf(0) }

                        LaunchedEffect(targetBalance) {
                            if (displayedBalance == 0) {
                                displayedBalance = targetBalance
                            } else if (displayedBalance != targetBalance) {
                                val diff = targetBalance - displayedBalance
                                val steps = 20
                                val delayTime = 30L
                                val stepSize = (diff / steps.toDouble())
                                for (i in 1..steps) {
                                    displayedBalance = (targetBalance - diff + (stepSize * i)).toInt()
                                    delay(delayTime)
                                }
                                displayedBalance = targetBalance
                            }
                        }

                        val tokenCountStr = java.text.NumberFormat.getIntegerInstance().format(displayedBalance)
                        Text(
                            text = tokenCountStr,
                            style = LabelMedium.copy(
                                color = Foreground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            lineHeight = 14.sp
                        )
                        Text(
                            text = "T1 Tokens",
                            style = BodySmall.copy(
                                color = MutedForeground,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            lineHeight = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = MutedForeground,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Profile Avatar Button (End)
                val profileInteraction = remember { MutableInteractionSource() }
                val isProfilePressed by profileInteraction.collectIsPressedAsState()
                val profileScale = if (isProfilePressed) 0.9f else 1f

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                        .scale(profileScale)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Card, SurfaceRaised)
                            )
                        )
                        .border(1.dp, Border, CircleShape)
                        .clickable(
                            interactionSource = profileInteraction,
                            indication = null,
                            onClick = {
                                Haptics.playLight(view)
                                onOpenProfile()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MutedForeground,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Permission Warning Banner
            if (dashboardState.permissionState is UsagePermissionState.Denied) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Danger.copy(alpha = 0.1f))
                        .border(1.dp, Danger.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { onOpenSettings() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Danger,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Usage Stats Revoked",
                            style = BodyMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tap to grant permission in Settings.",
                            style = BodySmall.copy(color = MutedForeground)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Welcome Greeting
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                val dayOfWeek = java.time.LocalDate.now().dayOfWeek
                val dynamicGreeting = when (dayOfWeek) {
                    java.time.DayOfWeek.MONDAY -> "Let's start strong."
                    java.time.DayOfWeek.TUESDAY -> "Yesterday was good. Can you beat it?"
                    java.time.DayOfWeek.FRIDAY -> "Weekend is coming. Don't lose your streak."
                    java.time.DayOfWeek.SUNDAY -> "Weekly report unlocks tonight."
                    else -> "Stay locked in."
                }
                Text(
                    text = "Hello, $username",
                    style = HeadlineSmall.copy(color = Foreground, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                )
                Text(
                    text = dynamicGreeting,
                    style = BodyMedium.copy(color = MutedForeground, fontWeight = FontWeight.Normal)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Goal Comparison & Status calculation (Overall Category Tracking)
            val primaryGoal = userProfile?.primaryGoal ?: "PRODUCTIVITY"
            val todayGoalUsageMs = when (primaryGoal.uppercase()) {
                "PRODUCTIVITY" -> dashboardState.categoryTimes["Productivity"] ?: dashboardState.todayBehaviour?.productiveTimeMs ?: 0L
                "EDUCATION" -> dashboardState.categoryTimes["Education"] ?: dashboardState.todayBehaviour?.educationTimeMs ?: 0L
                "SOCIAL" -> dashboardState.categoryTimes["Social"] ?: dashboardState.todayBehaviour?.socialTimeMs ?: 0L
                "ENTERTAINMENT" -> dashboardState.categoryTimes["Entertainment"] ?: dashboardState.todayBehaviour?.entertainmentTimeMs ?: 0L
                "COMMUNICATION" -> dashboardState.categoryTimes["Communication"] ?: dashboardState.todayBehaviour?.communicationTimeMs ?: 0L
                else -> dashboardState.categoryTimes["Productivity"] ?: dashboardState.todayBehaviour?.productiveTimeMs ?: 0L
            }

            val yesterdayGoalUsageMs = when (primaryGoal.uppercase()) {
                "PRODUCTIVITY" -> dashboardState.yesterdayCategoryTimes["Productivity"] ?: dashboardState.yesterdayBehaviour?.productiveTimeMs ?: dashboardState.baselineGoalTimeMs
                "EDUCATION" -> dashboardState.yesterdayCategoryTimes["Education"] ?: dashboardState.yesterdayBehaviour?.educationTimeMs ?: dashboardState.baselineGoalTimeMs
                "SOCIAL" -> dashboardState.yesterdayCategoryTimes["Social"] ?: dashboardState.yesterdayBehaviour?.socialTimeMs ?: dashboardState.baselineGoalTimeMs
                "ENTERTAINMENT" -> dashboardState.yesterdayCategoryTimes["Entertainment"] ?: dashboardState.yesterdayBehaviour?.entertainmentTimeMs ?: dashboardState.baselineGoalTimeMs
                "COMMUNICATION" -> dashboardState.yesterdayCategoryTimes["Communication"] ?: dashboardState.yesterdayBehaviour?.communicationTimeMs ?: dashboardState.baselineGoalTimeMs
                else -> dashboardState.yesterdayCategoryTimes["Productivity"] ?: dashboardState.yesterdayBehaviour?.productiveTimeMs ?: dashboardState.baselineGoalTimeMs
            }

            val isGrowth = primaryGoal.uppercase() == "PRODUCTIVITY" || primaryGoal.uppercase() == "EDUCATION"
            val hasYesterdayBaseline = yesterdayGoalUsageMs > 0L || (dashboardState.yesterdayBehaviour != null && dashboardState.yesterdayBehaviour.totalScreenTimeMs > 0L)
            val diffMinutes = if (hasYesterdayBaseline) {
                ((todayGoalUsageMs - yesterdayGoalUsageMs) / 60000L).toInt()
            } else {
                ((todayGoalUsageMs) / 60000L).toInt()
            }

            val goalTitle = when (primaryGoal.uppercase()) {
                "PRODUCTIVITY" -> "PRODUCTIVITY"
                "EDUCATION" -> "EDUCATION"
                "SOCIAL" -> "SOCIAL CONTROL"
                "ENTERTAINMENT" -> "ENTERTAINMENT CONTROL"
                "COMMUNICATION" -> "CHAT CONTROL"
                else -> "PRODUCTIVITY"
            }

            val deltaText = if (!hasYesterdayBaseline && todayGoalUsageMs > 0L) {
                "${(todayGoalUsageMs / 60000L).toInt()} min today"
            } else if (isGrowth) {
                if (diffMinutes >= 0) "+$diffMinutes min vs yesterday" else "${-diffMinutes} min to beat yesterday"
            } else {
                if (diffMinutes <= 0) "↓ ${-diffMinutes} min vs yesterday" else "↑ $diffMinutes min over yesterday"
            }

            val statusComparisonText = if (!hasYesterdayBaseline && todayGoalUsageMs > 0L) {
                "Tracking today's baseline"
            } else if (isGrowth) {
                if (diffMinutes < 0) {
                    "Keep building your focus"
                } else {
                    "You are doing better from yesterday"
                }
            } else {
                if (diffMinutes > 0) {
                    "You are not doing better from yesterday"
                } else {
                    "You are doing better from yesterday"
                }
            }

            // 2. Focus Ring Section (Hero UI)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // "Ahead of X% of users"
                AnimatedVisibility(
                    visible = showPercentileNudge,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                        initialOffsetY = { -20 },
                        animationSpec = tween(500)
                    )
                ) {
                    Text(
                        text = "Ahead of $percentile% of users",
                        style = BodyLarge.copy(
                            color = Foreground,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Interactive FocusRing
                FocusRing(
                    score = score,
                    showDetails = showBreakdown,
                    statusComparisonText = statusComparisonText,
                    statusGoalBadge = goalTitle,
                    statusDeltaText = deltaText,
                    onTap = {
                        Haptics.playLight(view)
                        showBreakdown = !showBreakdown
                    }
                )

                // "TOP Y% PERFORMER"
                AnimatedVisibility(
                    visible = showTopPerformerNudge,
                    enter = fadeIn(animationSpec = tween(500))
                ) {
                    Text(
                        text = "TOP ${100 - percentile}% PERFORMER",
                        style = LabelMedium.copy(
                            color = MutedForeground,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 2.sp
                        ),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                // "tap ring for status" hint
                AnimatedVisibility(
                    visible = showTapHint,
                    enter = fadeIn(animationSpec = tween(500)),
                    modifier = Modifier.alpha(0.4f)
                ) {
                    Text(
                        text = if (showBreakdown) "tap ring for score" else "tap ring for status",
                        style = BodySmall.copy(
                            fontSize = 10.sp,
                            color = MutedForeground
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            // Goal Comparison Card
            GoalComparisonCard(
                primaryGoal = primaryGoal,
                trend = dashboardState.trend,
                todayUsageMs = todayGoalUsageMs,
                baselineMs = yesterdayGoalUsageMs
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Stats Card Row
            AnimatedVisibility(
                visible = showStats,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(500)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card.copy(alpha = 0.8f))
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Streak
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$streak",
                                style = HeadlineMedium.copy(
                                    color = Foreground,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = streakColor,
                                modifier = Modifier.size(flameSize)
                            )
                        }
                        Text(
                            text = "STREAK",
                            style = TrackingNarrow.copy(
                                color = MutedForeground,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Border)
                    )

                    // Unlocks
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${dashboardState.todayBehaviour?.unlockCount ?: 0}",
                            style = HeadlineMedium.copy(
                                color = Foreground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "UNLOCKS",
                            style = TrackingNarrow.copy(
                                color = MutedForeground,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Border)
                    )

                    // App Opens
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${dashboardState.todayBehaviour?.appOpenCount ?: 0}",
                            style = HeadlineMedium.copy(
                                color = Foreground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "OPENS",
                            style = TrackingNarrow.copy(
                                color = MutedForeground,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Streak Multiplier Card (XP multiplier dopamine hook)
            val (multiplierText, multiplierVal) = when {
                streak >= 10 -> "🔥🔥🔥 10 Day Focus Multiplier" to "x3"
                streak >= 5 -> "🔥🔥 5 Day Focus Multiplier" to "x2"
                streak >= 3 -> "🔥 3 Day Focus Multiplier" to "x1.5"
                else -> "" to ""
            }
            if (multiplierText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFE0F2FE).copy(alpha = 0.05f),
                                    Color(0xFF0284C7).copy(alpha = 0.1f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Border, GlowPrimary.copy(alpha = 0.4f))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Multiplier",
                            tint = StreakIntense,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = multiplierText,
                                style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Maintain your streak to keep the token boost!",
                                style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GlowPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = multiplierVal,
                            style = LabelSmall.copy(color = GlowPrimary, fontWeight = FontWeight.Black)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Real Collected Usage Breakdown Cards
            if (dashboardState.permissionState is UsagePermissionState.Granted && dashboardState.todayBehaviour != null) {
                val behaviour = dashboardState.todayBehaviour
                val totalMin = behaviour.totalScreenTimeMs / 60000
                val hours = totalMin / 60
                val mins = totalMin % 60
                val formattedScreenTime = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card.copy(alpha = 0.8f))
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        text = "TODAY'S SCREEN TIME",
                        style = TrackingNarrow.copy(
                            color = MutedForeground,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = formattedScreenTime,
                        style = HeadlineLarge.copy(
                            color = Foreground,
                            fontWeight = FontWeight.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Categories breakdown progress bars
                    Text(
                        text = "CATEGORY BREAKDOWN",
                        style = TrackingNarrow.copy(
                            color = MutedForeground,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val categories = listOf(
                        Triple("Productivity", dashboardState.categoryTimes["Productivity"] ?: behaviour.productiveTimeMs, Success),
                        Triple("Communication", dashboardState.categoryTimes["Communication"] ?: behaviour.communicationTimeMs, StreakSmall),
                        Triple("Social Time", dashboardState.categoryTimes["Social"] ?: behaviour.socialTimeMs, BreakdownSocial),
                        Triple("Entertainment", dashboardState.categoryTimes["Entertainment"] ?: behaviour.entertainmentTimeMs, Info),
                        Triple("Education", dashboardState.categoryTimes["Education"] ?: behaviour.educationTimeMs, GlowPrimary),
                        Triple("Utility", dashboardState.categoryTimes["Utility"] ?: 0L, MutedForeground)
                    )

                    fun mapToCategoryLabel(legacyCategory: AppCategory, packageName: String): String {
                        val communicationPackages = setOf(
                            "com.slack", "com.microsoft.teams", "us.zoom.videomeetings", "com.zoom.videomeetings",
                            "com.google.android.gm", "com.microsoft.office.outlook", "com.google.android.apps.meetings",
                            "com.google.android.apps.tachyon", "com.google.android.apps.dynamite", "com.skype.raider",
                            "com.viber.voip", "com.imo.android.imoim", "com.whatsapp", "com.whatsapp.w4b",
                            "org.telegram.messenger", "org.thunderdog.challegram", "com.discord"
                        )
                        if (packageName in communicationPackages) return "Communication"

                        return when (legacyCategory) {
                            AppCategory.PRODUCTIVITY, AppCategory.DEVELOPMENT -> "Productivity"
                            AppCategory.EDUCATION -> "Education"
                            AppCategory.ENTERTAINMENT -> "Entertainment"
                            AppCategory.SOCIAL -> "Social Time"
                            AppCategory.COMMUNICATION -> "Communication"
                            else -> "Utility"
                        }
                    }

                    categories.forEach { (label, durationMs, color) ->
                        val durationMin = durationMs / 60000
                        val hr = durationMin / 60
                        val mn = durationMin % 60
                        val timeStr = if (hr > 0) "${hr}h ${mn}m" else "${mn}m"

                        val fraction = if (behaviour.totalScreenTimeMs > 0) {
                            durationMs.toFloat() / behaviour.totalScreenTimeMs
                        } else 0f

                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    style = BodyMedium.copy(color = Foreground, fontWeight = FontWeight.Medium)
                                )
                                Text(
                                    text = timeStr,
                                    style = BodyMedium.copy(color = MutedForeground)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            // Custom progress track
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Border)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(color)
                                )
                            }

                            // Sub-list of top apps in this category with comparative metrics (Yesterday diff)
                            val matchingApps = behaviour.topUsedApps.filter { app ->
                                mapToCategoryLabel(app.category, app.packageName) == label
                            }.take(3)

                            if (matchingApps.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    matchingApps.forEach { app ->
                                        val appMin = app.usageDurationMs / 60000
                                        if (appMin > 0) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(color.copy(alpha = 0.7f))
                                                    )
                                                    Text(
                                                        text = app.displayName,
                                                        style = LabelSmall.copy(color = Foreground, fontWeight = FontWeight.Normal)
                                                    )

                                                    // Comparative Metric relative to Yesterday
                                                    val yesterdayBehaviour = dashboardState.yesterdayBehaviour
                                                    if (yesterdayBehaviour != null) {
                                                        val yesterdayDuration = yesterdayBehaviour.topUsedApps
                                                            .firstOrNull { it.packageName == app.packageName }?.usageDurationMs ?: 0L
                                                        val diffMin = (app.usageDurationMs - yesterdayDuration) / 60000
                                                        if (diffMin != 0L) {
                                                            val isImproved = diffMin < 0
                                                            val arrow = if (isImproved) "↓" else "↑"
                                                            val label = if (isImproved) "less than yesterday" else "more than yesterday"
                                                            val diffText = "$arrow ${kotlin.math.abs(diffMin)} min $label"
                                                            val diffColor = if (isImproved) Success else Warning
                                                            Text(
                                                                text = diffText,
                                                                style = LabelSmall.copy(color = diffColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                            )
                                                        }
                                                    }
                                                }
                                                Text(
                                                    text = "${appMin} min",
                                                    style = LabelSmall.copy(color = MutedForeground)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Start Focus Session CTA Button
            AnimatedVisibility(
                visible = showCTA,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(500)
                )
            ) {
                val ctaInteraction = remember { MutableInteractionSource() }
                val isCtaPressed by ctaInteraction.collectIsPressedAsState()
                val ctaScale = if (isCtaPressed) 0.95f else 1f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(ctaScale)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(ButtonGradientStart, ButtonGradientEnd)
                            )
                        )
                        .clickable(
                            interactionSource = ctaInteraction,
                            indication = null,
                            onClick = {
                                Haptics.playMedium(view)
                                onOpenTimer()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Zap",
                            tint = Background,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Focus Session",
                            style = LabelLarge.copy(
                                color = Background,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Focus Intelligence Summary Card (FOCUS METRICS)
            AnimatedVisibility(
                visible = showStats,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                    initialOffsetY = { 10 },
                    animationSpec = tween(500)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card.copy(alpha = 0.8f))
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FOCUS METRICS",
                            style = TrackingNarrow.copy(
                                color = MutedForeground,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        val lastSyncStr = dashboardState.lastUpdated?.let {
                            val time = java.time.Instant.ofEpochMilli(it)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalTime()
                            "Updated: ${time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))}"
                        } ?: "Score: Local"
                        Text(
                            text = lastSyncStr,
                            style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Behaviour Score
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${dashboardState.behaviourScore}",
                                style = HeadlineLarge.copy(
                                    color = Success,
                                    fontWeight = FontWeight.Black
                                )
                            )
                            Text(
                                text = "BEHAVIOUR",
                                style = BodySmall.copy(color = MutedForeground, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            )
                        }

                        // Confidence
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${dashboardState.confidence}%",
                                style = HeadlineLarge.copy(
                                    color = GlowPrimary,
                                    fontWeight = FontWeight.Black
                                )
                            )
                            Text(
                                text = "CONFIDENCE",
                                style = BodySmall.copy(color = MutedForeground, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            )
                        }

                        // Trend
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            val trendColor = when (dashboardState.trend) {
                                "Improving" -> Success
                                "Declining" -> StreakSmall
                                else -> Foreground
                            }
                            Text(
                                text = dashboardState.trend.uppercase(),
                                style = HeadlineSmall.copy(
                                    color = trendColor,
                                    fontWeight = FontWeight.Black
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Text(
                                text = "DAILY TREND",
                                style = BodySmall.copy(color = MutedForeground, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    if (dashboardState.timeSaved != 0L) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Border)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val isSaved = dashboardState.timeSaved > 0
                        val absSavedMin = kotlin.math.abs(dashboardState.timeSaved) / 60000
                        val hr = absSavedMin / 60
                        val mn = absSavedMin % 60
                        val timeSavedStr = if (hr > 0) "${hr}h ${mn}m" else "${mn}m"

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isSaved) "Time Saved vs Yesterday" else "Time Lost vs Yesterday",
                                style = BodyMedium.copy(color = Foreground, fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = (if (isSaved) "+" else "-") + timeSavedStr,
                                style = BodyLarge.copy(
                                    color = if (isSaved) Success else StreakSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Floating AI Coach Button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(bottom = 96.dp, end = 20.dp), // Positioned safely above BottomNav across all screen sizes
        contentAlignment = Alignment.BottomEnd
    ) {
        val coachInfiniteTransition = rememberInfiniteTransition(label = "coachGlow")
        val coachGlowScale by coachInfiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "coachGlowScale"
        )
        val coachGlowAlpha by coachInfiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 0.65f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "coachGlowAlpha"
        )

        Box(
            modifier = Modifier.size(62.dp),
            contentAlignment = Alignment.Center
        ) {
            // Dynamic Ambient Aura
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(coachGlowScale)
                    .clip(CircleShape)
                    .background(GlowPrimary.copy(alpha = coachGlowAlpha * 0.4f))
            )

            // Main Robot Coach FAB
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GlowPrimary, Info)
                        )
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    .clickable {
                        Haptics.playMedium(view)
                        showAICoach = true
                    },
                contentAlignment = Alignment.Center
            ) {
                // High-visibility Futuristic Robot Icon
                androidx.compose.foundation.Canvas(modifier = Modifier.size(26.dp)) {
                    val w = size.width
                    val h = size.height

                    // Robot head outline
                    val headPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.15f, h * 0.25f)
                        lineTo(w * 0.85f, h * 0.25f)
                        lineTo(w * 0.92f, h * 0.75f)
                        lineTo(w * 0.65f, h * 0.95f)
                        lineTo(w * 0.35f, h * 0.95f)
                        lineTo(w * 0.08f, h * 0.75f)
                        close()
                    }
                    drawPath(headPath, color = Color.White)

                    // Antenna crown
                    val antennaPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.42f, h * 0.25f)
                        lineTo(w * 0.5f, h * 0.05f)
                        lineTo(w * 0.58f, h * 0.25f)
                        close()
                    }
                    drawPath(antennaPath, color = Color.White)

                    // Visor
                    drawRoundRect(
                        color = Color(0xFF0F172A),
                        topLeft = Offset(w * 0.24f, h * 0.42f),
                        size = androidx.compose.ui.geometry.Size(w * 0.52f, h * 0.22f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Neon glowing eyes
                    drawCircle(color = Color(0xFF38BDF8), radius = 2.5.dp.toPx(), center = Offset(w * 0.37f, h * 0.53f))
                    drawCircle(color = Color(0xFF38BDF8), radius = 2.5.dp.toPx(), center = Offset(w * 0.63f, h * 0.53f))
                }
            }
        }
    }

    // Floating AI Coach Overlay
    if (showAICoach) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable { showAICoach = false }, // Click outside to close
            contentAlignment = Alignment.BottomCenter
        ) {
            var userInput by remember { mutableStateOf("") }
            
            // Chat history state
            val messagesList = remember {
                val initialGreeting = AICoachEngine.generateInitialGreeting(
                    focusScore = dashboardState.currentFocusScore,
                    behaviourScore = dashboardState.behaviourScore,
                    confidence = dashboardState.confidence,
                    timeSavedMs = dashboardState.timeSaved,
                    screenTimeMs = dashboardState.todayBehaviour?.totalScreenTimeMs ?: 0L,
                    unlocks = dashboardState.todayBehaviour?.unlockCount ?: 0,
                    categoryTimes = dashboardState.categoryTimes
                )
                mutableStateListOf(
                    "coach" to initialGreeting
                )
            }

            // Chat card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 85.dp) // Floating above the BottomNav
                    .clip(RoundedCornerShape(24.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {} // Prevent click-through
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Megatron-like styled icon
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(GlowPrimary, Info)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.size(20.dp)) {
                                val w = size.width
                                val h = size.height
                                val path = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(w * 0.2f, h * 0.1f)
                                    lineTo(w * 0.8f, h * 0.1f)
                                    lineTo(w * 0.9f, h * 0.4f)
                                    lineTo(w * 0.5f, h * 0.95f)
                                    lineTo(w * 0.1f, h * 0.4f)
                                    close()
                                }
                                drawPath(path, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "T1 AI Coach",
                                style = HeadlineSmall.copy(color = Foreground, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Offline Insights Engine",
                                style = BodySmall.copy(color = Success, fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    // Close button
                    Text(
                        text = "CLOSE",
                        style = LabelMedium.copy(color = MutedForeground, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable {
                                Haptics.playLight(view)
                                showAICoach = false
                            }
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.height(1.dp).fillMaxWidth().background(Border))
                Spacer(modifier = Modifier.height(16.dp))

                // Message history list
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messagesList.size) { index ->
                        val msg = messagesList[index]
                        val isCoach = msg.first == "coach"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isCoach) Arrangement.Start else Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isCoach) 4.dp else 16.dp,
                                            bottomEnd = if (isCoach) 16.dp else 4.dp
                                        )
                                    )
                                    .background(if (isCoach) SurfaceRaised else GlowPrimary)
                                    .border(
                                        1.dp,
                                        if (isCoach) Border else Color.Transparent,
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isCoach) 4.dp else 16.dp,
                                            bottomEnd = if (isCoach) 16.dp else 4.dp
                                        )
                                    )
                                    .padding(14.dp)
                                    .widthIn(max = 260.dp)
                            ) {
                                Text(
                                    text = msg.second,
                                    style = BodyMedium.copy(color = if (isCoach) Foreground else Background)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceRaised)
                        .border(1.dp, Border, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier.weight(1f),
                        textStyle = BodyMedium.copy(color = Foreground),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(Foreground),
                        decorationBox = { innerTextField ->
                            Box {
                                if (userInput.isEmpty()) {
                                    Text(
                                        text = "Ask coach about your focus...",
                                        style = BodyMedium.copy(color = MutedForeground)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    
                    Text(
                        text = "SEND",
                        style = LabelMedium.copy(color = GlowPrimary, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable {
                                if (userInput.isNotBlank()) {
                                    Haptics.playLight(view)
                                    val query = userInput
                                    messagesList.add("user" to query)
                                    userInput = ""
                                    
                                    // Calculate response
                                    val reply = AICoachEngine.getResponse(
                                        query = query,
                                        focusScore = dashboardState.currentFocusScore,
                                        behaviourScore = dashboardState.behaviourScore,
                                        screenTimeMs = dashboardState.todayBehaviour?.totalScreenTimeMs ?: 0L,
                                        unlocks = dashboardState.todayBehaviour?.unlockCount ?: 0,
                                        categoryTimes = dashboardState.categoryTimes
                                    )
                                    messagesList.add("coach" to reply)
                                }
                            }
                            .padding(8.dp)
                    )
                }
            }
            }

            // 7. Gamified Dopamine Loop: Top Cards (Daily Insight, AI Coach, Tonight's Prediction)
            if (dashboardState.permissionState is UsagePermissionState.Granted && dashboardState.todayBehaviour != null) {
                val dailyInsight = remember(dashboardState.todayBehaviour, dashboardState.yesterdayBehaviour) {
                    val todayInsta = dashboardState.todayBehaviour.topUsedApps.firstOrNull { it.packageName == "com.instagram.android" }?.usageDurationMs ?: 0L
                    val yesterdayInsta = dashboardState.yesterdayBehaviour?.topUsedApps?.firstOrNull { it.packageName == "com.instagram.android" }?.usageDurationMs ?: 0L
                    val diffInstaMin = (yesterdayInsta - todayInsta) / 60000
                    
                    when {
                        diffInstaMin > 5 -> "🔥 Today you used Instagram $diffInstaMin minutes less. Great improvement!"
                        score > 80 -> "⚡ Your productivity was higher than 85% of yesterday."
                        else -> "📈 Only ${100 - percentile} Focus Points away from Top 20%."
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daily Insight Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card.copy(alpha = 0.8f))
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.04f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (dailyInsight.startsWith("🔥")) "🔥" else if (dailyInsight.startsWith("⚡")) "⚡" else "📈",
                                fontSize = 16.sp
                            )
                        }
                        Column {
                            Text(
                                text = "DAILY INSIGHT",
                                style = TrackingNarrow.copy(color = MutedForeground, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = dailyInsight.substring(2),
                                style = BodyMedium.copy(color = Foreground, fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AI Coach Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card.copy(alpha = 0.8f))
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GlowPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Coach",
                                tint = GlowPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            val currentHour = java.time.LocalTime.now().hour
                            val greetingTime = if (currentHour < 12) "Good Morning" else if (currentHour < 17) "Good Afternoon" else "Good Evening"
                            
                            Text(
                                text = "AI COACH",
                                style = TrackingNarrow.copy(color = GlowPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$greetingTime. Today if you reduce Instagram by 15 min, you'll reach ${score + 3} Focus Score.",
                                style = BodyMedium.copy(color = Foreground, fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tonight's Prediction Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card.copy(alpha = 0.8f))
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.04f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Prediction",
                                    tint = Success,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "TONIGHT'S PREDICTION",
                                    style = TrackingNarrow.copy(color = MutedForeground, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Estimated Score: ${score + 2}",
                                    style = BodyMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "+2 points if you avoid Instagram",
                                    style = BodySmall.copy(color = Success, fontSize = 10.sp)
                                )
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Success.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${score + 2}",
                                style = LabelSmall.copy(color = Success, fontWeight = FontWeight.Black)
                            )
                        }
                    }
                }
            }

            // 8. Gamified Dopamine Loop: Bottom Cards (Weekly Boss, Journey level rank progress)
            if (dashboardState.permissionState is UsagePermissionState.Granted && dashboardState.todayBehaviour != null) {
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Weekly Boss Challenge Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card.copy(alpha = 0.8f))
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.04f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Boss",
                                        tint = Warning,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "WEEKLY BOSS CHALLENGE",
                                        style = TrackingNarrow.copy(color = Warning, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Beat your last week",
                                        style = BodyMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                            
                            Text(
                                text = "Target: 80",
                                style = LabelSmall.copy(color = Warning, fontWeight = FontWeight.Bold)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Current: $score",
                                style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp),
                                modifier = Modifier.width(65.dp)
                            )
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(Border)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth((score.toFloat() / 80f).coerceIn(0f, 1f))
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(Warning)
                                )
                            }
                            
                            Text(
                                text = "80",
                                style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Journey level rank progress Card
                val currentLevel = when {
                    score >= 95 -> "Master"
                    score >= 88 -> "Elite"
                    score >= 80 -> "Deep Worker"
                    score >= 70 -> "Locked In"
                    score >= 60 -> "Focused"
                    else -> "Explorer"
                }
                val nextLevel = when (currentLevel) {
                    "Explorer" -> "Focused" to 60
                    "Focused" -> "Locked In" to 70
                    "Locked In" -> "Deep Worker" to 80
                    "Deep Worker" -> "Elite" to 88
                    "Elite" -> "Master" to 95
                    else -> "Ultimate" to 100
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card.copy(alpha = 0.8f))
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "JOURNEY RANK",
                                    style = TrackingNarrow.copy(color = MutedForeground, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentLevel,
                                    style = HeadlineSmall.copy(color = Foreground, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.04f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Next: ${nextLevel.first}",
                                    style = LabelSmall.copy(color = MutedForeground, fontSize = 10.sp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val levelProgress = (score.toFloat() / nextLevel.second.toFloat()).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(Border)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(levelProgress)
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(GlowPrimary, Success)
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }

    // Token Wallet Screen Overlay
    AnimatedVisibility(
        visible = showWalletScreen,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f, animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(250)) + scaleOut(targetScale = 0.95f, animationSpec = tween(250))
    ) {
        TokenWalletScreen(
            currentBalance = tokenWallet?.balance ?: 0,
            lifetimeEarned = tokenWallet?.lifetimeEarned ?: 0,
            lifetimeSpent = tokenWallet?.lifetimeSpent ?: 0,
            transactions = tokenTransactions,
            onClose = { showWalletScreen = false },
            onOpenMerch = {
                showWalletScreen = false
                showMerchCustomizer = true
            },
            viewModel = viewModel
        )
    }

    // Merchandise Customizer Overlay Screen
    AnimatedVisibility(
        visible = showMerchCustomizer,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f, animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(250)) + scaleOut(targetScale = 0.95f, animationSpec = tween(250))
    ) {
        MerchCustomizerScreen(
            currentTokens = tokenWallet?.balance ?: 0,
            onClose = { showMerchCustomizer = false }
        )
    }
}

    // Flying Coin Overlay (Satisfaction dopamine micro-animation)
    if (showFlyingCoin) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            // Floating Popup +25 Tokens Earned
            androidx.compose.animation.AnimatedVisibility(
                visible = coinAlpha.value > 0.3f,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Card.copy(alpha = 0.95f))
                        .border(1.dp, GlowPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = com.example.t1.R.drawable.t1_token),
                            contentDescription = "Token",
                            modifier = Modifier.size(20.dp).clip(CircleShape)
                        )
                        Text(
                            text = "+25 T1 Tokens Earned!",
                            style = LabelMedium.copy(color = GlowPrimary, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // The actual flying coin
            Box(
                modifier = Modifier
                    .offset(y = coinAnimY.value.dp)
                    .graphicsLayer(alpha = coinAlpha.value)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.t1.R.drawable.t1_token),
                    contentDescription = "Flying Coin",
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            }
        }
    }
}

@Composable
fun TShirtShowcase(
    color: Color,
    showBack: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Draw Pedestal (Stone base at the bottom)
        val pedCenterY = h * 0.82f
        val pedWidth = w * 0.8f
        val pedHeight = h * 0.12f

        // Pedestal base shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.6f),
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.1f, pedCenterY),
            size = androidx.compose.ui.geometry.Size(pedWidth, pedHeight)
        )
        // Pedestal top surface
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF2D2D30), Color(0xFF18181A)),
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, pedCenterY + pedHeight * 0.2f),
                radius = pedWidth * 0.5f
            ),
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.12f, pedCenterY - 4f),
            size = androidx.compose.ui.geometry.Size(pedWidth * 0.95f, pedHeight * 0.9f)
        )

        // 2. Draw floating stone particles/debris
        drawCircle(
            color = Color(0xFF3F3F46).copy(alpha = 0.5f),
            radius = 6f,
            center = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.65f)
        )
        drawCircle(
            color = Color(0xFF27272A).copy(alpha = 0.7f),
            radius = 10f,
            center = androidx.compose.ui.geometry.Offset(w * 0.82f, h * 0.55f)
        )
        drawCircle(
            color = Color(0xFF52525B).copy(alpha = 0.4f),
            radius = 4f,
            center = androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.45f)
        )

        // 3. Draw T-Shirt Main Body
        val tPath = Path().apply {
            moveTo(w * 0.42f, h * 0.15f) // Collar left
            lineTo(w * 0.26f, h * 0.18f) // Left shoulder
            lineTo(w * 0.15f, h * 0.42f) // Left sleeve outer
            lineTo(w * 0.28f, h * 0.48f) // Left sleeve opening
            lineTo(w * 0.32f, h * 0.38f) // Left underarm
            lineTo(w * 0.32f, h * 0.76f) // Left body side down
            lineTo(w * 0.68f, h * 0.76f) // Bottom hem
            lineTo(w * 0.68f, h * 0.38f) // Right body side up
            lineTo(w * 0.72f, h * 0.48f) // Right underarm
            lineTo(w * 0.85f, h * 0.42f) // Right sleeve opening
            lineTo(w * 0.74f, h * 0.18f) // Right sleeve outer
            lineTo(w * 0.58f, h * 0.15f) // Right shoulder
            quadraticTo(w * 0.5f, h * 0.23f, w * 0.42f, h * 0.15f) // Neck collar
            close()
        }

        // Base shading gradients for a premium 3D look
        val shirtGradient = Brush.linearGradient(
            colors = listOf(color, color.copy(alpha = 0.75f)),
            start = androidx.compose.ui.geometry.Offset(w * 0.3f, h * 0.15f),
            end = androidx.compose.ui.geometry.Offset(w * 0.7f, h * 0.76f)
        )

        drawPath(path = tPath, brush = shirtGradient)

        // 4. Draw Shadows & Highlight folds to look realistic
        val foldPath1 = Path().apply {
            moveTo(w * 0.32f, h * 0.38f)
            quadraticTo(w * 0.45f, h * 0.52f, w * 0.42f, h * 0.76f)
        }
        drawPath(
            path = foldPath1,
            color = Color.Black.copy(alpha = 0.25f),
            style = Stroke(width = 8f)
        )

        val foldPath2 = Path().apply {
            moveTo(w * 0.68f, h * 0.38f)
            quadraticTo(w * 0.55f, h * 0.52f, w * 0.58f, h * 0.76f)
        }
        drawPath(
            path = foldPath2,
            color = Color.Black.copy(alpha = 0.25f),
            style = Stroke(width = 8f)
        )

        // Collar Ribbing
        val collarPath = Path().apply {
            moveTo(w * 0.42f, h * 0.15f)
            quadraticTo(w * 0.5f, h * 0.23f, w * 0.58f, h * 0.15f)
        }
        drawPath(
            path = collarPath,
            color = if (color == Color(0xFFF5F5F7)) Color.DarkGray.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.25f),
            style = Stroke(width = 4f)
        )
    }
}

val TShirtIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "TShirt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        fill = null,
        stroke = androidx.compose.ui.graphics.SolidColor(Color.White),
        strokeLineWidth = 1.5f
    ) {
        moveTo(18f, 4f)
        lineTo(15f, 2.5f)
        quadTo(12f, 4.5f, 9f, 2.5f)
        lineTo(6f, 4f)
        lineTo(2f, 6f)
        lineTo(4f, 10f)
        lineTo(6.5f, 9f)
        lineTo(6.5f, 21f)
        lineTo(17.5f, 21f)
        lineTo(17.5f, 9f)
        lineTo(20f, 10f)
        lineTo(22f, 6f)
        close()
    }.build()
}

@Composable
fun MerchCustomizerScreen(
    currentTokens: Int,
    onClose: () -> Unit
) {
    val view = LocalView.current

    // Customization states
    val colors = listOf(
        Color(0xFF0F0F10) to "Midnight Black",
        Color(0xFFF5F5F7) to "Platinum White",
        Color(0xFF8A2BE2) to "Cyberpunk Violet",
        Color(0xFF39FF14) to "Neon Green"
    )
    var selectedColorIdx by remember { mutableStateOf(0) }

    val prints = listOf(
        "T1. DEEP WORK",
        "STREAK CHAMP",
        "FOCUS ENGAGED"
    )
    var selectedPrintIdx by remember { mutableStateOf(0) }

    val sizes = listOf("S", "M", "L", "XL", "XXL")
    var selectedSizeIdx by remember { mutableStateOf(1) } // Default to "M"

    var selectedTabIdx by remember { mutableStateOf(0) } // Front, Back, Style, Color, Size
    var selectedPreviewIdx by remember { mutableStateOf(0) } // Updates showing angle

    val showBack = selectedPreviewIdx == 1 || selectedTabIdx == 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B)) // Sleek dark zinc background
            .clickable(enabled = false) {} // block click propagation
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Header (Back Arrow + Merchandise Title + Outline Pill Button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Card)
                            .border(1.dp, Border, CircleShape)
                            .clickable {
                                Haptics.playLight(view)
                                onClose()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Foreground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Merchandise",
                            style = HeadlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Foreground,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = "Premium. Exclusive. Coming soon.",
                            style = BodySmall.copy(color = MutedForeground, fontSize = 11.sp)
                        )
                    }
                }

                // "How it works" outline pill button
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(1.dp, Border, CircleShape)
                        .clickable {
                            Haptics.playLight(view)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "How it works",
                        style = LabelSmall.copy(color = Foreground, fontWeight = FontWeight.Medium)
                    )
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MutedForeground,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Scrollable Container holding Product Display, Tabs, Thumbnails, Locked Card, Footer
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 2. Product Showcase Card (Oversized Tee)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Card, SurfaceRaised)
                            )
                        )
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Left Column: Custom labels & pricing
                        Column(
                            modifier = Modifier
                                .weight(1.1f)
                                .padding(end = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "PREMIUM MERCH",
                                    style = LabelSmall.copy(
                                        color = MutedForeground,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }

                            Text(
                                text = "T1. Signature\nOversized Tee",
                                style = HeadlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Foreground,
                                    fontSize = 21.sp,
                                    lineHeight = 25.sp
                                )
                            )

                            Text(
                                text = "Minimal design.\nMaximum identity.\nBuilt for the focused.",
                                style = BodySmall.copy(color = MutedForeground, lineHeight = 16.sp)
                            )

                            // Price Section
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Price",
                                    style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp)
                                )
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black.copy(alpha = 0.2f))
                                        .border(0.5.dp, Border, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = com.example.t1.R.drawable.t1_token),
                                        contentDescription = "T1 Token",
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                    )
                                    Column {
                                        Text(
                                            text = "6,900",
                                            style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "T1 Tokens",
                                            style = BodySmall.copy(color = MutedForeground, fontSize = 8.sp)
                                        )
                                    }
                                }
                            }

                            // Status Label
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Status",
                                    tint = MutedForeground,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Coming Soon",
                                    style = BodySmall.copy(color = MutedForeground, fontWeight = FontWeight.Medium)
                                )
                            }
                        }

                        // Right Column: Interactive 3D Mockup Showcase Card
                        Box(
                            modifier = Modifier
                                .weight(1.3f) // Give more weight for a premium visual prominence
                                .height(260.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val showcaseResId = when (selectedPreviewIdx) {
                                0 -> com.example.t1.R.drawable.merch_tshirt_main
                                1 -> com.example.t1.R.drawable.merch_tshirt_back
                                2 -> com.example.t1.R.drawable.merch_tshirt_collar
                                3 -> com.example.t1.R.drawable.merch_tshirt_tag
                                else -> com.example.t1.R.drawable.merch_tshirt_main
                            }
                            Image(
                                painter = painterResource(id = showcaseResId),
                                contentDescription = "T-Shirt Showcase",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                            )
                        }
                    }
                }

                // 3. Preview & Customize Header Title
                Text(
                    text = "Preview & Customize",
                    style = LabelLarge.copy(color = Foreground, fontWeight = FontWeight.Bold)
                )

                // 4. Customize Category Tab Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf(
                        Triple(TShirtIcon, "Front", 0),
                        Triple(TShirtIcon, "Back", 1),
                        Triple(TShirtIcon, "Style", 2),
                        Triple(Icons.Default.ColorLens, "Color", 3),
                        Triple(Icons.Default.Straighten, "Size", 4)
                    )

                    tabs.forEach { (icon, name, index) ->
                        val isSelected = selectedTabIdx == index
                        val contentColor = if (isSelected) GlowPrimary else MutedForeground

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    Haptics.playLight(view)
                                    selectedTabIdx = index
                                    if (index == 0) selectedPreviewIdx = 0
                                    if (index == 1) selectedPreviewIdx = 1
                                }
                                .padding(8.dp)
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = name,
                                tint = contentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = name,
                                style = LabelSmall.copy(color = contentColor, fontWeight = FontWeight.Medium)
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 16.dp, height = 2.dp)
                                        .clip(CircleShape)
                                        .background(GlowPrimary)
                                )
                            }
                        }
                    }
                }

                // Tab Specific Options
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Card)
                        .border(1.dp, Border, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    when (selectedTabIdx) {
                        0 -> { // Front Graphic Selection
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Graphic placement: Left chest",
                                    style = BodySmall.copy(color = MutedForeground)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(GlowPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "T1. Logo",
                                        style = LabelSmall.copy(color = GlowPrimary, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                        1 -> { // Back Graphic Print Options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                prints.forEachIndexed { idx, printText ->
                                    val isSel = selectedPrintIdx == idx
                                    val bgCol = if (isSel) Foreground else SurfaceRaised
                                    val textCol = if (isSel) Background else Foreground
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(bgCol)
                                            .clickable {
                                                Haptics.playLight(view)
                                                selectedPrintIdx = idx
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = printText.replace("T1. ", ""),
                                            style = LabelSmall.copy(color = textCol, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                        2 -> { // Style Selection
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Oversized", "Fitted").forEachIndexed { idx, style ->
                                    val isSel = idx == 0
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) Foreground else SurfaceRaised)
                                            .clickable { Haptics.playLight(view) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = style,
                                            style = LabelSmall.copy(color = if (isSel) Background else Foreground, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                        3 -> { // Color Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                colors.forEachIndexed { idx, (col, name) ->
                                    val isSel = selectedColorIdx == idx
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(col)
                                            .border(
                                                width = if (isSel) 2.dp else 1.dp,
                                                color = if (isSel) GlowPrimary else Border,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                Haptics.playLight(view)
                                                selectedColorIdx = idx
                                            }
                                    )
                                }
                            }
                        }
                        4 -> { // Size Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                sizes.forEachIndexed { idx, size ->
                                    val isSel = selectedSizeIdx == idx
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) Foreground else SurfaceRaised)
                                            .clickable {
                                                Haptics.playLight(view)
                                                selectedSizeIdx = idx
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = size,
                                            style = LabelSmall.copy(color = if (isSel) Background else Foreground, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Preview cards row (showing thumbnails of front, back, etc.)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val previewOptions = listOf(
                        "Front" to com.example.t1.R.drawable.merch_tshirt_front,
                        "Back" to com.example.t1.R.drawable.merch_tshirt_back,
                        "Collar" to com.example.t1.R.drawable.merch_tshirt_collar,
                        "Tag" to com.example.t1.R.drawable.merch_tshirt_tag
                    )

                    previewOptions.forEachIndexed { index, (label, resId) ->
                        val isSelected = selectedPreviewIdx == index
                        val borderCol = if (isSelected) GlowPrimary else Border

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Card)
                                .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                                .clickable {
                                    Haptics.playLight(view)
                                    selectedPreviewIdx = index
                                    if (index == 0) selectedTabIdx = 0
                                    if (index == 1) selectedTabIdx = 1
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = label,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                }

                // 6. Locked Banner Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF121214))
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .border(1.dp, Border, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = MutedForeground,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "This item is locked",
                                    style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Merchandise booking will open soon. Stay tuned!",
                                    style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp),
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        // COMING SOON pill button
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF1E3A8A).copy(alpha = 0.8f))
                                .border(1.dp, GlowPrimary.copy(alpha = 0.3f), CircleShape)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "COMING SOON",
                                style = LabelSmall.copy(color = Foreground, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            )
                        }
                    }
                }

                // 7. Feature Grid Footer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card)
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val features = listOf(
                        Triple(Icons.Default.Star, "Premium Quality", "High quality fabric and long lasting."),
                        Triple(Icons.Default.CardGiftcard, "Exclusive Drop", "Limited quantity for early supporters."),
                        Triple(Icons.Default.Shield, "Secure Checkout", "Safe and easy redemption.")
                    )

                    features.forEach { (icon, title, desc) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.04f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = MutedForeground,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = title,
                                    style = LabelSmall.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = desc,
                                    style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp)) // Extra scroll spacing
            }
        }
    }
}

@Composable
fun TokenWalletScreen(
    currentBalance: Int,
    lifetimeEarned: Int,
    lifetimeSpent: Int,
    transactions: List<com.example.t1.data.database.entity.TokenTransactionEntity>,
    onClose: () -> Unit,
    onOpenMerch: () -> Unit,
    viewModel: MainViewModel
) {
    val view = LocalView.current
    val context = androidx.compose.ui.platform.LocalContext.current

    // Infinite rotation for animated coin
    val infiniteTransition = rememberInfiniteTransition(label = "coin_rotation")
    val coinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "coin_rotation_anim"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B)) // Sleek dark zinc background
            .clickable(enabled = false) {} // block click propagation
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Header (Back Arrow + Title)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Card)
                        .border(1.dp, Border, CircleShape)
                        .clickable {
                            Haptics.playLight(view)
                            onClose()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Foreground,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Token Wallet",
                        style = HeadlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Foreground,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Your auditable reward balance.",
                        style = BodySmall.copy(color = MutedForeground, fontSize = 11.sp)
                    )
                }
            }

            // Scrollable sections
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 2. Balance Card (Header statistics)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Card, SurfaceRaised)
                            )
                        )
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Current Balance",
                                style = BodySmall.copy(color = MutedForeground, fontSize = 11.sp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = com.example.t1.R.drawable.t1_token),
                                    contentDescription = "Tokens",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .graphicsLayer(rotationY = coinRotation)
                                )
                                Text(
                                    text = java.text.NumberFormat.getIntegerInstance().format(currentBalance),
                                    style = HeadlineMedium.copy(color = Foreground, fontWeight = FontWeight.Bold, fontSize = 32.sp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Lifetime Earned",
                                        style = BodySmall.copy(color = MutedForeground, fontSize = 9.sp)
                                    )
                                    Text(
                                        text = "+${java.text.NumberFormat.getIntegerInstance().format(lifetimeEarned)}",
                                        style = LabelSmall.copy(color = GlowPrimary, fontWeight = FontWeight.Bold)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Lifetime Spent",
                                        style = BodySmall.copy(color = MutedForeground, fontSize = 9.sp)
                                    )
                                    Text(
                                        text = "-${java.text.NumberFormat.getIntegerInstance().format(lifetimeSpent)}",
                                        style = LabelSmall.copy(color = Color.Red.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        // Big pulsing animated coin
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(GlowPrimary.copy(alpha = 0.05f))
                                .border(1.dp, GlowPrimary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = com.example.t1.R.drawable.t1_token),
                                contentDescription = "Pulsing Coin",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .graphicsLayer(rotationY = coinRotation)
                            )
                        }
                    }
                }

                // 3. Developer / User Simulation Panel
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Simulate Token Earning (Test Limits & Cooldowns)",
                        style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Card)
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Tap any reward to simulate positive behavior earning. Limits (e.g. Daily login limit, Focus session limit of 10/day) are fully enforced and will trigger safe rollback if violated.",
                                style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp),
                                lineHeight = 14.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Daily Login (+5)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Daily Mission (+15)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Weekly (+100)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Focus Session (+5)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("7d Streak (+70)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                                        .border(0.5.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            Haptics.playMedium(view)
                                            android.widget.Toast.makeText(
                                                context,
                                                "Simulated clicks are disabled. Earn tokens automatically through positive behaviors!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Global T100 (+1K)", style = LabelSmall.copy(color = Foreground, fontSize = 9.sp))
                                }
                            }
                        }
                    }
                }

                // 4. Coming Soon Merchandise Card
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Exclusives & Merchandise Store",
                        style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Card)
                            .border(1.dp, Border, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "T1. Signature Oversized Tee",
                                    style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Unlock with 6,900 Tokens. Minimal identity.",
                                    style = BodySmall.copy(color = MutedForeground, fontSize = 10.sp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(GlowPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, GlowPrimary.copy(alpha = 0.3f), CircleShape)
                                    .clickable {
                                        Haptics.playLight(view)
                                        onOpenMerch()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "3D Preview",
                                    style = LabelSmall.copy(color = GlowPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }

                // 5. Recent Transactions Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Recent Transactions",
                        style = LabelMedium.copy(color = Foreground, fontWeight = FontWeight.Bold)
                    )
                    if (transactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Card)
                                .border(1.dp, Border, RoundedCornerShape(12.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transactions found yet.",
                                style = BodySmall.copy(color = MutedForeground)
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            transactions.take(15).forEach { tx ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Card)
                                        .border(1.dp, Border, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = tx.reason.replace("_", " "),
                                                style = LabelSmall.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(tx.timestamp),
                                                    style = BodySmall.copy(color = MutedForeground, fontSize = 9.sp)
                                                )
                                                // Sync tag
                                                val syncColor = if (tx.status == "SYNCED") GlowPrimary.copy(alpha = 0.7f) else Color.Yellow.copy(alpha = 0.7f)
                                                Text(
                                                    text = tx.status,
                                                    style = LabelSmall.copy(color = syncColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }

                                        val prefix = if (tx.amount > 0) "+" else ""
                                        val color = if (tx.amount > 0) GlowPrimary else Color.Red.copy(alpha = 0.8f)
                                        Text(
                                            text = "$prefix${tx.amount}",
                                            style = LabelMedium.copy(color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun GoalComparisonCard(
    primaryGoal: String,
    trend: String,
    todayUsageMs: Long,
    baselineMs: Long,
    modifier: Modifier = Modifier
) {
    val isGrowth = primaryGoal.uppercase() == "PRODUCTIVITY" || primaryGoal.uppercase() == "EDUCATION"
    val hasYesterdayBaseline = baselineMs > 0L
    val diffMinutes = if (hasYesterdayBaseline) {
        ((todayUsageMs - baselineMs) / 60000L).toInt()
    } else {
        ((todayUsageMs) / 60000L).toInt()
    }

    val goalTitle = when (primaryGoal.uppercase()) {
        "PRODUCTIVITY" -> "PRODUCTIVITY"
        "EDUCATION" -> "EDUCATION"
        "SOCIAL" -> "SOCIAL CONTROL"
        "ENTERTAINMENT" -> "ENTERTAINMENT CONTROL"
        "COMMUNICATION" -> "CHAT CONTROL"
        else -> "PRODUCTIVITY"
    }

    val deltaText = if (!hasYesterdayBaseline && todayUsageMs > 0L) {
        "${(todayUsageMs / 60000L).toInt()} min today"
    } else if (isGrowth) {
        if (diffMinutes >= 0) "+$diffMinutes min vs yesterday" else "${-diffMinutes} min to beat yesterday"
    } else {
        if (diffMinutes <= 0) "↓ ${-diffMinutes} min vs yesterday" else "↑ $diffMinutes min over yesterday"
    }

    val goalLower = when (primaryGoal.uppercase()) {
        "PRODUCTIVITY" -> "productivity"
        "EDUCATION" -> "education"
        "SOCIAL" -> "social"
        "ENTERTAINMENT" -> "entertainment"
        "COMMUNICATION" -> "chat control"
        else -> "productivity"
    }

    val motivationalText = when {
        !hasYesterdayBaseline && todayUsageMs > 0L -> "Tracking today's baseline."
        isGrowth && diffMinutes >= 0 -> "You are doing better from yesterday."
        isGrowth && diffMinutes < 0 -> "Keep building your $goalLower focus."
        !isGrowth && diffMinutes <= 0 -> "You are doing better from yesterday."
        else -> "You are not doing better from yesterday."
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Card.copy(alpha = 0.8f))
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .padding(vertical = 18.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Centered Pill Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF262D4A).copy(alpha = 0.6f))
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Text(
                text = goalTitle,
                style = LabelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8E99B4),
                    letterSpacing = 1.sp,
                    fontSize = 9.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main delta text
        Text(
            text = deltaText,
            style = HeadlineSmall.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = Foreground
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Motivational/status subtext
        Text(
            text = motivationalText,
            style = BodySmall.copy(
                color = MutedForeground,
                fontSize = 12.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}


