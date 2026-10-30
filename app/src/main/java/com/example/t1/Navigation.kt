package com.example.t1

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.t1.ui.splash.CinematicSplashScreen
import com.example.t1.ui.viewmodel.AuthState
import com.example.t1.ui.viewmodel.AuthViewModel
import com.example.t1.ui.viewmodel.MainViewModel
import com.example.t1.ui.viewmodel.OnboardingViewModel
import com.example.t1.ui.viewmodel.OnboardingUiState
import androidx.compose.runtime.LaunchedEffect
import com.example.t1.ui.tour.GuidedProductTourOverlay
import kotlinx.coroutines.delay

object SplashStateManager {
    var hasCompletedSplash: Boolean = false
}

/**
 * Main application navigation component.
 * Maps deterministic AuthState transitions directly to visual screens,
 * with an ultra-premium Netflix-style intro splash overlay that plays strictly once.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    previewScreen: String? = null,
    autoAcceptFightId: String? = null
) {
    when (previewScreen) {
        "questions" -> {
            com.example.t1.ui.onboarding.QuestionScreen(onComplete = {})
            return
        }
        "goals" -> {
            com.example.t1.ui.onboarding.GoalSelectionScreen(onGoalSelected = {})
            return
        }
        "apps" -> {
            com.example.t1.ui.onboarding.AppClassificationScreen(onClassificationComplete = {})
            return
        }
        "permission" -> {
            com.example.t1.ui.onboarding.PermissionScreen(onContinue = {})
            return
        }
        "username" -> {
            com.example.t1.ui.onboarding.UsernameScreen(onComplete = { _, _ -> })
            return
        }
        "manifesto" -> {
            com.example.t1.ui.onboarding.T1ManifestoScreen(onDismiss = {})
            return
        }
        "battle" -> {
            val battleViewModel: com.example.t1.ui.battle.FocusBattleViewModel = hiltViewModel()
            com.example.t1.ui.battle.FocusBattleScreen(viewModel = battleViewModel, onBackToHome = {})
            return
        }
    }

    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    var isCinematicSplashDone by rememberSaveable { mutableStateOf(SplashStateManager.hasCompletedSplash) }

    Box(modifier = Modifier.fillMaxSize()) {
        // ─── 1. Main Application Content (Deterministic Auth State Routing) ───
        AnimatedContent(
            targetState = authState,
            transitionSpec = {
                fadeIn(animationSpec = tween(350)).togetherWith(fadeOut(animationSpec = tween(250)))
            },
            label = "auth_state_routing",
            modifier = Modifier.fillMaxSize()
        ) { state ->
            when (state) {
                is AuthState.CheckingSession -> {
                    // OLED black placeholder while checking session in the background
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black))
                }
                is AuthState.Unauthenticated -> {
                    AuthScreen(viewModel = authViewModel)
                }
                is AuthState.Authenticating,
                is AuthState.Authenticated,
                is AuthState.LoadingProfile,
                is AuthState.SigningOut -> {
                    val message = when (state) {
                        is AuthState.Authenticating -> "Connecting to Google..."
                        is AuthState.Authenticated -> "Authenticating session with Supabase..."
                        is AuthState.LoadingProfile -> "Synchronizing user profile..."
                        is AuthState.SigningOut -> "Signing out safely..."
                        else -> "Please wait..."
                    }
                    SplashLoadingScreen(message = message)
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
                            onComplete = { username, displayName, focusScore, primaryGoal, overrides ->
                                com.example.t1.util.T1Logger.i("Onboarding finished. Saving profile.")
                                onboardingViewModel.completeOnboarding(
                                    userId = userId,
                                    username = username,
                                    displayName = displayName,
                                    focusScore = focusScore,
                                    primaryGoal = primaryGoal,
                                    customOverrides = overrides
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
                            onSignOut = { authViewModel.signOut() },
                            initialTab = if (previewScreen == "focus_fight" || previewScreen == "battle" || autoAcceptFightId != null) T1Tab.BATTLE else T1Tab.HOME,
                            autoAcceptFightId = autoAcceptFightId
                        )
                    }
                }
            }
        }

        // ─── 2. Netflix-Style Cinematic Splash Overlay (Guaranteed strictly ONCE) ───
        AnimatedVisibility(
            visible = !isCinematicSplashDone,
            enter = fadeIn(tween(100)),
            exit = fadeOut(animationSpec = tween(450, easing = LinearEasing)),
            modifier = Modifier.fillMaxSize()
        ) {
            CinematicSplashScreen(
                onAnimationComplete = {
                    SplashStateManager.hasCompletedSplash = true
                    isCinematicSplashDone = true
                }
            )
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
    modifier: Modifier = Modifier,
    initialTab: T1Tab = T1Tab.HOME,
    autoAcceptFightId: String? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var activeTab by remember { mutableStateOf(initialTab) }
    LaunchedEffect(initialTab) { activeTab = initialTab }
    var showSettings by remember { mutableStateOf(false) }
    var showTimer by remember { mutableStateOf(false) }

    val battleViewModel: com.example.t1.ui.battle.FocusBattleViewModel = hiltViewModel()
    LaunchedEffect(autoAcceptFightId) {
        if (!autoAcceptFightId.isNullOrBlank()) {
            activeTab = T1Tab.BATTLE
            battleViewModel.autoAcceptFight(autoAcceptFightId)
        }
    }
    val battleState by battleViewModel.uiState.collectAsStateWithLifecycle()
    val isBattleImmersive = activeTab == T1Tab.BATTLE && battleState.currentStep != com.example.t1.domain.model.battle.BattleStep.DISCOVERY
    val shouldShowBottomNav = !showSettings && !isBattleImmersive

    val liveProfile by mainViewModel.userProfile.collectAsStateWithLifecycle()
    val activeProfile = liveProfile ?: profile
    val nameToShow = activeProfile.displayName ?: activeProfile.username
    val cachedScore by mainViewModel.cachedFocusScore.collectAsStateWithLifecycle()
    val dashboardState by mainViewModel.dashboardState.collectAsStateWithLifecycle()
    val totalFocusSessions by mainViewModel.totalFocusSessions.collectAsStateWithLifecycle()
    val totalFocusDuration by mainViewModel.totalFocusDuration.collectAsStateWithLifecycle()
    val weeklyTrend by mainViewModel.weeklyTrend.collectAsStateWithLifecycle()

    val isTutorialCompleted by mainViewModel.tutorialCompleted.collectAsStateWithLifecycle()
    var isTourActive by remember { mutableStateOf(false) }

    LaunchedEffect(isTutorialCompleted) {
        if (!isTutorialCompleted) {
            delay(500)
            isTourActive = true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (showSettings) {
            SettingsScreen(
                onBack = { showSettings = false },
                onSignOut = {
                    showSettings = false
                    onSignOut()
                },
                onDeleteAccount = { onSuccess, onFailure ->
                    mainViewModel.deleteUserAccount(
                        onSuccess = {
                            showSettings = false
                            onSignOut()
                            onSuccess()
                        },
                        onFailure = onFailure
                    )
                },
                onReplayTour = {
                    showSettings = false
                    activeTab = T1Tab.HOME
                    mainViewModel.resetTutorial()
                    isTourActive = true
                }
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
                                streak = activeProfile.streak,
                                totalFocusSessions = totalFocusSessions,
                                viewModel = mainViewModel
                            )
                        }
                        T1Tab.RANK -> {
                            LeaderboardScreen(
                                username = activeProfile.username
                            )
                        }
                        T1Tab.BATTLE -> {
                            com.example.t1.ui.battle.FocusBattleScreen(
                                viewModel = battleViewModel,
                                onBackToHome = {
                                    battleViewModel.resetToDiscovery()
                                    activeTab = T1Tab.HOME
                                }
                            )
                        }
                        T1Tab.USER -> {
                            val focusRemindersEnabled by mainViewModel.focusRemindersEnabled.collectAsStateWithLifecycle()
                            val privacyGhostMode by mainViewModel.privacyGhostMode.collectAsStateWithLifecycle()
                            val goalLastChangedEpoch by mainViewModel.goalLastChangedEpoch.collectAsStateWithLifecycle()
                            ProfileScreen(
                                displayName = activeProfile.displayName,
                                username = activeProfile.username,
                                totalFocusSessions = totalFocusSessions,
                                totalFocusDuration = totalFocusDuration,
                                weeklyTrend = weeklyTrend,
                                streak = activeProfile.streak,
                                focusRemindersEnabled = focusRemindersEnabled,
                                onFocusRemindersChange = { mainViewModel.setFocusRemindersEnabled(it) },
                                privacyGhostMode = privacyGhostMode,
                                onPrivacyGhostModeChange = { mainViewModel.setPrivacyGhostMode(it) },
                                primaryGoal = activeProfile.primaryGoal ?: "PRODUCTIVITY",
                                goalLastChangedEpoch = goalLastChangedEpoch,
                                onGoalChange = { newGoal ->
                                    mainViewModel.updatePrimaryGoal(newGoal) { success, msg ->
                                        android.widget.Toast.makeText(
                                            context,
                                            msg,
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
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
                                onStartTour = {
                                    activeTab = T1Tab.HOME
                                    mainViewModel.resetTutorial()
                                    isTourActive = true
                                },
                                onSignOut = onSignOut
                            )
                        }
                    }
                }

                // Animated bottom navigation bar (automatically hides during immersive battle arena/result)
                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowBottomNav,
                    enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it }) + androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(250)),
                    exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { it }) + androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(200)),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    BottomNav(
                        active = activeTab,
                        onNavigate = { newTab -> activeTab = newTab }
                    )
                }
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

        // Interactive Guided Product Tour Overlay
        AnimatedVisibility(
            visible = isTourActive,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            GuidedProductTourOverlay(
                userGoal = profile.primaryGoal ?: "Social Control",
                onStepSync = { step ->
                    if (step.requireSettings) {
                        showSettings = true
                    } else {
                        if (showSettings) showSettings = false
                        activeTab = step.targetTab
                    }
                },
                onCompleteTour = {
                    isTourActive = false
                    showSettings = false
                    activeTab = T1Tab.HOME
                    mainViewModel.completeTutorial()
                },
                onSkipTour = {
                    isTourActive = false
                    showSettings = false
                    activeTab = T1Tab.HOME
                    mainViewModel.completeTutorial()
                }
            )
        }
    }
}
