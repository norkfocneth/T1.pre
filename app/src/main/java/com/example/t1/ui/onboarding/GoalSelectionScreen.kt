package com.example.t1.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.t1.R
import com.example.t1.theme.*
import com.example.t1.util.Haptics
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

data class GoalFeaturePillar(
    val iconEmoji: String,
    val label: String
)

data class FocusGoalOption(
    val id: String,
    val title: String,
    val accentedPart: String,
    val nonAccentedPart: String,
    val subtitle: String,
    val iconRes: Int,
    val modalMascotRes: Int,
    val accentColor: Color,
    val accentGradient: List<Color>,
    val detailInsight: String,
    val pillars: List<GoalFeaturePillar>
)

val FocusGoalOptions = listOf(
    FocusGoalOption(
        id = "ENTERTAINMENT",
        title = "Limit Entertainment & Gaming",
        accentedPart = "Entertainment & Gaming",
        nonAccentedPart = "Limit ",
        subtitle = "Reduce binge streaming, casual videos, and mobile games.",
        iconRes = R.drawable.ic_goal_entertainment,
        modalMascotRes = R.drawable.mascot_modal_entertainment,
        accentColor = Color(0xFFFB7185), // Coral Pink
        accentGradient = listOf(Color(0xFFFB7185), Color(0xFFE11D48)),
        detailInsight = "Target Locked: Entertainment Boundaries. T1 will prevent casual binge loops, enforce strict night cooldowns, and keep you in command of your downtime.",
        pillars = listOf(
            GoalFeaturePillar("🛑", "Kill Binge Loops"),
            GoalFeaturePillar("🎮", "Gaming Stasis"),
            GoalFeaturePillar("🌙", "Downtime Control")
        )
    ),
    FocusGoalOption(
        id = "EDUCATION",
        title = "Boost Learning & Study",
        accentedPart = "Learning & Study",
        nonAccentedPart = "Boost ",
        subtitle = "Increase time dedicated to study apps, courses, research & books.",
        iconRes = R.drawable.ic_goal_learning,
        modalMascotRes = R.drawable.mascot_modal_study,
        accentColor = Color(0xFF34D399), // Emerald
        accentGradient = listOf(Color(0xFF34D399), Color(0xFF059669)),
        detailInsight = "Target Locked: Study & Learning Mastery. T1 will maximize your cognitive endurance for deep reading while eliminating procrastination habits.",
        pillars = listOf(
            GoalFeaturePillar("🧠", "Cognitive Flow"),
            GoalFeaturePillar("📖", "Zero Distraction"),
            GoalFeaturePillar("🎯", "Study Streak")
        )
    ),
    FocusGoalOption(
        id = "PRODUCTIVITY",
        title = "Boost Productivity",
        accentedPart = "Productivity",
        nonAccentedPart = "Boost ",
        subtitle = "Maximize time spent on work, coding, writing & productive tasks.",
        iconRes = R.drawable.ic_goal_productivity,
        modalMascotRes = R.drawable.mascot_modal_productivity,
        accentColor = Color(0xFFA855F7), // Neon Violet / Purple
        accentGradient = listOf(Color(0xFFC084FC), Color(0xFFA855F7), Color(0xFF7C3AED)),
        detailInsight = "Target Locked: Peak Productivity. T1 will protect your deep focus hours, apply friction to distractions during work blocks, and award bonus consistency points.",
        pillars = listOf(
            GoalFeaturePillar("⚡", "Deep Work Sprint"),
            GoalFeaturePillar("🛡️", "App Friction Lock"),
            GoalFeaturePillar("🏆", "Consistency XP")
        )
    ),
    FocusGoalOption(
        id = "SOCIAL",
        title = "Limit Social Media",
        accentedPart = "Social Media",
        nonAccentedPart = "Limit ",
        subtitle = "Cut down on reels, shorts, endless feeds, and compulsive checking.",
        iconRes = R.drawable.ic_goal_social,
        modalMascotRes = R.drawable.mascot_modal_social,
        accentColor = Color(0xFFFB923C), // Glowing Orange / Amber
        accentGradient = listOf(Color(0xFFFDBA74), Color(0xFFFB923C), Color(0xFFEA580C)),
        detailInsight = "Target Locked: Social Media Detox. T1 will strictly intercept algorithmic loops, calculate your daily reclaimed hours, and rank your resistance on the Leaderboard.",
        pillars = listOf(
            GoalFeaturePillar("♾️", "Break Addiction Loops"),
            GoalFeaturePillar("📊", "Track Reclaimed Hours"),
            GoalFeaturePillar("🏆", "Compete on Leaderboard")
        )
    ),
    FocusGoalOption(
        id = "COMMUNICATION",
        title = "Limit Messaging & Chats",
        accentedPart = "Messaging & Chats",
        nonAccentedPart = "Limit ",
        subtitle = "Control time lost in continuous messaging, chats & groups.",
        iconRes = R.drawable.ic_goal_messaging,
        modalMascotRes = R.drawable.mascot_modal_messaging,
        accentColor = Color(0xFFC084FC), // Lavender Violet
        accentGradient = listOf(Color(0xFFE9D5FF), Color(0xFFC084FC), Color(0xFF9333EA)),
        detailInsight = "Target Locked: Messaging Boundaries. T1 will protect you from constant message anxiety and restore quiet mental clarity throughout your day.",
        pillars = listOf(
            GoalFeaturePillar("🛡️", "Chat Ping Shield"),
            GoalFeaturePillar("🧘", "Mental Clarity"),
            GoalFeaturePillar("🔕", "Batch Reply Mode")
        )
    )
)

