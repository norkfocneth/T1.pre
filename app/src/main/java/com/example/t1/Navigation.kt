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
import com.example.t1.ui.components.BottomNav
import com.example.t1.ui.components.FocusTimer
import com.example.t1.ui.components.T1Tab
import com.example.t1.ui.main.HomeScreen
import com.example.t1.ui.main.LeaderboardScreen
import com.example.t1.ui.main.ProfileScreen
import com.example.t1.ui.main.SettingsScreen
import com.example.t1.ui.onboarding.OnboardingFlow

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavigation() {
    var showOnboarding by remember { mutableStateOf(true) }
    var activeTab by remember { mutableStateOf(T1Tab.HOME) }
    var showSettings by remember { mutableStateOf(false) }
    var showTimer by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("You") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showOnboarding) {
            OnboardingFlow(
                onComplete = { name ->
                    username = name
                    showOnboarding = false
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
                                username = username,
                                onOpenTimer = { showTimer = true },
                                onOpenProfile = { activeTab = T1Tab.USER }
                            )
                        }
                        T1Tab.RANK -> {
                            LeaderboardScreen(
                                username = username
                            )
                        }
                        T1Tab.USER -> {
                            ProfileScreen(
                                username = username,
                                onBack = null, // BottomNav manages back behavior
                                onSettings = { showSettings = true },
                                onSignOut = {
                                    // Reset to onboarding on signout
                                    showOnboarding = true
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
                onClose = { showTimer = false }
            )
        }
    }
}
