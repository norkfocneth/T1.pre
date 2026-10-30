package com.example.t1.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.t1.data.database.dao.DailyBehaviourDao
import com.example.t1.data.database.dao.FocusSessionDao
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.UsageRepository
import com.example.t1.domain.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@HiltWorker
class T1NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val authRepository: AuthRepository,
    private val usageRepository: UsageRepository,
    private val userRepository: UserRepository,
    private val focusSessionDao: FocusSessionDao,
    private val dailyBehaviourDao: DailyBehaviourDao,
    private val supabaseService: com.example.t1.data.remote.SupabaseService,
    private val t1NotificationManager: T1NotificationManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val cachedProfile = userRepository.cachedProfileFlow.firstOrNull()
        val userId = authRepository.currentUserIdSync
            ?: cachedProfile?.id
            ?: return Result.success()

        // 0. Focus Fight Incoming Challenge Check (even when app is completely closed!)
        try {
            val pendingFight = supabaseService.getPendingFightForUser(userId)
            if (pendingFight?.id != null) {
                val fightId = pendingFight.id
                val isDismissed = t1NotificationManager.isFightDismissed(fightId)
                val isExpired = supabaseService.isFightExpired(pendingFight.createdAt, maxAgeMinutes = 3)
                if (!isDismissed && !isExpired) {
                    val challengerProfile = supabaseService.getAllProfiles().firstOrNull { it.id == pendingFight.challengerId }
                    val challengerName = challengerProfile?.displayName?.takeIf { it.isNotBlank() } ?: challengerProfile?.username ?: "Fighter"
                    t1NotificationManager.postNotification(
                        id = fightId.hashCode(),
                        title = "⚔️ Focus Fight Challenge!",
                        text = "@$challengerName challenged you to a ${pendingFight.durationMinutes}m focus duel!",
                        channelId = T1NotificationManager.CHANNEL_BATTLE_CHALLENGES,
                        type = "focus_fight_challenge",
                        navigateTab = "BATTLE",
                        fightId = fightId
                    )
                }
            }
        } catch (e: Exception) {
            // Non-critical background network error
        }

        val today = LocalDate.now()
        val zoneId = ZoneId.systemDefault()
        val startOfDay = today.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = today.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

        val todaySessionsCount = focusSessionDao.getFocusSessionCountForDay(userId, startOfDay, endOfDay)
        val profile = userRepository.cachedProfileFlow.firstOrNull()

        val currentHour = LocalTime.now().hour
        val currentMinute = LocalTime.now().minute

        // 1. Daily Reminder (Only if user has NOT completed a session today)
        if (todaySessionsCount == 0) {
            when (currentHour) {
                8, 9 -> {
                    t1NotificationManager.postNotification(
                        id = 2001,
                        title = "Start your day",
                        text = "Start your day with a higher Focus Score.",
                        channelId = T1NotificationManager.CHANNEL_NORMAL,
                        type = "daily_morning"
                    )
                }
                13, 14 -> {
                    t1NotificationManager.postNotification(
                        id = 2002,
                        title = "Keep focus",
                        text = "You're still on track. Complete one focus session today.",
                        channelId = T1NotificationManager.CHANNEL_NORMAL,
                        type = "daily_afternoon"
                    )
                }
                19, 20 -> {
                    t1NotificationManager.postNotification(
                        id = 2003,
                        title = "Focus Score",
                        text = "Today's Focus Score isn't calculated yet.",
                        channelId = T1NotificationManager.CHANNEL_NORMAL,
                        type = "daily_evening"
                    )
                }
                22 -> {
                    t1NotificationManager.postNotification(
                        id = 2004,
                        title = "Streak protection",
                        text = "One session now can protect your streak.",
                        channelId = T1NotificationManager.CHANNEL_NORMAL,
                        type = "daily_night"
                    )
                }
            }
        }

        // 2. Streak Safeguard Reminder (Late evening alert if streak exists but sessions = 0)
        if (todaySessionsCount == 0 && profile != null && profile.streak > 0 && currentHour >= 18 && currentHour <= 21) {
            if (profile.streak >= 30) {
                t1NotificationManager.postNotification(
                    id = 2005,
                    title = "Streak Safeguard",
                    text = "Don't lose your ${profile.streak}-day streak. You're closer than ever.",
                    channelId = T1NotificationManager.CHANNEL_CRITICAL,
                    type = "streak_critical"
                )
            } else {
                t1NotificationManager.postNotification(
                    id = 2006,
                    title = "Streak Safeguard",
                    text = "🔥 Your ${profile.streak}-day streak is still alive. Complete just one focus session today.",
                    channelId = T1NotificationManager.CHANNEL_CRITICAL,
                    type = "streak_critical"
                )
            }
        }

        // 3. Missed Usual Focus Time
        if (todaySessionsCount == 0) {
            val usualFocusHour = 20 // Default to 8:00 PM if no history
            if (currentHour == usualFocusHour && currentMinute >= 30 && currentMinute <= 59) {
                t1NotificationManager.postNotification(
                    id = 2007,
                    title = "Focus Time",
                    text = "Looks like you missed your usual focus time. Ready for one session?",
                    channelId = T1NotificationManager.CHANNEL_NORMAL,
                    type = "focus_reminder"
                )
            }
        }

        // 4. Real-time App Usage Alerts (Usage stats integration)
        val usageResult = usageRepository.getTodayUsage()
        if (usageResult.isSuccess) {
            val todayUsage = usageResult.getOrNull()
            if (todayUsage != null) {
                // A. Screen Time Limit crossed 6 hours (21,600,000 ms)
                if (todayUsage.totalScreenTimeMs > 6 * 60 * 60 * 1000) {
                    t1NotificationManager.postNotification(
                        id = 2008,
                        title = "Screen Time Warning",
                        text = "Your screen time crossed 6 hours. Focus Score may decrease.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "screen_time"
                    )
                }

                // B. YouTube Binge check (YouTube app package: com.google.android.youtube)
                val youtubeTime = todayUsage.foregroundSessions
                    .filter { it.packageName == "com.google.android.youtube" }
                    .sumOf { it.durationMs }
                if (youtubeTime > 2 * 60 * 60 * 1000) { // 2 hours
                    t1NotificationManager.postNotification(
                        id = 2009,
                        title = "YouTube Binge",
                        text = "You've watched videos for 2 hours. Want to take a short break?",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "youtube_binge"
                    )
                }

                // C. Social Spike (social package names time > 3x daily average or 1.5 hours)
                val socialPackages = setOf(
                    "com.instagram.android",
                    "com.facebook.katana",
                    "com.twitter.android",
                    "com.zhiliaoapp.musically", // TikTok
                    "com.snapchat.android"
                )
                val todaySocialTime = todayUsage.foregroundSessions
                    .filter { socialPackages.contains(it.packageName) }
                    .sumOf { it.durationMs }

                // Fetch historical average for social time
                val pastWeekStart = today.minusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE)
                val pastWeekEnd = today.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
                val history = dailyBehaviourDao.getForRange(userId, pastWeekStart, pastWeekEnd)
                val avgSocialTime = if (history.isNotEmpty()) history.map { it.socialTimeMs }.average() else 0.0

                val isSocialSpike = if (avgSocialTime > 0) {
                    todaySocialTime > 3 * avgSocialTime
                } else {
                    todaySocialTime > 1.5 * 60 * 60 * 1000 // Fallback to 1.5 hours
                }

                if (isSocialSpike) {
                    t1NotificationManager.postNotification(
                        id = 2010,
                        title = "Social Spike",
                        text = "Your social usage is already 3× higher than usual.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "social_spike"
                    )
                }

                // D. Late Night Scrolling warning
                if (currentHour >= 23 || currentHour < 3) {
                    // Check if user has active screen time in the late night window
                    val isScrolling = todayUsage.foregroundSessions
                        .filter { socialPackages.contains(it.packageName) || it.packageName == "com.google.android.youtube" }
                        .any { it.endTime > System.currentTimeMillis() - 15 * 60 * 1000 } // active within last 15 min

                    if (isScrolling) {
                        t1NotificationManager.postNotification(
                            id = 2011,
                            title = "Late Night Scrolling",
                            text = "Late-night scrolling detected. Sleeping now could protect tomorrow's Focus Score.",
                            channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                            type = "late_night"
                        )
                    }
                }
            }
        }

        return Result.success()
    }
}
