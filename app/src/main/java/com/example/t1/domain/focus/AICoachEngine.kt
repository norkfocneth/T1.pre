package com.example.t1.domain.focus

import com.example.t1.domain.model.behaviour.AppUsageSummary
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * T1 AI Coach Engine — Highly Intelligent, Context-Aware, Human-Like Discipline Mentor.
 * 
 * Analyzes multi-day historical telemetry, evaluates today vs yesterday deltas,
 * constructs concrete mathematical blueprints for tomorrow's score improvement,
 * and delivers empathetic, high-conviction guidance across English, Hindi, and Hinglish.
 */
object AICoachEngine {

    /**
     * Formats milliseconds into human-readable duration (e.g., "2h 15m" or "42m").
     */
    fun formatDuration(ms: Long): String {
        val totalMinutes = ms / 60000
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }

    /**
     * Returns the time-of-day greeting.
     */
    private fun getTimeOfDayGreeting(name: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val cleanName = if (name.isNotBlank()) name else "Champion"
        return when (hour) {
            in 4..11 -> "Good morning, $cleanName! 🌅 Let's set the standard today."
            in 12..16 -> "Good afternoon, $cleanName! ⚡ Midday focus check-in."
            in 17..21 -> "Good evening, $cleanName! 🌙 Let's review how you commanded your attention today."
            else -> "Late night check, $cleanName! 🌌 Protect your sleep and wind down."
        }
    }

    /**
     * Generates a dynamic, engaging, personalized initial greeting whenever the user opens AI Coach.
     */
    fun generateInitialGreeting(
        focusScore: Int,
        behaviourScore: Int,
        confidence: Int = 80,
        timeSavedMs: Long = 0L,
        screenTimeMs: Long = 0L,
        unlocks: Int = 0,
        categoryTimes: Map<String, Long> = emptyMap(),
        yesterdayCategoryTimes: Map<String, Long> = emptyMap(),
        topUsedApps: List<AppUsageSummary> = emptyList(),
        yesterdayTopUsedApps: List<AppUsageSummary> = emptyList(),
        weeklyTrend: List<Int> = emptyList(),
        userGoal: String = "Discipline",
        username: String = "",
        streak: Int = 0,
        percentile: Int = 75,
        tokenBalance: Int = 0,
        appOpens: Int = 0,
        baselineGoalTimeMs: Long = 0L
    ): String {
        val screenTimeStr = formatDuration(screenTimeMs)
        val timeSavedStr = formatDuration(abs(timeSavedMs))
        val greetingHeader = getTimeOfDayGreeting(username)

        // 1. Time Saved / Lost context
        val deltaInsight = when {
            timeSavedMs > 15 * 60000L -> "🔥 **+${timeSavedStr} saved** compared to yesterday! You're actively winning back your time."
            timeSavedMs < -20 * 60000L -> "⚠️ **${timeSavedStr} more screen time** than yesterday. Let's regain control before the day ends."
            else -> "⚖️ Usage is on track with your baseline."
        }

        // 2. Score & Rank Tier Evaluation
        val rankBadge = when {
            percentile >= 95 -> "🏆 Top 5% Elite Tier"
            percentile >= 80 -> "⚡ Top 20% High Performer"
            percentile >= 60 -> "🎯 Top 40% Contender"
            else -> "📈 Building Baseline"
        }

        // 3. Top Distraction or Dominant App Insight
        val topApp = topUsedApps.maxByOrNull { it.usageDurationMs }
        val appHighlight = if (topApp != null && topApp.usageDurationMs > 5 * 60000L) {
            val topAppTime = formatDuration(topApp.usageDurationMs)
            "📊 **Top App:** ${topApp.displayName} took $topAppTime today."
        } else {
            val dominantCat = categoryTimes.maxByOrNull { it.value }
            if (dominantCat != null && dominantCat.value > 0) {
                "📊 **Dominant Category:** ${dominantCat.key} (${formatDuration(dominantCat.value)})"
            } else {
                "📊 Clean slate today — no major distractions recorded."
            }
        }

        // 4. Micro Actionable Coaching Nudge
        val coachingTip = when {
            unlocks > 60 -> "💡 *Pro-tip: High unlock count ($unlocks). Turn off non-essential notifications to prevent attention fragmentation.*"
            screenTimeMs > 4 * 3600000L -> "💡 *Action step: Cap entertainment apps and start a 25m Deep Work session to boost your Focus Score.*"
            focusScore >= 85 -> "💡 *You are in flow state. Maintain this streak to unlock top leaderboard badges.*"
            else -> "💡 *Ready for a breakthrough? Tap the Focus Timer to start mining T1 tokens.*"
        }

        return """
            $greetingHeader

            • **Focus Score:** $focusScore/99 ($rankBadge)
            • **Screen Time:** $screenTimeStr | **Unlocks:** $unlocks
            
            $deltaInsight
            $appHighlight

            $coachingTip

            *Ask me anything about your history, 'How to improve tomorrow', or say 'Roast my screen time'!*
        """.trimIndent()
    }

