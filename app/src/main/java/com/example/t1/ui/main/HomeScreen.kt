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
import androidx.compose.ui.draw.drawBehind
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
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    username: String,
    onOpenTimer: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    val score = 85
    val percentile = 76
    var showBreakdown by remember { mutableStateOf(false) }

    // Streak parameters
    val streak = 12
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

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 100.dp) // Extra bottom padding for BottomNav
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

            // 2. Focus Ring Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(16.dp))

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
                    showBreakdown = showBreakdown,
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

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Linear Gradient Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 8.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Border, Color.Transparent)
                        )
                    )
            )

            Spacer(modifier = Modifier.height(20.dp))

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

                    // Sessions
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "24",
                            style = HeadlineMedium.copy(
                                color = Foreground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "SESSIONS",
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

                    // Saved Hours
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "5.5",
                            style = HeadlineMedium.copy(
                                color = Foreground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "SAVED HOURS",
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

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Start Focus Session CTA Button
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
        }
    }
}
