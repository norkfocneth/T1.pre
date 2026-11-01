package com.example.t1.ui.tour

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.Border
import com.example.t1.theme.Foreground
import com.example.t1.theme.HeadlineMedium
import com.example.t1.theme.HeadlineSmall
import com.example.t1.theme.LabelMedium
import com.example.t1.theme.LabelSmall
import com.example.t1.theme.MutedForeground
import com.example.t1.util.Haptics

private val TourPrimary = Color(0xFF38BDF8)
private val TourAccent = Color(0xFF6366F1)
private val CardBg = Color(0xFF0F0F14)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GuidedProductTourOverlay(
    userGoal: String,
    onStepSync: (TourStep) -> Unit,
    onCompleteTour: () -> Unit,
    onSkipTour: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = remember(userGoal) { TourStepCatalog.getSteps(userGoal) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentStep = steps.getOrElse(currentStepIndex) { steps.first() }
    val view = LocalView.current

    // Whether the detailed interactive inspection dialog is open
    var isDetailOpen by remember { mutableStateOf(false) }

    LaunchedEffect(currentStepIndex) {
        onStepSync(currentStep)
        isDetailOpen = false
    }

    BackHandler {
        if (isDetailOpen) {
            isDetailOpen = false
        } else if (currentStepIndex > 0) {
            currentStepIndex--
        } else {
            onSkipTour()
        }
    }

    // Bouncing Arrow Animation
    val infiniteTransition = rememberInfiniteTransition(label = "arrow_bounce")
    val bounceAnim by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Pulsing glow animation
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF040407).copy(alpha = if (isDetailOpen) 0.85f else 0.65f))
    ) {
        // ══════════════════════════════════════════════════════════════════
        // 1. TOP BAR: Step Counter & Skip Button
        // ══════════════════════════════════════════════════════════════════
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF181822).copy(alpha = 0.9f))
                    .border(1.dp, Color(0xFF2E2E3E), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "FEATURE  ${currentStepIndex + 1} OF ${steps.size}",
                    style = LabelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5B4FC),
                        letterSpacing = 1.1.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E1E28).copy(alpha = 0.85f))
                    .border(1.dp, Color(0xFF38384A), RoundedCornerShape(20.dp))
                    .clickable {
                        Haptics.playLight(view)
                        onSkipTour()
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Skip Tour",
                        style = LabelSmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Skip Tour",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════
        // 2. INTERACTIVE TARGET SPOTLIGHT & ANIMATED ARROW (When not in detail)
        // ══════════════════════════════════════════════════════════════════
        if (!isDetailOpen) {
            TargetSpotlightOverlay(
                step = currentStep,
                bounceAnim = bounceAnim,
                pulseGlow = pulseGlow,
                onTargetClick = {
                    Haptics.playMedium(view)
                    isDetailOpen = true
                }
            )
        }

        // ══════════════════════════════════════════════════════════════════
        // 3. BOTTOM PROMPT BANNER (When detail is closed)
        // ══════════════════════════════════════════════════════════════════
        if (!isDetailOpen) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF111116).copy(alpha = 0.95f))
                    .border(1.dp, Color(0xFF282838), RoundedCornerShape(20.dp))
                    .clickable {
                        Haptics.playMedium(view)
                        isDetailOpen = true
                    }
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentStep.stepLabel,
                            style = LabelSmall.copy(
                                color = Color(0xFFA5B4FC),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentStep.featureName,
                            style = HeadlineSmall.copy(
                                color = Foreground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(TourAccent, TourPrimary)
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap to Inspect ➔",
                            style = LabelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        )
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════
        // 4. DEEP-DIVE INSPECTION DIALOG (WITH COMPETITOR COMPARISON)
        // ══════════════════════════════════════════════════════════════════
        AnimatedVisibility(
            visible = isDetailOpen,
            enter = fadeIn(tween(250)) + slideInVertically(initialOffsetY = { 300 }),
            exit = fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { 300 }),
            modifier = Modifier.fillMaxSize()
        ) {
            FeatureDetailModal(
                step = currentStep,
                onClose = { isDetailOpen = false },
                onNext = {
                    Haptics.playLight(view)
                    if (currentStepIndex < steps.size - 1) {
                        currentStepIndex++
                    } else {
                        onCompleteTour()
                    }
                },
                onBack = {
                    Haptics.playLight(view)
                    if (currentStepIndex > 0) {
                        currentStepIndex--
                    }
                }
            )
        }
    }
}

/**
 * Renders target spotlight box and animated pointing arrow directly over the element.
 */
