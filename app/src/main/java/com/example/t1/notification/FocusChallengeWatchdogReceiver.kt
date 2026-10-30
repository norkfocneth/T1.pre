package com.example.t1.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import com.example.t1.data.database.dao.UserProfileDao
import com.example.t1.data.remote.SupabaseService
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.util.T1Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Universal Watchdog Receiver that runs across all Android devices (Xiaomi, Samsung, OnePlus, Vivo, Oppo, Realme, Pixel).
 * Revives background sync and polls pending Focus Fight challenges even in Deep Doze or when the app is swiped away.
 */
@AndroidEntryPoint
class FocusChallengeWatchdogReceiver : BroadcastReceiver() {

    @Inject
    lateinit var supabaseService: SupabaseService

    @Inject
    lateinit var t1NotificationManager: T1NotificationManager

    @Inject
    lateinit var userProfileDao: UserProfileDao

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "t1:FocusWatchdogWakeLock"
        )
        wakeLock?.acquire(8000L) // 8-second safety timeout

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {

                // 2. Direct high-speed challenge query
                val userId = userProfileDao.getProfile()?.id
                    ?: authRepository.currentUserIdSync

                if (userId != null) {
                    val pendingFight = supabaseService.getPendingFightForUser(userId)
                    if (pendingFight?.id != null) {
                        val fightId = pendingFight.id
                        val isDismissed = t1NotificationManager.isFightDismissed(fightId)
                        val isExpired = supabaseService.isFightExpired(pendingFight.createdAt, maxAgeMinutes = 3)

                        if (!isDismissed && !isExpired) {
                            val allProfiles = supabaseService.getAllProfiles()
                            val challengerProfile = allProfiles.firstOrNull { it.id == pendingFight.challengerId }
                            val challengerName = challengerProfile?.displayName?.takeIf { it.isNotBlank() }
                                ?: challengerProfile?.username
                                ?: "A friend"

                            t1NotificationManager.postNotification(
                                id = fightId.hashCode(),
                                title = "⚔️ Focus Fight Challenge!",
                                text = "@$challengerName challenged you to a ${pendingFight.durationMinutes}m duel!",
                                channelId = T1NotificationManager.CHANNEL_BATTLE_CHALLENGES,
                                type = "focus_fight_challenge",
                                navigateTab = "BATTLE",
                                fightId = fightId
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                T1Logger.w("FocusChallengeWatchdog check error: ${e.message}")
            } finally {
                // 3. Schedule next heartbeat watchdog (every 45s for near-instant responsiveness)
                scheduleWatchdog(context.applicationContext, 45_000L)
                try {
                    wakeLock?.release()
                } catch (_: Exception) {}
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val WATCHDOG_REQUEST_CODE = 9005
        const val ACTION_WATCHDOG = "com.example.t1.ACTION_FOCUS_WATCHDOG"

        fun scheduleWatchdog(context: Context, delayMillis: Long = 45_000L) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, FocusChallengeWatchdogReceiver::class.java).apply {
                action = ACTION_WATCHDOG
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                WATCHDOG_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerAt = System.currentTimeMillis() + delayMillis
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                }
            } catch (e: Exception) {
                T1Logger.w("Failed to schedule watchdog alarm: ${e.message}")
            }
        }

        fun cancel(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, FocusChallengeWatchdogReceiver::class.java).apply {
                action = ACTION_WATCHDOG
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                WATCHDOG_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}

