package com.example.t1.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.ui.onboarding.OnboardingBackground
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

val weeklyScores = listOf(72, 68, 75, 80, 78, 85, 82)
val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

data class Achievement(
    val icon: ImageVector,
    val label: String,
    val unlocked: Boolean
)

val achievements = listOf(
    Achievement(Icons.Default.LocalFireDepartment, "7 Day Streak", true),
    Achievement(Icons.Default.Star, "Top 10% Performer", true),
    Achievement(Icons.Default.FlashOn, "100 Focus Hours", true),
    Achievement(Icons.Default.EmojiEvents, "Elite Rank", false)
)

@Composable
fun ProfileScreen(
    onBack: (() -> Unit)?,
    onSettings: () -> Unit,
    onSignOut: () -> Unit,
    username: String,
    onUpdateName: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    var focusReminders by remember { mutableStateOf(false) }
    var privacyMode by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }

    var name by remember(username) { mutableStateOf(username) }
    var title by remember { mutableStateOf("Deep Work Enthusiast") }

    var editName by remember { mutableStateOf(name) }
    var editTitle by remember { mutableStateOf(title) }

    var showSignOut by remember { mutableStateOf(false) }

    val handleSaveEdit = {
        name = editName
        title = editTitle
        isEditing = false
        onUpdateName?.invoke(editName)
    }

    val maxScore = remember { weeklyScores.maxOrNull() ?: 100 }

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp), // space for bottom navigation
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                if (onBack != null) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Foreground,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clickable {
                                Haptics.playLight(view)
                                onBack()
                            }
                    )
                }

                Text(
                    text = "PROFILE",
                    style = TrackingWide.copy(
                        color = Foreground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MutedForeground,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            Haptics.playLight(view)
                            onSettings()
                        }
                )
            }

            // Avatar Section
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.size(112.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circular border container
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(Secondary)
                        .border(2.dp, Border, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
                    Text(
                        text = initials,
                        style = DisplaySmall.copy(
                            color = MutedForeground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Edit/Pencil Float Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Foreground)
                        .border(1.dp, Border, CircleShape)
                        .align(Alignment.BottomEnd)
                        .clickable {
                            Haptics.playLight(view)
                            editName = name
                            editTitle = title
                            isEditing = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Background,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Name Editing / Display
            if (isEditing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Name Edit Input
                    BasicTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        textStyle = TextStyle(
                            fontFamily = SpaceGrotesk,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Foreground,
                            textAlign = TextAlign.Center
                        ),
                        cursorBrush = SolidColor(Foreground),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Secondary)
                            .border(1.dp, Border, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )

                    // Title Edit Input
                    BasicTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        textStyle = TextStyle(
                            fontFamily = Inter,
                            fontSize = 12.sp,
                            color = MutedForeground,
                            textAlign = TextAlign.Center
                        ),
                        cursorBrush = SolidColor(Foreground),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Secondary)
                            .border(1.dp, Border, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    // Edit Actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, Border, RoundedCornerShape(20.dp))
                                .clickable { isEditing = false }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                style = LabelSmall.copy(color = MutedForeground)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Foreground)
                                .clickable { handleSaveEdit() }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Save",
                                style = LabelSmall.copy(color = Background, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = name,
                    style = HeadlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        brush = Brush.linearGradient(
                            colors = listOf(GradientTextStart, GradientTextEnd)
                        )
                    )
                )
                Text(
                    text = title.uppercase(),
                    style = TrackingWide.copy(
                        color = MutedForeground,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stat numbers card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "412",
                        style = ScoreLarge.copy(color = Foreground, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "FOCUS HOURS",
                        style = TrackingNarrow.copy(
                            color = MutedForeground,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .background(Border)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "84",
                        style = ScoreLarge.copy(color = Foreground, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "SESSIONS",
                        style = TrackingNarrow.copy(
                            color = MutedForeground,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weekly Trend Bar Chart
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Trending Up",
                        tint = MutedForeground,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WEEKLY TREND",
                        style = TrackingNarrow.copy(
                            color = MutedForeground,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyScores.forEachIndexed { i, score ->
                        val ratio = score.toFloat() / maxScore
                        val isToday = i == weeklyScores.size - 1

                        var heightFraction by remember { mutableStateOf(0f) }
                        LaunchedEffect(Unit) {
                            delay(300 + i * 60L)
                            heightFraction = ratio
                        }

                        val animatedHeight by animateFloatAsState(
                            targetValue = heightFraction,
                            animationSpec = tween(500),
                            label = "barHeight"
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$score",
                                style = BodySmall.copy(
                                    color = MutedForeground,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(animatedHeight.coerceAtLeast(0.05f))
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(
                                        if (isToday) {
                                            Brush.verticalGradient(
                                                colors = listOf(Foreground, Foreground.copy(alpha = 0.3f))
                                            )
                                        } else {
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    MutedForeground.copy(alpha = 0.4f),
                                                    Secondary
                                                )
                                            )
                                        }
                                    )
                                    .drawBehind {
                                        if (isToday) {
                                            drawRect(
                                                color = Color.White.copy(alpha = 0.15f),
                                                size = size
                                            )
                                        }
                                    }
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = weekDays[i],
                                style = BodySmall.copy(
                                    color = MutedForeground,
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Achievements Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "ACHIEVEMENTS",
                    style = TrackingWide.copy(
                        color = MutedForeground,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // First 2 Achievements
                    achievements.take(2).forEach { ach ->
                        AchievementCard(ach, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Next 2 Achievements
                    achievements.drop(2).take(2).forEach { ach ->
                        AchievementCard(ach, modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Preferences checklist
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "ACCOUNT PREFERENCES",
                    style = TrackingWide.copy(
                        color = MutedForeground,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Focus Reminders Preferences Toggle
                PreferenceRow(
                    icon = Icons.Default.Notifications,
                    title = "Focus Reminders",
                    trailing = {
                        T1Toggle(
                            value = focusReminders,
                            onValueChange = {
                                Haptics.playLight(view)
                                focusReminders = it
                            }
                        )
                    }
                )

                // Pomo Interval Info
                PreferenceRow(
                    icon = Icons.Default.Timer,
                    title = "Pomo Interval",
                    trailing = {
                        Text(
                            text = "25:00",
                            style = BodyMedium.copy(color = MutedForeground)
                        )
                    }
                )

                // Privacy Mode Toggle
                PreferenceRow(
                    icon = Icons.Default.Shield,
                    title = "Privacy Mode",
                    trailing = {
                        T1Toggle(
                            value = privacyMode,
                            onValueChange = {
                                Haptics.playLight(view)
                                privacyMode = it
                            }
                        )
                    }
                )

                // Sign Out Action Button
                PreferenceRow(
                    icon = Icons.Default.ExitToApp,
                    title = "Sign Out",
                    titleColor = SignOut,
                    onClick = {
                        Haptics.playLight(view)
                        showSignOut = true
                    }
                )
            }
        }

        // Sign Out Backdrop Modal Overlay
        if (showSignOut) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable {
                        // Dismiss modal on background tap
                        showSignOut = false
                    },
                contentAlignment = Alignment.Center
            ) {
                // Card Scale-in animation
                AnimatedVisibility(
                    visible = showSignOut,
                    enter = fadeIn() + scaleIn(initialScale = 0.9f),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .width(320.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Border, RoundedCornerShape(20.dp))
                            .background(Card)
                            .clickable(enabled = false) {} // Prevent event pass-through
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                text = "Sign Out?",
                                style = HeadlineSmall.copy(
                                    color = Foreground,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your data will be saved locally. You can sign back in anytime.",
                                style = BodyMedium.copy(
                                    color = MutedForeground,
                                    lineHeight = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .border(1.dp, Border, RoundedCornerShape(24.dp))
                                        .clickable { showSignOut = false },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Cancel",
                                        style = LabelLarge.copy(color = Foreground)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(Destructive)
                                        .clickable {
                                            showSignOut = false
                                            onSignOut()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Sign Out",
                                        style = LabelLarge.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val alpha = if (achievement.unlocked) 1.0f else 0.4f
    val bg = if (achievement.unlocked) Card else Secondary.copy(alpha = 0.5f)
    val borderColor = if (achievement.unlocked) Border.copy(alpha = 0.8f) else Border

    Row(
        modifier = modifier
            .alpha(alpha)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = achievement.icon,
            contentDescription = achievement.label,
            tint = if (achievement.unlocked) Foreground else MutedForeground,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = achievement.label,
            style = LabelSmall.copy(
                fontSize = 11.sp,
                color = if (achievement.unlocked) Foreground else MutedForeground,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1
        )
    }
}

@Composable
fun PreferenceRow(
    icon: ImageVector,
    title: String,
    titleColor: Color = Foreground,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .drawBehind {
                // Bottom border separator line
                drawLine(
                    color = Border,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (titleColor == SignOut) SignOut else MutedForeground,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = BodyMedium.copy(
                color = titleColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
fun T1Toggle(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val switchWidth = 48.dp
    val thumbSize = 20.dp
    val switchBg = if (value) Foreground else Secondary

    // Animate padding start offset of thumb (Framer Motion layout transition equivalent)
    val targetPadding by animateDpAsState(
        targetValue = if (value) 24.dp else 4.dp,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.75f),
        label = "thumbOffset"
    )

    Box(
        modifier = modifier
            .width(switchWidth)
            .height(28.dp)
            .clip(CircleShape)
            .background(switchBg)
            .clickable { onValueChange(!value) }
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = targetPadding)
                .size(thumbSize)
                .clip(CircleShape)
                .background(if (value) Background else MutedForeground)
        )
    }
}
