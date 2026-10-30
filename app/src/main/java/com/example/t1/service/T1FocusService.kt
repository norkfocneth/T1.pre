package com.example.t1.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.t1.MainActivity
import com.example.t1.R
import com.example.t1.data.database.dao.UserProfileDao
import com.example.t1.data.remote.SupabaseService
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.notification.FocusChallengeWatchdogReceiver
import com.example.t1.notification.T1NotificationManager
import com.example.t1.notification.T1NotificationScheduler
import com.example.t1.util.T1Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class T1FocusService : Service() {

    @Inject
    lateinit var supabaseService: SupabaseService

    @Inject
    lateinit var t1NotificationManager: T1NotificationManager

    @Inject
    lateinit var userProfileDao: UserProfileDao

    @Inject
    lateinit var authRepository: AuthRepository

    private var serviceJob = SupervisorJob()
    private var serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var pollerJob: Job? = null
    private var isScreenOn = true

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    isScreenOn = true
                    T1Logger.d("T1FocusService: Screen turned ON. High-frequency polling active.")
                }
                Intent.ACTION_SCREEN_OFF -> {
                    isScreenOn = false
                    T1Logger.d("T1FocusService: Screen turned OFF. Low-power polling active.")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        T1Logger.i("T1FocusService onCreate: Starting persistent focus fight guardian.")

        // Ensure active CoroutineScope
        if (serviceJob.isCancelled) {
            serviceJob = SupervisorJob()
            serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
        }

        try {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
            registerReceiver(screenReceiver, filter)
        } catch (e: Exception) {
            T1Logger.w("Could not register screen receiver: ${e.message}")
        }

        startServiceInForeground()
        startChallengePoller()

        // Kick off AlarmManager Watchdog for failsafe resilience across OEMs (Xiaomi, Samsung, OnePlus)
        FocusChallengeWatchdogReceiver.scheduleWatchdog(applicationContext, 45_000L)
    }

    private fun startServiceInForeground() {
        createCompanionChannel()

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            9001,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_COMPANION)
            .setContentTitle("T1 Focus Companion")
            .setContentText("Ready for incoming focus duels")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                } else {
                    0
                }
                startForeground(NOTIFICATION_ID, notification, serviceType)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            T1Logger.w("startForeground warning: ${e.message}")
        }
    }

    private fun createCompanionChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_COMPANION,
                "T1 Companion Service",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Keeps T1 connected for instant incoming battle challenges across all devices."
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun startChallengePoller() {
        pollerJob?.cancel()
        pollerJob = serviceScope.launch {
            val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager

            while (isActive) {
                // Adaptive delay: 4s when phone is actively used, 20s when phone is asleep (battery friendly)
                val pollDelay = if (isScreenOn) 4000L else 20000L
                delay(pollDelay)

                // Brief wakeLock ensures network socket finishes if device is in doze/screen off
                val wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "t1:FocusServicePollWakeLock")
                try {
                    wakeLock?.acquire(3500L)

                    // Fetch user ID reliably even from cold-start background process
                    val userId = userProfileDao.getProfile()?.id
                        ?: authRepository.currentUserIdSync
                        ?: continue

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

                            // Trigger High-Priority Heads-Up Alert on ALL devices
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
                } catch (e: Exception) {
                    // Ignore transient network errors
                } finally {
                    try {
                        if (wakeLock?.isHeld == true) {
                            wakeLock.release()
                        }
                    } catch (_: Exception) {}
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (serviceJob.isCancelled) {
            serviceJob = SupervisorJob()
            serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
        }
        if (pollerJob?.isActive != true) {
            startChallengePoller()
        }
        FocusChallengeWatchdogReceiver.scheduleWatchdog(applicationContext, 45_000L)
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        T1Logger.i("T1FocusService onTaskRemoved: App swiped away. Scheduling instant watchdog revival.")
        FocusChallengeWatchdogReceiver.scheduleWatchdog(applicationContext, 1000L)
        T1NotificationScheduler.enqueueFastCheck(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(screenReceiver)
        } catch (_: Exception) {}
        serviceJob.cancel()
        // If system kills service, schedule watchdog to resurrect within 2s
        FocusChallengeWatchdogReceiver.scheduleWatchdog(applicationContext, 2000L)
        T1Logger.i("T1FocusService onDestroy.")
    }

    companion object {
        const val NOTIFICATION_ID = 9002
        const val CHANNEL_COMPANION = "t1_companion_service"

        fun start(context: Context) {
            try {
                val intent = Intent(context, T1FocusService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                T1Logger.w("Failed to start T1FocusService: ${e.message}")
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, T1FocusService::class.java)
                context.stopService(intent)
            } catch (_: Exception) {}
        }
    }
}
