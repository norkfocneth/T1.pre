package com.example.t1.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.t1.theme.*
import kotlin.math.min

enum class OnboardingStep {
    AUTH, QUESTIONS, PERMISSIONS, ANALYSIS, RESULT, USERNAME
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingFlow(
    onComplete: (String, String?, Int) -> Unit,
    modifier: Modifier = Modifier,
    initialStep: OnboardingStep = OnboardingStep.AUTH
) {
    var step by remember { mutableStateOf(initialStep) }
    var focusScore by remember { mutableIntStateOf(68) }

    LaunchedEffect(initialStep) {
        step = initialStep
    }

    val handleQuestionsComplete: (List<Int>) -> Unit = { answers ->
        val base = 55
        val seriousness = answers.getOrNull(5) ?: 0
        val bonus = seriousness * 8 + (Math.random() * 15).toInt()
        focusScore = min(base + bonus, 92)
        step = OnboardingStep.PERMISSIONS
    }

    AnimatedContent(
        targetState = step,
        transitionSpec = {
            // Replicate framer-motion transition: initial {opacity: 0, x: 30, scale: 0.98} -> exit {opacity: 0, x: -30}
            (fadeIn(animationSpec = tween(400)) + androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { 30 },
                animationSpec = tween(400)
            )).togetherWith(
                fadeOut(animationSpec = tween(400)) + androidx.compose.animation.slideOutHorizontally(
                    targetOffsetX = { -30 },
                    animationSpec = tween(400)
                )
            )
        },
        label = "onboarding_step_transitions",
        modifier = modifier.fillMaxSize()
    ) { currentStep ->
        when (currentStep) {
            OnboardingStep.AUTH -> {
                AuthScreen(
                    onContinue = { step = OnboardingStep.QUESTIONS }
                )
            }
            OnboardingStep.QUESTIONS -> {
                QuestionScreen(
                    onComplete = handleQuestionsComplete
                )
            }
            OnboardingStep.PERMISSIONS -> {
                PermissionScreen(
                    onContinue = { step = OnboardingStep.ANALYSIS }
                )
            }
            OnboardingStep.ANALYSIS -> {
                AnalysisScreen(
                    onComplete = { step = OnboardingStep.RESULT }
                )
            }
            OnboardingStep.RESULT -> {
                ResultScreen(
                    score = focusScore,
                    onContinue = { step = OnboardingStep.USERNAME }
                )
            }
            OnboardingStep.USERNAME -> {
                UsernameScreen(
                    onComplete = { username, displayName -> onComplete(username, displayName, focusScore) }
                )
            }
        }
    }
}
