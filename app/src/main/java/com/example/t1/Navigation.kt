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
import com.example.t1.ui.onboarding.OnboardingFlow
import com.example.t1.ui.onboarding.OnboardingStep
import com.example.t1.ui.viewmodel.AuthState
import com.example.t1.ui.viewmodel.AuthViewModel
import com.example.t1.ui.viewmodel.MainViewModel
import com.example.t1.ui.viewmodel.OnboardingViewModel
import com.example.t1.ui.viewmodel.OnboardingUiState
import androidx.compose.runtime.LaunchedEffect

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
                val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                val onboardingState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
                val userId = state.userId

                LaunchedEffect(onboardingState) {
                    if (onboardingState is OnboardingUiState.Success) {
                        com.example.t1.util.T1Logger.i("Onboarding completed successfully. Restoring session.")
                        authViewModel.restoreSession()
                        onboardingViewModel.resetState()
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    OnboardingFlow(
                        initialStep = OnboardingStep.QUESTIONS,
                        onComplete = { username, displayName, focusScore ->
                            com.example.t1.util.T1Logger.i("Onboarding quiz finished. Saving profile.")
                            onboardingViewModel.completeOnboarding(
                                userId = userId,
                                username = username,
                                displayName = displayName,
                                focusScore = focusScore
                            )
                        }
                    )

                    when (val oState = onboardingState) {
                        is OnboardingUiState.Loading -> {
                            SplashLoadingScreen(message = "Saving your profile...")
                        }
                        is OnboardingUiState.Error -> {
                            AuthErrorScreen(
                                errorType = com.example.t1.domain.model.AuthError.SUPABASE_ERROR,
                                message = oState.message,
                                onRetry = { onboardingViewModel.resetState() },
                                onSignOut = { authViewModel.signOut() }
                            )
                        }
                        else -> {}
                    }
                }
            }
            is AuthState.Dashboard -> {
                val dashboardState by mainViewModel.dashboardState.collectAsStateWithLifecycle()
                var dismissedPermission by remember { mutableStateOf(false) }

                if (dashboardState.permissionState is com.example.t1.domain.permission.UsagePermissionState.Denied && !dismissedPermission) {
                    com.example.t1.ui.permission.UsagePermissionScreen(
                        onOpenSettings = { mainViewModel.openPermissionSettings() },
                        onRetry = { mainViewModel.refreshBehaviourData() },
                        onLater = { dismissedPermission = true }
                    )
                } else {
                    DashboardShell(
                        profile = state.profile,
                        mainViewModel = mainViewModel,
                        onSignOut = { authViewModel.signOut() }
                    )
                }
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
    val context = androidx.compose.ui.platform.LocalContext.current
    var activeTab by remember { mutableStateOf(T1Tab.HOME) }
    var showSettings by remember { mutableStateOf(false) }
    var showTimer by remember { mutableStateOf(false) }

    val nameToShow = profile.displayName ?: profile.username
    val cachedScore by mainViewModel.cachedFocusScore.collectAsStateWithLifecycle()
    val dashboardState by mainViewModel.dashboardState.collectAsStateWithLifecycle()
    val totalFocusSessions by mainViewModel.totalFocusSessions.collectAsStateWithLifecycle()
    val totalFocusDuration by mainViewModel.totalFocusDuration.collectAsStateWithLifecycle()
    val weeklyTrend by mainViewModel.weeklyTrend.collectAsStateWithLifecycle()

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
                                dashboardState = dashboardState,
                                onOpenSettings = { mainViewModel.openPermissionSettings() },
                                focusScore = dashboardState.currentFocusScore,
                                streak = profile.streak
                            )
                        }
                        T1Tab.RANK -> {
                            LeaderboardScreen(
                                username = profile.username
                            )
                        }
                        T1Tab.USER -> {
                            ProfileScreen(
                                displayName = profile.displayName,
                                username = profile.username,
                                totalFocusSessions = totalFocusSessions,
                                totalFocusDuration = totalFocusDuration,
                                weeklyTrend = weeklyTrend,
                                onBack = null,
                                onSettings = { showSettings = true },
                                onUpdateName = { newName ->
                                    mainViewModel.updateDisplayName(newName) { success ->
                                        if (!success) {
                                            android.widget.Toast.makeText(
                                                context,
                                                "Failed to sync name changes. Please check connection.",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
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
                onClose = { elapsedSeconds ->
                    showTimer = false
                    mainViewModel.logFocusSession(elapsedSeconds)
                }
            )
        }
    }
}
