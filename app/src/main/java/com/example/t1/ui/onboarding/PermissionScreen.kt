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
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.t1.R
import com.example.t1.util.Haptics

/**
 * Ultra-premium Onboarding Permission flow matching the T1 cyberpunk dark-mode design:
 * - Step 1: Notifications Enabled screen with 3D Mascot sitting on lunar rocks,
 *           feature breakdown frosted card, system permission trigger, and smooth continue/skip.
 * - Step 2: Why We Need Usage Access screen with 3D Mascot presenting the T1 Neon Shield,
 *           privacy guarantee frosted card, direct System Settings trigger, and auto-detect resume.
 */
@Composable
fun PermissionScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Step 1 = Notifications, Step 2 = Usage Access
    var currentStep by remember { mutableIntStateOf(1) }
    var isUsageGranted by remember { mutableStateOf(false) }
    var isNotificationGranted by remember { mutableStateOf(false) }

    fun checkPermissions() {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
        isUsageGranted = if (appOps != null) {
            @Suppress("DEPRECATION")
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
            } else {
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
                val wasUsageGranted = isUsageGranted
                checkPermissions()
                if (!wasUsageGranted && isUsageGranted) {
                    Haptics.playSuccess(context)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        checkPermissions()
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isNotificationGranted = granted
        if (granted) {
            Haptics.playSuccess(context)
        } else {
            Haptics.playLight(view)
        }
    }

    BackHandler(enabled = currentStep > 1) {
        Haptics.playLight(view)
        currentStep = 1
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF06040A),
                        Color(0xFF10091E),
                        Color(0xFF07040D)
                    )
                )
            )
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally(
                        initialOffsetX = { 300 },
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy)
                    ) + fadeIn(tween(300))).togetherWith(
                        slideOutHorizontally(
                            targetOffsetX = { -300 },
                            animationSpec = tween(250)
                        ) + fadeOut(tween(250))
                    )
                } else {
                    (slideInHorizontally(
                        initialOffsetX = { -300 },
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy)
                    ) + fadeIn(tween(300))).togetherWith(
                        slideOutHorizontally(
                            targetOffsetX = { 300 },
                            animationSpec = tween(250)
                        ) + fadeOut(tween(250))
                    )
                }
            },
            label = "permission_step_pager"
        ) { step ->
            when (step) {
                1 -> NotificationPermissionScreen(
                    isGranted = isNotificationGranted,
                    onRequestPermission = {
                        Haptics.playMedium(view)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            currentStep = 2
                        }
                    },
                    onContinue = {
                        Haptics.playLight(view)
                        currentStep = 2
                    },
                    onSkip = {
                        Haptics.playLight(view)
                        currentStep = 2
                    }
                )
                2 -> UsageAccessPermissionScreen(
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
                    onContinue = {
                        Haptics.playSuccess(context)
                        onContinue()
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
 * Step 1: Notifications Screen
 * - Left side: Back button, Header with "Notifications Enabled ✓", subtext, step indicator
 * - Right side: Clean 3D Mascot sitting on rocks with purple planetary atmosphere
 * - Center: Frosted glass card with 4 rows (Morning briefing, 1v1 fights, streak freeze, no spam)
 * - Bottom: Glowing purple pill button (Allow Notifications / Continue) & "Skip for now"
 */
@Composable
private fun NotificationPermissionScreen(
    isGranted: Boolean,
    onRequestPermission: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    val view = LocalView.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .drawBehind {
                // Soft purple ambient radial glow on top right behind mascot
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.35f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.22f),
                        radius = size.width * 0.6f
                    )
                )
                // Bottom ambient glow reflection
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF6D28D9).copy(alpha = 0.12f),
                            Color(0xFF8B5CF6).copy(alpha = 0.16f),
                            Color.Transparent
                        ),
                        startY = size.height * 0.72f,
                        endY = size.height
                    )
                )
            }
            .padding(horizontal = 22.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top row with circular back button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF161224).copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFF2E2442), CircleShape)
                            .clickable {
                                Haptics.playLight(view)
                                onSkip()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero Row: Header text on left, Mascot on right
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    // Left Column Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .align(Alignment.TopStart)
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            text = "Notifications",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 34.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isGranted) "Enabled" else "Enable",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold,
                                style = TextStyle(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFFA855F7), Color(0xFFC084FC))
                                    )
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFF7C3AED).copy(alpha = 0.5f),
                                                Color(0xFF9333EA).copy(alpha = 0.4f)
                                            )
                                        )
                                    )
                                    .border(1.dp, Color(0xFFA855F7).copy(alpha = 0.7f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isGranted) Icons.Default.Check else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Stay in the loop with your focus journey. No spam, only what matters.",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stepper pill indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFA855F7))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF382F4D))
                            )
                        }
                    }

                    // Mascot Image on right (pure clean crop with NO letter 'S' and antialiased fade)
                    Image(
                        painter = painterResource(id = R.drawable.mascot_perm_notif),
                        contentDescription = "Mascot sitting on rocks",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(190.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 10.dp, y = (-8).dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Frosted glass card with 4 rows
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF130E22).copy(alpha = 0.88f))
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF7C3AED).copy(alpha = 0.55f),
                                    Color(0xFF3B1D6E).copy(alpha = 0.25f)
                                )
                            ),
                            RoundedCornerShape(22.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        NotifFeatureRow(
                            icon = Icons.Default.WbSunny,
                            title = "Daily Morning Briefing",
                            subtitle = "Your Focus Score, goals and plan.",
                            isApproved = true
                        )
                        NotifFeatureRow(
                            icon = Icons.Default.SportsMma,
                            title = "1v1 Focus Fight Invites",
                            subtitle = "Challenge friends and climb the ranks.",
                            isApproved = true
                        )
                        NotifFeatureRow(
                            icon = Icons.Default.Bolt,
                            title = "Streak Freeze Warnings",
                            subtitle = "Get a heads-up before you lose a streak.",
                            isApproved = true
                        )
                        NotifFeatureRow(
                            icon = Icons.Default.NotificationsOff,
                            title = "No Marketing Spam",
                            subtitle = "Only useful, focus-related updates.",
                            isApproved = false
                        )
                    }
                }
            }

            // Bottom CTA section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary purple glowing button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF6D28D9),
                                    Color(0xFF9333EA),
                                    Color(0xFFA855F7)
                                )
                            )
                        )
                        .border(1.dp, Color(0xFFD8B4FE).copy(alpha = 0.6f), RoundedCornerShape(27.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = if (!isGranted) onRequestPermission else onContinue
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isGranted) "Continue ➔" else "Allow Notifications ➔",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Skip for now",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onSkip
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Step 2: Usage Access Screen
 */
@Composable
private fun UsageAccessPermissionScreen(
    isGranted: Boolean,
    onOpenSettings: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    val view = LocalView.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .drawBehind {
                // Soft central purple radial glow behind shield
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.32f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.22f),
                        radius = size.width * 0.5f
                    )
                )
                // Bottom ambient light streak
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF7C3AED).copy(alpha = 0.16f),
                            Color.Transparent
                        ),
                        startY = size.height * 0.88f,
                        endY = size.height
                    )
                )
            }
            .padding(horizontal = 22.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top row with "Skip →" capsule on top right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1B1629).copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFF382D4D), RoundedCornerShape(20.dp))
                            .clickable {
                                Haptics.playLight(view)
                                onSkip()
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Skip ➔",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                // Center Hero Graphic: Mascot + Neon Shield on Pedestal
                Image(
                    painter = painterResource(id = R.drawable.mascot_perm_usage),
                    contentDescription = "Mascot presenting T1 Shield",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(175.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Step pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF6366F1).copy(alpha = 0.22f))
                        .border(1.dp, Color(0xFF818CF8).copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✦  STEP 2 OF 2",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFC7D2FE),
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Headline
                Text(
                    text = "Why We Need",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Usage Access",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFA855F7), Color(0xFFE879F9))
                        )
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle explanation
                Text(
                    text = "T1 is a Sprint Analyzer, not a spy. We read only aggregate app usage and screen unlock counts on your device to compute your Focus Score, track daily screen time by category, and power a fair Focus Fight experience.",
                    fontSize = 12.5.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 17.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Frosted Card with 3 rows
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF130E22).copy(alpha = 0.88f))
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF7C3AED).copy(alpha = 0.55f),
                                    Color(0xFF3B1D6E).copy(alpha = 0.25f)
                                )
                            ),
                            RoundedCornerShape(22.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        UsageFeatureRow(
                            icon = Icons.Default.BarChart,
                            title = "What we read",
                            subtitle = "App category usage, screen time and unlock counts."
                        )
                        UsageFeatureRow(
                            icon = Icons.Default.Bolt,
                            title = "How we use it",
                            subtitle = "To calculate your Focus Score, 7-day baseline, category breakdown and anti-cheat checks."
                        )
                        UsageFeatureRow(
                            icon = Icons.Default.Security,
                            title = "What we never access",
                            subtitle = "Messages, photos, contacts, GPS, or any private account data."
                        )
                    }
                }
            }

            // Bottom CTA section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF6D28D9),
                                    Color(0xFF9333EA),
                                    Color(0xFFA855F7)
                                )
                            )
                        )
                        .border(1.dp, Color(0xFFD8B4FE).copy(alpha = 0.6f), RoundedCornerShape(27.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = if (!isGranted) onOpenSettings else onContinue
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isGranted) "Continue ➔" else "Enable ➔",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (!isGranted) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Set up later in Settings",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onSkip
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Reusable Row for Notification Card
 */
@Composable
private fun NotifFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isApproved: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF22173D))
                .border(1.dp, Color(0xFF3B2766), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Badge indicator on right
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(
                    if (isApproved) Color(0xFF7C3AED).copy(alpha = 0.35f)
                    else Color(0xFF1E1A2B)
                )
                .border(
                    1.dp,
                    if (isApproved) Color(0xFFA855F7).copy(alpha = 0.85f)
                    else Color(0xFF332D45),
                    RoundedCornerShape(7.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isApproved) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (isApproved) Color(0xFFF3E8FF) else Color(0xFF64748B),
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

/**
 * Reusable Row for Usage Access Card
 */
@Composable
private fun UsageFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color(0xFF22173D))
                .border(1.dp, Color(0xFF3B2766), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFC084FC),
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 15.sp
            )
        }
    }
}