    /**
     * Primary conversation handler. Understands intent in English, Hindi, and Hinglish,
     * referencing live user data to deliver human-like, actionable responses.
     */
    fun getResponse(
        query: String,
        focusScore: Int,
        behaviourScore: Int,
        screenTimeMs: Long = 0L,
        unlocks: Int = 0,
        categoryTimes: Map<String, Long> = emptyMap(),
        yesterdayCategoryTimes: Map<String, Long> = emptyMap(),
        topUsedApps: List<AppUsageSummary> = emptyList(),
        yesterdayTopUsedApps: List<AppUsageSummary> = emptyList(),
        weeklyTrend: List<Int> = emptyList(),
        confidence: Int = 80,
        timeSavedMs: Long = 0L,
        userGoal: String = "Discipline",
        username: String = "",
        streak: Int = 0,
        percentile: Int = 75,
        tokenBalance: Int = 0,
        appOpens: Int = 0,
        baselineGoalTimeMs: Long = 0L
    ): String {
        val q = query.lowercase().trim()
        val screenTimeStr = formatDuration(screenTimeMs)
        val timeSavedStr = formatDuration(abs(timeSavedMs))

        // ── 1. HOW TO IMPROVE TOMORROW / HISTORICAL AUDIT & GAME PLAN ──
        // (Triggers when user asks how to improve, how to do better tomorrow, plan for tomorrow, etc.)
        val isAskingImprovement = q.contains("improve") || q.contains("increase") || q.contains("better") || 
            q.contains("kal") || q.contains("tomorrow") || q.contains("badhau") || q.contains("kaise") || 
            q.contains("plan") || q.contains("history") || q.contains("analyse") || q.contains("analyze") ||
            q.contains("strategy") || q.contains("levers") || q.contains("score gir gaya") || q.contains("tips")

        val isTomorrowOrHistorySpecific = q.contains("kal") || q.contains("tomorrow") || q.contains("history") || 
            q.contains("past") || q.contains("better") || q.contains("karenge") || q.contains("improve") || 
            q.contains("kaise") || q.contains("game plan") || q.contains("kya karu")

        if (isAskingImprovement && isTomorrowOrHistorySpecific) {
            return generateTomorrowBlueprint(
                username = username,
                currentScore = focusScore,
                behaviourScore = behaviourScore,
                screenTimeMs = screenTimeMs,
                unlocks = unlocks,
                timeSavedMs = timeSavedMs,
                categoryTimes = categoryTimes,
                yesterdayCategoryTimes = yesterdayCategoryTimes,
                topUsedApps = topUsedApps,
                yesterdayTopUsedApps = yesterdayTopUsedApps,
                weeklyTrend = weeklyTrend,
                userGoal = userGoal,
                streak = streak,
                tokenBalance = tokenBalance,
                baselineGoalTimeMs = baselineGoalTimeMs
            )
        }

        // ── 2. ROAST / REALITY CHECK (Brutally honest, humorous, motivating) ──
        if (q.contains("roast") || q.contains("sach") || q.contains("be honest") || q.contains("aukaat") || q.contains("cooked") || q.contains("harsh") || q.contains("judge")) {
            val topDistraction = topUsedApps.filter { 
                val cat = it.category.name.uppercase()
                cat == "SOCIAL" || cat == "ENTERTAINMENT" || cat == "COMMUNICATION" 
            }.maxByOrNull { it.usageDurationMs }

            val roastLead = if (username.isNotBlank()) "Alright @$username, here is the unfiltered truth:" else "Alright, here is the unfiltered truth:"
            
            return when {
                screenTimeMs > 4 * 3600000L && topDistraction != null -> {
                    val appTime = formatDuration(topDistraction.usageDurationMs)
                    val yesterdayDistraction = yesterdayTopUsedApps.firstOrNull { it.packageName == topDistraction.packageName }?.usageDurationMs ?: 0L
                    val appDelta = if (yesterdayDistraction > 0) {
                        val diff = (topDistraction.usageDurationMs - yesterdayDistraction) / 60000
                        if (diff > 0) " (+${diff}m more than yesterday!)" else " (${abs(diff)}m less than yesterday)"
                    } else ""
                    
                    """
                    $roastLead 🔥
                    
                    You've spent **$screenTimeStr** staring at glass today, with **$appTime solely dedicated to ${topDistraction.displayName}$appDelta**. 
                    
                    And you checked your phone **$unlocks times**. That's like checking the fridge every 8 minutes hoping new discipline magically appeared.
                    
                    Your competitors in the Top 1% are building businesses, coding, and studying while you're donating your dopamine to algorithms. 
                    
                    Put the phone down, start a 25-minute focus session right now, and prove me wrong. ⚡
                    """.trimIndent()
                }
                unlocks > 70 -> {
                    """
                    $roastLead 📱
                    
                    You have unlocked your phone **$unlocks times** today! That means your attention span is currently lasting about as long as a goldfish. Every time your brain feels a micro-second of boredom, your thumb reaches for a screen.
                    
                    Try this challenge: Leave your phone in another room for the next 45 minutes. Let's see if you can survive the withdrawal. 🧘‍♂️
                    """.trimIndent()
                }
                focusScore < 60 -> {
                    """
                    $roastLead 📉
                    
                    Your Focus Score is sitting at **$focusScore/99**. Right now, your phone owns you — you don't own your phone. 
                    
                    The good news? It's fixable today. Cut social media for the next 3 hours and finish 1 focus session. Let's get that score off the floor. 🚀
                    """.trimIndent()
                }
                else -> {
                    """
                    $roastLead ✨
                    
                    Honestly? Your Focus Score is **$focusScore/99** with only **$screenTimeStr** screen time. You're actually doing better than 85% of people today.
                    
                    Don't get cocky though — discipline is rented every day, and rent is due every 24 hours. Keep pushing toward that Top 1% rank. 👑
                    """.trimIndent()
                }
            }
        }

        // ── 3. SPECIFIC APP INQUIRIES (Instagram, YouTube, WhatsApp, Reddit, Games, etc.) ──
        val matchedApp = topUsedApps.firstOrNull { app ->
            val name = app.displayName.lowercase()
            val pkg = app.packageName.lowercase()
            q.contains(name) || (q.contains("insta") && pkg.contains("instagram")) ||
            (q.contains("yt") && pkg.contains("youtube")) ||
            (q.contains("wa") && pkg.contains("whatsapp")) ||
            (q.contains("snap") && pkg.contains("snapchat")) ||
            (q.contains("reddit") && pkg.contains("reddit")) ||
            (q.contains("twitter") && (pkg.contains("twitter") || pkg.contains("x.android")))
        }
        if (matchedApp != null) {
            val appDuration = formatDuration(matchedApp.usageDurationMs)
            val appPct = if (screenTimeMs > 0) ((matchedApp.usageDurationMs.toDouble() / screenTimeMs) * 100).roundToInt() else 0
            
            // Look up yesterday's usage for this exact app
            val yApp = yesterdayTopUsedApps.firstOrNull { it.packageName == matchedApp.packageName }
            val yDurationStr = if (yApp != null) formatDuration(yApp.usageDurationMs) else "0m"
            val deltaMs = matchedApp.usageDurationMs - (yApp?.usageDurationMs ?: 0L)
            val deltaStr = if (deltaMs > 0) "+${formatDuration(deltaMs)} vs yesterday (Increased ⚠️)" else if (deltaMs < 0) "-${formatDuration(abs(deltaMs))} vs yesterday (Improved ✅)" else "Same as yesterday"

            val appAdvice = when {
                matchedApp.packageName.contains("instagram") -> 
                    "Reels are infinite dopamine traps designed by algorithms. Turn off notification popups and put Instagram inside a nested folder to add friction."
                matchedApp.packageName.contains("youtube") -> 
                    "If you use YouTube for learning, mark it as 'Education' in Dual-Purpose App settings so it boosts your score instead of penalizing you!"
                matchedApp.packageName.contains("whatsapp") -> 
                    "WhatsApp group chats fragment deep work. Try batching your replies to twice a day (1 PM and 7 PM) instead of instant replies."
                else -> 
                    "This app ate $appPct% of your total phone usage today. Try replacing 15 minutes of it with an active Focus Timer block."
            }

            return """
                📱 **${matchedApp.displayName} Diagnostic:**
                
                • **Today's Time:** $appDuration ($appPct% of total screen time)
                • **Yesterday's Time:** $yDurationStr ($deltaStr)
                • **Category:** ${matchedApp.category.name}
                
                💡 **Tactical Intervention:**
                $appAdvice
                
                *Ask me: 'How do I cut ${matchedApp.displayName} time tomorrow?' for a full protocol!*
            """.trimIndent()
        }

        // ── 4. ANALYZE TODAY'S STATS / DAILY BREAKDOWN / SUMMARY ──
        if (q.contains("summary") || q.contains("stats") || q.contains("breakdown") || q.contains("kaisa") || q.contains("report") || q.contains("today") || q.contains("progress") || q.contains("status")) {
            val socialMs = categoryTimes["Social"] ?: 0L
            val entMs = categoryTimes["Entertainment"] ?: 0L
            val prodMs = categoryTimes["Productivity"] ?: 0L
            val eduMs = categoryTimes["Education"] ?: 0L

            val top3Apps = topUsedApps.take(3).joinToString("\n") { 
                "  • **${it.displayName}**: ${formatDuration(it.usageDurationMs)}"
            }.ifBlank { "  • No app usage logged yet" }

            val deltaText = if (timeSavedMs > 0) "✅ Saved ${timeSavedStr} vs yesterday" else if (timeSavedMs < 0) "⚠️ Spent ${timeSavedStr} more than yesterday" else "⚖️ Stable vs yesterday"

            return """
                📊 **Comprehensive Daily Telemetry:**
                
                • **Focus Score:** $focusScore/99 ($percentile% Percentile)
                • **Behaviour Score:** $behaviourScore/100 (Discipline Index)
                • **Total Screen Time:** $screenTimeStr ($deltaText)
                • **Unlocks:** $unlocks unlocks | **Confidence:** $confidence%
                
                📂 **Category Distribution:**
                • Productivity & Education: ${formatDuration(prodMs + eduMs)}
                • Social & Entertainment: ${formatDuration(socialMs + entMs)}
                
                🏆 **Top Apps Today:**
                $top3Apps
                
                *Want to know how to beat these numbers tomorrow? Say 'Kal ka plan batao'!*
            """.trimIndent()
        }

        // ── 5. FOCUS SESSIONS / POMODORO / DEEP WORK / STUDY ──
        if (q.contains("focus session") || q.contains("timer") || q.contains("pomodoro") || q.contains("deep work") || q.contains("study") || q.contains("padhai") || q.contains("concentrate")) {
            return """
                ⏱️ **Deep Work & Focus Session Protocol:**
                
                • **The 25/5 Rule:** Work with total intensity for 25 minutes, then take a 5-minute break. In that break, **do not look at your screen**. Drink water, stretch, or look at the horizon.
                • **Proof-of-Focus Mining:** Every finished session mints T1 tokens directly to your wallet. You currently have **$tokenBalance Tokens**.
                • **Haptic Immersion:** When you start the T1 Focus Timer, put your phone face down. The app tracks your session integrity.
                
                Ready to lock in? Tap the Focus Timer button to start a session! 🚀
            """.trimIndent()
        }

        // ── 6. MOTIVATION / LAZINESS / PROCRASTINATION / DOPAMINE ──
        if (q.contains("lazy") || q.contains("alas") || q.contains("unmotivated") || q.contains("procrastinat") || q.contains("bored") || q.contains("distract") || q.contains("man nahi") || q.contains("dopamine") || q.contains("tired")) {
            return """
                🧠 **Neuroscience Reality Check:**
                
                Motivation is a feeling; discipline is a system. You don't need to "feel like it" to start.
                
                1. **The 2-Minute Rule:** Tell yourself you will only focus on your task for 2 minutes. The hardest friction is starting. Once you begin, dopamine kicks in and momentum takes over.
                2. **Dopamine Reset:** When you feel bored, your brain craves high-stimulus apps (Reels, TikTok, YouTube). Lean into the boredom for 5 minutes without touching your phone. Your baseline dopamine will recalibrate.
                3. **Physical Reset:** Stand up, drink cold water, take 10 deep breaths, and put your phone 10 feet away.
                
                You have the power to turn today around right now. What is the #1 task you need to conquer?
            """.trimIndent()
        }

        // ── 7. TOKENS, REWARDS, WAGERS & MERCH ──
        if (q.contains("token") || q.contains("coin") || q.contains("merch") || q.contains("wager") || q.contains("wallet") || q.contains("reward") || q.contains("spend") || q.contains("t-shirt")) {
            return """
                🪙 **T1 Token Economy (Proof-of-Focus):**
                
                • **Your Balance:** $tokenBalance T1 Tokens
                • **How to Mine:** Tokens *cannot be bought with real money*. You only mine them through verified deep work sessions and maintaining discipline streaks.
                • **Wager Leagues:** Stake your tokens against friends or global peers in focus challenges. Highest weekly score takes the pool!
                • **Merch Store:** Redeem your earned tokens for exclusive T1 physical merchandise (apparel, custom focus ring themes, and status badges).
                
                Your focus literally has real economic utility in T1. 💎
            """.trimIndent()
        }

        // ── 8. SCREEN TIME & UNLOCKS SPECIFIC ──
        if (q.contains("screen time") || q.contains("usage") || q.contains("unlock") || q.contains("phone kitna") || q.contains("time waste")) {
            val avgUnlockMin = if (unlocks > 0) ((screenTimeMs / 60000) / unlocks) else 0
            return """
                📱 **Screen Time & Unlock Dynamics:**
                
                • **Today's Screen Time:** $screenTimeStr
                • **Total Unlocks:** $unlocks unlocks
                • **Average Session Length:** ~$avgUnlockMin minutes per unlock
                
                💡 **Coach Insight:**
                ${if (unlocks > 50) "You are picking up your phone very frequently in short bursts. This constantly breaks your attention flow. Try setting designated phone check windows." else "Great job keeping unlocks under control today!"}
            """.trimIndent()
        }

        // ── 9. NIGHT TIME / SLEEP & SCREEN HABITS ──
        if (q.contains("sleep") || q.contains("night") || q.contains("neend") || q.contains("bed") || q.contains("raat")) {
            return """
                🌙 **Sleep & Cognitive Restoration Protocol:**
                
                • Blue light from phone screens suppresses melatonin release by up to 50%, destroying REM sleep and next-day focus.
                • **Action Rule:** 45 minutes before sleep, set a digital curfew. Charge your phone outside arms-reach.
                • Your Focus Score tomorrow directly depends on your sleep quality tonight! 🛌
            """.trimIndent()
        }

        // ── 10. HOW T1 WORKS / ALGORITHM / PRIVACY ──
        if (q.contains("how t1 works") || q.contains("algorithm") || q.contains("baseline") || q.contains("privacy") || q.contains("how is score calculated") || q.contains("formula")) {
            return """
                ⚙️ **How T1's Engine Works:**
                
                • **7-Day Rolling Baseline:** T1 doesn't compare you to arbitrary internet averages. It compares your today with *your own last 7 days*.
                • **Dual Engines:** **Focus Score (0-99)** measures your specific goal progress. **Behaviour Score (0-100)** measures total digital hygiene.
                • **Confidence Metric:** Scales from 0% to 100% across your first 7 days as the engine learns your genuine habits.
                • **100% Privacy Guarantee:** All telemetry analysis runs **strictly on your phone** via SQLite/Room. Zero raw usage data ever leaves your device.
            """.trimIndent()
        }

        // ── 11. GREETINGS / CHIT-CHAT / PERSONA ──
        if (q.contains("hello") || q.contains("hi") || q.contains("hey") || q.contains("namaste") || q.contains("sup") || q.contains("kaisa hai")) {
            val nameStr = if (username.isNotBlank()) " @$username" else ""
            return "Hey there$nameStr! 👋 I'm your T1 AI Coach. I'm actively analyzing your real-time attention telemetry. How can I help you dominate your focus today?"
        }

        if (q.contains("who are you") || q.contains("what can you do") || q.contains("kya kar sakte ho")) {
            return """
                🤖 I am your **T1 AI Coach** — a private, on-device intelligence engine built to help you join the Top 1% of disciplined individuals.
                
                Here is what I can do:
                1. 📊 **Deep-dive into your screen time & apps**
                2. 🎯 **Analyze your history & create tomorrow's game-plan**
                3. 🧠 **Provide neuroscience-backed focus protocols**
                4. 🔥 **Roast your screen time with brutal honesty**
                5. 🪙 **Track your T1 tokens & wager strategies**
                
                Try asking: *"Kal se better kaise ho sakta hu?"* or *"Roast my phone usage"*!
            """.trimIndent()
        }

        if (q.contains("thank") || q.contains("shukriya") || q.contains("great") || q.contains("mast") || q.contains("awesome") || q.contains("good job")) {
            return "Always here to keep you sharp! ⚡ Stay relentless, protect your attention, and let's conquer the day."
        }

        // ── 12. SMART CONTEXTUAL FALLBACK ──
        val primaryApp = topUsedApps.firstOrNull()?.displayName ?: "apps"
        return """
            I hear you! Let's connect that to your digital discipline goals.
            
            Right now you're at a **Focus Score of $focusScore/99** with **$screenTimeStr of screen time** ($unlocks unlocks).
            
            Here are three things we can explore right now:
            • Say **'Kal se better kaise ho sakta hu?'** for tomorrow's custom blueprint
            • Say **'Analyze my day'** for a full category breakdown
            • Say **'Roast me'** for an unfiltered reality check on your $primaryApp usage!
        """.trimIndent()
    }

