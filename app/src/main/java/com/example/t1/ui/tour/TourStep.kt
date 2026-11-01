package com.example.t1.ui.tour

import com.example.t1.ui.components.T1Tab

enum class HighlightTarget {
    FOCUS_RING,
    GOAL_CARD,
    TOKEN_WALLET,
    BATTLE_ARENA,
    WHITELIST_TOOLS,
    LEADERBOARD_PODIUM
}

enum class ArrowDirection {
    POINTING_UP,
    POINTING_DOWN
}

data class ComparisonPoint(
    val title: String,
    val t1Advantage: String,
    val competitorFlaw: String
)

data class TourStep(
    val id: Int,
    val totalSteps: Int = 6,
    val stepLabel: String,
    val featureName: String,
    val tapPrompt: String,
    val highlightTarget: HighlightTarget,
    val arrowDirection: ArrowDirection = ArrowDirection.POINTING_DOWN,
    val targetTab: T1Tab = T1Tab.HOME,
    val requireSettings: Boolean = false,
    val whatItDoesSummary: String,
    val howToUseSteps: List<String>,
    val competitorName: String,
    val comparisons: List<ComparisonPoint>,
    val keyPills: List<String> = emptyList()
) {
    val isFinal: Boolean get() = id == totalSteps - 1
    val isWelcome: Boolean get() = id == 0
}

object TourStepCatalog {

