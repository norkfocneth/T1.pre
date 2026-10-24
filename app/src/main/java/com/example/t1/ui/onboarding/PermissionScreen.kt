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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.ui.components.PrivacyOverviewDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.t1.theme.*
import com.example.t1.util.Haptics

@Composable
fun PermissionScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uriHandler = LocalUriHandler.current

    var currentStep by remember { mutableIntStateOf(1) } // 1: Usage Access, 2: Notifications
    var isUsageGranted by remember { mutableStateOf(false) }
    var isNotificationGranted by remember { mutableStateOf(false) }
    var isConsentChecked by remember { mutableStateOf(false) }
    var showPrivacyOverview by remember { mutableStateOf(false) }

    fun checkPermissions() {
        // Check Usage Access
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

        // Check Notification Access
        isNotificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        // Auto advance removed: user sees verification state and clicks Continue
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        checkPermissions()
    }

    // Launcher for Notification permission (Android 13+)
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isNotificationGranted = isGranted
        if (isGranted) {
            Haptics.playSuccess(context)
            onContinue()
        }
    }

    OnboardingBackground(modifier = modifier) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                (fadeIn(tween(350)) + slideInHorizontally(initialOffsetX = { 300 })).togetherWith(
                    fadeOut(tween(250)) + slideOutHorizontally(targetOffsetX = { -300 })
                )
            },
            label = "permission_screen_pager"
        ) { step ->
            when (step) {
                1 -> {
                    UsageIntelligenceStep(
                        isGranted = isUsageGranted,
                        isConsentChecked = isConsentChecked,
                        onConsentChanged = { isConsentChecked = it },
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
                        onVerifyState = {
                            Haptics.playLight(view)
                            checkPermissions()
                            if (isUsageGranted) {
                                currentStep = 2
                            }
                        },
                        onSkipLater = {
                            Haptics.playLight(view)
                            currentStep = 2
                        },
                        onOpenPrivacyPolicy = {
                            showPrivacyOverview = true
                        }
                    )
                }
                2 -> {
                    NotificationIntelligenceStep(
                        isGranted = isNotificationGranted,
                        onRequestPermission = {
                            Haptics.playMedium(view)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onContinue()
                            }
                        },
                        onContinue = {
                            Haptics.playLight(view)
                            onContinue()
                        }
                    )
                }
            }
        }

        if (showPrivacyOverview) {
            PrivacyOverviewDialog(
                onDismiss = { showPrivacyOverview = false },
                onOpenWebPolicy = {
                    try {
                        uriHandler.openUri("https://t1premainweb-nine.vercel.app/home/privacy")
                    } catch (_: Exception) {}
                }
            )
        }
    }
}

/**
 * Step 1: The exact "Usage Intelligence Access Required" trust card from User's Image 1.
 */
