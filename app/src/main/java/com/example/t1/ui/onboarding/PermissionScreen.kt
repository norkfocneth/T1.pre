package com.example.t1.ui.onboarding

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.t1.theme.*
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay
import android.view.HapticFeedbackConstants

@Composable
fun PermissionScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Steps: 1 = Notification explain, 2 = Notification grant, 3 = Usage explain, 4 = Usage grant
    var currentStep by remember { mutableIntStateOf(1) }
    var isUsageGranted by remember { mutableStateOf(false) }
    var isNotificationGranted by remember { mutableStateOf(false) }

    fun checkPermissions() {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
        isUsageGranted = if (appOps != null) {
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
            }
            mode == AppOpsManager.MODE_ALLOWED
        } else false

        isNotificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) { checkPermissions() }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isNotificationGranted = isGranted
    }

    OnboardingBackground(modifier = modifier) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                (fadeIn(tween(350)) + slideInHorizontally(initialOffsetX = { 300 })).togetherWith(
                    fadeOut(tween(250)) + slideOutHorizontally(targetOffsetX = { -300 })
                )
            },
            label = "permission_step_pager"
        ) { step ->
            when (step) {
                1 -> TypewriterPermissionExplainer(
                    icon = Icons.Default.Notifications,
                    iconTint = Color(0xFF38BDF8),
                    stepLabel = "STEP 1 OF 2",
                    title = "Why We Need Notification Access",
                    explanation = "T1 sends you intelligent, non-intrusive notifications — never spam. We notify you about daily Focus Score briefings every morning, 1v1 Focus Fight challenge invitations from friends, streak freeze warnings before you lose your streak, and gentle micro-nudges when you exceed your own app time limits. You stay in full control and can disable any channel anytime.",
                    onNext = { currentStep = 2 },
                    onSkip = { currentStep = 3 }
                )
                2 -> NotificationGrantStep(
                    isGranted = isNotificationGranted,
                    onRequestPermission = {
                        Haptics.playMedium(view)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    onContinue = {
                        Haptics.playLight(view)
                        currentStep = 3
                    },
                    onSkip = {
                        Haptics.playLight(view)
                        currentStep = 3
                    }
                )
                3 -> TypewriterPermissionExplainer(
                    icon = Icons.Default.Security,
                    iconTint = Color(0xFF818CF8),
                    stepLabel = "STEP 2 OF 2",
                    title = "Why We Need Usage Access",
                    explanation = "T1 is a Sprint Analyzer, not a spy. We read only aggregate app category usage durations and screen unlock counts, entirely on your device. This data never leaves your phone. We use it to compute your Focus Score against your personal 7-day rolling baseline, track daily screen time distribution across Social, Entertainment, Productivity, and Education categories, and power the real-time Focus Fight anti-cheat system. We never read your messages, photos, contacts, GPS location, or any private account data.",
                    onNext = { currentStep = 4 },
                    onSkip = { onContinue() }
                )
                4 -> UsageGrantStep(
                    isGranted = isUsageGranted,
                    onOpenSettings = {
                        Haptics.playMedium(view)
                        try {
                            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            try {
                                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                            } catch (_: Exception) {
                                context.startActivity(Intent(Settings.ACTION_SETTINGS))
                            }
                        }
                    },
                    onVerify = {
                        Haptics.playLight(view)
                        checkPermissions()
                        if (isUsageGranted) onContinue()
                    },
                    onSkip = {
                        Haptics.playLight(view)
                        onContinue()
                    }
                )
            }
        }
    }
}

/**
 * Typewriter-animated explainer screen for a permission.
 * Text appears character-by-character. Once done, "Enable" button fades in.
 * Tapping screen during typing reveals full text instantly.
 */
