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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.t1.domain.model.UserProfile
import com.example.t1.ui.components.BottomNav
import com.example.t1.ui.components.FocusTimer
import com.example.t1.ui.components.T1Tab
import com.example.t1.ui.main.HomeScreen
import com.example.t1.ui.main.LeaderboardScreen
import com.example.t1.ui.main.ProfileScreen
import com.example.t1.ui.main.SettingsScreen
import com.example.t1.ui.onboarding.AuthErrorScreen
import com.example.t1.ui.onboarding.AuthScreen
import com.example.t1.ui.onboarding.OnboardingPlaceholderScreen
import com.example.t1.ui.onboarding.SplashLoadingScreen
import com.example.t1.ui.viewmodel.AuthState
import com.example.t1.ui.viewmodel.AuthViewModel
import com.example.t1.ui.viewmodel.MainViewModel

/**
 * Main application navigation component.
 * Maps deterministic AuthState transitions directly to visual screens.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = authState,
        transitionSpec = {
            fadeIn(animationSpec = tween(250)).togetherWith(fadeOut(animationSpec = tween(250)))
        },
        label = "auth_state_routing",
        modifier = Modifier.fillMaxSize()
    ) { state ->
        when (state) {
            is AuthState.CheckingSession -> {
                SplashLoadingScreen(message = "Restoring active session...")
            }
            is AuthState.Unauthenticated -> {
                AuthScreen(viewModel = authViewModel)
            }
            is AuthState.Authenticating -> {
                SplashLoadingScreen(message = "Connecting to Google...")
            }
            is AuthState.Authenticated -> {
                SplashLoadingScreen(message = "Authenticating session with Supabase...")
            }
            is AuthState.LoadingProfile -> {
                SplashLoadingScreen(message = "Synchronizing user profile...")
            }
            is AuthState.SigningOut -> {
                SplashLoadingScreen(message = "Signing out safely...")
            }
            is AuthState.Error -> {
                AuthErrorScreen(
                    errorType = state.errorType,
                    message = state.message,
                    onRetry = { authViewModel.restoreSession() },
                    onSignOut = { authViewModel.signOut() }
                )
            }
            is AuthState.NavigateToOnboarding -> {
                // In Phase 1, we show a clean onboarding entry placeholder allowing user to sign out and return
                OnboardingPlaceholderScreen(
                    onSignOut = { authViewModel.signOut() }
                )
            }
            is AuthState.Dashboard -> {
                DashboardShell(
                    profile = state.profile,
                    mainViewModel = mainViewModel,
                    onSignOut = { authViewModel.signOut() }
                )
            }
        }
    }
}

/**
 * Container rendering the main dashboard with BottomNav, HomeScreen, Leaderboard, and Settings views.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardShell(
    profile: UserProfile,
    mainViewModel: MainViewModel,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(T1Tab.HOME) }
    var showSettings by remember { mutableStateOf(false) }
    var showTimer by remember { mutableStateOf(false) }

    val nameToShow = profile.displayName ?: profile.username
    val cachedScore by mainViewModel.cachedFocusScore.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        if (showSettings) {
            SettingsScreen(
                onBack = { showSettings = false }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)).togetherWith(fadeOut(animationSpec = tween(200)))
                    },
                    label = "dashboard_tab_transition",
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
                                username = profile.username
                            )
                        }
                        T1Tab.USER -> {
                            ProfileScreen(
                                username = nameToShow,
                                onBack = null,
                                onSettings = { showSettings = true },
                                onUpdateName = { newName ->
                                    mainViewModel.updateDisplayName(newName)
                                },
                                onSignOut = onSignOut
                            )
                        }
                    }
                }

                // Fixed bottom navigation bar
                BottomNav(
                    active = activeTab,
                    onNavigate = { newTab -> activeTab = newTab },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // Fullscreen Focus Timer Overlay
        AnimatedVisibility(
            visible = showTimer,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            FocusTimer(
                onClose = {
                    showTimer = false
                    mainViewModel.logFocusSession(1500L)
                }
            )
        }
    }
}