    /**
     * Constructs a comprehensive, multi-point mathematical game-plan based on historical analysis.
     */
    private fun generateTomorrowBlueprint(
        username: String,
        currentScore: Int,
        behaviourScore: Int,
        screenTimeMs: Long,
        unlocks: Int,
        timeSavedMs: Long,
        categoryTimes: Map<String, Long>,
        yesterdayCategoryTimes: Map<String, Long>,
        topUsedApps: List<AppUsageSummary>,
        yesterdayTopUsedApps: List<AppUsageSummary>,
        weeklyTrend: List<Int>,
        userGoal: String,
        streak: Int,
        tokenBalance: Int,
        baselineGoalTimeMs: Long
    ): String {
        val nameTag = if (username.isNotBlank()) "@$username" else "Champion"
        val screenTimeStr = formatDuration(screenTimeMs)
        
        // 1. History Trend Insight
        val validScores = weeklyTrend.filter { it > 0 }
        val trendInsight = when {
            validScores.size >= 3 && validScores.takeLast(3).zipWithNext().all { it.first <= it.second } ->
                "📈 **Historical Momentum:** You are on an upward curve over the last few days! You have positive momentum."
            validScores.size >= 3 && validScores.takeLast(3).zipWithNext().all { it.first >= it.second } ->
                "📉 **Historical Trajectory:** Your scores have drifted down over the last 3 days. Tomorrow is our turnaround day."
            timeSavedMs > 15 * 60000L ->
                "🔥 **Yesterday vs Today:** You saved ${formatDuration(timeSavedMs)} today! We can compound this win tomorrow."
            timeSavedMs < -20 * 60000L ->
                "⚠️ **Yesterday vs Today:** Screen time climbed by ${formatDuration(abs(timeSavedMs))} today. Tomorrow we recover."
            else ->
                "⚖️ **Baseline Pattern:** Your focus scores have been stable around $currentScore. Time to break the plateau."
        }

        // 2. Identify Primary Leak / Distraction App
        val topDistraction = topUsedApps.filter { 
            val cat = it.category.name.uppercase()
            cat == "SOCIAL" || cat == "ENTERTAINMENT" || cat == "COMMUNICATION" 
        }.maxByOrNull { it.usageDurationMs }

        val leakName = topDistraction?.displayName ?: "Social Media"
        val leakTimeMs = topDistraction?.usageDurationMs ?: (categoryTimes["Social"] ?: 0L)
        val leakTimeStr = formatDuration(leakTimeMs)
        
        // Target reduction for tomorrow (cut by ~35% or 30 mins)
        val targetReductionMin = if (leakTimeMs > 45 * 60000L) 30 else 15
        val targetCapStr = formatDuration(kotlin.math.max(15 * 60000L, leakTimeMs - (targetReductionMin * 60000L)))

        // 3. Mathematical Score Forecast
        val targetScore = kotlin.math.min(98, currentScore + 6 + if (currentScore < 75) 4 else 2)

        // 4. Return Structured Blueprint
        return """
            🎯 **Tomorrow's Strategic Game-Plan for $nameTag:**
            
            $trendInsight
            
            ---
            
            ### 🔍 1. Root-Cause Historical Audit:
            • **Current Focus Score:** $currentScore/99 (Behaviour Score: $behaviourScore)
            • **Primary Time Leak:** **$leakName** took **$leakTimeStr** today.
            • **Attention Fragmentation:** $unlocks unlocks fragmented your deep work blocks.
            
            ---
            
            ### ⚡ 2. Three Precision Levers to hit $targetScore+ Tomorrow:
            
            1. **Cap $leakName to Under $targetCapStr (-$targetReductionMin mins):**
               Putting an app timer or placing $leakName in a hidden folder eliminates your biggest category drag.
            
            2. **Execute 2 x 25-Minute Focus Sessions:**
               Complete one session before 11:00 AM and one at 3:30 PM. This activates the Focus Engine multiplier (+4 raw points) and mines fresh T1 Tokens.
            
            3. **Target Sub-40 Unlocks:**
               Batch your message checks to 3 fixed times tomorrow (12 PM, 5 PM, 9 PM) to eliminate notification reflex.
            
            ---
            
            ### 🌅 3. Tomorrow's Execution Protocol:
            • **08:00 - 10:30:** *Zero Social Media Rule* (Win the morning, win the day).
            • **11:00 - 11:25:** *Focus Session #1* (High-priority deep work).
            • **15:30 - 15:55:** *Focus Session #2* (Beat the afternoon slump).
            • **22:00:** *Digital Curfew* (Protect tomorrow's baseline).
            
            *Execute these 3 levers tomorrow, and your Focus Score will climb from $currentScore to $targetScore+. Are you locking this in?* 🚀
        """.trimIndent()
    }
}


