package com.example.t1.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.t1.MainActivity
import com.example.t1.data.preferences.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class T1NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) {
    companion object {
        const val CHANNEL_CRITICAL = "t1_critical"
        const val CHANNEL_BATTLE_CHALLENGES = "t1_battle_challenges_v2"
        const val CHANNEL_IMPORTANT = "t1_important"
        const val CHANNEL_NORMAL = "t1_normal"
        const val CHANNEL_SILENT = "t1_silent"

        private const val PREFS_NAME = "t1_notification_spam_prefs"
        private const val KEY_DAILY_COUNT = "daily_count_"
        private const val KEY_LAST_SENT_TIME = "last_sent_time"
        private const val KEY_SENT_HASHES = "sent_hashes_"
        private const val KEY_DISMISS_COUNT = "dismiss_count_"
    }

    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val criticalChannel = NotificationChannel(
                CHANNEL_CRITICAL,
                "Critical Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts such as streak breaks and focus score warnings."
                enableLights(true)
                enableVibration(true)
            }

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST)
                .build()

            val battleChannel = NotificationChannel(
                CHANNEL_BATTLE_CHALLENGES,
                "Focus Fight Challenges",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority alerts for incoming Focus Fight challenges from friends."
                enableLights(true)
                lightColor = android.graphics.Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setSound(soundUri, audioAttributes)
            }

            val importantChannel = NotificationChannel(
                CHANNEL_IMPORTANT,
                "Important Insights",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Weekly reports, rank changes, and milestone achievements."
            }

            val normalChannel = NotificationChannel(
                CHANNEL_NORMAL,
                "General Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily focus reminders and schedules."
            }

            val silentChannel = NotificationChannel(
                CHANNEL_SILENT,
                "Silent Reports",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Quiet background analytics and reports."
                enableLights(false)
                enableVibration(false)
            }

            manager.createNotificationChannel(criticalChannel)
            manager.createNotificationChannel(battleChannel)
            manager.createNotificationChannel(importantChannel)
            manager.createNotificationChannel(normalChannel)
            manager.createNotificationChannel(silentChannel)
        }
    }

    suspend fun postNotification(
        id: Int,
        title: String,
        text: String,
        channelId: String,
        type: String,
        navigateTab: String? = null,
        fightId: String? = null
    ) {
        // Check global setting in UserPreferences first
        val isEnabled = userPreferences.notificationsEnabledFlow.firstOrNull() ?: true
        if (!isEnabled) {
            android.util.Log.d("T1Notification", "Notifications are disabled in settings. Suppressing: $title")
            return
        }

        if (!hasNotificationPermission()) {
            android.util.Log.w("T1Notification", "POST_NOTIFICATIONS permission not granted. Cannot post.")
            return
        }

        // Apply anti-spam filtering rules (skip critical/battle alerts from restrictions)
        if (channelId != CHANNEL_CRITICAL && channelId != CHANNEL_BATTLE_CHALLENGES && !canPostNotification(type, text)) {
            android.util.Log.d("T1Notification", "Notification blocked by Anti-Spam filters: $title ($type)")
            return
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (navigateTab != null) {
                putExtra("navigate_tab", navigateTab)
                putExtra("preview_screen", "focus_fight")
            }
            if (fightId != null) {
                putExtra("fight_id", fightId)
            }
        }

        val contentIntent = PendingIntent.getActivity(
            context,
            id,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss receiver setup
        val dismissIntent = Intent(context, NotificationDismissedReceiver::class.java).apply {
            putExtra("type", type)
            if (fightId != null) {
                putExtra("fight_id", fightId)
            }
        }
        val pendingDismissIntent = PendingIntent.getBroadcast(
            context,
            type.hashCode(),
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Find standard app launcher icon or default to default android system drawable
        val iconRes = com.example.t1.R.mipmap.ic_launcher

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setDeleteIntent(pendingDismissIntent)
            .setPriority(
                when (channelId) {
                    CHANNEL_BATTLE_CHALLENGES, CHANNEL_CRITICAL -> NotificationCompat.PRIORITY_MAX
                    CHANNEL_IMPORTANT -> NotificationCompat.PRIORITY_DEFAULT
                    CHANNEL_SILENT -> NotificationCompat.PRIORITY_LOW
                    else -> NotificationCompat.PRIORITY_DEFAULT
                }
            )

        // Enhance Focus Fight Challenges for ALL Android devices (Heads-up banner & lockscreen popup)
        if (type == "focus_fight_challenge" && fightId != null) {
            builder.setCategory(NotificationCompat.CATEGORY_EVENT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))

            // Action 1: Direct ACCEPT button on notification
            val acceptIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("navigate_tab", "BATTLE")
                putExtra("preview_screen", "focus_fight")
                putExtra("auto_accept_fight_id", fightId)
            }
            val acceptPendingIntent = PendingIntent.getActivity(
                context,
                (fightId + "_accept").hashCode(),
                acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(
                android.R.drawable.ic_media_play,
                "⚔️ ACCEPT",
                acceptPendingIntent
            )

            // Action 2: Direct DECLINE button on notification
            val declineIntent = Intent(context, NotificationDismissedReceiver::class.java).apply {
                action = "com.example.t1.DECLINE_FIGHT"
                putExtra("type", type)
                putExtra("fight_id", fightId)
                putExtra("notification_id", id)
            }
            val declinePendingIntent = PendingIntent.getBroadcast(
                context,
                (fightId + "_decline").hashCode(),
                declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "DECLINE",
                declinePendingIntent
            )
        }

        // Works across ALL Android versions (Android 8 to Android 15+)
        val notificationManager = NotificationManagerCompat.from(context)
        if (hasNotificationPermission() && notificationManager.areNotificationsEnabled()) {
            try {
                notificationManager.notify(id, builder.build())
                recordNotificationSent(text)
                android.util.Log.i("T1Notification", "Notification successfully posted: $id | $title")
            } catch (e: Exception) {
                android.util.Log.e("T1Notification", "Failed to post notification: ${e.message}", e)
            }
        } else {
            android.util.Log.w("T1Notification", "Notifications blocked by system permissions or channel toggle.")
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    private fun canPostNotification(type: String, text: String): Boolean {
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        // 1. Check daily limit (max 3 notifications/day)
        val dailyCountKey = KEY_DAILY_COUNT + todayStr
        val dailyCount = sharedPrefs.getInt(dailyCountKey, 0)
        if (dailyCount >= 3) {
            android.util.Log.d("T1Notification", "Anti-Spam: Daily limit of 3 reached today.")
            return false
        }

        // 2. Check 2-hour gap limit
        val lastSentTime = sharedPrefs.getLong(KEY_LAST_SENT_TIME, 0L)
        val now = System.currentTimeMillis()
        if (now - lastSentTime < 2 * 60 * 60 * 1000) { // 2 hours in ms
            android.util.Log.d("T1Notification", "Anti-Spam: 2-hour spacing violation.")
            return false
        }

        // 3. Check duplicate notification text today
        val textHash = text.hashCode().toString()
        val sentHashesKey = KEY_SENT_HASHES + todayStr
        val sentHashes = sharedPrefs.getStringSet(sentHashesKey, emptySet()) ?: emptySet()
        if (sentHashes.contains(textHash)) {
            android.util.Log.d("T1Notification", "Anti-Spam: Duplicate text detected today.")
            return false
        }

        // 4. Check dismissal count (muted if dismissed 3 times)
        val dismissCount = sharedPrefs.getInt(KEY_DISMISS_COUNT + type, 0)
        if (dismissCount >= 3) {
            android.util.Log.d("T1Notification", "Anti-Spam: Category $type muted due to 3 dismissals.")
            return false
        }

        // 5. Quiet hours check (11:00 PM to 7:00 AM)
        val currentHour = java.time.LocalTime.now().hour
        if (type != "late_night" && (currentHour >= 23 || currentHour < 7)) {
            android.util.Log.d("T1Notification", "Quiet hours active. Silencing: $type")
            return false
        }

        return true
    }

    private fun recordNotificationSent(text: String) {
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val dailyCountKey = KEY_DAILY_COUNT + todayStr
        val dailyCount = sharedPrefs.getInt(dailyCountKey, 0)
        val textHash = text.hashCode().toString()
        val sentHashesKey = KEY_SENT_HASHES + todayStr
        val sentHashes = sharedPrefs.getStringSet(sentHashesKey, emptySet())?.toMutableSet() ?: mutableSetOf()
        sentHashes.add(textHash)

        sharedPrefs.edit()
            .putInt(dailyCountKey, dailyCount + 1)
            .putLong(KEY_LAST_SENT_TIME, System.currentTimeMillis())
            .putStringSet(sentHashesKey, sentHashes)
            .apply()
    }

    fun cancelNotification(id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }

    fun markFightDismissed(fightId: String) {
        sharedPrefs.edit().putBoolean("dismissed_fight_$fightId", true).apply()
    }

    fun isFightDismissed(fightId: String): Boolean {
        return sharedPrefs.getBoolean("dismissed_fight_$fightId", false)
    }
}
