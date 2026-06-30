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
    streak: Int = 12
) {
    val view = LocalView.current
    val context = LocalContext.current

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
            // 1. Header (Logo + Profile Button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "T1.",
                    style = HeadlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        brush = Brush.linearGradient(
                            colors = listOf(GradientTextStart, GradientTextEnd)
                        )
                    ),
                    letterSpacing = (-1).sp
                )

                // Profile Avatar Button
                val profileInteraction = remember { MutableInteractionSource() }
                val isProfilePressed by profileInteraction.collectIsPressedAsState()
                val profileScale = if (isProfilePressed) 0.9f else 1f

                Box(
                    modifier = Modifier
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

            Spacer(modifier = Modifier.height(24.dp))

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

                // Dynamic segments
                val breakdownSegments = remember(dashboardState.confidence) {
                    listOf(
                        com.example.t1.ui.components.BreakdownSegment("Questionnaire", 100 - dashboardState.confidence, StreakSmall),
                        com.example.t1.ui.components.BreakdownSegment("Behaviour", dashboardState.confidence, Success)
                    )
                }

                // Interactive FocusRing
                FocusRing(
                    score = score,
                    showBreakdown = showBreakdown,
                    breakdownSegments = breakdownSegments,
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

                // "tap ring for breakdown" hint
                AnimatedVisibility(
                    visible = showTapHint,
                    enter = fadeIn(animationSpec = tween(500)),
                    modifier = Modifier.alpha(0.4f)
                ) {
                    Text(
                        text = "tap ring for breakdown",
                        style = BodySmall.copy(
                            fontSize = 10.sp,
                            color = MutedForeground
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

            }

            Spacer(modifier = Modifier.height(24.dp))

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
                        Triple("Social Time", dashboardState.categoryTimes["Social"] ?: behaviour.socialTimeMs, StreakSmall),
                        Triple("Entertainment", dashboardState.categoryTimes["Entertainment"] ?: behaviour.entertainmentTimeMs, Info),
                        Triple("Education", dashboardState.categoryTimes["Education"] ?: behaviour.educationTimeMs, GlowPrimary),
                        Triple("Utility", dashboardState.categoryTimes["Utility"] ?: 0L, MutedForeground)
                    )

                    categories.forEach { (label, durationMs, color) ->
                        val durationMin = durationMs / 60000
                        val hr = durationMin / 60
                        val mn = durationMin % 60
                        val timeStr = if (hr > 0) "${hr}h ${mn}m" else "${mn}m"

                        val fraction = if (behaviour.totalScreenTimeMs > 0) {
                            durationMs.toFloat() / behaviour.totalScreenTimeMs
                        } else 0f

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
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
            .padding(bottom = 90.dp, end = 24.dp), // Positioned above the BottomNav
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(GlowPrimary, Info)
                    )
                )
                .clickable {
                    Haptics.playMedium(view)
                    showAICoach = true
                },
            contentAlignment = Alignment.Center
        ) {
            // Draw a beautiful geometric Megatron/Transformer face logo
            androidx.compose.foundation.Canvas(modifier = Modifier.size(24.dp)) {
                val w = size.width
                val h = size.height
                val path = androidx.compose.ui.graphics.Path().apply {
                    // Decepticon / Transformer geometric crest
                    moveTo(w * 0.2f, h * 0.1f)
                    lineTo(w * 0.8f, h * 0.1f)
                    lineTo(w * 0.9f, h * 0.4f)
                    lineTo(w * 0.6f, h * 0.6f)
                    lineTo(w * 0.7f, h * 0.9f)
                    lineTo(w * 0.5f, h * 0.7f)
                    lineTo(w * 0.3f, h * 0.9f)
                    lineTo(w * 0.4f, h * 0.6f)
                    lineTo(w * 0.1f, h * 0.4f)
                    close()
                }
                drawPath(path, color = Color.White)
                
                // Small glowing red eyes
                drawCircle(color = Destructive, radius = 2.dp.toPx(), center = Offset(w * 0.4f, h * 0.35f))
                drawCircle(color = Destructive, radius = 2.dp.toPx(), center = Offset(w * 0.6f, h * 0.35f))
            }
        }
    }

    // Floating AI Coach Overlay
    if (showAICoach) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
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
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp) // Floating above the BottomNav
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
    }
}
}
