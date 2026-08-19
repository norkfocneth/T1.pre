package com.example.t1.ui.tour

import com.example.t1.ui.components.T1Tab

enum class HighlightTarget {
    FULL_SCREEN_WELCOME,
    FOCUS_RING,
    GOAL_BADGE,
    SCREEN_TIME_CARD,
    APP_CATEGORIES,
    LEADERBOARD_PODIUM,
    TOKENS_WALLET,
    PROFILE_IDENTITY,
    SETTINGS_PRIVACY,
    SETTINGS_DELETE
}

data class TourStep(
    val id: Int,
    val totalSteps: Int = 9,
    val stepLabel: String,
    val title: String,
    val description: String,
    val highlightTarget: HighlightTarget,
    val primaryButtonText: String = "Next →",
    val secondaryActionText: String? = null,
    val targetTab: T1Tab = T1Tab.HOME,
    val requireSettings: Boolean = false
) {
    val isWelcome: Boolean get() = id == 0
    val isFinal: Boolean get() = id == totalSteps
}

object TourStepCatalog {

    fun getSteps(userGoal: String = "Social Control"): List<TourStep> {
        val cleanGoal = when {
            userGoal.contains("Prod", ignoreCase = true) -> "Productivity"
            userGoal.contains("Edu", ignoreCase = true) -> "Education"
            userGoal.contains("Ent", ignoreCase = true) -> "Entertainment"
            userGoal.contains("Comm", ignoreCase = true) -> "Communication"
            else -> "Social Control"
        }

        return listOf(
            // Step 0: Welcome
            TourStep(
                id = 0,
                stepLabel = "WELCOME TO T1",
                title = "Welcome to T1",
                description = "Your digital behaviour. Your progress. Your Focus Score.\n\nTake a quick interactive tour to master your focus system.",
                highlightTarget = HighlightTarget.FULL_SCREEN_WELCOME,
                primaryButtonText = "Let's explore T1 →",
                targetTab = T1Tab.HOME
            ),
            // Step 1: Focus Score
            TourStep(
                id = 1,
                stepLabel = "STEP 1 OF 9 • FOCUS SCORE",
                title = "Your Focus Score",
                description = "T1 analyses your real digital behaviour to track your progress. Your score isn't based on what you say — it's calculated from what you actually do.",
                highlightTarget = HighlightTarget.FOCUS_RING,
                primaryButtonText = "Next →",
                targetTab = T1Tab.HOME
            ),
            // Step 2: Goal
            TourStep(
                id = 2,
                stepLabel = "STEP 2 OF 9 • FOCUS GOAL",
                title = "Your Focus Goal",
                description = "You chose $cleanGoal. T1 will track your daily patterns, celebrate improvements, and keep you disciplined in this exact focus area.",
                highlightTarget = HighlightTarget.GOAL_BADGE,
                primaryButtonText = "Next →",
                targetTab = T1Tab.HOME
            ),
            // Step 3: Screen Time
            TourStep(
                id = 3,
                stepLabel = "STEP 3 OF 9 • SCREEN TIME",
                title = "Understand your time",
                description = "T1 breaks your screen time into clear category distributions so you can instantly see where your hours actually go without manual logging.",
                highlightTarget = HighlightTarget.SCREEN_TIME_CARD,
                primaryButtonText = "Next →",
                targetTab = T1Tab.HOME
            ),
            // Step 4: App Categories & Dual Purpose
            TourStep(
                id = 4,
                stepLabel = "STEP 4 OF 9 • DUAL-PURPOSE APPS",
                title = "You decide what an app means to you",
                description = "YouTube can be Education for you and Entertainment for someone else. You have complete control to customize how dual-purpose apps count toward your score.",
                highlightTarget = HighlightTarget.APP_CATEGORIES,
                primaryButtonText = "Next →",
                targetTab = T1Tab.HOME
            ),
            // Step 5: Leaderboard
            TourStep(
                id = 5,
                stepLabel = "STEP 5 OF 9 • LEADERBOARD",
                title = "Compete without guessing",
                description = "Your genuine Focus Score determines your position on Global, College, and Friends leaderboards. No fake streaks — only real digital discipline.",
                highlightTarget = HighlightTarget.LEADERBOARD_PODIUM,
                primaryButtonText = "Next →",
                targetTab = T1Tab.RANK
            ),
            // Step 6: Tokens
            TourStep(
                id = 6,
                stepLabel = "STEP 6 OF 9 • T1 TOKENS",
                title = "Earn T1 Tokens",
                description = "Complete focus sessions and maintain consistency to earn tokens. Redeem them for exclusive app upgrades and future merchandise.",
                highlightTarget = HighlightTarget.TOKENS_WALLET,
                primaryButtonText = "Next →",
                targetTab = T1Tab.HOME
            ),
            // Step 7: Profile
            TourStep(
                id = 7,
                stepLabel = "STEP 7 OF 9 • YOUR PROFILE",
                title = "Your T1 Identity",
                description = "Track your lifetime focus hours, active streaks, unlocked performance badges, and personal evolution in one unified identity hub.",
                highlightTarget = HighlightTarget.PROFILE_IDENTITY,
                primaryButtonText = "Next →",
                targetTab = T1Tab.USER
            ),
            // Step 8: Privacy
            TourStep(
                id = 8,
                stepLabel = "STEP 8 OF 9 • PRIVACY & CONTROL",
                title = "You're in control",
                description = "Your usage data stays securely on your device. Manage all permissions, notifications, and telemetry preferences directly from Settings.",
                highlightTarget = HighlightTarget.SETTINGS_PRIVACY,
                primaryButtonText = "Next →",
                targetTab = T1Tab.USER,
                requireSettings = true
            ),
            // Step 9: Delete Account
            TourStep(
                id = 9,
                stepLabel = "STEP 9 OF 9 • DATA OWNERSHIP",
                title = "Your data is strictly yours",
                description = "Zero lock-in. You have the absolute right to permanently delete your T1 account and all associated cloud data at any time with a single tap.",
                highlightTarget = HighlightTarget.SETTINGS_DELETE,
                primaryButtonText = "Explore T1 ✨",
                targetTab = T1Tab.USER,
                requireSettings = true
            )
        )
    }
}
