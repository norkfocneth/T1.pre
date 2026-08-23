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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FocusRing(
    score: Int,
    onTap: () -> Unit,
    showDetails: Boolean = false,
    statusComparisonText: String = "You are better from yesterday",
    statusGoalBadge: String = "",
    statusDeltaText: String = "",
    modifier: Modifier = Modifier
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
            val progressFraction = (animatedScore.value / 100f).coerceIn(0f, 1f)
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

        // 4. Inner dark circle container (230dp)
        Box(
            modifier = Modifier
                .size(230.dp)
                .clip(CircleShape)
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = showDetails,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)).togetherWith(fadeOut(animationSpec = tween(300)))
                },
                label = "centerContent"
            ) { isDetails ->
                if (isDetails) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        if (statusGoalBadge.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF262D4A).copy(alpha = 0.6f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = statusGoalBadge,
                                    style = LabelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8E99B4),
                                        letterSpacing = 1.sp,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Text(
                            text = statusComparisonText,
                            style = HeadlineSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Foreground
                            ),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        if (statusDeltaText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = statusDeltaText,
                                style = BodySmall.copy(
                                    color = MutedForeground,
                                    fontSize = 12.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Score
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        if (score <= 0) {
                            Text(
                                text = "Permission Required",
                                style = LabelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Foreground,
                                    fontSize = 14.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Grant usage access to calculate your focus score",
                                style = BodySmall.copy(
                                    color = MutedForeground,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        } else {
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
                                    fontSize = 11.sp,
                                    letterSpacing = 2.sp,
                                    color = MutedForeground
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
