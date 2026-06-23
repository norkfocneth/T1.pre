package com.example.t1.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

data class AnalysisStep(
    val text: String,
    val psych: String
)

val analysisSteps = listOf(
    AnalysisStep("Fetching your real screen time data...", "Scanning your digital footprint"),
    AnalysisStep("Comparing with other users...", "Seeing where you stand"),
    AnalysisStep("Detecting distraction patterns...", "Identifying your dopamine triggers"),
    AnalysisStep("Analyzing your real behavior patterns...", "Mapping your focus windows"),
    AnalysisStep("Calculating your real focus score...", "Building your performance profile")
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnalysisScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    var progress by remember { mutableFloatStateOf(0f) }
    var currentStep by remember { mutableIntStateOf(0) }

    // Run the analysis animation for 6000ms
    LaunchedEffect(Unit) {
        val totalDuration = 6000
        val stepDuration = totalDuration / analysisSteps.size
        val startTime = System.currentTimeMillis()
        var lastStep = -1

        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            val fraction = (elapsed.toFloat() / totalDuration).coerceIn(0f, 1f)
            progress = fraction * 100f

            val calculatedStep = (elapsed / stepDuration).toInt().coerceIn(0, analysisSteps.size - 1)
            currentStep = calculatedStep

            if (calculatedStep > lastStep) {
                if (calculatedStep > 0) {
                    Haptics.playLight(view)
                }
                lastStep = calculatedStep
            }

            if (fraction >= 1f) {
                Haptics.playHeavy(view)
                delay(600)
                onComplete()
                break
            }
            delay(30)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular progress ring with pulse glow
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow circle behind
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Foreground.copy(alpha = pulseAlpha), Color.Transparent)
                            )
                        )
                )

                // Arc Drawing
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = 80.dp.toPx()
                    val strokeWidth = 3.dp.toPx()
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)

                    // Track
                    drawCircle(
                        color = Border,
                        radius = radius,
                        center = centerOffset,
                        style = Stroke(width = strokeWidth),
                        alpha = 0.3f
                    )

                    // Progress Arc
                    val sweepAngle = (progress / 100f) * 360f
                    drawArc(
                        color = Foreground,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth)
                    )
                }

                // Center percentage text
                Text(
                    text = "${progress.toInt()}%",
                    style = DisplaySmall.copy(
                        color = Foreground,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Analyzing your real behavior patterns...",
                style = HeadlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Foreground
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Fetching your last 7 days of screen time",
                style = BodySmall.copy(
                    color = MutedForeground,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-status line transition
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)).togetherWith(fadeOut(animationSpec = tween(300)))
                },
                label = "subStatus"
            ) { stepIdx ->
                val subTexts = listOf(
                    "Comparing with other users...",
                    "Detecting distraction patterns...",
                    "Analyzing your real behavior patterns...",
                    "Calculating your real focus score...",
                    "Finalizing your performance mirror..."
                )
                Text(
                    text = subTexts.getOrElse(stepIdx) { "" },
                    style = BodySmall.copy(
                        color = Foreground.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Psych line cycling
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn(animationSpec = tween(350)).togetherWith(fadeOut(animationSpec = tween(350)))
                },
                label = "psychText"
            ) { stepIdx ->
                Text(
                    text = analysisSteps.getOrNull(stepIdx)?.psych ?: "",
                    style = BodySmall.copy(
                        color = MutedForeground,
                        fontSize = 10.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Dynamic Step Checklist
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                analysisSteps.forEachIndexed { idx, step ->
                    val isCompleted = idx < currentStep
                    val isActive = idx == currentStep
                    val isPending = idx > currentStep

                    val alpha = if (isPending) 0.3f else 1.0f

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(alpha),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Animated status indicator circle/check
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = when {
                                    isCompleted -> "completed"
                                    isActive -> "active"
                                    else -> "pending"
                                },
                                transitionSpec = {
                                    scaleIn(
                                        animationSpec = spring(
                                            stiffness = Spring.StiffnessMediumLow,
                                            dampingRatio = Spring.DampingRatioMediumBouncy
                                        )
                                    ).togetherWith(scaleOut(animationSpec = tween(150)))
                                },
                                label = "stepIndicator"
                            ) { state ->
                                when (state) {
                                    "completed" -> {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Foreground),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Completed",
                                                tint = Background,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    "active" -> {
                                        Box(contentAlignment = Alignment.Center) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(Foreground)
                                            )
                                            val pulseIndicator = rememberInfiniteTransition(label = "pulseIndicator")
                                            val innerPulseScale by pulseIndicator.animateFloat(
                                                initialValue = 1f,
                                                targetValue = 1.6f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(1000, easing = LinearEasing),
                                                    repeatMode = RepeatMode.Restart
                                                ),
                                                label = "innerPulse"
                                            )
                                            val innerPulseAlpha by pulseIndicator.animateFloat(
                                                initialValue = 0.5f,
                                                targetValue = 0f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(1000, easing = LinearEasing),
                                                    repeatMode = RepeatMode.Restart
                                                ),
                                                label = "innerAlpha"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .scale(innerPulseScale)
                                                    .alpha(innerPulseAlpha)
                                                    .clip(CircleShape)
                                                    .background(Foreground)
                                            )
                                        }
                                    }
                                    else -> {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(MutedForeground)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Text sliding animation on completion (+4dp offset)
                        val textOffset by animateFloatAsState(
                            targetValue = if (isCompleted) 4f else 0f,
                            animationSpec = tween(300),
                            label = "textOffset"
                        )

                        Text(
                            text = step.text,
                            style = BodyMedium.copy(
                                fontWeight = if (isActive || isCompleted) FontWeight.Medium else FontWeight.Normal,
                                color = if (isActive || isCompleted) Foreground else MutedForeground
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .alpha(if (isPending) 0.6f else 1.0f)
                                .padding(start = textOffset.dp)
                        )
                    }
                }
            }
        }
    }
}
