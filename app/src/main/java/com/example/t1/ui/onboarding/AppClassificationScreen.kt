package com.example.t1.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics

data class DualPurposeApp(
    val packageName: String,
    val appName: String,
    val defaultCategory: String,
    val iconEmoji: String
)

val DualPurposeAppList = listOf(
    DualPurposeApp("com.google.android.youtube", "YouTube", "EDUCATION", "📺"),
    DualPurposeApp("com.whatsapp", "WhatsApp", "COMMUNICATION", "💬"),
    DualPurposeApp("org.telegram.messenger", "Telegram", "COMMUNICATION", "✈️"),
    DualPurposeApp("com.discord", "Discord", "COMMUNICATION", "👾"),
    DualPurposeApp("com.slack", "Slack", "PRODUCTIVITY", "💼"),
    DualPurposeApp("com.microsoft.teams", "Microsoft Teams", "PRODUCTIVITY", "👥"),
    DualPurposeApp("us.zoom.videomeetings", "Zoom", "PRODUCTIVITY", "📹"),
    DualPurposeApp("notion.id", "Notion", "PRODUCTIVITY", "📝"),
    DualPurposeApp("com.reddit.frontpage", "Reddit", "ENTERTAINMENT", "🤖"),
    DualPurposeApp("com.android.chrome", "Google Chrome", "PRODUCTIVITY", "🌐"),
    DualPurposeApp("com.linkedin.android", "LinkedIn", "PRODUCTIVITY", "👔"),
    DualPurposeApp("com.spotify.music", "Spotify", "ENTERTAINMENT", "🎵")
)

@Composable
fun AppClassificationScreen(
    onClassificationComplete: (Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    // Map of packageName -> selected category ("PRODUCTIVITY", "EDUCATION", "ENTERTAINMENT", "SOCIAL", "COMMUNICATION")
    val selectedClassifications = remember {
        mutableStateMapOf<String, String>().apply {
            DualPurposeAppList.forEach { app ->
                put(app.packageName, app.defaultCategory)
            }
        }
    }

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
                text = "STEP 3 OF 4",
                style = LabelSmall.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlowPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Classify Dual-Purpose Apps",
                style = HeadlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Foreground
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "How do you primarily use these tools? Fixed social feeds remain locked.",
                style = BodyMedium.copy(
                    color = MutedForeground,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // App list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(DualPurposeAppList, key = { it.packageName }) { app ->
                    val currentCategory = selectedClassifications[app.packageName] ?: app.defaultCategory

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Card)
                            .border(1.dp, Border, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = app.iconEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = app.appName,
                                style = LabelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Foreground
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ClassificationChip(
                                label = "Productive",
                                isSelected = currentCategory == "PRODUCTIVITY",
                                activeColor = Color(0xFF60A5FA),
                                onClick = {
                                    Haptics.playLight(view)
                                    selectedClassifications[app.packageName] = "PRODUCTIVITY"
                                },
                                modifier = Modifier.weight(1f)
                            )

                            ClassificationChip(
                                label = "Learning",
                                isSelected = currentCategory == "EDUCATION",
                                activeColor = Color(0xFF34D399),
                                onClick = {
                                    Haptics.playLight(view)
                                    selectedClassifications[app.packageName] = "EDUCATION"
                                },
                                modifier = Modifier.weight(1f)
                            )

                            ClassificationChip(
                                label = "Distraction",
                                isSelected = currentCategory == "ENTERTAINMENT" || currentCategory == "SOCIAL",
                                activeColor = Color(0xFFF87171),
                                onClick = {
                                    Haptics.playLight(view)
                                    selectedClassifications[app.packageName] = "ENTERTAINMENT"
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue Button
            Button(
                onClick = {
                    Haptics.playHeavy(view)
                    onClassificationComplete(selectedClassifications.toMap())
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
                    text = "Save & Continue",
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

@Composable
private fun ClassificationChip(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) activeColor.copy(alpha = 0.2f) else Secondary,
        animationSpec = tween(150), label = "chip_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else Color.Transparent,
        animationSpec = tween(150), label = "chip_border"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = LabelMedium.copy(
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) activeColor else MutedForeground
            )
        )
    }
}
