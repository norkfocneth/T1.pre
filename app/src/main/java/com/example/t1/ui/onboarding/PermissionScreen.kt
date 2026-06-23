package com.example.t1.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

data class PermissionItem(
    val title: String,
    val desc: String,
    val icon: ImageVector
)

val permissionItems = listOf(
    PermissionItem(
        title = "Usage Access",
        desc = "Track screen time and app usage",
        icon = Icons.Default.BarChart
    ),
    PermissionItem(
        title = "Notification Access",
        desc = "Send insights and reminders",
        icon = Icons.Default.Notifications
    ),
    PermissionItem(
        title = "Background Activity",
        desc = "Analyze behavior throughout the day",
        icon = Icons.Default.Sync
    )
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PermissionScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    var grantedSet by remember { mutableStateOf(setOf<Int>()) }
    val allGranted = grantedSet.size == permissionItems.size

    var introVisible by remember { mutableStateOf(false) }
    var cardsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        introVisible = true
        delay(300)
        cardsVisible = true
    }

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            // Header Section
            AnimatedVisibility(
                visible = introVisible,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut()
            ) {
                Column {
                    Text(
                        text = "One more thing",
                        style = HeadlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            brush = Brush.linearGradient(
                                colors = listOf(GradientTextStart, GradientTextEnd)
                            )
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "To give you accurate insights, we need access to your usage data.",
                        style = BodyMedium.copy(
                            color = MutedForeground,
                            lineHeight = 20.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "We NEVER store your personal data. Everything stays on your device.",
                        style = BodySmall.copy(
                            color = GlowGreen,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }

            // Cards list
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                permissionItems.forEachIndexed { idx, perm ->
                    val isGranted = grantedSet.contains(idx)

                    var cardVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(cardsVisible) {
                        if (cardsVisible) {
                            delay(idx * 120L)
                            cardVisible = true
                        }
                    }

                    AnimatedVisibility(
                        visible = cardVisible,
                        enter = fadeIn(animationSpec = tween(400)) + androidx.compose.animation.slideInVertically(
                            initialOffsetY = { 25 },
                            animationSpec = tween(400)
                        ),
                        exit = fadeOut()
                    ) {
                        PermissionCard(
                            item = perm,
                            isGranted = isGranted,
                            onAllow = {
                                Haptics.playLight(view)
                                grantedSet = grantedSet + idx
                            }
                        )
                    }
                }
            }

            // Continue Button at bottom
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale = if (isPressed && allGranted) 0.97f else 1f

            val buttonAlpha by animateFloatAsState(
                targetValue = if (allGranted) 1.0f else 0.3f,
                animationSpec = tween(300),
                label = "buttonAlpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(buttonScale)
                    .alpha(buttonAlpha)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Foreground)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = allGranted
                    ) {
                        Haptics.playMedium(view)
                        onContinue()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Continue",
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

@Composable
fun PermissionCard(
    item: PermissionItem,
    isGranted: Boolean,
    onAllow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBorderColor = if (isGranted) Border.copy(alpha = 0.8f) else Border
    val cardBg = if (isGranted) Secondary else Secondary.copy(alpha = 0.3f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, cardBorderColor, RoundedCornerShape(20.dp))
            .background(cardBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Container with animate scale/glow when granted
        val containerScale by animateFloatAsState(
            targetValue = if (isGranted) 1.0f else 0.9f,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "containerScale"
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(containerScale)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isGranted) Foreground else Accent)
                .drawBehind {
                    if (isGranted) {
                        // Shadow glow effect when granted
                        drawCircle(
                            color = Color.White.copy(alpha = 0.15f),
                            radius = size.width * 0.8f
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Icon transition: spring rotation & scale
            AnimatedContent(
                targetState = isGranted,
                transitionSpec = {
                    scaleIn(
                        animationSpec = spring(
                            stiffness = 500f,
                            dampingRatio = 0.6f
                        )
                    ).togetherWith(
                        scaleOut(animationSpec = tween(150))
                    )
                },
                label = "iconSwap"
            ) { granted ->
                if (granted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Granted",
                        tint = Background,
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer {
                                // Rotation effect during entry
                            }
                    )
                } else {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = MutedForeground,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Titles/Descriptions
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = BodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Foreground
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.desc,
                style = BodySmall.copy(
                    color = MutedForeground
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Allow Button
        if (!isGranted) {
            val buttonInteraction = remember { MutableInteractionSource() }
            val isButtonPressed by buttonInteraction.collectIsPressedAsState()
            val buttonScale = if (isButtonPressed) 0.95f else 1f

            Box(
                modifier = Modifier
                    .scale(buttonScale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Foreground)
                    .clickable(
                        interactionSource = buttonInteraction,
                        indication = null,
                        onClick = onAllow
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Allow",
                    style = LabelSmall.copy(
                        color = Background,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
