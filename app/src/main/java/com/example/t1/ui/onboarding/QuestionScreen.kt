package com.example.t1.ui.onboarding

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class QuestionOption(
    val text: String,
    val statTag: String,
    val insight: String
)

data class Question(
    val title: String,
    val subtitle: String?,
    val options: List<QuestionOption>
)

val questions = listOf(
    Question(
        title = "Be honest — what eats most of your time?",
        subtitle = "Be honest. This shapes your baseline score.",
        options = listOf(
            QuestionOption(
                text = "Social media (Instagram, reels)",
                statTag = "84% SHARE THIS STRUGGLE",
                insight = "84% of high achievers were trapped in infinite algorithmic feeds before breaking free. Admitting this leak is your first victory."
            ),
            QuestionOption(
                text = "Studying / productive work",
                statTag = "TOP 12% MINDS",
                insight = "You're already driven. T1 will help protect your stamina, eliminate subtle burnouts, and make your deep flow effortless."
            ),
            QuestionOption(
                text = "Gaming",
                statTag = "HYPERFOCUS ASSET",
                insight = "Gamers possess 3x sharper reaction speed and focus endurance. Redirecting that hyperfocus into your goals changes everything."
            ),
            QuestionOption(
                text = "Chatting & Messaging",
                statTag = "UNBREAKABLE BOUNDARIES",
                insight = "Constant availability drains deep mental clarity. Reclaiming quiet focus blocks gives you your freedom and life back."
            )
        )
    ),
    Question(
        title = "How many hours are you actually on your phone?",
        subtitle = "Calibrating your focus capacity...",
        options = listOf(
            QuestionOption(
                text = "2–3 hours",
                statTag = "ELITE TIER DISCIPLINE",
                insight = "You're in the top 10% cleanest digital diets. Now let's turn every single remaining hour into pure mastery."
            ),
            QuestionOption(
                text = "3–5 hours",
                statTag = "AVERAGE PHONE USER",
                insight = "Cutting just 90 minutes gives you 22 full days back each year. Think of the empire you can build with that time."
            ),
            QuestionOption(
                text = "5–7 hours",
                statTag = "HIGH RECLAIM POTENTIAL",
                insight = "76% of T1 members start here. In week one, they reclaim an average of 2.4 hours every single day."
            ),
            QuestionOption(
                text = "7+ hours",
                statTag = "UNSTOPPABLE SHIFT",
                insight = "Brutal honesty takes courage. You're here because you refuse to stay trapped. Together, we're going to conquer this."
            )
        )
    ),
    Question(
        title = "When does your focus completely collapse?",
        subtitle = "Pinpointing your daily energy leaks...",
        options = listOf(
            QuestionOption(
                text = "Morning",
                statTag = "MOMENTUM KEY",
                insight = "Win the morning, win the day. Guarding your first 45 minutes builds an unbreakable fortress for the rest of your day."
            ),
            QuestionOption(
                text = "Afternoon",
                statTag = "BIOLOGICAL SLUMP",
                insight = "Afternoon dopamine fatigue is normal human biology. Structured micro-resets will eliminate this slump forever."
            ),
            QuestionOption(
                text = "Evening",
                statTag = "WILLPOWER RESET",
                insight = "Evening screen fatigue drains tomorrow's drive. A clear digital cutoff locks in deep, restorative peace."
            ),
            QuestionOption(
                text = "Late night",
                statTag = "RECOVERY RESTORATION",
                insight = "Blue light destroys deep REM sleep. Reclaiming your nights sharpens your cognitive clarity by 40% tomorrow."
            )
        )
    ),
    Question(
        title = "Deep down, you know you're wasting time. How much?",
        subtitle = "Honesty here powers your custom focus plan.",
        options = listOf(
            QuestionOption(
                text = "Not really",
                statTag = "HIGH CONFIDENCE",
                insight = "Strong mental boundaries. Let's channel that natural clarity into top-tier peak performance."
            ),
            QuestionOption(
                text = "Sometimes",
                statTag = "HIGH POTENTIAL",
                insight = "Awareness is 80% of the battle. Consistent friction rules will close the remaining gap effortlessly."
            ),
            QuestionOption(
                text = "Yes, often",
                statTag = "TRANSFORMATION STARTS NOW",
                insight = "91% felt this exact weight before joining. That raw honesty is the exact fuel you need to break free."
            )
        )
    ),
    Question(
        title = "What do you actually want to fix?",
        subtitle = "Setting your core North Star metric...",
        options = listOf(
            QuestionOption(
                text = "Laser Focus",
                statTag = "CORE SUPERPOWER",
                insight = "Deep, uninterrupted work is the rarest and most valuable superpower of the 21st century."
            ),
            QuestionOption(
                text = "Reduce screen time",
                statTag = "TIME FREEDOM",
                insight = "Less screen time means more life lived. You're about to reclaim hundreds of precious hours."
            ),
            QuestionOption(
                text = "Better study consistency",
                statTag = "COMPOUND ADVANTAGE",
                insight = "Daily consistency outperforms raw talent every single time. You're building an unfair advantage."
            ),
            QuestionOption(
                text = "Build discipline",
                statTag = "THE MASTER KEY",
                insight = "Discipline is freedom. The mental resilience you forge here will elevate every single area of your life."
            )
        )
    ),
    Question(
        title = "How serious are you — really?",
        subtitle = "This is your commitment to yourself.",
        options = listOf(
            QuestionOption(
                text = "Just exploring",
                statTag = "CURIOUS MIND",
                insight = "Curiosity is where every legendary journey begins. Feel the mental clarity shift in your first 48 hours."
            ),
            QuestionOption(
                text = "Somewhat serious",
                statTag = "READY TO GROW",
                insight = "Small 1% daily upgrades compound into massive transformation over 30 days. You're on the right path."
            ),
            QuestionOption(
                text = "Fully committed",
                statTag = "TOP 1% MINDSET",
                insight = "You are in the top 1% of committed individuals. T1 was built specifically for minds that refuse mediocrity."
            )
        )
    )
)