@Composable
fun GoalSelectionScreen(
    onGoalSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    // Center initially on PRODUCTIVITY (index 2)
    val initialIndex = 2
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { FocusGoalOptions.size }
    )

    var showConfirmationModal by remember { mutableStateOf(false) }
    val currentGoal = FocusGoalOptions[pagerState.currentPage]

    // Tactile haptic feedback whenever page changes
    var isInitialLaunch by remember { mutableStateOf(true) }
    LaunchedEffect(pagerState.currentPage) {
        if (!isInitialLaunch) {
            Haptics.playLight(view)
        } else {
            isInitialLaunch = false
        }
    }

    // Floating breathing animation for Mascot Pointer
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_pointer_anim")
    val mascotFloatY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_float"
    )
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_pulse"
    )

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // TOP SECTION: Header + Headline
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Navigation Bar: "T1." | "02 / 04" | "Skip"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // T1 Logo
                    Text(
                        text = "T1.",
                        style = HeadlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            letterSpacing = (-0.5).sp,
                            color = Color.White
                        )
                    )

                    // Step Capsules & Number
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 0 until 4) {
                                val isActive = i == 1
                                val isCompleted = i < 1
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(3.5.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            when {
                                                isActive -> Color.White
                                                isCompleted -> Color(0xFF8B5CF6)
                                                else -> Color.White.copy(alpha = 0.16f)
                                            }
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "02 / 04",
                            style = LabelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.55f)
                            )
                        )
                    }

                    // Skip Button
                    Text(
                        text = "Skip",
                        style = LabelMedium.copy(
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                Haptics.playLight(view)
                                onGoalSelected("PRODUCTIVITY")
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Headline Area
                Text(
                    text = "What is your primary",
                    style = HeadlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                // "focus goal?" with glowing cursive & curved underline + sparkle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "focus goal?",
                            style = HeadlineMedium.copy(
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFEDE9FE),
                                        Color(0xFFC084FC),
                                        Color(0xFFA855F7)
                                    )
                                )
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Sparkle star
                        Canvas(modifier = Modifier.size(15.dp)) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val path = Path().apply {
                                moveTo(center.x, 0f)
                                quadraticTo(center.x, center.y, size.width, center.y)
                                quadraticTo(center.x, center.y, center.x, size.height)
                                quadraticTo(center.x, center.y, 0f, center.y)
                                quadraticTo(center.x, center.y, center.x, 0f)
                                close()
                            }
                            drawPath(path, color = Color(0xFFEDE9FE))
                        }
                    }

                    // Curved glow underline
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth(0.52f)
                            .height(12.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width * 0.05f, size.height * 0.85f)
                            quadraticTo(
                                size.width * 0.5f,
                                size.height * 0.15f,
                                size.width * 0.95f,
                                size.height * 0.45f
                            )
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFFA855F7).copy(alpha = 0.85f),
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "T1 calibrates its scoring engine around your personal objective.",
                    style = BodySmall.copy(
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 12.sp,
                        lineHeight = 15.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // ==========================================
            // CENTER HERO: 3D Mascot Pointer (Compact & Prominent)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp),
                contentAlignment = Alignment.Center
            ) {
                // Spherical Glowing Halo behind Mascot
                Canvas(
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            scaleX = haloPulse
                            scaleY = haloPulse
                        }
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF7C3AED).copy(alpha = 0.38f),
                                Color(0xFFA855F7).copy(alpha = 0.14f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width / 2
                        )
                    )
                    // Halo outer rim
                    drawArc(
                        color = Color(0xFFC084FC).copy(alpha = 0.45f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Mascot Asset pointing down
                Image(
                    painter = painterResource(id = R.drawable.mascot_goal_pointer),
                    contentDescription = "T1 Mascot Pointing Down",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(122.dp)
                        .graphicsLayer {
                            translationY = mascotFloatY
                        }
                )
            }

            // ==========================================
            // SLEEK 3D COVERFLOW CAROUSEL (Compact & Premium Proportion)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp),
                contentAlignment = Alignment.Center
            ) {
                // Holographic Ground Ring at the base of the cards
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(64.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = (-6).dp)
                ) {
                    // Outer neon ellipse
                    drawOval(
                        color = Color(0xFFA855F7).copy(alpha = 0.65f),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height),
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                    // Inner glow
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFA855F7).copy(alpha = 0.22f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width / 2
                        ),
                        topLeft = Offset(size.width * 0.08f, size.height * 0.12f),
                        size = Size(size.width * 0.84f, size.height * 0.76f)
                    )
                    // Tick mark
                    drawLine(
                        color = Color.White.copy(alpha = 0.55f),
                        start = Offset(size.width / 2, size.height),
                        end = Offset(size.width / 2, size.height + 5.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // Horizontal Pager with 3D CoverFlow transformations & sleek compact card size
                val density = LocalDensity.current
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 96.dp),
                    pageSpacing = (-26).dp,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                    val absOffset = pageOffset.absoluteValue
                    val isCenter = absOffset < 0.5f
                    val option = FocusGoalOptions[page]

                    // CoverFlow calculations: clean compact scaling and natural inward tilt
                    val scale = (1f - (absOffset * 0.15f)).coerceIn(0.72f, 1.0f)
                    val rotationY = (-pageOffset * 22f).coerceIn(-32f, 32f)
                    val alpha = (1f - (absOffset * 0.32f)).coerceIn(0.38f, 1.0f)
                    val zIdx = 10f - absOffset

                    Box(
                        modifier = Modifier
                            .width(172.dp)
                            .height(246.dp)
                            .zIndex(zIdx)
                            .graphicsLayer {
                                this.scaleX = scale
                                this.scaleY = scale
                                this.rotationY = rotationY
                                this.alpha = alpha
                                this.cameraDistance = 14f * density.density
                            }
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = if (isCenter) {
                                        listOf(
                                            Color(0xFF221A38),
                                            Color(0xFF151124),
                                            Color(0xFF0D0A18)
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFF171326).copy(alpha = 0.88f),
                                            Color(0xFF0F0C1B).copy(alpha = 0.88f)
                                        )
                                    }
                                )
                            )
                            .border(
                                width = if (isCenter) 1.8.dp else 1.dp,
                                brush = if (isCenter) {
                                    Brush.verticalGradient(
                                        listOf(
                                            option.accentColor,
                                            Color(0xFFC084FC).copy(alpha = 0.6f),
                                            Color(0xFF381D63).copy(alpha = 0.25f)
                                        )
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.16f),
                                            Color.White.copy(alpha = 0.04f)
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(22.dp)
                            )
                            .clickable {
                                Haptics.playMedium(view)
                                if (page == pagerState.currentPage) {
                                    showConfirmationModal = true
                                } else {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(page)
                                    }
                                }
                            }
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Row: Checkmark (center) or subtle ring (side)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (isCenter) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                                    )
                                }
                            }

                            // Center 3D Icon Badge
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(if (isCenter) 62.dp else 48.dp)
                                    .padding(vertical = 2.dp)
                            ) {
                                if (isCenter) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    option.accentColor.copy(alpha = 0.42f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                    }
                                }
                                Image(
                                    painter = painterResource(id = option.iconRes),
                                    contentDescription = option.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            // Title & Description (Clean, sharp, premium typography)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = option.title,
                                    style = LabelLarge.copy(
                                        fontSize = if (isCenter) 15.sp else 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCenter) Color.White else Color.White.copy(alpha = 0.75f),
                                        lineHeight = if (isCenter) 18.sp else 15.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )

                                if (isCenter) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = option.subtitle,
                                        style = BodySmall.copy(
                                            color = Color.White.copy(alpha = 0.68f),
                                            fontSize = 10.5.sp,
                                            lineHeight = 14.sp
                                        ),
                                        textAlign = TextAlign.Center,
                                        maxLines = 3
                                    )
                                }
                            }

                            // Bottom subtle dot for inactive cards
                            if (!isCenter) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                )
                            } else {
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        }
                    }
                }
            }

            // ==========================================
            // CAROUSEL CONTROLS: [<]  • • • • •  [>]
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Left Arrow
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E192D).copy(alpha = 0.75f))
                        .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape)
                        .clickable {
                            if (pagerState.currentPage > 0) {
                                Haptics.playLight(view)
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Goal",
                        tint = if (pagerState.currentPage > 0) Color.White else Color.White.copy(alpha = 0.28f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 5 Dots Pagination
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until FocusGoalOptions.size) {
                        val isCurrent = i == pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .size(width = if (isCurrent) 16.dp else 5.5.dp, height = 5.5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isCurrent) Color.White else Color.White.copy(alpha = 0.22f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right Arrow
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E192D).copy(alpha = 0.75f))
                        .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape)
                        .clickable {
                            if (pagerState.currentPage < FocusGoalOptions.size - 1) {
                                Haptics.playLight(view)
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Goal",
                        tint = if (pagerState.currentPage < FocusGoalOptions.size - 1) Color.White else Color.White.copy(alpha = 0.28f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // ==========================================
            // BOTTOM CTA: "Continue ➔" (Luminous Pill)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFF3E8FF),
                                Color(0xFFE9D5FF),
                                Color(0xFFD8B4FE)
                            )
                        )
                    )
                    .clickable {
                        Haptics.playHeavy(view)
                        showConfirmationModal = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue",
                        style = LabelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.5.sp,
                            color = Color(0xFF1A102E)
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "➔",
                        style = LabelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.5.sp,
                            color = Color(0xFF1A102E)
                        )
                    )
                }
            }
        }

        // =========================================================================
        // GOAL DEEP-DIVE CONFIRMATION MODAL (Matching Reference Image 2!)
        // =========================================================================
        if (showConfirmationModal) {
            Dialog(
                onDismissRequest = { showConfirmationModal = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.82f))
                        .padding(horizontal = 22.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Modal Card Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF181524),
                                        Color(0xFF120F1D),
                                        Color(0xFF0B0914)
                                    )
                                )
                            )
                            .border(
                                width = 1.6.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        currentGoal.accentColor,
                                        currentGoal.accentColor.copy(alpha = 0.45f),
                                        Color.Transparent
                                    )
                                ),
                                shape = RoundedCornerShape(30.dp)
                            )
                            .clickable(enabled = false) {}
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Top Row: Close Button (✕)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .clickable {
                                            Haptics.playLight(view)
                                            showConfirmationModal = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            // Mascot Hero Visual for this specific goal
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(180.dp)) {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                currentGoal.accentColor.copy(alpha = 0.35f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                }

                                Image(
                                    painter = painterResource(id = currentGoal.modalMascotRes),
                                    contentDescription = currentGoal.title,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.88f),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            // Glowing curved horizon divider
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp)
                            ) {
                                val path = Path().apply {
                                    moveTo(0f, size.height * 0.8f)
                                    quadraticTo(
                                        size.width / 2,
                                        0f,
                                        size.width,
                                        size.height * 0.8f
                                    )
                                }
                                drawPath(
                                    path = path,
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            currentGoal.accentColor,
                                            Color.Transparent
                                        )
                                    ),
                                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Modal Title with highlighted keyword
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentGoal.nonAccentedPart,
                                    style = HeadlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 21.sp,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = currentGoal.accentedPart,
                                    style = HeadlineSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 21.sp,
                                        color = currentGoal.accentColor
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Target Locked Description
                            Text(
                                text = currentGoal.detailInsight,
                                style = BodySmall.copy(
                                    color = Color.White.copy(alpha = 0.78f),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 3 Feature Pillars Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currentGoal.pillars.forEach { pillar ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(78.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .border(
                                                1.dp,
                                                currentGoal.accentColor.copy(alpha = 0.25f),
                                                RoundedCornerShape(15.dp)
                                            )
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = pillar.iconEmoji,
                                                fontSize = 19.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = pillar.label,
                                                style = LabelSmall.copy(
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    lineHeight = 12.5.sp
                                                ),
                                                textAlign = TextAlign.Center,
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // CTA: "Lock Goal & Continue ➔"
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(25.dp))
                                    .background(
                                        Brush.horizontalGradient(currentGoal.accentGradient)
                                    )
                                    .clickable {
                                        Haptics.playHeavy(view)
                                        showConfirmationModal = false
                                        onGoalSelected(currentGoal.id)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Lock Goal & Continue",
                                        style = LabelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "➔",
                                        style = LabelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
