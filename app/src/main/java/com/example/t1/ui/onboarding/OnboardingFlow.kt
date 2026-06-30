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
        var points = 0
        
        // Q1: Be honest — what eats most of your time?
        points += when (answers.getOrNull(0)) {
            1 -> 3 // Studying / productive work
            else -> 0
        }
        
        // Q2: How many hours are you actually on your phone?
        points += when (answers.getOrNull(1)) {
            0 -> 3 // 2–3 hours
            1 -> 2 // 3–5 hours
            2 -> 1 // 5–7 hours
            else -> 0 // 7+ hours
        }
        
        // Q3: focus completely collapse (neutral)
        points += 1
        
        // Q4: deep down wasting time
        points += when (answers.getOrNull(3)) {
            0 -> 2 // Not really
            1 -> 1 // Sometimes
            else -> 0 // Yes, often
        }
        
        // Q5: want to fix (neutral)
        points += 1
        
        // Q6: how serious
        points += when (answers.getOrNull(5)) {
            2 -> 4 // Fully committed
            1 -> 2 // Somewhat serious
            else -> 0 // Just exploring
        }
        
        // Map 2..14 points to 40..74 range
        val minPoints = 2
        val maxPoints = 14
        val minScore = 40
        val maxScore = 74
        val calculated = minScore + ((points - minPoints).toFloat() / (maxPoints - minPoints).toFloat() * (maxScore - minScore).toFloat()).toInt()
        
        focusScore = calculated.coerceIn(40, 74)
        step = OnboardingStep.ANALYSIS
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
                androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize())
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