    fun getSteps(userGoal: String = "Productivity"): List<TourStep> {
        return listOf(
            // Step 0: Focus Score Engine
            TourStep(
                id = 0,
                totalSteps = 6,
                stepLabel = "STEP 1 OF 6 • FOCUS SCORE ENGINE",
                featureName = "Your Live Focus Score (0 - 100)",
                tapPrompt = "Tap the Focus Ring to inspect how your score is calculated",
                highlightTarget = HighlightTarget.FOCUS_RING,
                arrowDirection = ArrowDirection.POINTING_DOWN,
                targetTab = T1Tab.HOME,
                whatItDoesSummary = "An objective, on-device behavioral score (0 to 100) computed live every minute. It measures your deep work and productive focus time directly against your personal 7-day rolling baseline.",
                howToUseSteps = listOf(
                    "Your score updates dynamically as you use your device throughout the day.",
                    "Score rises only when you outperform your own past 7-day average — beating your past self, not arbitrary targets.",
                    "Maintaining an 80+ score unlocks Top 1% tier multipliers and higher token earning rates."
                ),
                competitorName = "Forest & Apple Screen Time",
                comparisons = listOf(
                    ComparisonPoint(
                        title = "Personal Baseline vs Arbitrary Quota",
                        t1Advantage = "Compares today directly against your own 7-day rolling performance. Honest relative growth.",
                        competitorFlaw = "Apple Screen Time gives passive weekly reports after the damage is done. Forest plants fake trees even if your phone is sitting idle."
                    ),
                    ComparisonPoint(
                        title = "True Behavioral Mirror",
                        t1Advantage = "Differentiates true deep work from mindless tab-switching in real time.",
                        competitorFlaw = "Traditional blockers treat all usage as evil, causing 87% of users to uninstall within 48 hours."
                    )
                ),
                keyPills = listOf("7-Day Baseline", "Objective 0-100", "No Fake Streaks")
            ),

            // Step 1: Goal Tracking & Delta
            TourStep(
                id = 1,
                totalSteps = 6,
                stepLabel = "STEP 2 OF 6 • GOAL DELTA MIRROR",
                featureName = "Live Delta: You vs Yesterday",
                tapPrompt = "Tap the Goal Card to inspect your daily improvement delta",
                highlightTarget = HighlightTarget.GOAL_CARD,
                arrowDirection = ArrowDirection.POINTING_UP,
                targetTab = T1Tab.HOME,
                whatItDoesSummary = "Shows your exact minute-by-minute delta in your chosen primary focus goal (Productivity, Education, Social Control) compared strictly to yesterday's performance.",
                howToUseSteps = listOf(
                    "Choose your primary focus area during onboarding or change it anytime in Settings.",
                    "The card displays a live delta: e.g. '↓ 139 min vs yesterday' shows you cut distraction by over 2 hours.",
                    "Green badge confirms when you are officially outperforming yesterday."
                ),
                competitorName = "Opal & Freedom Blocker",
                comparisons = listOf(
                    ComparisonPoint(
                        title = "Context-Aware Discipline",
                        t1Advantage = "Targets the specific app category you want to conquer without locking you out of your device.",
                        competitorFlaw = "Opal charges \$100/yr for blunt-force lockouts that users bypass the moment an urgent task arrives."
                    ),
                    ComparisonPoint(
                        title = "Immediate Feedback Loop",
                        t1Advantage = "Live delta tells you exactly where you stand right now so you can adjust course instantly.",
                        competitorFlaw = "Other apps only summarize data after you have already relapsed."
                    )
                ),
                keyPills = listOf("Live Delta", "Category Control", "Beating Yesterday")
            ),

            // Step 2: T1 Tokens & Obsidian Merch
            TourStep(
                id = 2,
                totalSteps = 6,
                stepLabel = "STEP 3 OF 6 • PROOF-OF-WORK TOKENS",
                featureName = "T1 Token Economy & Obsidian Drops",
                tapPrompt = "Tap the Token Pill to inspect proof-of-focus rewards",
                highlightTarget = HighlightTarget.TOKEN_WALLET,
                arrowDirection = ArrowDirection.POINTING_UP,
                targetTab = T1Tab.HOME,
                whatItDoesSummary = "A non-inflationary token economy minted purely through proven deep work. Complete focus sessions, win 1v1 duels, and redeem tokens for real physical Obsidian merchandise.",
                howToUseSteps = listOf(
                    "Mint 1 T1 Token for every 10 minutes of verified uninterrupted deep work.",
                    "Wager tokens in live 1v1 multiplayer duels into an escrow prize pot.",
                    "Redeem accumulated tokens in the Merch Store for physical Obsidian Drops (T1 Tees, Caps, Metal NFC Badges)."
                ),
                competitorName = "Duolingo & Forest Virtual Coins",
                comparisons = listOf(
                    ComparisonPoint(
                        title = "Physical Real-World Value",
                        t1Advantage = "Tokens redeem for high-grade physical luxury apparel and tangible proof-of-focus drops.",
                        competitorFlaw = "Other apps give useless cartoon gems that only buy virtual avatar hats or digital stickers."
                    ),
                    ComparisonPoint(
                        title = "Anti-Cheat Minting",
                        t1Advantage = "Cryptographic on-device verification ensures tokens can only be minted through true focus.",
                        competitorFlaw = "Users easily cheat other apps by setting timers while doing other tasks."
                    )
                ),
                keyPills = listOf("Physical Drops", "Token Escrow", "Real Proof-of-Work")
            ),

            // Step 3: 1v1 Live Focus Fight Arena
            TourStep(
                id = 3,
                totalSteps = 6,
                stepLabel = "STEP 4 OF 6 • MULTIPLAYER ARENA",
                featureName = "1v1 Live Focus Fight Arena",
                tapPrompt = "Tap the Fight Card to inspect high-stakes multiplayer duels",
                highlightTarget = HighlightTarget.BATTLE_ARENA,
                arrowDirection = ArrowDirection.POINTING_DOWN,
                targetTab = T1Tab.BATTLE,
                whatItDoesSummary = "High-stakes 25-minute live focus duels with wagered tokens held in escrow. If a player leaves T1 to browse blacklisted apps for >15 seconds, they immediately forfeit to their rival!",
                howToUseSteps = listOf(
                    "Challenge a friend directly or find a live rival in the Global Matchmaking pool.",
                    "Both players wager tokens (e.g. 25 tokens each into a 50-token escrow pot).",
                    "Enter the Live Arena: stay locked in for 25 minutes. Winner takes the entire wager pot!"
                ),
                competitorName = "Solo Productivity Apps",
                comparisons = listOf(
                    ComparisonPoint(
                        title = "High-Stakes Peer Pressure",
                        t1Advantage = "Live peer accountability with real token escrow prevents secret relapse at 11 PM or 2 AM.",
                        competitorFlaw = "Solo apps leave you completely alone; willpower collapses when there is no skin in the game."
                    ),
                    ComparisonPoint(
                        title = "Anti-Surrender Shield",
                        t1Advantage = "Minimizing the app triggers a 15-second forfeit alarm that forces you back to focus.",
                        competitorFlaw = "Traditional blockers let you disable restrictions with a simple passcode or bypass button."
                    )
                ),
                keyPills = listOf("25m Live Duels", "Token Pot Escrow", "15s Surrender Shield")
            ),

            // Step 4: Smart Study Whitelist
            TourStep(
                id = 4,
                totalSteps = 6,
                stepLabel = "STEP 5 OF 6 • STUDY WHITELIST",
                featureName = "Smart Whitelist: Zero False Forfeits",
                tapPrompt = "Tap to see how Notion & study tools are protected",
                highlightTarget = HighlightTarget.WHITELIST_TOOLS,
                arrowDirection = ArrowDirection.POINTING_DOWN,
                targetTab = T1Tab.BATTLE,
                whatItDoesSummary = "Customize allowed study tools (Notion, Google Docs, WhatsApp for study doubts, Slack) so you can do legitimate coursework during active focus fights without penalty.",
                howToUseSteps = listOf(
                    "Tap 'Allowed Tools' in the Fight tab to configure your study toolkit.",
                    "Add tools you legitimately require for homework, programming, or team collaboration.",
                    "Switch to whitelisted apps anytime during battles — T1 keeps your session fully alive!"
                ),
                competitorName = "Opal & Freedom App Lockers",
                comparisons = listOf(
                    ComparisonPoint(
                        title = "Dual-Purpose App Protection",
                        t1Advantage = "Allows Notion and WhatsApp study doubts while aggressively blocking Instagram, TikTok, and YouTube Shorts.",
                        competitorFlaw = "Locks down the entire phone, forcing users to delete the blocker to reply to a critical project message."
                    ),
                    ComparisonPoint(
                        title = "Built for Real Builders",
                        t1Advantage = "Designed for engineers, students, and founders who actually work on their devices.",
                        competitorFlaw = "Naive blockers treat all screen time as toxic, ignoring productive workflows."
                    )
                ),
                keyPills = listOf("Notion & Slack Safe", "No False Alarms", "Work Uninterrupted")
            ),

            // Step 5: Verified Leaderboards
            TourStep(
                id = 5,
                totalSteps = 6,
                stepLabel = "STEP 6 OF 6 • VERIFIED RANKS",
                featureName = "Cryptographic Proof-of-Work Ranks",
                tapPrompt = "Tap to inspect verified Global & College rankings",
                highlightTarget = HighlightTarget.LEADERBOARD_PODIUM,
                arrowDirection = ArrowDirection.POINTING_DOWN,
                targetTab = T1Tab.RANK,
                whatItDoesSummary = "Climb verified Global, College, and Mutual Friends leaderboards based strictly on verified deep work hours. Top performers earn prestigious Obsidian badges.",
                howToUseSteps = listOf(
                    "Ranks recalculate dynamically based on verified daily and weekly focus score percentiles.",
                    "Filter rankings across Global, Your Specific College, or your Personal Friend circle.",
                    "Clinch the #1 spot on your campus to earn the official campus champion Obsidian badge."
                ),
                competitorName = "Duolingo & Strava Leaderboards",
                comparisons = listOf(
                    ComparisonPoint(
                        title = "Anti-Bot Verification",
                        t1Advantage = "On-device usage telemetry ensures every single minute on the podium is genuinely earned.",
                        competitorFlaw = "Duolingo leaderboards are plagued by bot scripts and XP exploits that ruin fair competition."
                    ),
                    ComparisonPoint(
                        title = "Campus & Industry Prestige",
                        t1Advantage = "Recognized proof-of-work ranking you can showcase to employers and peers.",
                        competitorFlaw = "Generic virtual leagues reset every week with zero lasting reputation."
                    )
                ),
                keyPills = listOf("Global & College", "Anti-Cheat Verified", "Obsidian Prestige")
            )
        )
    }
}
