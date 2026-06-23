package com.example.t1.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t1.theme.*
import com.example.t1.util.Haptics
import kotlinx.coroutines.delay

data class Question(
    val title: String,
    val subtitle: String?,
    val options: List<String>
)

val questions = listOf(
    Question(
        title = "Be honest — what eats most of your time?",
        subtitle = "Be honest. This affects your score.",
        options = listOf("Social media (Instagram, reels)", "Studying / productive work", "Gaming", "Chatting")
    ),
    Question(
        title = "How many hours are you actually on your phone?",
        subtitle = "We're building your profile...",
        options = listOf("2–3 hours", "3–5 hours", "5–7 hours", "7+ hours")
    ),
    Question(
        title = "When does your focus completely collapse?",
        subtitle = "Almost there...",
        options = listOf("Morning", "Afternoon", "Evening", "Late night")
    ),
    Question(
        title = "Deep down, you know you're wasting time. How much?",
        subtitle = "Be honest. This affects your score.",
        options = listOf("Not really", "Sometimes", "Yes, often")
    ),
    Question(
        title = "What do you actually want to fix?",
        subtitle = "We're building your profile...",
        options = listOf("Focus", "Reduce screen time", "Better study consistency", "Build discipline")
    ),
    Question(
        title = "How serious are you — really?",
        subtitle = "Almost there...",
        options = listOf("Just exploring", "Somewhat serious", "Fully committed")
    )
)

@Composable
fun QuestionScreen(
    onComplete: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    var currentQ by remember { mutableStateOf(0) }
    val answers = remember { mutableListOf<Int>() }

    val q = questions[currentQ]
    val progress = ((currentQ + 1).toFloat() / questions.size)

    BackHandler(enabled = currentQ > 0) {
        currentQ--
        if (answers.isNotEmpty()) {
            answers.removeAt(answers.size - 1)
        }
    }

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            // Header: Step progress text
            Text(
                text = "Step ${currentQ + 1} of ${questions.size}",
                style = BodySmall.copy(
                    fontSize = 10.sp,
                    color = MutedForeground,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Progress bar with glow shadow
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(500),
                label = "progress"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Secondary)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Foreground)
                        .drawBehind {
                            // Draw foreground glow shadow
                            drawRect(
                                color = Color.White.copy(alpha = 0.3f),
                                size = size
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Slide Transitions between questions using AnimatedContent
            AnimatedContent(
                targetState = currentQ,
                transitionSpec = {
                    // Enter from right (+300), exit to left (-300)
                    (fadeIn(animationSpec = tween(350)) + androidx.compose.animation.slideInHorizontally(
                        initialOffsetX = { 300 },
                        animationSpec = tween(350)
                    )).togetherWith(
                        fadeOut(animationSpec = tween(350)) + androidx.compose.animation.slideOutHorizontally(
                            targetOffsetX = { -300 },
                            animationSpec = tween(350)
                        )
                    )
                },
                label = "question_slider",
                modifier = Modifier.weight(1f)
            ) { targetQIndex ->
                val targetQ = questions[targetQIndex]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Question Title with shimmer/gradient style
                    Text(
                        text = targetQ.title,
                        style = HeadlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            brush = Brush.linearGradient(
                                colors = listOf(GradientTextStart, GradientTextEnd)
                            )
                        ),
                        lineHeight = 36.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Subtitle
                    if (targetQ.subtitle != null) {
                        Text(
                            text = targetQ.subtitle,
                            style = BodyMedium.copy(
                                color = MutedForeground,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Options list
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        targetQ.options.forEachIndexed { idx, option ->
                            androidx.compose.runtime.key(targetQIndex, idx) {
                                // Staggered visibility fade-in
                                var itemVisible by remember { mutableStateOf(false) }
                                LaunchedEffect(targetQIndex) {
                                    itemVisible = false
                                    delay(100 + idx * 80L)
                                    itemVisible = true
                                }

                                AnimatedVisibility(
                                    visible = itemVisible,
                                    enter = fadeIn(animationSpec = tween(350)) + slideInVertically(
                                        initialOffsetY = { 20 },
                                        animationSpec = tween(350)
                                    ),
                                    exit = fadeOut()
                                ) {
                                    OptionButton(
                                        text = option,
                                        onClick = {
                                            Haptics.playLight(view)
                                            answers.add(idx)

                                            if (currentQ < questions.size - 1) {
                                                currentQ++
                                            } else {
                                                Haptics.playSuccess(context)
                                                // Trigger onComplete with answers
                                                onComplete(answers.toList())
                                            }
                                        }
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

@Composable
fun OptionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.97f else 1f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .background(Secondary.copy(alpha = 0.5f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        Text(
            text = text,
            style = BodyMedium.copy(
                color = Foreground,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