@Composable
private fun TypewriterPermissionExplainer(
    icon: ImageVector,
    iconTint: Color,
    stepLabel: String,
    title: String,
    explanation: String,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val view = LocalView.current
    var displayedChars by remember { mutableIntStateOf(0) }
    var isDone by remember { mutableStateOf(false) }

    LaunchedEffect(explanation) {
        displayedChars = 0
        isDone = false
        delay(300L)
        for (i in 1..explanation.length) {
            if (isDone) break
            displayedChars = i
            if (!explanation[i - 1].isWhitespace()) {
                if (Haptics.isEnabled) {
                    try { view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP) } catch (_: Exception) {}
                }
            }
            delay(14L)
        }
        displayedChars = explanation.length
        isDone = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (!isDone) {
                        displayedChars = explanation.length
                        isDone = true
                    }
                }
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Skip button top-right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1E1E28).copy(alpha = 0.7f))
                .border(1.dp, Color(0xFF38384A), RoundedCornerShape(20.dp))
                .clickable(onClick = onSkip)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Skip ➔",
                style = BodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF13131A))
                    .border(1.dp, Color(0xFF2E2E42), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step label pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF6366F1).copy(alpha = 0.22f))
                    .border(1.dp, Color(0xFF818CF8).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "✦  $stepLabel",
                    style = BodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFA5B4FC), letterSpacing = 1.2.sp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = title,
                style = HeadlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Foreground,
                    textAlign = TextAlign.Center
                ),
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Typewriter explanation text
            Text(
                text = explanation.take(displayedChars),
                style = BodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // "Enable" button appears after typing is done
            AnimatedVisibility(
                visible = isDone,
                enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(Color.White)
                        .clickable(onClick = onNext),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Enable ➔",
                        style = BodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Notification grant action step.
 */
@Composable
private fun NotificationGrantStep(
    isGranted: Boolean,
    onRequestPermission: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF13131A))
                    .border(1.dp, Color(0xFF2E2E42), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (isGranted) "Notifications Enabled ✓" else "Enable Notifications",
                style = HeadlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Foreground, textAlign = TextAlign.Center),
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isGranted) "Perfect! You'll receive Focus Score updates and Focus Fight invitations." else "Tap the button below to allow T1 to send you Focus Score briefings and duel invitations.",
                style = BodyMedium.copy(color = MutedForeground, fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Center),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Quick summary pills
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF101015).copy(alpha = 0.9f))
                    .border(1.dp, Color(0xFF222230), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PermVerifyItem(text = "Daily morning Focus Score briefing")
                    PermVerifyItem(text = "1v1 Focus Fight challenge invitations")
                    PermVerifyItem(text = "Streak freeze warnings before losing streaks")
                    PermNeverItem(text = "Zero marketing spam or annoying promotions")
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(if (isGranted) Color(0xFF10B981) else Color.White)
                    .clickable(onClick = if (!isGranted) onRequestPermission else onContinue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isGranted) "Continue ➔" else "Allow Notifications",
                    style = BodyMedium.copy(fontWeight = FontWeight.Bold, color = if (isGranted) Color.White else Color.Black, fontSize = 15.sp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Skip for now",
                style = BodySmall.copy(fontSize = 12.sp, color = MutedForeground, fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clickable(onClick = onSkip).padding(8.dp)
            )
        }
    }
}

/**
 * Usage access grant action step.
 */
@Composable
private fun UsageGrantStep(
    isGranted: Boolean,
    onOpenSettings: () -> Unit,
    onVerify: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF13131A))
                    .border(1.dp, Color(0xFF2E2E42), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF818CF8), modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (isGranted) "Usage Access Granted ✓" else "Grant Usage Access",
                style = HeadlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Foreground, textAlign = TextAlign.Center),
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isGranted) "All set! T1 can now compute your Focus Score from real usage data." else "Open System Settings and enable Usage Access for T1 to compute your personalized Focus Score.",
                style = BodyMedium.copy(color = MutedForeground, fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Center),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF101015).copy(alpha = 0.9f))
                    .border(1.dp, Color(0xFF222230), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PermVerifyItem(text = "Total Daily Screen Time")
                    PermVerifyItem(text = "App Category Breakdown (Social, Educ., etc.)")
                    PermVerifyItem(text = "Daily Screen Unlocks & Session Counts")
                    PermNeverItem(text = "Messages, Chats, Contacts, GPS — never")
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(if (isGranted) Color(0xFF10B981) else Color.White)
                    .clickable(onClick = if (!isGranted) onOpenSettings else onVerify),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(
                        imageVector = if (isGranted) Icons.Default.Check else Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = if (isGranted) Color.White else Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGranted) "Continue ➔" else "Open System Settings",
                        style = BodyMedium.copy(fontWeight = FontWeight.Bold, color = if (isGranted) Color.White else Color.Black, fontSize = 15.sp)
                    )
                }
            }

            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF161620))
                        .border(1.dp, Color(0xFF272738), RoundedCornerShape(24.dp))
                        .clickable(onClick = onVerify),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Verify Permission State",
                        style = BodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFFA1A1AA), fontSize = 14.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Set up later in Settings",
                style = BodySmall.copy(fontSize = 12.sp, color = MutedForeground, fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clickable(onClick = onSkip).padding(8.dp)
            )
        }
    }
}

@Composable
private fun PermVerifyItem(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(17.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, style = BodyMedium.copy(fontSize = 13.sp, color = Foreground.copy(alpha = 0.9f), lineHeight = 18.sp))
    }
}

@Composable
private fun PermNeverItem(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(17.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, style = BodyMedium.copy(fontSize = 13.sp, color = Foreground.copy(alpha = 0.75f), lineHeight = 18.sp))
    }
}
