package com.example.t1.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TestNotificationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var t1NotificationManager: T1NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: "daily_morning"
        android.util.Log.i("TestNotificationReceiver", "Triggering test notification: $type")

        // Reset spam database so notifications show up immediately during testing
        val sharedPrefs = context.getSharedPreferences("t1_notification_spam_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()

        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            when (type) {
                "daily_morning" -> {
                    t1NotificationManager.postNotification(
                        id = 1001,
                        title = "Start your day",
                        text = "Start your day with a higher Focus Score.",
                        channelId = T1NotificationManager.CHANNEL_NORMAL,
                        type = "daily_morning"
                    )
                }
                "daily_afternoon" -> {
                    t1NotificationManager.postNotification(
                        id = 1002,
                        title = "Keep focus",
                        text = "You're still on track. Complete one focus session today.",
                        channelId = T1NotificationManager.CHANNEL_NORMAL,
                        type = "daily_afternoon"
                    )
                }
                "daily_evening" -> {
                    t1NotificationManager.postNotification(
                        id = 1003,
                        title = "Focus Score",
                        text = "Today's Focus Score isn't calculated yet.",
                        channelId = T1NotificationManager.CHANNEL_NORMAL,
                        type = "daily_evening"
                    )
                }
                "daily_night" -> {
                    t1NotificationManager.postNotification(
                        id = 1004,
                        title = "Streak protection",
                        text = "One session now can protect your streak.",
                        channelId = T1NotificationManager.CHANNEL_NORMAL,
                        type = "daily_night"
                    )
                }
                "weekly_report" -> {
                    t1NotificationManager.postNotification(
                        id = 1005,
                        title = "Weekly Focus Report",
                        text = "Average Focus Score: 74 (+6 this week). Total Focus Sessions: 18. You are ahead of 72% of users.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "weekly_report"
                    )
                }
                "streak_critical" -> {
                    t1NotificationManager.postNotification(
                        id = 1006,
                        title = "Streak Safeguard",
                        text = "🔥 Your 14-day streak is still alive. Complete just one focus session today.",
                        channelId = T1NotificationManager.CHANNEL_CRITICAL,
                        type = "streak_critical"
                    )
                }
                "streak_critical_30" -> {
                    t1NotificationManager.postNotification(
                        id = 1007,
                        title = "Streak Safeguard",
                        text = "Don't lose your 30-day streak. You're closer than ever.",
                        channelId = T1NotificationManager.CHANNEL_CRITICAL,
                        type = "streak_critical"
                    )
                }
                "streak_broken" -> {
                    t1NotificationManager.postNotification(
                        id = 1008,
                        title = "Streak Broken",
                        text = "Let's build it again. Today's a fresh start.",
                        channelId = T1NotificationManager.CHANNEL_CRITICAL,
                        type = "streak_broken"
                    )
                }
                "focus_reminder" -> {
                    t1NotificationManager.postNotification(
                        id = 1009,
                        title = "Focus Time",
                        text = "Looks like you missed your usual focus time. Ready for one session?",
                        channelId = T1NotificationManager.CHANNEL_NORMAL,
                        type = "focus_reminder"
                    )
                }
                "social_spike" -> {
                    t1NotificationManager.postNotification(
                        id = 1010,
                        title = "Social Spike",
                        text = "Your social usage is already 3× higher than usual.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "social_spike"
                    )
                }
                "youtube_binge" -> {
                    t1NotificationManager.postNotification(
                        id = 1011,
                        title = "YouTube Binge",
                        text = "You've watched videos for 2 hours. Want to take a short break?",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "youtube_binge"
                    )
                }
                "productivity_drop" -> {
                    t1NotificationManager.postNotification(
                        id = 1012,
                        title = "Productivity Drop",
                        text = "Today your productivity time is 42% lower than yesterday.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "productivity_drop"
                    )
                }
                "high_productivity" -> {
                    t1NotificationManager.postNotification(
                        id = 1013,
                        title = "High Productivity",
                        text = "Excellent. Today is your most productive day this week.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "high_productivity"
                    )
                }
                "screen_time" -> {
                    t1NotificationManager.postNotification(
                        id = 1014,
                        title = "Screen Time Warning",
                        text = "Your screen time crossed 6 hours. Focus Score may decrease.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "screen_time"
                    )
                }
                "late_night" -> {
                    t1NotificationManager.postNotification(
                        id = 1015,
                        title = "Late Night Scrolling",
                        text = "Late-night scrolling detected. Sleeping now could protect tomorrow's Focus Score.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "late_night"
                    )
                }
                "focus_score_drop" -> {
                    t1NotificationManager.postNotification(
                        id = 1016,
                        title = "Focus Score Dropped",
                        text = "Your Focus Score dropped by 4 today. Social usage increased by 38 minutes.",
                        channelId = T1NotificationManager.CHANNEL_CRITICAL,
                        type = "focus_score_drop"
                    )
                }
                "rank_improved" -> {
                    t1NotificationManager.postNotification(
                        id = 1017,
                        title = "Rank Improved",
                        text = "You moved up 17 places on the leaderboard. Keep going.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "rank_improved"
                    )
                }
                "rank_down" -> {
                    t1NotificationManager.postNotification(
                        id = 1018,
                        title = "Rank Dropped",
                        text = "You dropped 6 places today. One good day can recover it.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "rank_down"
                    )
                }
                "friend_overtook" -> {
                    t1NotificationManager.postNotification(
                        id = 1019,
                        title = "Friend Passed You",
                        text = "Rohan just passed you in your Friends leaderboard.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "friend_overtook"
                    )
                }
                "college_rank" -> {
                    t1NotificationManager.postNotification(
                        id = 1020,
                        title = "College Rank Update",
                        text = "You're now Top 15 in MAIT.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "college_rank"
                    )
                }
                "milestone" -> {
                    t1NotificationManager.postNotification(
                        id = 1021,
                        title = "Milestone Achieved",
                        text = "Congratulations. 100 Focus Sessions completed.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "milestone"
                    )
                }
                "token_reward" -> {
                    t1NotificationManager.postNotification(
                        id = 1022,
                        title = "Tokens Earned",
                        text = "+25 T1 Tokens earned. Reward added to your wallet.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "token_reward"
                    )
                }
                "ai_recommendation" -> {
                    t1NotificationManager.postNotification(
                        id = 1023,
                        title = "AI Coach Update",
                        text = "Your behaviour changed today. AI Coach has new recommendations.",
                        channelId = T1NotificationManager.CHANNEL_IMPORTANT,
                        type = "ai_recommendation"
                    )
                }
                "reset_spam" -> {
                    android.util.Log.i("TestNotificationReceiver", "Anti-Spam counts successfully cleared.")
                }
                else -> {
                    android.util.Log.w("TestNotificationReceiver", "Unknown type: $type")
                }
            }
        }
    }
}
