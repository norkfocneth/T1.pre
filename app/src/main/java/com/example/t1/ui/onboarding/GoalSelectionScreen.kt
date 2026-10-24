package com.example.t1.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.example.t1.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

data class FocusGoalOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val accentColor: Color,
    val detailInsight: String
)

val FocusGoalOptions = listOf(
    FocusGoalOption(
        id = "PRODUCTIVITY",
        title = "Boost Productivity",
        subtitle = "Maximize time spent on work, coding, writing & productive tasks.",
        iconRes = R.drawable.ic_goal_productivity,
        accentColor = Color(0xFF60A5FA),
        detailInsight = "Target Locked: Peak Productivity. T1 will protect your deep focus hours, apply friction to distractions during work blocks, and award bonus consistency points."
    ),
    FocusGoalOption(
        id = "EDUCATION",
        title = "Boost Learning & Study",
        subtitle = "Increase time dedicated to study apps, courses, research & books.",
        iconRes = R.drawable.ic_goal_learning,
        accentColor = Color(0xFF34D399),
        detailInsight = "Target Locked: Study & Learning Mastery. T1 will maximize your cognitive endurance for deep reading while eliminating procrastination habits."
    ),
    FocusGoalOption(
        id = "SOCIAL",
        title = "Limit Social Media",
        subtitle = "Cut down on reels, shorts, endless feeds, and compulsive checking.",
        iconRes = R.drawable.ic_goal_social,
        accentColor = Color(0xFFF87171),
        detailInsight = "Target Locked: Social Media Detox. T1 will strictly intercept algorithmic loops, calculate your daily reclaimed hours, and rank your resistance on the Leaderboard."
    ),
    FocusGoalOption(
        id = "ENTERTAINMENT",
        title = "Limit Entertainment & Gaming",
        subtitle = "Reduce binge streaming, casual videos, and mobile games.",
        iconRes = R.drawable.ic_goal_entertainment,
        accentColor = Color(0xFFFB923C),
        detailInsight = "Target Locked: Entertainment Boundaries. T1 will prevent casual binge loops and ensure you stay in complete command of your downtime."
    ),
    FocusGoalOption(
        id = "COMMUNICATION",
        title = "Limit Messaging & Chats",
        subtitle = "Control time lost in continuous messaging, chats & groups.",
        iconRes = R.drawable.ic_goal_messaging,
        accentColor = Color(0xFFA78BFA),
        detailInsight = "Target Locked: Messaging Boundaries. T1 will protect you from constant message anxiety and restore quiet mental clarity throughout your day."
    )
)

@Composable
fun GoalSelectionScreen(
    onGoalSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var selectedGoal by remember { mutableStateOf("PRODUCTIVITY") }
    var showGoalInsightModal by remember { mutableStateOf(false) }

    var titleChars by remember { mutableIntStateOf(0) }
    var isTypingDone by remember { mutableStateOf(false) }

    val fullTitle = "What is your primary focus goal?"

    LaunchedEffect(Unit) {
        delay(150L)
        for (i in 1..fullTitle.length) {
            titleChars = i
            if (Haptics.isEnabled && !fullTitle[i - 1].isWhitespace()) {
                try { view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP) } catch (_: Exception) {}
            }
            delay(32L)
        }
        isTypingDone = true
    }

    val animatedTopPadding by animateDpAsState(
        targetValue = if (isTypingDone) 0.dp else 120.dp,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 220f),
        label = "goal_center_to_top"
    )

    val currentOption = FocusGoalOptions.firstOrNull { it.id == selectedGoal } ?: FocusGoalOptions.first()

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(animatedTopPadding))

            Spacer(modifier = Modifier.height(10.dp))

            // Step Counter
            Text(
                text = "STEP 2 OF 4",
                style = LabelSmall.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlowPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Glowing Title
            Text(
                text = fullTitle.take(titleChars),
                style = HeadlineMedium.copy(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White, Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                    )
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(visible = isTypingDone) {
                Text(
                    text = "T1 calibrates its scoring engine around your personal objective.",
                    style = BodyMedium.copy(
                        color = MutedForeground,
                        fontSize = 13.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Goal cards list
            AnimatedVisibility(
                visible = isTypingDone,
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(FocusGoalOptions, key = { it.id }) { option ->
                        val isSelected = selectedGoal == option.id
                        val borderColor by animateColorAsState(
                            targetValue = if (isSelected) option.accentColor else Border,
                            animationSpec = tween(200),
                            label = "borderColor"
                        )
                        val bgAlpha by animateFloatAsState(
                            targetValue = if (isSelected) 0.15f else 0.05f,
                            animationSpec = tween(200),
                            label = "bgAlpha"
                        )

                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale = if (isPressed) 0.98f else 1f

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(scale)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(option.accentColor.copy(alpha = bgAlpha))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    Haptics.playMedium(view)
                                    selectedGoal = option.id
                                }
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = option.iconRes),
                                    contentDescription = option.title,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .padding(end = 12.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = option.title,
                                        style = LabelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (isSelected) Foreground else Foreground.copy(alpha = 0.85f)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = option.subtitle,
                                        style = BodySmall.copy(
                                            color = MutedForeground,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Radio indicator
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = if (isSelected) 5.dp else 1.5.dp,
                                            color = if (isSelected) option.accentColor else MutedForeground.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .background(if (isSelected) Color.White else Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Continue button
            AnimatedVisibility(visible = isTypingDone) {
                Button(
                    onClick = {
                        Haptics.playHeavy(view)
                        showGoalInsightModal = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Continue ➔",
                        style = LabelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Deep-Dive Goal Confirmation Modal (Audio 2 requirement)
        if (showGoalInsightModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable(onClick = { showGoalInsightModal = false }),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF13131A))
                        .border(1.dp, currentOption.accentColor.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                        .clickable(enabled = false) {}
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = currentOption.iconRes),
                            contentDescription = currentOption.title,
                            modifier = Modifier
                                .size(78.dp)
                                .padding(bottom = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentOption.title,
                            style = HeadlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Foreground,
                                fontSize = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentOption.detailInsight,
                            style = BodyMedium.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color.White)
                                .clickable {
                                    Haptics.playHeavy(view)
                                    showGoalInsightModal = false
                                    onGoalSelected(selectedGoal)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Lock Goal & Continue ➔",
                                style = BodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
