package com.example.t1.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics

/**
 * Ultra-premium, in-app Privacy & Data Pledge modal card.
 * Provides transparent, in-depth explanation of on-device analysis,
 * zero data collection of private content, instant 0-day deletion,
 * and a direct link to the full web policy.
 */
@Composable
fun PrivacyOverviewDialog(
    onDismiss: () -> Unit,
    onOpenWebPolicy: () -> Unit
) {
    val view = LocalView.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .padding(horizontal = 16.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(24.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume taps on card */ }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ─── Header ───
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceRaised)
                                .border(1.dp, GlowPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Privacy Shield",
                                tint = GlowPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Privacy & Data Pledge",
                                style = HeadlineSmall.copy(
                                    color = Foreground,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Local-First • Zero Ads • Complete Trust",
                                style = LabelSmall.copy(
                                    color = MutedForeground,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }

                    // Close X Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SurfaceRaised)
                            .clickable {
                                Haptics.playLight(view)
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Foreground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Border)
                )

                // ─── Scrollable Content ───
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. What T1 Analyzes (On-Device)
                    PrivacyCardSection(
                        icon = Icons.Default.Smartphone,
                        iconTint = Emerald400,
                        title = "What T1 Analyzes (100% On-Device)",
                        badgeText = "LOCAL ONLY",
                        badgeColor = Emerald400
                    ) {
                        PrivacyBulletPoint(
                            icon = Icons.Default.Check,
                            iconTint = Emerald400,
                            title = "Foreground Screen Time",
                            desc = "Measures minutes spent per app to evaluate focus vs. distraction habits."
                        )
                        PrivacyBulletPoint(
                            icon = Icons.Default.Check,
                            iconTint = Emerald400,
                            title = "App Launches & Unlocks",
                            desc = "Detects context switching and impulsive checking patterns."
                        )
                        PrivacyBulletPoint(
                            icon = Icons.Default.Check,
                            iconTint = Emerald400,
                            title = "Focus Score (0–100) & 7-Day Baseline",
                            desc = "Calculated completely on your device. Only your final daily score is backed up to your profile."
                        )
                    }

                    // 2. What T1 NEVER Collects
                    PrivacyCardSection(
                        icon = Icons.Default.VisibilityOff,
                        iconTint = Red400,
                        title = "What We NEVER Collect or Touch",
                        badgeText = "0% ACCESS",
                        badgeColor = Red400
                    ) {
                        PrivacyBulletPoint(
                            icon = Icons.Default.Close,
                            iconTint = Red400,
                            title = "No Personal Messages or Chats",
                            desc = "We cannot and do not read WhatsApp, Telegram, SMS, or emails."
                        )
                        PrivacyBulletPoint(
                            icon = Icons.Default.Close,
                            iconTint = Red400,
                            title = "No Notification Text or Keystrokes",
                            desc = "We never inspect notification payloads or log what you type."
                        )
                        PrivacyBulletPoint(
                            icon = Icons.Default.Close,
                            iconTint = Red400,
                            title = "No Photos, Media, or GPS",
                            desc = "Zero access to your camera, microphone, files, or real-time location."
                        )
                        PrivacyBulletPoint(
                            icon = Icons.Default.Close,
                            iconTint = Red400,
                            title = "No Third-Party Ads or Data Selling",
                            desc = "T1 contains zero ad networks (No AdMob/Meta). Your data is never monetized or sold."
                        )
                    }

                    // 3. Instant 0-Day Data Deletion
                    PrivacyCardSection(
                        icon = Icons.Default.DeleteSweep,
                        iconTint = Warning,
                        title = "Data Ownership & Instant Deletion",
                        badgeText = "0-DAY PURGE",
                        badgeColor = Warning
                    ) {
                        Text(
                            text = "You have full control over your digital footprint. Tap 'Delete Account' in Settings → Danger Zone anytime.",
                            style = BodySmall.copy(color = Foreground.copy(alpha = 0.9f), lineHeight = 18.sp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Cloud Profile & Score History: Purged immediately from Supabase.\n• Local Usage Cache: Completely wiped from your device instantly.",
                            style = BodySmall.copy(color = MutedForeground, lineHeight = 18.sp)
                        )
                    }

                    // 4. Web Privacy Policy Link Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceRaised)
                            .border(1.dp, GlowPrimary.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                            .clickable {
                                Haptics.playLight(view)
                                onOpenWebPolicy()
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(GlowPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = "Web Link",
                                        tint = GlowPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "View Full Privacy Policy",
                                        style = LabelLarge.copy(
                                            color = Foreground,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = "Read complete legal documentation on focneth.com",
                                        style = BodySmall.copy(
                                            color = MutedForeground,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open Web",
                                tint = GlowPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // ─── Footer Action ───
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Card)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Foreground)
                            .clickable {
                                Haptics.playMedium(view)
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Understood",
                            style = LabelLarge.copy(
                                color = Background,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyCardSection(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    badgeText: String,
    badgeColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = LabelLarge.copy(
                            color = Foreground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = LabelSmall.copy(
                            color = badgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun PrivacyBulletPoint(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(11.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = BodySmall.copy(
                    color = Foreground,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
            Text(
                text = desc,
                style = BodySmall.copy(
                    color = MutedForeground,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )
        }
    }
}