@Composable
fun QuestionScreen(
    onComplete: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    var currentQ by remember { mutableIntStateOf(0) }
    val answers = remember { mutableListOf<Int>() }

    // State for the Interstitial Motivational Screen
    var motivationalOption by remember { mutableStateOf<QuestionOption?>(null) }

    val progress = ((currentQ + 1).toFloat() / questions.size)

    BackHandler(enabled = motivationalOption != null || currentQ > 0) {
        if (motivationalOption != null) {
            motivationalOption = null
        } else if (currentQ > 0) {
            currentQ--
            if (answers.isNotEmpty()) {
                answers.removeAt(answers.size - 1)
            }
        }
    }

    OnboardingBackground(modifier = modifier) {
        // If an option was selected, show the Minimalist Zen Interstitial Screen
        if (motivationalOption != null) {
            InterstitialMotivationalScreen(
                option = motivationalOption!!,
                onContinue = {
                    motivationalOption = null
                    if (currentQ < questions.size - 1) {
                        currentQ++
                    } else {
                        Haptics.playSuccess(context)
                        onComplete(answers.toList())
                    }
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Header: Step progress text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STEP ${currentQ + 1} OF ${questions.size}",
                        style = BodySmall.copy(
                            fontSize = 11.sp,
                            color = MutedForeground,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = BodySmall.copy(
                            fontSize = 11.sp,
                            color = Foreground.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Progress bar with glowing gradient
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(500, easing = FastOutSlowInEasing),
                    label = "progress"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF1E1E26))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF818CF8), Color(0xFFFFFFFF), Color(0xFF38BDF8))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Slide Transitions between questions using AnimatedContent
                AnimatedContent(
                    targetState = currentQ,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(320)) + slideInHorizontally(
                            initialOffsetX = { 240 },
                            animationSpec = tween(320)
                        )).togetherWith(
                            fadeOut(animationSpec = tween(240)) + slideOutHorizontally(
                                targetOffsetX = { -240 },
                                animationSpec = tween(240)
                            )
                        )
                    },
                    label = "question_slider",
                    modifier = Modifier.weight(1f)
                ) { targetQIndex ->
                    val targetQ = questions[targetQIndex]

                    QuestionCardContent(
                        question = targetQ,
                        questionIndex = targetQIndex,
                        onOptionSelected = { selectedIdx ->
                            answers.add(selectedIdx)
                            val chosenOption = targetQ.options[selectedIdx]
                            // Open Interstitial Motivational Screen
                            motivationalOption = chosenOption
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionCardContent(
    question: Question,
    questionIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    // Typewriter state
    var displayedCharCount by remember(questionIndex) { mutableIntStateOf(0) }
    var isTypingDone by remember(questionIndex) { mutableStateOf(false) }

    // Selected option state for instant tactile feedback before interstitial
    var selectedOptionIdx by remember(questionIndex) { mutableStateOf<Int?>(null) }

    val fullTitle = question.title

    // Typewriter effect coroutine: deliberate pacing (36ms) + tactile keyboard ticks
    LaunchedEffect(questionIndex) {
        displayedCharCount = 0
        isTypingDone = false
        selectedOptionIdx = null

        // Delay 150ms before typing begins
        delay(150L)

        for (i in 1..fullTitle.length) {
            displayedCharCount = i
            val char = fullTitle[i - 1]

            // Soft tactile feedback on characters
            if (!char.isWhitespace()) {
                if (Haptics.isEnabled) {
                    try {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    } catch (_: Exception) {}
                }
            }

            // High-end deliberate typing speed: 36ms
            delay(36L)
        }
        isTypingDone = true
    }

    // Blinking cursor transition while typing
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    // Tap-to-skip typing: tap anywhere while typing to instantly reveal full title & glide up
    val onSkipTyping = {
        if (!isTypingDone) {
            displayedCharCount = fullTitle.length
            isTypingDone = true
        }
    }

    // Center-to-Top Glide: while typing it is centered (160dp padding), then glides to top (0dp)
    val animatedTopPadding by animateDpAsState(
        targetValue = if (isTypingDone) 0.dp else 160.dp,
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = 220f
        ),
        label = "center_to_top_glide"
    )

    val displayedTitle = fullTitle.take(displayedCharCount)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSkipTyping
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        // Space that shrinks as the title glides from Center to Top
        Spacer(modifier = Modifier.height(animatedTopPadding))

        // Question Title Section with Ambient Aura
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6366F1).copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.2f, size.height * 0.5f),
                            radius = size.width * 0.8f
                        )
                    )
                }
                .padding(bottom = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = displayedTitle,
                    style = HeadlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 27.sp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),
                                Color(0xFFE2E8F0),
                                Color(0xFFCBD5E1)
                            )
                        )
                    ),
                    lineHeight = 36.sp
                )

                // Glowing cursor while typing
                if (!isTypingDone) {
                    Text(
                        text = " |",
                        style = HeadlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 27.sp,
                            color = Color(0xFF818CF8).copy(alpha = cursorAlpha)
                        ),
                        lineHeight = 36.sp
                    )
                }
            }
        }

        // Subtitle (fades in smoothly as typing finishes)
        AnimatedVisibility(
            visible = isTypingDone && question.subtitle != null,
            enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 10 })
        ) {
            question.subtitle?.let {
                Text(
                    text = it,
                    style = BodyMedium.copy(
                        color = MutedForeground,
                        lineHeight = 20.sp,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )
            }
        }

        if (!isTypingDone || question.subtitle == null) {
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Options List with Staggered Cascading Reveal (only visible after title glides up)
        AnimatedVisibility(
            visible = isTypingDone,
            enter = fadeIn(tween(250))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val optionLetters = listOf("A", "B", "C", "D")

                question.options.forEachIndexed { idx, option ->
                    val isSelected = selectedOptionIdx == idx
                    val letterBadge = optionLetters.getOrElse(idx) { "${idx + 1}" }

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(
                            animationSpec = tween(350, delayMillis = idx * 70)
                        ) + slideInVertically(
                            initialOffsetY = { 35 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    ) {
                        OpalOptionCard(
                            letter = letterBadge,
                            option = option,
                            isSelected = isSelected,
                            isAnySelected = selectedOptionIdx != null,
                            onClick = {
                                if (selectedOptionIdx == null) {
                                    selectedOptionIdx = idx
                                    Haptics.playMedium(view)

                                    // Brief 220ms visual tap feedback before opening Interstitial
                                    coroutineScope.launch {
                                        delay(220L)
                                        onOptionSelected(idx)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun OpalOptionCard(
    letter: String,
    option: QuestionOption,
    isSelected: Boolean,
    isAnySelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.98f else 1f

    val targetAlpha = if (isAnySelected && !isSelected) 0.40f else 1f
    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(200),
        label = "option_alpha"
    )

    val borderColor = if (isSelected) Color.White else Color(0xFF262632)
    val backgroundColor = if (isSelected) Color(0xFF1E1E2A) else Color(0xFF121217).copy(alpha = 0.85f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(animatedAlpha)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 16.dp, horizontal = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White else Color(0xFF22222E)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    style = BodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else Color(0xFFA1A1AA)
                    )
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = option.text,
                style = BodyMedium.copy(
                    color = if (isSelected) Color.White else Foreground,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                ),
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF38BDF8))
                )
            }
        }
    }
}

/**
 * Dedicated Minimalist Zen Interstitial Screen.
 * Displays when an option is selected. It features an ambient glowing aura,
 * letter-by-letter insight typing, and tap-to-continue progression.
 */
@Composable
fun InterstitialMotivationalScreen(
    option: QuestionOption,
    onContinue: () -> Unit
) {
    val view = LocalView.current
    var displayedQuoteCount by remember { mutableIntStateOf(0) }
    var isQuoteDone by remember { mutableStateOf(false) }

    val fullQuote = option.insight

    // Typewriter effect for the motivational quote
    LaunchedEffect(option) {
        displayedQuoteCount = 0
        isQuoteDone = false
        delay(100L)

        for (i in 1..fullQuote.length) {
            displayedQuoteCount = i
            val char = fullQuote[i - 1]
            if (!char.isWhitespace()) {
                if (Haptics.isEnabled) {
                    try {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    } catch (_: Exception) {}
                }
            }
            delay(22L)
        }
        isQuoteDone = true

        // Auto-advance after 2.2 seconds of reading time
        delay(2200L)
        onContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090C))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onContinue
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background glowing aura
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6366F1).copy(alpha = 0.14f),
                                Color(0xFF0F172A).copy(alpha = 0.04f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.45f),
                            radius = size.width * 0.85f
                        )
                    )
                }
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Badge Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF6366F1).copy(alpha = 0.25f))
                    .border(1.dp, Color(0xFF818CF8).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "✦  ${option.statTag}",
                    style = BodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFA5B4FC),
                        letterSpacing = 1.2.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Quote Text (Centered & Glowing)
            Text(
                text = fullQuote.take(displayedQuoteCount),
                style = HeadlineMedium.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFF1F5F9),
                            Color(0xFFCBD5E1)
                        )
                    ),
                    textAlign = TextAlign.Center
                ),
                lineHeight = 32.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Tap to continue pulse button
            AnimatedVisibility(
                visible = isQuoteDone,
                enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 20 })
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1E1E28))
                        .border(1.dp, Color(0xFF38384A), RoundedCornerShape(24.dp))
                        .clickable(onClick = onContinue)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Continue",
                        style = BodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "➔",
                        style = BodyMedium.copy(
                            color = Color(0xFF38BDF8),
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}
