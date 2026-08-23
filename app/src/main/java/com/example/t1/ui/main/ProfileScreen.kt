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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.draw.blur
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

val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

data class Achievement(
    val icon: ImageVector,
    val label: String,
    val unlocked: Boolean
)

@Composable
fun ProfileScreen(
    onBack: (() -> Unit)?,
    onSettings: () -> Unit,
    onSignOut: () -> Unit,
    displayName: String?,
    username: String,
    totalFocusSessions: Int,
    totalFocusDuration: Long,
    weeklyTrend: List<Int>,
    streak: Int,
    focusRemindersEnabled: Boolean,
    onFocusRemindersChange: (Boolean) -> Unit,
    privacyGhostMode: Boolean = false,
    onPrivacyGhostModeChange: (Boolean) -> Unit = {},
    primaryGoal: String = "PRODUCTIVITY",
    goalLastChangedEpoch: Long = 0L,
    onGoalChange: ((String) -> Unit)? = null,
    onUpdateName: ((String) -> Unit)? = null,
    onStartTour: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    val achievements = remember(streak, totalFocusSessions, totalFocusDuration) {
        listOf(
            Achievement(Icons.Default.LocalFireDepartment, "7 Day Streak", streak >= 7),
            Achievement(Icons.Default.Star, "Top Performer", totalFocusSessions >= 10),
            Achievement(Icons.Default.FlashOn, "100 Focus Hours", totalFocusDuration >= 100 * 3600),
            Achievement(Icons.Default.EmojiEvents, "Elite Rank", streak >= 10 || totalFocusSessions >= 20)
        )
    }
    val context = androidx.compose.ui.platform.LocalContext.current

    var isEditing by remember { mutableStateOf(false) }

    val currentDisplayName = displayName.takeIf { !it.isNullOrBlank() } ?: username
    var name by remember(displayName, username) { mutableStateOf(currentDisplayName) }
    var title by remember { mutableStateOf("Deep Work Enthusiast") }

    var editName by remember(isEditing) { mutableStateOf(displayName ?: "") }
    var editTitle by remember { mutableStateOf(title) }

    var showSignOut by remember { mutableStateOf(false) }
    var showGoalSelector by remember { mutableStateOf(false) }

    val handleSaveEdit = {
        val trimmed = editName.trim()
        name = trimmed.takeIf { it.isNotEmpty() } ?: username
        title = editTitle
        isEditing = false
        onUpdateName?.invoke(trimmed)
    }

    val maxScore = remember(weeklyTrend) { weeklyTrend.maxOrNull()?.coerceAtLeast(1) ?: 100 }

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 140.dp), // space for bottom navigation
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

                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sign Out",
                        tint = SignOut,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable {
                                Haptics.playLight(view)
                                showSignOut = true
                            }
                    )

                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MutedForeground,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable {
                                Haptics.playLight(view)
                                onSettings()
                            }
                    )
                }
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
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.Center) {
                                if (editName.isEmpty()) {
                                    Text(
                                        text = "Display Name (Optional)",
                                        style = TextStyle(
                                            fontFamily = SpaceGrotesk,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MutedForeground.copy(alpha = 0.4f),
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
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
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            Haptics.playLight(view)
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("T1 Username", username)
                            clipboard.setPrimaryClip(clip)
                            android.widget.Toast.makeText(context, "Username copied!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "@$username",
                        style = BodyMedium.copy(
                            color = MutedForeground,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Username",
                        tint = MutedForeground,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
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
            val hours = totalFocusDuration / 3600
            val minutes = (totalFocusDuration % 3600) / 60
            val seconds = totalFocusDuration % 60
            val durationText = "${hours}h ${minutes}m ${seconds}s"

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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text(
                        text = durationText,
                        style = ScoreMedium.copy(color = Foreground, fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                    Text(
                        text = "FOCUS TIME",
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

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.8f)
                ) {
                    Text(
                        text = totalFocusSessions.toString(),
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
                    val todayIdx = java.time.LocalDate.now().dayOfWeek.value - 1
                    weeklyTrend.forEachIndexed { i, score ->
                        val ratio = score.toFloat() / maxScore
                        val isToday = i == todayIdx

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
                            value = focusRemindersEnabled,
                            onValueChange = {
                                Haptics.playLight(view)
                                onFocusRemindersChange(it)
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

                // Privacy Mode Toggle (Option A: Leaderboard Stealth / Ghost Mode)
                PreferenceRow(
                    icon = Icons.Default.Shield,
                    title = "Stealth Privacy Mode",
                    subtitle = "Mask your name on public leaderboards as Anonymous / Shadow",
                    trailing = {
                        T1Toggle(
                            value = privacyGhostMode,
                            onValueChange = {
                                Haptics.playLight(view)
                                onPrivacyGhostModeChange(it)
                            }
                        )
                    }
                )

                // Primary Focus Goal Preference (1 switch / week cooldown)
                val sevenDaysMs = 7 * 24 * 60 * 60 * 1000L
                val now = System.currentTimeMillis()
                val isGoalLocked = goalLastChangedEpoch > 0L && (now - goalLastChangedEpoch) < sevenDaysMs
                val remainingGoalDays = if (isGoalLocked) {
                    val remainingMs = (goalLastChangedEpoch + sevenDaysMs) - now
                    kotlin.math.max(1, (remainingMs / (24 * 60 * 60 * 1000L)).toInt())
                } else 0

                val goalDisplayName = when (primaryGoal.uppercase()) {
                    "PRODUCTIVITY" -> "Productivity"
                    "SOCIAL" -> "Social Control"
                    "EDUCATION" -> "Education"
                    "ENTERTAINMENT" -> "Entertainment Control"
                    "COMMUNICATION" -> "Chat Control"
                    else -> "Productivity"
                }

                PreferenceRow(
                    icon = Icons.Default.Adjust,
                    title = "Primary Focus Goal",
                    subtitle = if (isGoalLocked) {
                        "$goalDisplayName · Locked for $remainingGoalDays day${if (remainingGoalDays > 1) "s" else ""}"
                    } else {
                        "$goalDisplayName · 1 switch/week available"
                    },
                    trailing = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isGoalLocked) Card else Foreground.copy(alpha = 0.1f))
                                    .border(1.dp, if (isGoalLocked) Border else Foreground.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = goalDisplayName,
                                    style = LabelSmall.copy(
                                        color = if (isGoalLocked) MutedForeground else Foreground,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Icon(
                                imageVector = if (isGoalLocked) Icons.Default.Lock else Icons.Default.ChevronRight,
                                contentDescription = "Change Goal",
                                tint = MutedForeground,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    onClick = {
                        Haptics.playLight(view)
                        showGoalSelector = true
                    }
                )

                // Interactive Guided Product Tour
                PreferenceRow(
                    icon = Icons.Default.AutoAwesome,
                    title = "Interactive Product Tour",
                    onClick = {
                        Haptics.playLight(view)
                        onStartTour?.invoke()
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

            Spacer(modifier = Modifier.height(20.dp))

            // Dedicated High-Visibility Sign Out Button Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Card)
                    .border(1.dp, SignOut.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .clickable {
                        Haptics.playLight(view)
                        showSignOut = true
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sign Out",
                        tint = SignOut,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Sign Out Account",
                        style = LabelLarge.copy(
                            color = SignOut,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
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

        // Primary Goal Selector Modal Overlay
        if (showGoalSelector) {
            GoalSelectorModal(
                currentGoal = primaryGoal,
                goalLastChangedEpoch = goalLastChangedEpoch,
                onDismiss = { showGoalSelector = false },
                onConfirmGoal = { newGoal ->
                    onGoalChange?.invoke(newGoal)
                }
            )
        }
    }
}

@Composable
fun GoalSelectorModal(
    currentGoal: String,
    goalLastChangedEpoch: Long,
    onDismiss: () -> Unit,
    onConfirmGoal: (String) -> Unit
) {
    val view = LocalView.current
    val sevenDaysMs = 7 * 24 * 60 * 60 * 1000L
    val now = System.currentTimeMillis()
    val isLocked = goalLastChangedEpoch > 0L && (now - goalLastChangedEpoch) < sevenDaysMs
    val remainingMs = if (isLocked) (goalLastChangedEpoch + sevenDaysMs) - now else 0L
    val remainingDays = kotlin.math.max(1, (remainingMs / (24 * 60 * 60 * 1000L)).toInt())
    val remainingHours = kotlin.math.max(1, (remainingMs / (60 * 60 * 1000L)).toInt())

    var selectedGoal by remember { mutableStateOf(currentGoal.uppercase()) }

    val goals = listOf(
        Triple("SOCIAL", "Social Control", "Minimize addictive social apps (Instagram, Snapchat, Reddit, X, FB)"),
        Triple("PRODUCTIVITY", "Productivity", "Maximize deep work, coding, design, docs, and builder craft"),
        Triple("EDUCATION", "Education", "Optimize study focus, active learning, reading, and exam preparation"),
        Triple("ENTERTAINMENT", "Entertainment Control", "Restrain YouTube bingeing, OTT streaming, and gaming"),
        Triple("COMMUNICATION", "Chat Control", "Eliminate impulsive messaging and non-stop chat pings")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF0D0E13))
                .border(1.dp, Border, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Border)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PRIMARY FOCUS GOAL",
                            style = TrackingWide.copy(
                                color = Foreground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Adapts daily benchmarks & focus ring tracking",
                            style = BodySmall.copy(color = MutedForeground, fontSize = 11.sp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MutedForeground,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                Haptics.playLight(view)
                                onDismiss()
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Warning / Cooldown Banner
                if (isLocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1414))
                            .border(1.dp, Color(0xFFE53935).copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Goal Switch Locked",
                                    style = BodySmall.copy(color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "You can change again in $remainingDays day${if (remainingDays > 1) "s" else ""} ($remainingHours hrs). Focus algorithms require weekly stability.",
                                    style = LabelSmall.copy(color = MutedForeground, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF181610))
                            .border(1.dp, Color(0xFFE5A135).copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = "Warning",
                                tint = Color(0xFFF1B04C),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Choose Wisely · Think Again",
                                    style = BodySmall.copy(color = Color(0xFFF1B04C), fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "You can only switch your Primary Goal ONCE every 7 days. Once confirmed, it will lock for a full week.",
                                    style = LabelSmall.copy(color = MutedForeground, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Goals List
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    goals.forEach { (key, label, desc) ->
                        val isSelected = selectedGoal == key
                        val isCurrent = currentGoal.uppercase() == key

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Foreground.copy(alpha = 0.08f) else Card)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Foreground else Border,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (!isLocked) {
                                        Haptics.playLight(view)
                                        selectedGoal = key
                                    }
                                }
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = label,
                                            style = BodyMedium.copy(
                                                color = Foreground,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        )
                                        if (isCurrent) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "CURRENT",
                                                style = LabelSmall.copy(
                                                    color = Color(0xFF34C759),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = desc,
                                        style = BodySmall.copy(
                                            color = MutedForeground,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    )
                                }

                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        if (!isLocked) {
                                            Haptics.playLight(view)
                                            selectedGoal = key
                                        }
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Foreground,
                                        unselectedColor = MutedForeground.copy(alpha = 0.4f)
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm CTA Button
                val isBtnEnabled = !isLocked && selectedGoal != currentGoal.uppercase()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(
                            if (isBtnEnabled) Foreground else MutedForeground.copy(alpha = 0.2f)
                        )
                        .clickable(enabled = isBtnEnabled) {
                            Haptics.playLight(view)
                            onConfirmGoal(selectedGoal)
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isLocked) {
                            "Locked (Available in $remainingDays d)"
                        } else if (selectedGoal == currentGoal.uppercase()) {
                            "Select a different goal"
                        } else {
                            "Confirm Goal Switch (Lock 7 Days)"
                        },
                        style = LabelLarge.copy(
                            color = if (isBtnEnabled) Background else MutedForeground,
                            fontWeight = FontWeight.Bold
                        )
                    )
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
    val alpha = if (achievement.unlocked) 1.0f else 0.5f
    val bg = if (achievement.unlocked) Card else Secondary.copy(alpha = 0.3f)
    val borderColor = if (achievement.unlocked) Border.copy(alpha = 0.8f) else Border

    Row(
        modifier = modifier
            .alpha(alpha)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically
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
        if (!achievement.unlocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = MutedForeground.copy(alpha = 0.7f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun PreferenceRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = BodyMedium.copy(
                    color = titleColor,
                    fontWeight = FontWeight.Medium
                )
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = BodySmall.copy(
                        color = MutedForeground.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                )
            }
        }
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
