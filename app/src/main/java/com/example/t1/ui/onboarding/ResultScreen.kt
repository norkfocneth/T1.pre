package com.example.t1.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun ResultScreen(
    score: Int,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    // Animated score count
    val animatedScore = remember { Animatable(0f) }
    val percentile = remember(score) {
        // Reproduce percentile calculation: Math.round(score * 0.7 + Math.random() * 10)
        (score * 0.7f + (Math.random() * 10).toFloat()).toInt().coerceIn(1, 99)
    }

    // Animation visibility states (staggered)
    var showRing by remember { mutableStateOf(false) }
    var showScoreText by remember { mutableStateOf(false) }
    var showPercentile by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showEmotionalLine by remember { mutableStateOf(false) }
    var showCTA by remember { mutableStateOf(false) }

    LaunchedEffect(score) {
        // Start ring reveal
        showRing = true
        delay(400)
        showScoreText = true
        
        // Eased score counter from 0 to target score
        delay(200)
        animatedScore.animateTo(
            targetValue = score.toFloat(),
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        // Rest of staggered reveal
        delay(1500)
        showPercentile = true
        delay(300)
        showStats = true
        delay(400)
        showEmotionalLine = true
        delay(300)
        showCTA = true
    }

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Score Ring with staggered entry
            AnimatedVisibility(
                visible = showRing,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = tween(800)
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier.size(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val radius = 110.dp.toPx()
                        val strokeWidth = 3.dp.toPx()
                        val centerOffset = Offset(size.width / 2f, size.height / 2f)

                        // Track Background
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
                            color = Foreground,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth)
                        )
                    }

                    // Score Labels inside the ring
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedVisibility(
                            visible = showScoreText,
                            enter = fadeIn(animationSpec = tween(400))
                        ) {
                            Text(
                                text = "YOUR FOCUS SCORE",
                                style = TrackingWide.copy(
                                    color = MutedForeground,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Text(
                            text = "${animatedScore.value.toInt()}",
                            style = DisplayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                brush = Brush.linearGradient(
                                    colors = listOf(GradientTextStart, GradientTextEnd)
                                )
                            ),
                            lineHeight = 72.sp
                        )
                    }
                }
            }

            // Percentile comparison
            AnimatedVisibility(
                visible = showPercentile,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(500)
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = remember(percentile) {
                        "You are ahead of $percentile% of users"
                    },
                    style = BodyMedium.copy(
                        color = MutedForeground
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // Stats row (staggered)
            AnimatedVisibility(
                visible = showStats,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(500)
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "4.2h",
                            style = HeadlineMedium.copy(
                                color = Foreground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AVG SCREEN TIME",
                            style = TrackingNarrow.copy(
                                color = MutedForeground,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Border)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Social",
                            style = HeadlineMedium.copy(
                                color = Foreground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "TOP CATEGORY",
                            style = TrackingNarrow.copy(
                                color = MutedForeground,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Emotional line
            AnimatedVisibility(
                visible = showEmotionalLine,
                enter = fadeIn(animationSpec = tween(600)),
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Text(
                    text = "You're doing better than average — but there's room to improve.",
                    style = BodySmall.copy(
                        color = MutedForeground,
                        lineHeight = 18.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(260.dp)
                )
            }

            // CTA Button
            AnimatedVisibility(
                visible = showCTA,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(500)
                )
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale = if (isPressed) 0.97f else 1f

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp)
                        .scale(scale)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Foreground)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onContinue
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Let's Begin",
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
