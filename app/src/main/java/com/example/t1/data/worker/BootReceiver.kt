package com.example.t1.data.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.t1.notification.FocusChallengeWatchdogReceiver
import com.example.t1.notification.T1NotificationScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            DailyAnalysisScheduler.schedule(context)
            T1NotificationScheduler.schedule(context)
            FocusChallengeWatchdogReceiver.scheduleWatchdog(context, 10_000L)
        }
    }
}
