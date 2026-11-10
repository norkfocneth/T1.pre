package com.example.t1.ui.tour

import com.example.t1.ui.components.T1Tab

data class SpotlightTargetSpec(
    val cxFraction: Float,
    val cyFraction: Float,
    val radiusDp: Float,
    val isRect: Boolean = false,
    val widthFraction: Float = 0.85f,
    val heightFraction: Float = 0.12f
)

data class TourArrowPath(
    val startXFraction: Float,
    val startYFraction: Float,
    val cp1XFraction: Float,
    val cp1YFraction: Float,
    val cp2XFraction: Float,
    val cp2YFraction: Float,
    val endXFraction: Float,
    val endYFraction: Float
)

data class TourStep(
    val id: Int,
    val totalSteps: Int = 15,
    val category: String,
    val targetTab: T1Tab,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val cardXFraction: Float,
    val cardYFraction: Float,
    val cardWidthFraction: Float = 0.88f,
    val spotlight: SpotlightTargetSpec? = null,
    val arrow: TourArrowPath? = null,
    val isBattleInteractiveDemo: Boolean = false,
    val isFinale: Boolean = false,
    val requireSettings: Boolean = false
) {
    val isFinal: Boolean get() = id == totalSteps - 1
    val isWelcome: Boolean get() = id == 0
}

object TourStepCatalog {

    fun getSteps(userGoal: String = "Productivity"): List<TourStep> {
        val total = 15
        return listOf(
            // ═════════════════════════════════════════════════════════════════
            // PHASE 1: HOME (Dashboard & Focus Score Engine) - Steps 1 to 4
            // ═════════════════════════════════════════════════════════════════

            // Step 1: Live Focus Score Ring
            TourStep(
                id = 0,
                totalSteps = total,
                category = "HOME",
                targetTab = T1Tab.HOME,
                title = "Live Focus Score (0 - 100)",
                subtitle = "Calculated live every minute based on your focus vs your rolling 7-day baseline. Outperforming your past self raises your score.",
                iconEmoji = "⚡",
                cardXFraction = 0.06f,
                cardYFraction = 0.44f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.28f,
                    radiusDp = 92f,
                    isRect = false
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.44f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.40f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.38f,
                    endXFraction = 0.50f,
                    endYFraction = 0.36f
                )
            ),

            // Step 2: Goal Delta Mirror
            TourStep(
                id = 1,
                totalSteps = total,
                category = "HOME",
                targetTab = T1Tab.HOME,
                title = "Goal Delta: You vs Yesterday",
                subtitle = "Tracks how many minutes you saved or stayed focused in your primary goal compared strictly to yesterday. Beating yesterday earns consistency XP.",
                iconEmoji = "🎯",
                cardXFraction = 0.06f,
                cardYFraction = 0.23f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.43f,
                    radiusDp = 50f,
                    isRect = true,
                    widthFraction = 0.88f,
                    heightFraction = 0.12f
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.32f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.34f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.35f,
                    endXFraction = 0.50f,
                    endYFraction = 0.37f
                )
            ),

