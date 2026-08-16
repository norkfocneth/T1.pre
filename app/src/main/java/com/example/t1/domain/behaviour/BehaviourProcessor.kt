package com.example.t1.domain.behaviour

import android.content.Context
import android.content.pm.PackageManager
import com.example.t1.domain.model.behaviour.AppCategory
import com.example.t1.domain.model.behaviour.AppUsageSummary
import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import com.example.t1.domain.model.usage.DailyUsageData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BehaviourProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appCategoryEngine: AppCategoryEngine
) {
    private val labelCache = ConcurrentHashMap<String, String>()
    private val packageManager: PackageManager = context.packageManager

    private val wellKnownAppNames = mapOf(
        // Social
        "com.instagram.android" to "Instagram",
        "com.facebook.katana" to "Facebook",
        "com.facebook.lite" to "Facebook Lite",
        "com.facebook.orca" to "Messenger",
        "com.twitter.android" to "X",
        "co.threads.android" to "Threads",
        "com.reddit.frontpage" to "Reddit",
        "com.linkedin.android" to "LinkedIn",
        "com.snapchat.android" to "Snapchat",
        "com.tiktok.android" to "TikTok",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.pinterest" to "Pinterest",
        "com.tumblr" to "Tumblr",
        "com.badoo.mobile" to "Badoo",
        "com.tinder" to "Tinder",
        "com.bumble.app" to "Bumble",
        "co.hinge.app" to "Hinge",

        // Communication
        "org.telegram.messenger" to "Telegram",
        "org.thunderdog.challegram" to "Telegram X",
        "com.whatsapp" to "WhatsApp",
        "com.whatsapp.w4b" to "WhatsApp Business",
        "com.discord" to "Discord",
        "com.slack" to "Slack",
        "com.microsoft.teams" to "Microsoft Teams",
        "com.google.android.gm" to "Gmail",
        "com.microsoft.office.outlook" to "Outlook",
        "com.google.android.apps.meetings" to "Google Meet",
        "com.google.android.apps.tachyon" to "Google Meet",
        "us.zoom.videomeetings" to "Zoom",
        "com.skype.raider" to "Skype",
        "com.viber.voip" to "Viber",
        "com.imo.android.imoim" to "IMO",
        "com.google.android.apps.dynamite" to "Google Chat",

        // Entertainment
        "com.google.android.youtube" to "YouTube",
        "com.netflix.mediaclient" to "Netflix",
        "com.amazon.avod.thirdpartyclient" to "Prime Video",
        "com.disney.disneyplus" to "Disney+",
        "com.hotstar" to "Disney+ Hotstar",
        "com.hulu" to "Hulu",
        "tv.twitch.android.app" to "Twitch",
        "com.spotify.music" to "Spotify",
        "com.jio.media.ondemand" to "JioCinema",
        "com.mxtech.videoplayer.ad" to "MX Player",
        "com.mxtech.videoplayer.pro" to "MX Player Pro",
        "com.crunchyroll.crunchyroid" to "Crunchyroll",

        // Productivity & Tools
        "com.openai.chatgpt" to "ChatGPT",
        "a.s.g.chatgpt" to "ChatGPT",
        "com.anthropic.claude" to "Claude",
        "com.perplexity.perplexity" to "Perplexity",
        "com.microsoft.copilot" to "Microsoft Copilot",
        "com.notion.id" to "Notion",
        "com.notion.org" to "Notion",
        "notion.id" to "Notion",
        "com.google.android.apps.docs" to "Google Docs",
        "com.google.android.apps.docs.editors.sheets" to "Google Sheets",
        "com.google.android.apps.docs.editors.slides" to "Google Slides",
        "com.google.android.apps.bard" to "Gemini",
        "com.google.android.googlequicksearchbox" to "Google",
        "com.android.chrome" to "Chrome",
        "org.mozilla.firefox" to "Firefox",
        "com.microsoft.emmx" to "Microsoft Edge",
        "com.brave.browser" to "Brave Browser",
        "com.todoist" to "Todoist",
        "com.ticktick.task" to "TickTick",
        "com.trello" to "Trello",
        "com.asana.app" to "Asana",
        "com.dropbox.android" to "Dropbox",
        "com.box.android" to "Box",
        "com.atlassian.android.jira" to "Jira",
        "com.clickup.clickup" to "ClickUp",
        "com.linear.android" to "Linear",
        "md.obsidian" to "Obsidian"
    )

    private fun getApplicationLabel(packageName: String): String {
        val cached = labelCache[packageName]
        if (cached != null) return cached

        // 1. Check known app mapping first
        val known = wellKnownAppNames[packageName]
        if (known != null) {
            labelCache[packageName] = known
            return known
        }

        // 2. Query PackageManager
        val labelFromPm = try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val lbl = packageManager.getApplicationLabel(appInfo).toString().trim()
            if (lbl.isNotEmpty() && !lbl.equals("Android", ignoreCase = true) && !lbl.equals("Android System", ignoreCase = true)) {
                lbl
            } else null
        } catch (e: Exception) {
            null
        }

        if (labelFromPm != null) {
            labelCache[packageName] = labelFromPm
            return labelFromPm
        }

        // 3. Fallback: Parse meaningful package name segment
        val ignoredSegments = setOf(
            "com", "org", "net", "io", "co", "app", "apps", "android", "mobile",
            "client", "mediaclient", "thirdpartyclient", "frontpage", "messenger",
            "pro", "lite", "release", "katana", "w4b", "imoim", "challegram", "id", "music", "task"
        )
        val segments = packageName.split(".").filter { it.isNotBlank() && it.lowercase() !in ignoredSegments }
        val target = segments.lastOrNull() ?: packageName.split(".").lastOrNull() ?: packageName
        val formatted = target.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        labelCache[packageName] = formatted
        return formatted
    }

    fun process(usageData: DailyUsageData): DailyBehaviourSummary {
        val categoryDurations = mutableMapOf<AppCategory, Long>()
        for (category in AppCategory.values()) {
            categoryDurations[category] = 0L
        }

        val appDurations = mutableMapOf<String, Long>()

        for (session in usageData.foregroundSessions) {
            val category = appCategoryEngine.getCategory(session.packageName)
            val currentDuration = categoryDurations[category] ?: 0L
            categoryDurations[category] = currentDuration + session.durationMs

            val currentAppDuration = appDurations[session.packageName] ?: 0L
            appDurations[session.packageName] = currentAppDuration + session.durationMs
        }

        // Build sorted top app usage list (limited to top 20)
        val topApps = appDurations.entries
            .map { (packageName, durationMs) ->
                AppUsageSummary(
                    packageName = packageName,
                    displayName = getApplicationLabel(packageName),
                    usageDurationMs = durationMs,
                    category = appCategoryEngine.getCategory(packageName)
                )
            }
            .sortedByDescending { it.usageDurationMs }
            .take(20)

        return DailyBehaviourSummary(
            date = usageData.date,
            totalScreenTimeMs = usageData.totalScreenTimeMs,
            socialTimeMs = categoryDurations[AppCategory.SOCIAL] ?: 0L,
            entertainmentTimeMs = categoryDurations[AppCategory.ENTERTAINMENT] ?: 0L,
            productiveTimeMs = categoryDurations[AppCategory.PRODUCTIVITY] ?: 0L,
            educationTimeMs = categoryDurations[AppCategory.EDUCATION] ?: 0L,
            communicationTimeMs = categoryDurations[AppCategory.COMMUNICATION] ?: 0L,
            financeTimeMs = categoryDurations[AppCategory.FINANCE] ?: 0L,
            healthTimeMs = categoryDurations[AppCategory.HEALTH] ?: 0L,
            developmentTimeMs = categoryDurations[AppCategory.DEVELOPMENT] ?: 0L,
            otherTimeMs = categoryDurations[AppCategory.OTHER] ?: 0L,
            unlockCount = usageData.unlockCount,
            appOpenCount = usageData.appOpenCount,
            foregroundSessionCount = usageData.foregroundSessions.size,
            topUsedApps = topApps,
            collectionTimestamp = System.currentTimeMillis()
        )
    }
}
