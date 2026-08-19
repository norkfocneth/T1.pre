package com.example.t1.ui.tour

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.Border
import com.example.t1.theme.Foreground
import com.example.t1.theme.HeadlineSmall
import com.example.t1.theme.LabelMedium
import com.example.t1.theme.LabelSmall
import com.example.t1.theme.MutedForeground
import com.example.t1.theme.TrackingWide
import com.example.t1.util.Haptics

private val TourPrimary = Color(0xFF38BDF8) // Bright Cyan / Sky Blue
private val TourAccent = Color(0xFF3B82F6)  // Vibrant Royal Blue
private val TourGlow = Color(0xFF60A5FA)    // Soft Glow

/**
 * Interactive Spotlight Coach Mark Overlay for first-time onboarding tour.
 * Darkens background, highlights active UI components with glowing animated cutouts,
 * and renders a glassmorphic educational card.
 */
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

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val view = LocalView.current

    // Keep active screen/tab synced with the step target
    LaunchedEffect(currentStepIndex) {
        onStepSync(currentStep)
    }

    // Intercept hardware back button to navigate backwards in tour
    BackHandler {
        if (currentStepIndex > 0) {
            currentStepIndex--
        } else {
            onSkipTour()
        }
    }

    // Subtle pulsing neon glow animation around spotlight
    val infiniteTransition = rememberInfiniteTransition(label = "spotlight_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(enabled = false) {} // Prevent touches leaking through to background
    ) {
        // ══════════════════════════════════════════════════════════════════════
        // 1. SPOTLIGHT SCRIM CANVAS (DARK OVERLAY + CUTOUT + NEON OUTLINE)
        // ══════════════════════════════════════════════════════════════════════
        val targetRect = remember(currentStep.highlightTarget, screenWidthPx, screenHeightPx) {
            calculateTargetRect(
                target = currentStep.highlightTarget,
                width = screenWidthPx,
                height = screenHeightPx,
                density = density.density
            )
        }

        if (targetRect != null) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = 0.99f) // Required for BlendMode.Clear
            ) {
                // Dim full screen with dark scrim
                drawRect(
                    color = Color(0xFF040407).copy(alpha = 0.88f),
                    size = size
                )

                // Cut out spotlight rectangle
                val cornerRadiusPx = 24.dp.toPx()
                val path = Path().apply {
                    addRoundRect(
                        roundRect = RoundRect(
                            rect = targetRect,
                            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                        )
                    )
                }
                drawPath(
                    path = path,
                    color = Color.Transparent,
                    blendMode = BlendMode.Clear
                )

                // Draw glowing neon border around cutout
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            TourPrimary.copy(alpha = pulseAlpha),
                            TourGlow.copy(alpha = pulseAlpha * 0.7f),
                            TourPrimary.copy(alpha = pulseAlpha * 0.3f)
                        )
                    ),
                    topLeft = targetRect.topLeft,
                    size = targetRect.size,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        } else {
            // Full-screen dark backdrop for Welcome Step
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF050508).copy(alpha = 0.92f))
            )
        }

        // ══════════════════════════════════════════════════════════════════════
        // 2. TOP BAR: SKIP TOUR BUTTON
        // ══════════════════════════════════════════════════════════════════════
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF141418).copy(alpha = 0.8f))
                    .border(1.dp, Border.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .clickable {
                        Haptics.playLight(view)
                        onSkipTour()
                    }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Skip Tour",
                        style = LabelSmall.copy(
                            color = MutedForeground,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Skip Tour",
                        tint = MutedForeground,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════════
        // 3. COACH MARK CARD
        // ══════════════════════════════════════════════════════════════════════
        val cardAlignment = when (currentStep.highlightTarget) {
            HighlightTarget.FULL_SCREEN_WELCOME -> Alignment.Center
            HighlightTarget.FOCUS_RING -> Alignment.BottomCenter
            HighlightTarget.GOAL_BADGE -> Alignment.TopCenter
            HighlightTarget.SCREEN_TIME_CARD -> Alignment.TopCenter
            HighlightTarget.APP_CATEGORIES -> Alignment.TopCenter
            HighlightTarget.LEADERBOARD_PODIUM -> Alignment.BottomCenter
            HighlightTarget.TOKENS_WALLET -> Alignment.BottomCenter
            HighlightTarget.PROFILE_IDENTITY -> Alignment.BottomCenter
            HighlightTarget.SETTINGS_PRIVACY -> Alignment.BottomCenter
            HighlightTarget.SETTINGS_DELETE -> Alignment.TopCenter
        }

        val cardPaddingModifier = when (cardAlignment) {
            Alignment.BottomCenter -> Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp)
            Alignment.TopCenter -> Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 70.dp, start = 20.dp, end = 20.dp)
            else -> Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
        }

        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                (slideInVertically { 30 } + fadeIn(tween(250)))
                    .togetherWith(slideOutVertically { -30 } + fadeOut(tween(200)))
            },
            label = "coach_mark_anim",
            modifier = cardPaddingModifier
        ) { step ->
            CoachMarkCard(
                step = step,
                totalSteps = steps.size - 1, // Exclude step 0 in progress count
                onBack = {
                    if (currentStepIndex > 0) {
                        Haptics.playLight(view)
                        currentStepIndex--
                    }
                },
                onNext = {
                    Haptics.playLight(view)
                    if (currentStepIndex < steps.size - 1) {
                        currentStepIndex++
                    } else {
                        onCompleteTour()
                    }
                }
            )
        }
    }
}