            // Step 3: Screen Time & Category Calibrations
            TourStep(
                id = 2,
                totalSteps = total,
                category = "HOME",
                targetTab = T1Tab.HOME,
                title = "Screen Time & Active Limits",
                subtitle = "No need to switch between apps to check usage. See all your top categories, reclaimed hours, and active limits right here.",
                iconEmoji = "📱",
                cardXFraction = 0.06f,
                cardYFraction = 0.38f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.58f,
                    radiusDp = 50f,
                    isRect = true,
                    widthFraction = 0.88f,
                    heightFraction = 0.14f
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.47f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.49f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.50f,
                    endXFraction = 0.50f,
                    endYFraction = 0.51f
                )
            ),

            // Step 4: Quick Focus Session
            TourStep(
                id = 3,
                totalSteps = total,
                category = "HOME",
                targetTab = T1Tab.HOME,
                title = "Start Focus Session",
                subtitle = "Tap here anytime to enter a deep work session with app stasis, anti-distraction shield, and ambient sensory haptics.",
                iconEmoji = "⏱️",
                cardXFraction = 0.06f,
                cardYFraction = 0.55f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.72f,
                    radiusDp = 40f,
                    isRect = true,
                    widthFraction = 0.88f,
                    heightFraction = 0.09f
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.64f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.66f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.67f,
                    endXFraction = 0.50f,
                    endYFraction = 0.68f
                )
            ),

            // ═════════════════════════════════════════════════════════════════
            // PHASE 2: RANK (Leaderboard - Steps 5 to 7)
            // ═════════════════════════════════════════════════════════════════

            // Step 5: Top Performers Podium
            TourStep(
                id = 4,
                totalSteps = total,
                category = "RANK",
                targetTab = T1Tab.RANK,
                title = "Top Performers Podium",
                subtitle = "Meet the campus and global champions with the highest verified Focus Scores this week. Proof-of-work ensures every rank is legitimately earned.",
                iconEmoji = "🏆",
                cardXFraction = 0.06f,
                cardYFraction = 0.44f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.28f,
                    radiusDp = 88f,
                    isRect = false
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.44f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.41f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.39f,
                    endXFraction = 0.50f,
                    endYFraction = 0.37f
                )
            ),

            // Step 6: Your Rank & Progress
            TourStep(
                id = 5,
                totalSteps = total,
                category = "RANK",
                targetTab = T1Tab.RANK,
                title = "Your Rank & Live Standing",
                subtitle = "Track your current position, percentile category, and live Focus Score change from last period in real time.",
                iconEmoji = "👤",
                cardXFraction = 0.06f,
                cardYFraction = 0.56f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.72f,
                    radiusDp = 40f,
                    isRect = true,
                    widthFraction = 0.88f,
                    heightFraction = 0.09f
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.65f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.67f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.68f,
                    endXFraction = 0.50f,
                    endYFraction = 0.69f
                )
            ),

            // Step 7: Leaderboard Categories (Global, Founder, Aukaat)
            TourStep(
                id = 6,
                totalSteps = total,
                category = "RANK",
                targetTab = T1Tab.RANK,
                title = "Leaderboard Modes & Filters",
                subtitle = "Switch between Global standings, Founder networks, or your private Aukaat peer circles to compete with friends.",
                iconEmoji = "🌐",
                cardXFraction = 0.06f,
                cardYFraction = 0.22f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.13f,
                    radiusDp = 30f,
                    isRect = true,
                    widthFraction = 0.90f,
                    heightFraction = 0.06f
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.25f,
                    startYFraction = 0.22f,
                    cp1XFraction = 0.25f,
                    cp1YFraction = 0.19f,
                    cp2XFraction = 0.20f,
                    cp2YFraction = 0.18f,
                    endXFraction = 0.18f,
                    endYFraction = 0.16f
                )
            ),

            // ═════════════════════════════════════════════════════════════════
            // PHASE 3: FIGHT (Focus Battle Arena & Simulation - Steps 8 to 10)
            // ═════════════════════════════════════════════════════════════════

            // Step 8: Focus Fight Arena Overview
            TourStep(
                id = 7,
                totalSteps = total,
                category = "FIGHT",
                targetTab = T1Tab.BATTLE,
                title = "1v1 Focus Fight Arena",
                subtitle = "Welcome to the arena! Two users lock into a real-time focus duel. The player who stays in T1 with uninterrupted deep focus wins the match.",
                iconEmoji = "⚔️",
                cardXFraction = 0.06f,
                cardYFraction = 0.44f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.26f,
                    radiusDp = 50f,
                    isRect = true,
                    widthFraction = 0.88f,
                    heightFraction = 0.18f
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.44f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.40f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.38f,
                    endXFraction = 0.50f,
                    endYFraction = 0.36f
                )
            ),

            // Step 9: Search & Challenge Opponents
            TourStep(
                id = 8,
                totalSteps = total,
                category = "FIGHT",
                targetTab = T1Tab.BATTLE,
                title = "Search & Challenge Rivals",
                subtitle = "Search any user by handle or college. Tap to inspect their win rate, send a direct battle challenge, or enter global matchmaking.",
                iconEmoji = "🔍",
                cardXFraction = 0.06f,
                cardYFraction = 0.42f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.32f,
                    radiusDp = 30f,
                    isRect = true,
                    widthFraction = 0.88f,
                    heightFraction = 0.08f
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.42f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.39f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.38f,
                    endXFraction = 0.50f,
                    endYFraction = 0.36f
                )
            ),

            // Step 10: Interactive Battle Simulation (Wager & Duration)
            TourStep(
                id = 9,
                totalSteps = total,
                category = "FIGHT",
                targetTab = T1Tab.BATTLE,
                title = "Duel Stakes & Token Pot",
                subtitle = "Choose your duration (25m) and stake T1 tokens into escrow. Winner takes the whole 40-token bounty! Leaving T1 during battle results in instant defeat.",
                iconEmoji = "🪙",
                cardXFraction = 0.06f,
                cardYFraction = 0.64f,
                cardWidthFraction = 0.88f,
                isBattleInteractiveDemo = true,
                spotlight = null,
                arrow = null
            ),

            // ═════════════════════════════════════════════════════════════════
            // PHASE 4: USER (Profile & Settings - Steps 11 to 15)
            // ═════════════════════════════════════════════════════════════════

            // Step 11: Your Profile & Deep Work Title
            TourStep(
                id = 10,
                totalSteps = total,
                category = "PROFILE",
                targetTab = T1Tab.USER,
                title = "Your Profile & Deep Work Title",
                subtitle = "Tap the edit icon to customize your display name and avatar. Your title tag reflects your unique focus style and journey.",
                iconEmoji = "👤",
                cardXFraction = 0.06f,
                cardYFraction = 0.33f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.18f,
                    radiusDp = 65f,
                    isRect = false
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.33f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.29f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.27f,
                    endXFraction = 0.50f,
                    endYFraction = 0.25f
                )
            ),

            // Step 12: Focus Time & 7-Day Trend
            TourStep(
                id = 11,
                totalSteps = total,
                category = "PROFILE",
                targetTab = T1Tab.USER,
                title = "Focus Activity & 7-Day Trend",
                subtitle = "Review your total deep work hours, completed focus sessions, and 7-day progress bars. Consistency raises your rank.",
                iconEmoji = "📊",
                cardXFraction = 0.06f,
                cardYFraction = 0.62f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.44f,
                    radiusDp = 50f,
                    isRect = true,
                    widthFraction = 0.88f,
                    heightFraction = 0.22f
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.62f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.58f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.56f,
                    endXFraction = 0.50f,
                    endYFraction = 0.55f
                )
            ),

            // Step 13: Achievements & Milestones
            TourStep(
                id = 12,
                totalSteps = total,
                category = "PROFILE",
                targetTab = T1Tab.USER,
                title = "Achievements & Streaks",
                subtitle = "Unlock streaks, focus hour milestones, and special ranks as you stay locked in day after day.",
                iconEmoji = "🏆",
                cardXFraction = 0.06f,
                cardYFraction = 0.50f,
                cardWidthFraction = 0.88f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.50f,
                    cyFraction = 0.70f,
                    radiusDp = 40f,
                    isRect = true,
                    widthFraction = 0.88f,
                    heightFraction = 0.14f
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.50f,
                    startYFraction = 0.59f,
                    cp1XFraction = 0.50f,
                    cp1YFraction = 0.62f,
                    cp2XFraction = 0.50f,
                    cp2YFraction = 0.64f,
                    endXFraction = 0.50f,
                    endYFraction = 0.65f
                )
            ),

            // Step 14: Settings & Account Management
            TourStep(
                id = 13,
                totalSteps = total,
                category = "PROFILE",
                targetTab = T1Tab.USER,
                title = "Settings & Preferences",
                subtitle = "Configure focus notifications, privacy ghost mode, replay this tour anytime, or manage and delete your account.",
                iconEmoji = "⚙️",
                cardXFraction = 0.12f,
                cardYFraction = 0.13f,
                cardWidthFraction = 0.82f,
                spotlight = SpotlightTargetSpec(
                    cxFraction = 0.92f,
                    cyFraction = 0.055f,
                    radiusDp = 28f,
                    isRect = false
                ),
                arrow = TourArrowPath(
                    startXFraction = 0.80f,
                    startYFraction = 0.13f,
                    cp1XFraction = 0.86f,
                    cp1YFraction = 0.11f,
                    cp2XFraction = 0.90f,
                    cp2YFraction = 0.09f,
                    endXFraction = 0.91f,
                    endYFraction = 0.075f
                )
            ),

            // Step 15: Grand Finale - Welcome to T1!
            TourStep(
                id = 14,
                totalSteps = total,
                category = "COMPLETE",
                targetTab = T1Tab.HOME,
                title = "You're Ready to Dominate Focus!",
                subtitle = "T1 is now fully calibrated around your personal objective. Beat your past self, win 1v1 focus fights, and climb the ranks.",
                iconEmoji = "🚀",
                cardXFraction = 0.06f,
                cardYFraction = 0.28f,
                cardWidthFraction = 0.88f,
                isFinale = true,
                spotlight = null,
                arrow = null
            )
        )
    }
}
