package com.example.t1.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*

data class BreakdownSegment(
    val label: String,
    val value: Int,
    val color: Color
)

val breakdownData = listOf(
    BreakdownSegment("Social", 35, BreakdownSocial),
    BreakdownSegment("Productive", 45, BreakdownProductive),
    BreakdownSegment("Idle", 20, MutedForeground)
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FocusRing(
    score: Int,
    onTap: () -> Unit,
    showBreakdown: Boolean,
    modifier: Modifier = Modifier,
    breakdownSegments: List<BreakdownSegment> = listOf(
        BreakdownSegment("Social", 35, BreakdownSocial),
        BreakdownSegment("Productive", 45, BreakdownProductive),
        BreakdownSegment("Idle", 20, MutedForeground)
    )
) {
    val animatedScore = remember { Animatable(0f) }

    LaunchedEffect(score) {
        animatedScore.animateTo(
            targetValue = score.toFloat(),
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    val interactionSource = remember { MutableInteractionSource() }

    // Outer dashed rotation
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )

    // Pulse glow animation
    val pulseTransition = rememberInfiniteTransition(label = "glow")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.03f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .size(300.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        // 1. Outer dashed ring (rotating 60s)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationAngle)
        ) {
            val radius = 145.dp.toPx()
            drawCircle(
                color = Border,
                radius = radius,
                center = center,
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 8.dp.toPx()), 0f)
                ),
                alpha = 0.4f
            )
        }

        // 2. Pulse glow layer
        Box(
            modifier = Modifier
                .size(270.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(GlowPrimary.copy(alpha = pulseAlpha), Color.Transparent)
                    )
                )
        )

        // 3. Score ring track & arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = 130.dp.toPx()
            val strokeWidth = 3.dp.toPx()
            val centerOffset = center

            // Background Track
            drawCircle(
                color = Border,
                radius = radius,
                center = centerOffset,
                style = Stroke(width = strokeWidth),
                alpha = 0.3f
            )

            // Progress Arc
            val progressFraction = animatedScore.value / 100f
            val sweepAngle = progressFraction * 360f
            drawArc(
                color = GlowPrimary, // Using GlowPrimary token for primary active ring
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
        }

        // 4. Breakdown arcs (drawn as segments of circle radius 115dp with stroke 6dp)
        if (showBreakdown) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = 115.dp.toPx()
                val strokeWidth = 6.dp.toPx()
                val centerOffset = center

                var accumulatedAngle = -90f

                breakdownSegments.forEach { segment ->
                    val sweepAngle = (segment.value / 100f) * 360f

                    // Draw arc segment (reducing angle slightly to add a small gap)
                    drawArc(
                        color = segment.color,
                        startAngle = accumulatedAngle + 2f,
                        sweepAngle = sweepAngle - 4f,
                        useCenter = false,
                        topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth)
                    )

                    accumulatedAngle += sweepAngle
                }
            }
        }

        // 5. Inner dark circle container (230dp)
        Box(
            modifier = Modifier
                .size(230.dp)
                .clip(CircleShape)
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = showBreakdown,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)).togetherWith(fadeOut(animationSpec = tween(300)))
                },
                label = "centerContent"
            ) { displayingBreakdown ->
                if (displayingBreakdown) {
                    // Legend
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        breakdownSegments.forEach { segment ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(segment.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = segment.label,
                                    style = BodySmall.copy(
                                        color = MutedForeground,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.width(80.dp)
                                )
                                Text(
                                    text = "${segment.value}%",
                                    style = BodySmall.copy(
                                        color = Foreground,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                } else {
                    // Score
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${animatedScore.value.toInt()}",
                            style = ScoreXL.copy(
                                fontWeight = FontWeight.Bold,
                                brush = Brush.linearGradient(
                                    colors = listOf(GradientTextStart, GradientTextEnd)
                                )
                            ),
                            lineHeight = 72.sp
                        )
                        Text(
                            text = "FOCUS SCORE",
                            style = TrackingWide.copy(
                                color = MutedForeground,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
