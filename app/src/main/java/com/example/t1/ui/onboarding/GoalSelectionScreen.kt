package com.example.t1.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics

data class FocusGoalOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val accentColor: Color
)

val FocusGoalOptions = listOf(
    FocusGoalOption(
        id = "PRODUCTIVITY",
        title = "Boost Productivity",
        subtitle = "Maximize time spent on work, coding, writing & productive tasks.",
        emoji = "⚡",
        accentColor = Color(0xFF60A5FA)
    ),
    FocusGoalOption(
        id = "EDUCATION",
        title = "Boost Learning & Education",
        subtitle = "Increase time dedicated to study apps, courses, research & books.",
        emoji = "📚",
        accentColor = Color(0xFF34D399)
    ),
    FocusGoalOption(
        id = "SOCIAL",
        title = "Limit Social Media",
        subtitle = "Cut down on reels, shorts, endless feeds, and compulsive checking.",
        emoji = "🛡️",
        accentColor = Color(0xFFF87171)
    ),
    FocusGoalOption(
        id = "ENTERTAINMENT",
        title = "Limit Entertainment & Gaming",
        subtitle = "Reduce binge streaming, casual videos, and mobile games.",
        emoji = "🎬",
        accentColor = Color(0xFFFB923C)
    ),
    FocusGoalOption(
        id = "COMMUNICATION",
        title = "Limit Messaging & Chats",
        subtitle = "Control time lost in continuous messaging, chats & groups.",
        emoji = "💬",
        accentColor = Color(0xFFA78BFA)
    )
)

@Composable
fun GoalSelectionScreen(
    onGoalSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var selectedGoal by remember { mutableStateOf("PRODUCTIVITY") }

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

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

            Text(
                text = "What is your primary focus goal?",
                style = HeadlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Foreground
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "T1 calibrates its scoring engine around your personal objective.",
                style = BodyMedium.copy(
                    color = MutedForeground,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Goal cards list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(FocusGoalOptions, key = { it.id }) { option ->
                    val isSelected = selectedGoal == option.id
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) option.accentColor.copy(alpha = 0.8f) else Border,
                        animationSpec = tween(200), label = "border_color"
                    )
                    val cardBg by animateColorAsState(
                        targetValue = if (isSelected) option.accentColor.copy(alpha = 0.12f) else Card,
                        animationSpec = tween(200), label = "card_bg"
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.02f else 1f,
                        animationSpec = tween(200), label = "scale"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scale)
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                Haptics.playLight(view)
                                selectedGoal = option.id
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji Badge
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(option.accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = option.emoji, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option.title,
                                style = LabelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = if (isSelected) Foreground else Foreground.copy(alpha = 0.9f)
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = option.subtitle,
                                style = BodySmall.copy(
                                    color = MutedForeground,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }

                        // Radio check indicator
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .border(
                                    width = if (isSelected) 6.dp else 2.dp,
                                    color = if (isSelected) option.accentColor else MutedForeground.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(11.dp)
                                )
                                .background(if (isSelected) Color.White else Color.Transparent)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue button
            Button(
                onClick = {
                    Haptics.playHeavy(view)
                    onGoalSelected(selectedGoal)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonGradientStart,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Continue",
                    style = LabelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