@Composable
private fun TargetSpotlightOverlay(
    step: TourStep,
    bounceAnim: Float,
    pulseGlow: Float,
    onTargetClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp

    // Calculate vertical center for each element
    val targetTopDp = when (step.highlightTarget) {
        HighlightTarget.TOKEN_WALLET -> 48.dp
        HighlightTarget.FOCUS_RING -> 180.dp
        HighlightTarget.GOAL_CARD -> (screenHeightDp * 0.54f).dp
        HighlightTarget.BATTLE_ARENA -> 120.dp
        HighlightTarget.WHITELIST_TOOLS -> 360.dp
        HighlightTarget.LEADERBOARD_PODIUM -> 100.dp
    }

    val targetHeightDp = when (step.highlightTarget) {
        HighlightTarget.TOKEN_WALLET -> 52.dp
        HighlightTarget.FOCUS_RING -> 230.dp
        HighlightTarget.GOAL_CARD -> 130.dp
        HighlightTarget.BATTLE_ARENA -> 210.dp
        HighlightTarget.WHITELIST_TOOLS -> 170.dp
        HighlightTarget.LEADERBOARD_PODIUM -> 220.dp
    }

    val targetWidthDp = when (step.highlightTarget) {
        HighlightTarget.TOKEN_WALLET -> 170.dp
        HighlightTarget.FOCUS_RING -> 250.dp
        else -> (configuration.screenWidthDp - 36).dp
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Glowing target box around the real feature
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = targetTopDp)
                .width(targetWidthDp)
                .height(targetHeightDp)
                .clip(RoundedCornerShape(22.dp))
                .border(
                    width = 2.dp,
                    color = TourPrimary.copy(alpha = pulseGlow),
                    shape = RoundedCornerShape(22.dp)
                )
                .background(TourPrimary.copy(alpha = 0.08f))
                .clickable(onClick = onTargetClick)
        )

        // Bouncing Arrow & Pulsing Callout Pill
        val calloutY = if (step.arrowDirection == ArrowDirection.POINTING_DOWN) {
            targetTopDp - 60.dp + bounceAnim.dp
        } else {
            targetTopDp + targetHeightDp + 12.dp + bounceAnim.dp
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = calloutY)
                .padding(horizontal = 24.dp)
                .clickable(onClick = onTargetClick),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (step.arrowDirection == ArrowDirection.POINTING_DOWN) {
                InteractiveCalloutPill(text = step.tapPrompt)
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Inspect",
                    tint = TourPrimary,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Inspect",
                    tint = TourPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                InteractiveCalloutPill(text = step.tapPrompt)
            }
        }
    }
}

@Composable
private fun InteractiveCalloutPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E1E2C))
            .border(1.dp, TourPrimary.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TouchApp,
                contentDescription = null,
                tint = TourPrimary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = LabelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
        }
    }
}

/**
 * Detailed bottom sheet showing complete feature breakdown AND competitor comparison.
 */
@Composable
private fun FeatureDetailModal(
    step: TourStep,
    onClose: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 54.dp, bottom = 12.dp, start = 14.dp, end = 14.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(CardBg)
                .border(1.dp, Color(0xFF282838), RoundedCornerShape(26.dp))
                .padding(20.dp)
                .verticalScroll(scrollState)
        ) {
            // Header: Step pill & Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TourAccent.copy(alpha = 0.22f))
                        .border(1.dp, TourAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✦  ${step.stepLabel}",
                        style = LabelSmall.copy(
                            color = Color(0xFFA5B4FC),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            letterSpacing = 1.1.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E28))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Feature Name
            Text(
                text = step.featureName,
                style = HeadlineMedium.copy(
                    color = Foreground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Summary description
            Text(
                text = step.whatItDoesSummary,
                style = LabelMedium.copy(
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ══════════════════════════════════════════════════════════════
            // SECTION 1: HOW IT WORKS IN PRACTICE
            // ══════════════════════════════════════════════════════════════
            Text(
                text = "HOW TO USE THIS FEATURE",
                style = LabelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            step.howToUseSteps.forEachIndexed { index, stepText ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(TourAccent.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = LabelSmall.copy(
                                color = Color(0xFFA5B4FC),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stepText,
                        style = LabelMedium.copy(
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            lineHeight = 18.5.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ══════════════════════════════════════════════════════════════
            // SECTION 2: WHY T1 BEATS COMPETITION (Forest / Opal / Freedom)
            // ══════════════════════════════════════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF09090D))
                    .border(1.dp, Color(0xFF232332), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "⚔️ WHY T1 IS BETTER THAN ${step.competitorName.uppercase()}",
                        style = LabelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF38BDF8),
                            letterSpacing = 1.1.sp
                        )
                    )

                    step.comparisons.forEach { cmp ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "• " + cmp.title,
                                style = LabelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            )
                            // T1 Advantage
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = "✓ T1: ",
                                    style = LabelSmall.copy(
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    )
                                )
                                Text(
                                    text = cmp.t1Advantage,
                                    style = LabelSmall.copy(
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Competitor Flaw
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = "✕ Others: ",
                                    style = LabelSmall.copy(
                                        color = Color(0xFFEF4444),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    )
                                )
                                Text(
                                    text = cmp.competitorFlaw,
                                    style = LabelSmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!step.isWelcome) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E1E28))
                            .border(1.dp, Border, RoundedCornerShape(14.dp))
                            .clickable {
                                onBack()
                                onClose()
                            }
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Back",
                            style = LabelMedium.copy(
                                color = MutedForeground,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(TourAccent, TourPrimary)
                            )
                        )
                        .clickable {
                            onNext()
                            onClose()
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (step.isFinal) "Finish Tour & Start Focusing ✨" else "Got it! Next Feature ➔",
                        style = LabelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    )
                }
            }
        }
    }
}
