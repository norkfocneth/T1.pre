package com.example.t1.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
        type: String
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

        // Apply anti-spam filtering rules (skip critical security alerts from restrictions if needed)
        if (channelId != CHANNEL_CRITICAL && !canPostNotification(type, text)) {
            android.util.Log.d("T1Notification", "Notification blocked by Anti-Spam filters: $title ($type)")
            return
        }

        val contentIntent = PendingIntent.getActivity(
            context,
            id,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss receiver setup
        val dismissIntent = Intent(context, NotificationDismissedReceiver::class.java).apply {
            putExtra("type", type)
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
                    CHANNEL_CRITICAL -> NotificationCompat.PRIORITY_HIGH
                    CHANNEL_IMPORTANT -> NotificationCompat.PRIORITY_DEFAULT
                    CHANNEL_SILENT -> NotificationCompat.PRIORITY_LOW
                    else -> NotificationCompat.PRIORITY_DEFAULT
                }
            )

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(id, builder.build())
                recordNotificationSent(text)
            }
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
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
}
