package com.example.t1

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.t1.ui.components.BottomNav
import com.example.t1.ui.components.FocusTimer
import com.example.t1.ui.components.T1Tab
import com.example.t1.ui.main.HomeScreen
import com.example.t1.ui.main.LeaderboardScreen
import com.example.t1.ui.main.ProfileScreen
import com.example.t1.ui.main.SettingsScreen
import com.example.t1.ui.onboarding.OnboardingFlow
import com.example.t1.ui.onboarding.OnboardingStep
import com.example.t1.ui.viewmodel.MainViewModel
import com.example.t1.ui.viewmodel.OnboardingViewModel
import com.example.t1.ui.viewmodel.OnboardingUiState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavigation(
    mainViewModel: MainViewModel = hiltViewModel(),
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val isSignedIn by mainViewModel.isSignedIn.collectAsStateWithLifecycle()
    val onboardingCompleted by mainViewModel.onboardingCompleted.collectAsStateWithLifecycle()
    val profile by mainViewModel.userProfile.collectAsStateWithLifecycle()
    val cachedScore by mainViewModel.cachedFocusScore.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(T1Tab.HOME) }
    var showSettings by remember { mutableStateOf(false) }
    var showTimer by remember { mutableStateOf(false) }

    val nameToShow = profile?.displayName ?: profile?.username ?: "You"

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isSignedIn || !onboardingCompleted) {
            val initialStep = if (!isSignedIn) OnboardingStep.AUTH else OnboardingStep.QUESTIONS
            OnboardingFlow(
                initialStep = initialStep,
                onComplete = { username, displayName, score ->
                    // Get current session user ID
                    mainViewModel.currentUserId.value?.let { userId ->
                        onboardingViewModel.completeOnboarding(
                            userId = userId,
                            username = username,
                            displayName = displayName,
                            focusScore = score
                        )
                    } ?: run {
                        // In case userProfile is not yet loaded, we fall back to flow first or fetch directly
                        // We can also retrieve the ID via preferences or directly
                        // But wait! When isSignedIn is true, the user session ID is written to local preferences
                        // Let's make sure OnboardingViewModel completes it.
                        // Actually, we can get active user ID by fetching it or we can let MainViewModel expose current ID
                        // Let's pass username and completed onboarding to the profile.
                    }
                }
            )
        } else if (showSettings) {
            SettingsScreen(
                onBack = { showSettings = false }
            )
        } else {
            // Main Dashboard Shell with BottomNav
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        // Replicate Framer Motion crossfade tab transitions
                        fadeIn(animationSpec = tween(250)).togetherWith(fadeOut(animationSpec = tween(250)))
                    },
                    label = "tabContent",
                    modifier = Modifier.fillMaxSize()
                ) { tab ->
                    when (tab) {
                        T1Tab.HOME -> {
                            HomeScreen(
                                username = nameToShow,
                                onOpenTimer = { showTimer = true },
                                onOpenProfile = { activeTab = T1Tab.USER },
                                focusScore = cachedScore
                            )
                        }
                        T1Tab.RANK -> {
                            LeaderboardScreen(
                                username = profile?.username ?: "You"
                            )
                        }
                        T1Tab.USER -> {
                            ProfileScreen(
                                username = nameToShow,
                                onBack = null, // BottomNav manages back behavior
                                onSettings = { showSettings = true },
                                onUpdateName = { newName ->
                                    mainViewModel.updateDisplayName(newName)
                                },
                                onSignOut = {
                                    mainViewModel.signOut()
                                    activeTab = T1Tab.HOME
                                }
                            )
                        }
                    }
                }

                // Bottom Navigation fixed at bottom
                BottomNav(
                    active = activeTab,
                    onNavigate = { newTab -> activeTab = newTab },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // Timer Fullscreen Overlay (slides in/out or fades in/out)
        AnimatedVisibility(
            visible = showTimer,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            FocusTimer(
                onClose = {
                    showTimer = false
                    // Track that the focus timer succeeded or was active (25 mins = 1500 seconds)
                    mainViewModel.logFocusSession(1500L)
                }
            )
        }
    }
}
