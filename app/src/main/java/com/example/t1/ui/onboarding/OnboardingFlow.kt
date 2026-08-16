package com.example.t1.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

enum class OnboardingStep {
    AUTH, QUESTIONS, GOAL_SELECTION, APP_CLASSIFICATION, PERMISSIONS, ANALYSIS, RESULT, USERNAME
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingFlow(
    onComplete: (username: String, displayName: String?, focusScore: Int, primaryGoal: String, overrides: Map<String, String>) -> Unit,
    modifier: Modifier = Modifier,
    initialStep: OnboardingStep = OnboardingStep.AUTH,
    viewModel: com.example.t1.ui.viewmodel.OnboardingViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    var step by remember { mutableStateOf(initialStep) }
    var focusScore by remember { mutableIntStateOf(50) }
    var avgScreenTime by remember { mutableStateOf("4.2h") }
    var topCategory by remember { mutableStateOf("Social") }
    var selectedGoal by remember { mutableStateOf("PRODUCTIVITY") }
    var customOverrides by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(initialStep) {
        step = initialStep
    }

    val handleQuestionsComplete: (List<Int>) -> Unit = { _ ->
        // Question responses provide context but do NOT alter the behavior-based focus score
        step = OnboardingStep.GOAL_SELECTION
    }

    AnimatedContent(
        targetState = step,
        transitionSpec = {
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
            OnboardingStep.GOAL_SELECTION -> {
                GoalSelectionScreen(
                    onGoalSelected = { goal ->
                        selectedGoal = goal
                        step = OnboardingStep.APP_CLASSIFICATION
                    }
                )
            }
            OnboardingStep.APP_CLASSIFICATION -> {
                AppClassificationScreen(
                    onClassificationComplete = { overrides ->
                        customOverrides = overrides
                        step = OnboardingStep.PERMISSIONS
                    }
                )
            }
            OnboardingStep.PERMISSIONS -> {
                PermissionScreen(
                    onContinue = { step = OnboardingStep.ANALYSIS }
                )
            }
            OnboardingStep.ANALYSIS -> {
                AnalysisScreen(
                    onAnalyze = {
                        viewModel.analyze7DaysUsage(
                            primaryGoal = selectedGoal,
                            customOverrides = customOverrides
                        )
                    },
                    onComplete = { calculatedScore, avgTime, topCat ->
                        focusScore = calculatedScore
                        avgScreenTime = avgTime
                        topCategory = topCat
                        step = OnboardingStep.RESULT
                    }
                )
            }
            OnboardingStep.RESULT -> {
                ResultScreen(
                    score = focusScore,
                    avgDailyScreenTime = avgScreenTime,
                    topCategory = topCategory,
                    onContinue = { step = OnboardingStep.USERNAME }
                )
            }
            OnboardingStep.USERNAME -> {
                UsernameScreen(
                    onComplete = { username, displayName ->
                        onComplete(username, displayName, focusScore, selectedGoal, customOverrides)
                    }
                )
            }
        }
    }
}
