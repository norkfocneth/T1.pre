package com.example.t1.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.ui.components.PrivacyOverviewDialog
import com.example.t1.ui.onboarding.OnboardingBackground
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

import androidx.compose.material.icons.filled.ExitToApp

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: (onSuccess: () -> Unit, onFailure: (String) -> Unit) -> Unit,
    onReplayTour: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var darkMode by remember { mutableStateOf(true) }
    var soundEffects by remember { mutableStateOf(Haptics.isSoundEnabled) }
    var hapticFeedback by remember { mutableStateOf(Haptics.isEnabled) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showSignOutConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showPrivacyOverview by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    BackHandler {
        if (showPrivacyOverview) {
            showPrivacyOverview = false
        } else if (showResetConfirm) {
            showResetConfirm = false
        } else if (showSignOutConfirm) {
            showSignOutConfirm = false
        } else if (showDeleteConfirm && !isDeleting) {
            showDeleteConfirm = false
        } else if (!isDeleting) {
            onBack()
        }
    }


    Box(modifier = modifier.fillMaxSize()) {
        OnboardingBackground(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Header bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
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

                Text(
                    text = "SETTINGS",
                    style = TrackingWide.copy(
                        color = Foreground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // GENERAL section
                Text(
                    text = "GENERAL",
                    style = TrackingWide.copy(
                        color = MutedForeground,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Dark Mode Locked
                PreferenceRow(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = "OLED Obsidian Signature (Always Active)",
                    trailing = {
                        T1Toggle(
                            value = true,
                            onValueChange = {
                                Haptics.playLight(view)
                                android.widget.Toast.makeText(
                                    context,
                                    "T1 is crafted for OLED Pitch Black. Light Mode coming soon.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                )

                // Sound Effects Toggle
                PreferenceRow(
                    icon = Icons.Default.VolumeUp,
                    title = "Sound Effects",
                    subtitle = "Relaxing Zen audio & acoustic feedback",
                    trailing = {
                        T1Toggle(
                            value = soundEffects,
                            onValueChange = {
                                soundEffects = it
                                Haptics.save(context, hapticsEnabled = hapticFeedback, soundsEnabled = it)
                                if (it) {
                                    Haptics.playZenChime()
                                } else {
                                    Haptics.playLight(view)
                                }
                            }
                        )
                    }
                )

                // Haptic Feedback Toggle
                PreferenceRow(
                    icon = Icons.Default.SettingsSuggest,
                    title = "Haptic Feedback",
                    trailing = {
                        T1Toggle(
                            value = hapticFeedback,
                            onValueChange = {
                                hapticFeedback = it
                                Haptics.save(context, hapticsEnabled = it, soundsEnabled = soundEffects)
                                Haptics.playLight(view)
                            }
                        )
                    }
                )

                // Time Format info
                PreferenceRow(
                    icon = Icons.Default.AccessTime,
                    title = "Time Format",
                    trailing = {
                        Text(
                            text = "24h",
                            style = BodyMedium.copy(color = MutedForeground)
                        )
                    }
                )

                // Language Format info
                PreferenceRow(
                    icon = Icons.Default.Language,
                    title = "Language",
                    trailing = {
                        Text(
                            text = "English",
                            style = BodyMedium.copy(color = MutedForeground)
                        )
                    }
                )

                // APP Section
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "APP",
                    style = TrackingWide.copy(
                        color = MutedForeground,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Replay Product Tour
                PreferenceRow(
                    icon = Icons.Default.AutoAwesome,
                    title = "Replay Product Tour",
                    onClick = {
                        Haptics.playLight(view)
                        onReplayTour?.invoke()
                    }
                )

                // About T1
                PreferenceRow(
                    icon = Icons.Default.Info,
                    title = "About T1",
                    onClick = {
                        Haptics.playLight(view)
                        try {
                            uriHandler.openUri("https://t1premainweb-j7kcxf2u2-norkfocneths-projects.vercel.app/home/about")
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Privacy Policy
                PreferenceRow(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy",
                    onClick = {
                        Haptics.playLight(view)
                        showPrivacyOverview = true
                    }
                )

                // Terms & Conditions
                PreferenceRow(
                    icon = Icons.Default.Description,
                    title = "Terms & Conditions",
                    onClick = {
                        Haptics.playLight(view)
                        try {
                            uriHandler.openUri("https://t1premainweb-j7kcxf2u2-norkfocneths-projects.vercel.app/home/terms")
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Help & Support
                PreferenceRow(
                    icon = Icons.Default.HelpOutline,
                    title = "Help & Support",
                    onClick = {
                        Haptics.playLight(view)
                        try {
                            uriHandler.openUri("mailto:support.t1.help@gmail.com")
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open email client", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // DANGER ZONE Section
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "DANGER ZONE",
                    style = TrackingWide.copy(
                        color = Destructive.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Reset All Progress
                PreferenceRow(
                    icon = Icons.Default.Warning,
                    title = "Reset All Progress",
                    titleColor = Destructive,
                    onClick = {
                        Haptics.playLight(view)
                        showResetConfirm = true
                    }
                )

                // Sign Out
                PreferenceRow(
                    icon = Icons.Default.ExitToApp,
                    title = "Sign Out",
                    titleColor = SignOut,
                    onClick = {
                        Haptics.playLight(view)
                        showSignOutConfirm = true
                    }
                )

                // Delete Account
                PreferenceRow(
                    icon = Icons.Default.Warning,
                    title = "Delete Account",
                    titleColor = Destructive,
                    onClick = {
                        Haptics.playLight(view)
                        showDeleteConfirm = true
                    }
                )


                // App version details
                Spacer(modifier = Modifier.height(48.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "T1.",
                        style = HeadlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Foreground
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Version 1.0.0",
                        style = BodySmall.copy(
                            color = MutedForeground,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Reset Confirmation Overlay Modal
        if (showResetConfirm) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable {
                        // Dismiss modal on background tap
                        showResetConfirm = false
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = showResetConfirm,
                    enter = fadeIn() + scaleIn(initialScale = 0.9f),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .width(320.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Destructive.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .background(Card)
                            .clickable(enabled = false) {} // Prevent click passthrough
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = Destructive,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Reset Progress?",
                                    style = HeadlineSmall.copy(
                                        color = Foreground,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "This will permanently erase your streak, focus score, and all session data. This action cannot be undone.",
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
                                        .clickable { showResetConfirm = false },
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
                                            // Reset progress action
                                            showResetConfirm = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Reset",
                                        style = LabelLarge.copy(color = Foreground, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delete Account Confirmation Overlay Modal
        if (showDeleteConfirm) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable {
                        if (!isDeleting) showDeleteConfirm = false
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = showDeleteConfirm,
                    enter = fadeIn() + scaleIn(initialScale = 0.9f),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .width(320.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Destructive.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .background(Card)
                            .clickable(enabled = false) {}
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = Destructive,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Delete Account?",
                                    style = HeadlineSmall.copy(
                                        color = Foreground,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "This will permanently delete your profile, tokens, focus scores, and historical activity from both your device and cloud servers. This cannot be undone.",
                                style = BodyMedium.copy(
                                    color = MutedForeground,
                                    lineHeight = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            if (isDeleting) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.material3.CircularProgressIndicator(color = Destructive)
                                }
                            } else {
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
                                            .clickable { showDeleteConfirm = false },
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
                                                Haptics.playMedium(view)
                                                isDeleting = true
                                                onDeleteAccount(
                                                    {
                                                        isDeleting = false
                                                        showDeleteConfirm = false
                                                        Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_LONG).show()
                                                    },
                                                    { error ->
                                                        isDeleting = false
                                                        showDeleteConfirm = false
                                                        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                                                    }
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Delete",
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

        // Sign Out Confirmation Overlay Modal
        if (showSignOutConfirm) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable {
                        showSignOutConfirm = false
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = showSignOutConfirm,
                    enter = fadeIn() + scaleIn(initialScale = 0.9f),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .width(320.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Border, RoundedCornerShape(20.dp))
                            .background(Card)
                            .clickable(enabled = false) {}
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Sign Out",
                                    tint = SignOut,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sign Out?",
                                    style = HeadlineSmall.copy(
                                        color = Foreground,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your profile and focus progress are securely preserved. You can sign back in anytime with your Google account.",
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
                                        .clickable { showSignOutConfirm = false },
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
                                        .background(SignOut)
                                        .clickable {
                                            Haptics.playMedium(view)
                                            showSignOutConfirm = false
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

        // Privacy Policy In-App Overview Modal
        if (showPrivacyOverview) {
            PrivacyOverviewDialog(
                onDismiss = { showPrivacyOverview = false },
                onOpenWebPolicy = {
                    try {
                        uriHandler.openUri("https://t1premainweb-j7kcxf2u2-norkfocneths-projects.vercel.app/home/privacy")
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}
}




