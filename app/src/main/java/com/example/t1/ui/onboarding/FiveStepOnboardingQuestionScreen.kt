package com.example.t1.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.R
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

data class LiveOnboardingOption(
    val id: Int,
    val text: String,
    val icon: ImageVector
)

data class LiveOnboardingStep(
    val stepIndex: Int,
    val titlePrefix: String,
    val titleHighlight: String,
    val titleSuffix: String = "",
    val subtitle: String,
    val mascotRes: Int,
    val speechBubbleText: String? = null,
    val options: List<LiveOnboardingOption>,
    val defaultSelectedIndex: Int = 1,
    val isLastStep: Boolean = false
)

val liveOnboardingSteps = listOf(
    // ─── STEP 1 (01 / 05) : THE PHONE STRUGGLE ───
    LiveOnboardingStep(
        stepIndex = 0,
        titlePrefix = "What’s your biggest\nstruggle with ",
        titleHighlight = "your phone?",
        titleSuffix = "",
        subtitle = "Be real. This helps T1 understand your habits and support you better.",
        mascotRes = R.drawable.mascot_live_struggle,
        defaultSelectedIndex = 1,
        options = listOf(
            LiveOnboardingOption(0, "I keep scrolling longer than I intend to.", Icons.Default.AllInclusive),
            LiveOnboardingOption(1, "I get distracted while studying or working.", Icons.Default.GpsFixed),
            LiveOnboardingOption(2, "I check my phone without even thinking.", Icons.Default.PhonelinkRing),
            LiveOnboardingOption(3, "I struggle to stay focused for long.", Icons.Default.HourglassEmpty),
            LiveOnboardingOption(4, "I spend too much time on social media.", Icons.Default.CameraAlt),
            LiveOnboardingOption(5, "I just want better control over my habits.", Icons.Default.Tune)
        )
    ),

    // ─── STEP 2 (02 / 05) : BEDTIME DISTRACTION ───
    LiveOnboardingStep(
        stepIndex = 1,
        titlePrefix = "Have you ever picked up your\nphone for one thing and ended\nup doing something ",
        titleHighlight = "completely\ndifferent?",
        titleSuffix = "",
        subtitle = "It happens to the best of us. How often does it happen to you?",
        mascotRes = R.drawable.mascot_live_bedtime,
        defaultSelectedIndex = 0,
        options = listOf(
            LiveOnboardingOption(0, "Almost every day.", Icons.Default.Sync),
            LiveOnboardingOption(1, "Happens quite often.", Icons.Default.CalendarMonth),
            LiveOnboardingOption(2, "Sometimes.", Icons.Default.Casino),
            LiveOnboardingOption(3, "Rarely.", Icons.Default.RemoveCircleOutline)
        )
    ),

    // ─── STEP 3 (03 / 05) : SCREEN TIME TELEMETRY ───
    LiveOnboardingStep(
        stepIndex = 2,
        titlePrefix = "How much time do you\nspend on ",
        titleHighlight = "your phone",
        titleSuffix = " daily?",
        subtitle = "Be honest. This helps T1 set the right plan for you.",
        mascotRes = R.drawable.mascot_live_analytics,
        defaultSelectedIndex = 2,
        options = listOf(
            LiveOnboardingOption(0, "Less than 2 hours", Icons.Default.AccessTime),
            LiveOnboardingOption(1, "2 – 4 hours", Icons.Default.PhoneAndroid),
            LiveOnboardingOption(2, "4 – 6 hours", Icons.Default.BarChart),
            LiveOnboardingOption(3, "6 – 8 hours", Icons.Default.Headphones),
            LiveOnboardingOption(4, "More than 8 hours", Icons.Default.AllInclusive)
        )
    ),

    // ─── STEP 4 (04 / 05) : GOAL TRANSFORMATION ───
    LiveOnboardingStep(
        stepIndex = 3,
        titlePrefix = "What would you like to\nimprove ",
        titleHighlight = "the most?",
        titleSuffix = "",
        subtitle = "Choose the one thing that matters most to you right now.",
        mascotRes = R.drawable.mascot_live_improve,
        defaultSelectedIndex = 0,
        options = listOf(
            LiveOnboardingOption(0, "Focus & concentration", Icons.Default.Psychology),
            LiveOnboardingOption(1, "Better sleep", Icons.Default.Bedtime),
            LiveOnboardingOption(2, "Health & fitness", Icons.Default.FitnessCenter),
            LiveOnboardingOption(3, "Productivity & time management", Icons.Default.Person),
            LiveOnboardingOption(4, "Self growth & learning", Icons.Default.Star),
            LiveOnboardingOption(5, "Less screen time", Icons.Default.PhoneAndroid)
        )
    ),

    // ─── STEP 5 (05 / 05) : SUMMIT VISION ───
    LiveOnboardingStep(
        stepIndex = 4,
        titlePrefix = "Imagine having better control over your phone.\nWhat would ",
        titleHighlight = "change first? ✨",
        titleSuffix = "",
        subtitle = "Pick the one that feels most valuable to you.",
        mascotRes = R.drawable.mascot_live_hero,
        speechBubbleText = "Imagine having better control over your phone. What would change first? ✨",
        defaultSelectedIndex = 0,
        isLastStep = true,
        options = listOf(
            LiveOnboardingOption(0, "I’d get more work done.", Icons.Default.Laptop),
            LiveOnboardingOption(1, "I’d spend more time on myself.", Icons.Default.FavoriteBorder),
            LiveOnboardingOption(2, "I’d be more present with people.", Icons.Default.PeopleOutline),
            LiveOnboardingOption(3, "I’d sleep and wake up better.", Icons.Default.Bedtime),
            LiveOnboardingOption(4, "I’d finally feel in control.", Icons.Default.WorkspacePremium)
        )
    )
)