@Composable
private fun UsageIntelligenceStep(
    isGranted: Boolean,
    isConsentChecked: Boolean,
    onConsentChanged: (Boolean) -> Unit,
    onOpenSettings: () -> Unit,
    onVerifyState: () -> Unit,
    onSkipLater: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
            .padding(top = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Shield Icon with glowing ring
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color(0xFF13131A))
                .border(1.dp, Color(0xFF2E2E42), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Shield",
                tint = Color(0xFF818CF8),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "Usage Intelligence\nAccess Required",
            style = HeadlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Foreground,
                textAlign = TextAlign.Center
            ),
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Subtitle
        Text(
            text = "T1 requires Usage Stats Access to track daily screen time, app categories, and focus metrics in the background.",
            style = BodyMedium.copy(
                color = MutedForeground,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Comprehensive Trust Card (Image 1 replica)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF101015).copy(alpha = 0.9f))
                .border(1.dp, Color(0xFF222230), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Section 1: WHAT WE VERIFY
                Text(
                    text = "WHAT WE VERIFY",
                    style = BodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA1A1AA),
                        letterSpacing = 1.2.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                VerifyItem(text = "Total Daily Screen Time")
                VerifyItem(text = "App Category Breakdown (Social, Entert., etc.)")
                VerifyItem(text = "Daily Screen Unlocks & Session Counts")
                VerifyItem(text = "Active App Session Durations")

                Spacer(modifier = Modifier.height(18.dp))

                // Divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF1F1F2C))
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: WHAT WE NEVER COLLECT
                Text(
                    text = "WHAT WE NEVER COLLECT",
                    style = BodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA1A1AA),
                        letterSpacing = 1.2.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                NeverCollectItem(text = "Messages, Chats or Notification Contents")
                NeverCollectItem(text = "Contacts, Images or Media Files")
                NeverCollectItem(text = "Real-time GPS Location Coordinates")
                NeverCollectItem(text = "Private / Sensitive Account Identifiers")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Checkbox: Consent
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onConsentChanged(!isConsentChecked) }
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isConsentChecked || isGranted,
                onCheckedChange = { onConsentChanged(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF6366F1),
                    uncheckedColor = Color(0xFF4B5563),
                    checkmarkColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = buildAnnotatedString {
                    append("I read the ")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF38BDF8),
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("Privacy Policy")
                    }
                    append(" and consent to T1 aggregating and analyzing my usage data.")
                },
                style = BodySmall.copy(
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 17.sp
                ),
                modifier = Modifier.clickable { onOpenPrivacyPolicy() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Button: Open System Settings
        val canOpen = isConsentChecked || isGranted
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale = if (isPressed) 0.98f else 1f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .scale(scale)
                .alpha(if (canOpen) 1f else 0.5f)
                .clip(RoundedCornerShape(27.dp))
                .background(
                    if (isGranted) Color(0xFF10B981) else Color.White
                )
                .clickable(
                    enabled = canOpen,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onOpenSettings
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.Check else Icons.Default.OpenInNew,
                    contentDescription = null,
                    tint = if (isGranted) Color.White else Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isGranted) "Permission Granted ✓" else "Open System Settings",
                    style = BodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isGranted) Color.White else Color.Black,
                        fontSize = 15.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Verify Permission State or Continue Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF161620))
                .border(1.dp, Color(0xFF272738), RoundedCornerShape(24.dp))
                .clickable(onClick = onVerifyState),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isGranted) "Continue ➔" else "Verify Permission State",
                style = BodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isGranted) Color(0xFF38BDF8) else Color(0xFFA1A1AA),
                    fontSize = 14.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skip / Setup later link
        Text(
            text = "Set up later in Settings",
            style = BodySmall.copy(
                fontSize = 12.sp,
                color = MutedForeground,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .clickable(onClick = onSkipLater)
                .padding(8.dp)
        )
    }
}

/**
 * Step 2: Non-intrusive notification permission explanation.
 */
@Composable
private fun NotificationIntelligenceStep(
    isGranted: Boolean,
    onRequestPermission: () -> Unit,
    onContinue: () -> Unit
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF13131A))
                    .border(1.dp, Color(0xFF2E2E42), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Timely Focus\nInterceptions",
                style = HeadlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Foreground,
                    textAlign = TextAlign.Center
                ),
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "T1 uses intelligent, non-intrusive notifications for daily score updates, duel reminders, and saving you when doomscrolling.",
                style = BodyMedium.copy(
                    color = MutedForeground,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Information Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF101015).copy(alpha = 0.9f))
                    .border(1.dp, Color(0xFF222230), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    VerifyItem(text = "Daily morning Focus Score briefing")
                    VerifyItem(text = "Micro-nudges when exceeding app limits")
                    VerifyItem(text = "Streak freeze & leaderboard position alerts")
                    NeverCollectItem(text = "Zero marketing spam or annoying promotions")
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(Color.White)
                    .clickable(
                        onClick = if (!isGranted) onRequestPermission else onContinue
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isGranted) "Continue ➔" else "Enable Notifications",
                    style = BodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Skip for now",
                style = BodySmall.copy(
                    fontSize = 12.sp,
                    color = MutedForeground,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onContinue)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun VerifyItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = BodyMedium.copy(
                fontSize = 13.sp,
                color = Foreground.copy(alpha = 0.9f),
                lineHeight = 18.sp
            )
        )
    }
}

@Composable
private fun NeverCollectItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = BodyMedium.copy(
                fontSize = 13.sp,
                color = Foreground.copy(alpha = 0.75f),
                lineHeight = 18.sp
            )
        )
    }
}