/**
 * The Glassmorphic Coach Mark Content Card.
 */
@Composable
private fun CoachMarkCard(
    step: TourStep,
    totalSteps: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Ambient soft blue neon glow behind card
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .blur(20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(TourPrimary.copy(alpha = 0.15f))
        )

        // Glassmorphic Card Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF16161D),
                            Color(0xFF0F0F14)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Border.copy(alpha = 0.8f),
                            TourPrimary.copy(alpha = 0.3f),
                            Border.copy(alpha = 0.4f)
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Step Badge & Dots Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step Badge Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TourPrimary.copy(alpha = 0.15f))
                        .border(0.5.dp, TourPrimary.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(TourPrimary)
                    )
                    Text(
                        text = step.stepLabel,
                        style = TrackingWide.copy(
                            color = TourPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }

                // Micro Progress Dots (Steps 1..9)
                if (!step.isWelcome) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..totalSteps) {
                            val isActive = i == step.id
                            val isDone = i < step.id
                            Box(
                                modifier = Modifier
                                    .size(width = if (isActive) 14.dp else 5.dp, height = 5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        when {
                                            isActive -> TourPrimary
                                            isDone -> TourPrimary.copy(alpha = 0.45f)
                                            else -> Color(0xFF262630)
                                        }
                                    )
                            )
                        }
                    }
                }
            }

            // Title
            Text(
                text = step.title,
                style = HeadlineSmall.copy(
                    color = Foreground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 26.sp
                )
            )

            // Description Body
            Text(
                text = step.description,
                style = LabelMedium.copy(
                    color = Color(0xFFC8C8D2),
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (hidden on Welcome)
                if (!step.isWelcome) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B1B22))
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .clickable { onBack() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
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

                // Primary Next / Explore CTA
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    TourAccent,
                                    TourPrimary
                                )
                            )
                        )
                        .clickable { onNext() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = step.primaryButtonText,
                            style = LabelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Calculates spotlight cutout bounding box in screen pixels for each targeted element.
 */
private fun calculateTargetRect(
    target: HighlightTarget,
    width: Float,
    height: Float,
    density: Float
): Rect? {
    val dpToPx = density

    return when (target) {
        HighlightTarget.FULL_SCREEN_WELCOME -> null

        HighlightTarget.FOCUS_RING -> {
            // Focus ring centered horizontally in top area of HomeScreen
            val cx = width * 0.5f
            val cy = 350f * dpToPx
            val radius = 150f * dpToPx
            Rect(cx - radius, cy - radius, cx + radius, cy + radius)
        }

        HighlightTarget.GOAL_BADGE -> {
            // Focus Goal & Comparison card right below Focus Ring
            val left = 20f * dpToPx
            val right = width - 20f * dpToPx
            val top = 540f * dpToPx
            val bottom = 710f * dpToPx
            Rect(left, top, right, bottom)
        }

        HighlightTarget.SCREEN_TIME_CARD -> {
            // Screen Time & Activity Breakdown Card on HomeScreen
            val left = 16f * dpToPx
            val right = width - 16f * dpToPx
            val top = 635f * dpToPx
            val bottom = 780f * dpToPx
            Rect(left, top, right, bottom)
        }

        HighlightTarget.APP_CATEGORIES -> {
            // Dual-Purpose App Categories breakdown section
            val left = 16f * dpToPx
            val right = width - 16f * dpToPx
            val top = 635f * dpToPx
            val bottom = 820f * dpToPx
            Rect(left, top, right, bottom)
        }

        HighlightTarget.LEADERBOARD_PODIUM -> {
            // Leaderboard Podium (#1, #2, #3)
            val left = 16f * dpToPx
            val right = width - 16f * dpToPx
            val top = 100f * dpToPx
            val bottom = 445f * dpToPx
            Rect(left, top, right, bottom)
        }

        HighlightTarget.TOKENS_WALLET -> {
            // Top Bar Token Wallet Pill
            val cx = width * 0.5f
            val halfWidth = 80f * dpToPx
            val top = 40f * dpToPx
            val bottom = 95f * dpToPx
            Rect(cx - halfWidth, top, cx + halfWidth, bottom)
        }

        HighlightTarget.PROFILE_IDENTITY -> {
            // Profile identity avatar, streak badge & stats
            val left = 16f * dpToPx
            val right = width - 16f * dpToPx
            val top = 55f * dpToPx
            val bottom = 270f * dpToPx
            Rect(left, top, right, bottom)
        }

        HighlightTarget.SETTINGS_PRIVACY -> {
            // Privacy Policy & Settings Options
            val left = 16f * dpToPx
            val right = width - 16f * dpToPx
            val top = 260f * dpToPx
            val bottom = 480f * dpToPx
            Rect(left, top, right, bottom)
        }

        HighlightTarget.SETTINGS_DELETE -> {
            // Danger Zone / Delete Account Section
            val left = 16f * dpToPx
            val right = width - 16f * dpToPx
            val top = 490f * dpToPx
            val bottom = 720f * dpToPx
            Rect(left, top, right, bottom)
        }
    }
}
