package com.example.t1.ui.permission

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.t1.ui.onboarding.OnboardingBackground
import com.example.t1.util.Haptics

@Composable
fun UsagePermissionScreen(
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val scrollState = rememberScrollState()

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
                .padding(top = 24.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Card, SurfaceRaised)))
                    .border(1.dp, Border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security",
                    tint = GlowPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Usage Intelligence\nAccess Required",
                style = HeadlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    brush = Brush.linearGradient(
                        colors = listOf(GradientTextStart, GradientTextEnd)
                    )
                ),
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "T1 requires Usage Stats Access to track daily screen time, app categories, and focus metrics in the background.",
                style = BodyLarge.copy(
                    color = MutedForeground,
                    textAlign = TextAlign.Center
                ),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Detailed Comparison Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "WHAT WE VERIFY",
                    style = TrackingNarrow.copy(
                        color = Foreground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                val collectedItems = listOf(
                    "Total Daily Screen Time",
                    "App Category Breakdown (Social, Entert., etc.)",
                    "Daily Screen Unlocks & Session Counts",
                    "Active App Session Durations"
                )

                collectedItems.forEach { item ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Collected",
                            tint = Success,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item,
                            style = BodyMedium.copy(color = MutedForeground)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Border)
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "WHAT WE NEVER COLLECT",
                    style = TrackingNarrow.copy(
                        color = Foreground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                val nonCollectedItems = listOf(
                    "Messages, Chats or Notification Contents",
                    "Contacts, Images or Media Files",
                    "Real-time GPS Location Coordinates",
                    "Private / Sensitive Account Identifiers"
                )

                nonCollectedItems.forEach { item ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Not Collected",
                            tint = Danger,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item,
                            style = BodyMedium.copy(color = MutedForeground)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Action Buttons
            val openSettingsInteraction = remember { MutableInteractionSource() }
            val isOpenSettingsPressed by openSettingsInteraction.collectIsPressedAsState()
            val openSettingsScale = if (isOpenSettingsPressed) 0.95f else 1f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(openSettingsScale)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.verticalGradient(listOf(ButtonGradientStart, ButtonGradientEnd)))
                    .clickable(
                        interactionSource = openSettingsInteraction,
                        indication = null,
                        onClick = {
                            Haptics.playMedium(view)
                            onOpenSettings()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Settings",
                        tint = Background,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open System Settings",
                        style = LabelLarge.copy(
                            color = Background,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Retry Button
            val retryInteraction = remember { MutableInteractionSource() }
            val isRetryPressed by retryInteraction.collectIsPressedAsState()
            val retryScale = if (isRetryPressed) 0.95f else 1f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .scale(retryScale)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.Transparent)
                    .border(1.dp, Border, RoundedCornerShape(25.dp))
                    .clickable(
                        interactionSource = retryInteraction,
                        indication = null,
                        onClick = {
                            Haptics.playLight(view)
                            onRetry()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Verify Permission State",
                    style = LabelLarge.copy(
                        color = Foreground,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Later Button
            val laterInteraction = remember { MutableInteractionSource() }
            val isLaterPressed by laterInteraction.collectIsPressedAsState()
            val laterScale = if (isLaterPressed) 0.95f else 1f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .scale(laterScale)
                    .clip(RoundedCornerShape(25.dp))
                    .clickable(
                        interactionSource = laterInteraction,
                        indication = null,
                        onClick = {
                            Haptics.playLight(view)
                            onLater()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Decide Later",
                    style = LabelLarge.copy(
                        color = MutedForeground,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}
