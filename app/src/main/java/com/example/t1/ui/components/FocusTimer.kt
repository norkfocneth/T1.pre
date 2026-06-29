package com.example.t1.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.ui.onboarding.OnboardingBackground
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

val PRESETS = listOf(15, 25, 45, 60)

@Composable
fun FocusTimer(
    onClose: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    var duration by remember { mutableIntStateOf(25) } // minutes
    var timeLeft by remember { mutableIntStateOf(25 * 60) } // seconds
    var isRunning by remember { mutableStateOf(false) }
    var hasStarted by remember { mutableStateOf(false) }

    val totalSeconds = duration * 60
    val elapsedSeconds = if (hasStarted) (totalSeconds - timeLeft).toLong() else 0L

    BackHandler {
        onClose(elapsedSeconds)
    }

    // Sync timeLeft to duration before start
    LaunchedEffect(duration, hasStarted) {
        if (!hasStarted) {
            timeLeft = duration * 60
        }
    }

    // Countdown loop
    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            if (timeLeft <= 1) {
                isRunning = false
                timeLeft = 0
                Haptics.playSuccess(context)
            } else {
                timeLeft -= 1
            }
        }
    }

    val progress = if (hasStarted) (totalSeconds - timeLeft).toFloat() / totalSeconds else 0f
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val isComplete = timeLeft == 0 && hasStarted

    OnboardingBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Close Button top right
            Box(
                modifier = Modifier
                    .padding(top = 24.dp, end = 24.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Secondary)
                    .align(Alignment.TopEnd)
                    .clickable {
                        Haptics.playLight(view)
                        onClose(elapsedSeconds)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MutedForeground,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "FOCUS SESSION",
                    style = TrackingWide.copy(
                        color = MutedForeground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Timer ring layout (280x280dp)
                Box(
                    modifier = Modifier.size(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val radius = 120.dp.toPx()
                        val strokeWidth = 3.dp.toPx()
                        val centerOffset = center

                        // Track Background
                        drawCircle(
                            color = Border,
                            radius = radius,
                            center = centerOffset,
                            style = Stroke(width = strokeWidth),
                            alpha = 0.3f
                        )

                        // Progress Arc
                        val sweepAngle = progress * 360f
                        val progressColor = if (isComplete) Success else Foreground

                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth)
                        )
                    }

                    // Numeric Countdown inside ring
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            style = TimerDisplay.copy(
                                color = Foreground,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        if (isComplete) {
                            Text(
                                text = "Session Complete!",
                                style = BodyMedium.copy(
                                    color = Success,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Duration adjustment (only before session starts)
                if (!hasStarted) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Plus/Minus adjusters
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Secondary)
                                    .clickable {
                                        Haptics.playLight(view)
                                        duration = maxOf(5, duration - 5)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "-",
                                    style = HeadlineSmall.copy(color = MutedForeground)
                                )
                            }

                            Spacer(modifier = Modifier.width(24.dp))

                            Text(
                                text = "$duration min",
                                style = HeadlineSmall.copy(
                                    color = Foreground,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.width(90.dp),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.width(24.dp))

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Secondary)
                                    .clickable {
                                        Haptics.playLight(view)
                                        duration = minOf(120, duration + 5)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+",
                                    style = HeadlineSmall.copy(color = MutedForeground)
                                )
                            }
                        }

                        // Presets Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PRESETS.forEach { p ->
                                val isSelected = duration == p
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) Foreground else Secondary)
                                        .clickable {
                                            Haptics.playLight(view)
                                            duration = p
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${p}m",
                                        style = LabelSmall.copy(
                                            color = if (isSelected) Background else MutedForeground,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Controls row
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Reset Button (appears after starting)
                    if (hasStarted) {
                        val resetInteraction = remember { MutableInteractionSource() }
                        val isResetPressed by resetInteraction.collectIsPressedAsState()
                        val resetScale = if (isResetPressed) 0.9f else 1f

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .scale(resetScale)
                                .clip(CircleShape)
                                .background(Secondary)
                                .clickable(
                                    interactionSource = resetInteraction,
                                    indication = null,
                                    onClick = {
                                        Haptics.playMedium(view)
                                        isRunning = false
                                        hasStarted = false
                                        timeLeft = duration * 60
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = MutedForeground,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(24.dp))
                    }

                    // Play / Pause Button (large center button)
                    val playInteraction = remember { MutableInteractionSource() }
                    val isPlayPressed by playInteraction.collectIsPressedAsState()
                    val playScale = if (isPlayPressed) 0.9f else 1f

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(playScale)
                            .clip(CircleShape)
                            .background(Foreground)
                            .clickable(
                                interactionSource = playInteraction,
                                indication = null,
                                onClick = {
                                    Haptics.playMedium(view)
                                    if (isRunning) {
                                        isRunning = false
                                    } else {
                                        isRunning = true
                                        hasStarted = true
                                    }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Play",
                            tint = Background,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
