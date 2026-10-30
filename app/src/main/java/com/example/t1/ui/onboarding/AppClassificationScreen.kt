package com.example.t1.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import com.example.t1.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.delay
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
    val iconRes: Int,
    val tag: String,
    val brandColor: Color
)

val DualPurposeAppList = listOf(
    DualPurposeApp("com.android.chrome", "Google Chrome", "PRODUCTIVITY", R.drawable.ic_app_chrome, "Web Browsing (Work or Distraction)", Color(0xFF4285F4)),
    DualPurposeApp("com.brave.browser", "Brave Browser", "PRODUCTIVITY", R.drawable.ic_app_chrome, "Privacy Browser (Work or Distraction)", Color(0xFFFB542B)),
    DualPurposeApp("com.google.android.youtube", "YouTube", "EDUCATION", R.drawable.ic_app_youtube, "Videos & Learning", Color(0xFFFF0000)),
    DualPurposeApp("com.whatsapp", "WhatsApp", "COMMUNICATION", R.drawable.ic_app_whatsapp, "Chats & Calls", Color(0xFF25D366)),
    DualPurposeApp("org.telegram.messenger", "Telegram", "COMMUNICATION", R.drawable.ic_app_telegram, "Channels & Groups", Color(0xFF24A1DE)),
    DualPurposeApp("com.discord", "Discord", "COMMUNICATION", R.drawable.ic_app_discord, "Communities & Gaming", Color(0xFF5865F2)),
    DualPurposeApp("com.slack", "Slack", "PRODUCTIVITY", R.drawable.ic_app_slack, "Workplace Sync", Color(0xFFE01E5A)),
    DualPurposeApp("com.microsoft.teams", "Microsoft Teams", "PRODUCTIVITY", R.drawable.ic_app_teams, "Team Meetings", Color(0xFF505AC9)),
    DualPurposeApp("us.zoom.videomeetings", "Zoom", "PRODUCTIVITY", R.drawable.ic_app_zoom, "Video Conferences", Color(0xFF0B5CFF)),
    DualPurposeApp("notion.id", "Notion", "PRODUCTIVITY", R.drawable.ic_app_notion, "Notes & Knowledge", Color(0xFFFFFFFF)),
    DualPurposeApp("com.reddit.frontpage", "Reddit", "ENTERTAINMENT", R.drawable.ic_app_reddit, "Forums & Feeds", Color(0xFFFF4500)),
    DualPurposeApp("com.linkedin.android", "LinkedIn", "PRODUCTIVITY", R.drawable.ic_app_linkedin, "Career & Network", Color(0xFF0A66C2)),
    DualPurposeApp("com.spotify.music", "Spotify", "ENTERTAINMENT", R.drawable.ic_app_spotify, "Audio & Podcasts", Color(0xFF1DB954))
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

            var titleChars by remember { mutableIntStateOf(0) }
            var subChars by remember { mutableIntStateOf(0) }
            val fullTitle = "Classify Dual-Purpose Apps"
            val fullSubtitle = "Every mind uses apps differently. An app that wastes someone's day might be your study superpower. Choose honestly."

            LaunchedEffect(Unit) {
                for (i in 1..fullTitle.length) {
                    titleChars = i
                    if (Haptics.isEnabled && !fullTitle[i - 1].isWhitespace()) {
                        try { view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP) } catch (_: Exception) {}
                    }
                    delay(30L)
                }
                for (i in 1..fullSubtitle.length) {
                    subChars = i
                    delay(14L)
                }
            }

            Text(
                text = fullTitle.take(titleChars),
                style = HeadlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White, Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                    )
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = fullSubtitle.take(subChars),
                style = BodyMedium.copy(
                    color = MutedForeground,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

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
                            .clip(RoundedCornerShape(18.dp))
                            .background(Card)
                            .border(1.dp, Border, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = app.iconRes),
                                contentDescription = app.appName,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.appName,
                                    style = LabelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Foreground
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = app.tag,
                                    style = BodySmall.copy(
                                        fontSize = 12.sp,
                                        color = MutedForeground
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ClassificationChip(
                                label = "Work / Utility",
                                isSelected = currentCategory == "PRODUCTIVITY" || currentCategory == "UTILITY",
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
                                label = "Social / Distraction",
                                isSelected = currentCategory == "ENTERTAINMENT" || currentCategory == "SOCIAL",
                                activeColor = Color(0xFFF87171),
                                onClick = {
                                    Haptics.playLight(view)
                                    selectedClassifications[app.packageName] = "SOCIAL"
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "App logos and trademarks belong to their respective copyright holders and are displayed solely for user categorization.",
                        style = BodySmall.copy(
                            color = MutedForeground.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp)
                    )
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