@Composable
fun FiveStepOnboardingQuestionScreen(
    onComplete: (answers: List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    var currentStep by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }

    // Initialize defaults
    LaunchedEffect(Unit) {
        liveOnboardingSteps.forEach { step ->
            if (!userAnswers.containsKey(step.stepIndex)) {
                userAnswers[step.stepIndex] = step.defaultSelectedIndex
            }
        }
    }

    BackHandler(enabled = currentStep > 0) {
        currentStep--
    }

    val stepConfig = liveOnboardingSteps[currentStep]

    val bgDark = Color(0xFF090710)
    val purplePrimary = Color(0xFF9333EA)
    val purpleGlow = Color(0xFFC084FC)
    val lilacAccent = Color(0xFFD8B4FE)
    val textMuted = Color(0xFFA59AB8)

    // ─── ALIVE MASCOT ORGANIC FLOATING PHYSICS ───
    val infiniteTransition = rememberInfiniteTransition(label = "mascotBreathing")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    // ─── SEQUENTIAL STAGGERED ENTRANCE CHOREOGRAPHY ───
    var mascotAppeared by remember(currentStep) { mutableStateOf(false) }
    var headlineAppeared by remember(currentStep) { mutableStateOf(false) }
    var visibleOptionCount by remember(currentStep) { mutableIntStateOf(0) }
    var ctaAppeared by remember(currentStep) { mutableStateOf(false) }

    LaunchedEffect(currentStep) {
        mascotAppeared = false
        headlineAppeared = false
        visibleOptionCount = 0
        ctaAppeared = false

        // 1. Mascot lands with spring bounce
        delay(40)
        mascotAppeared = true

        // 2. Headline reveals
        delay(220)
        headlineAppeared = true

        // 3. Options cascade down one by one
        val count = stepConfig.options.size
        for (i in 1..count) {
            delay(85)
            visibleOptionCount = i
        }

        // 4. CTA glows
        delay(80)
        ctaAppeared = true
    }

    val animatedMascotScale by animateFloatAsState(
        targetValue = if (mascotAppeared) 1f else 0.65f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mascotEntranceScale"
    )
    val animatedMascotAlpha by animateFloatAsState(
        targetValue = if (mascotAppeared) 1f else 0f,
        animationSpec = tween(350),
        label = "mascotEntranceAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── 1. TOP BRAND HEADER & ANIMATED PROGRESS ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // T1. Brand Logo
                Text(
                    text = "T1.",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    fontSize = 25.sp,
                    letterSpacing = (-0.5).sp
                )

                // 5-Segment Progress Indicator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until 5) {
                            val isCompletedOrActive = i <= currentStep
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (isCompletedOrActive) {
                                            Brush.horizontalGradient(listOf(purplePrimary, purpleGlow))
                                        } else {
                                            Brush.horizontalGradient(
                                                listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.12f))
                                            )
                                        }
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "0${currentStep + 1} / 05",
                        color = textMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Skip Action
                Text(
                    text = "Skip",
                    color = textMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable {
                            Haptics.playLight(view)
                            val finalAnswers = (0 until 5).map { userAnswers[it] ?: 0 }
                            onComplete(finalAnswers)
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            // ─── 2. MAIN SCROLLABLE ONBOARDING STAGE ───
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width / 2 } + fadeIn(tween(300)))
                            .togetherWith(slideOutHorizontally { width -> -width / 2 } + fadeOut(tween(200)))
                    } else {
                        (slideInHorizontally { width -> -width / 2 } + fadeIn(tween(300)))
                            .togetherWith(slideOutHorizontally { width -> width / 2 } + fadeOut(tween(200)))
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "step_content_transition"
            ) { stepIdx ->
                val config = liveOnboardingSteps[stepIdx]
                val selectedOption = userAnswers[stepIdx] ?: config.defaultSelectedIndex

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ─── ALIVE ISOLATED 3D MASCOT STAGE (NO RECTANGULAR BOX!) ───
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Ambient Breathing Radial Neon Glow (Directly on canvas)
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .scale(breathingScale)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF8B5CF6).copy(alpha = 0.32f),
                                            Color(0xFF9333EA).copy(alpha = 0.12f),
                                            Color.Transparent
                                        )
                                    ),
                                    CircleShape
                                )
                        )

                        // Isolated Transparent 3D Character (Free floating & alive)
                        Image(
                            painter = painterResource(id = config.mascotRes),
                            contentDescription = "T1 3D Mascot",
                            modifier = Modifier
                                .fillMaxHeight()
                                .scale(animatedMascotScale * breathingScale)
                                .offset(y = floatY.dp),
                            contentScale = ContentScale.Fit,
                            alpha = animatedMascotAlpha
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ─── DYNAMIC HEADLINE & SUBTITLE WITH GLOW HIGHLIGHT ───
                    AnimatedVisibility(
                        visible = headlineAppeared,
                        enter = slideInVertically(initialOffsetY = { 30 }) + fadeIn(tween(300))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            val annotatedTitle = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 21.sp
                                    )
                                ) {
                                    append(config.titlePrefix)
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = lilacAccent,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 21.sp
                                    )
                                ) {
                                    append(config.titleHighlight)
                                }
                                if (config.titleSuffix.isNotEmpty()) {
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 21.sp
                                        )
                                    ) {
                                        append(config.titleSuffix)
                                    }
                                }
                            }

                            Text(
                                text = annotatedTitle,
                                lineHeight = 27.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = config.subtitle,
                                color = textMuted,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ─── STAGGERED ANIMATED OPTION CARDS ───
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        config.options.forEachIndexed { optIndex, opt ->
                            val isItemVisible = optIndex < visibleOptionCount
                            val isSelected = selectedOption == opt.id

                            AnimatedVisibility(
                                visible = isItemVisible,
                                enter = slideInVertically(
                                    initialOffsetY = { 50 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ) + fadeIn(tween(250)) + scaleIn(initialScale = 0.94f)
                            ) {
                                StaggeredOptionCard(
                                    option = opt,
                                    isSelected = isSelected,
                                    onSelect = {
                                        Haptics.playLight(view)
                                        userAnswers[stepIdx] = opt.id
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            // ─── 3. BOTTOM ACTION CONTROLS (Back + Continue / Finish Setup) ───
            AnimatedVisibility(
                visible = ctaAppeared,
                enter = slideInVertically(initialOffsetY = { 40 }) + fadeIn(tween(300))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Back Circular Button (←)
                    val backInteraction = remember { MutableInteractionSource() }
                    val isBackPressed by backInteraction.collectIsPressedAsState()
                    val backScale by animateFloatAsState(
                        targetValue = if (isBackPressed) 0.9f else 1f,
                        label = "backScale"
                    )

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .scale(backScale)
                            .clip(CircleShape)
                            .background(Color(0xFF1B142B))
                            .border(
                                1.dp,
                                if (currentStep > 0) Color(0xFF3E2D58) else Color(0xFF251C38),
                                CircleShape
                            )
                            .clickable(
                                enabled = currentStep > 0,
                                interactionSource = backInteraction,
                                indication = null,
                                onClick = {
                                    Haptics.playLight(view)
                                    currentStep--
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (currentStep > 0) Color.White else Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // CTA Glowing Button (Continue → or Finish Setup ✨)
                    val ctaInteraction = remember { MutableInteractionSource() }
                    val isCtaPressed by ctaInteraction.collectIsPressedAsState()
                    val ctaScale by animateFloatAsState(
                        targetValue = if (isCtaPressed) 0.96f else 1f,
                        label = "ctaScale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .scale(ctaScale)
                            .shadow(
                                elevation = 18.dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = purplePrimary,
                                spotColor = purpleGlow
                            )
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF7C3AED),
                                        Color(0xFF9333EA),
                                        Color(0xFFC084FC),
                                        Color(0xFFE9D5FF)
                                    )
                                )
                            )
                            .clickable(
                                interactionSource = ctaInteraction,
                                indication = null,
                                onClick = {
                                    if (stepConfig.isLastStep || currentStep >= 4) {
                                        Haptics.playSuccess(context)
                                        val finalAnswers = (0 until 5).map { userAnswers[it] ?: 0 }
                                        onComplete(finalAnswers)
                                    } else {
                                        Haptics.playMedium(view)
                                        currentStep++
                                    }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (stepConfig.isLastStep) "Finish Setup" else "Continue",
                                color = Color(0xFF0F0C1B),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 0.3.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (stepConfig.isLastStep) {
                                Text(text = "✨", fontSize = 15.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFF0F0C1B),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun StaggeredOptionCard(
    option: LiveOnboardingOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "optCardScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.5.dp)
            .scale(animatedScale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF2E1B4F).copy(alpha = 0.95f),
                            Color(0xFF22143A).copy(alpha = 0.95f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF191228).copy(alpha = 0.65f),
                            Color(0xFF140E20).copy(alpha = 0.65f)
                        )
                    )
                }
            )
            .border(
                border = if (isSelected) {
                    BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(
                            listOf(Color(0xFF8B5CF6), Color(0xFFC084FC), Color(0xFFE9D5FF))
                        )
                    )
                } else {
                    BorderStroke(1.dp, Color(0xFF2B203E))
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onSelect
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container box
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isSelected) Color(0xFF3D2568) else Color(0xFF241938)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color(0xFFD8B4FE),
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(13.dp))

        // Option text
        Text(
            text = option.text,
            color = Color.White,
            fontSize = 13.5.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Radio checkmark indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFA855F7), Color(0xFFC084FC))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFF4C3B65), CircleShape)
            )
        }
    }
}
