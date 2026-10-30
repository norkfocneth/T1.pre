package com.example.t1.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.t1.data.remote.SupabaseService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDismissedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var supabaseService: SupabaseService

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: return
        android.util.Log.d("NotificationDismissed", "Notification of type $type dismissed by user.")

        val sharedPrefs = context.getSharedPreferences("t1_notification_spam_prefs", Context.MODE_PRIVATE)
        val key = "dismiss_count_$type"
        val currentCount = sharedPrefs.getInt(key, 0)

        sharedPrefs.edit()
            .putInt(key, currentCount + 1)
            .apply()

        val notificationId = intent.getIntExtra("notification_id", -1)
        if (notificationId != -1) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
            notificationManager?.cancel(notificationId)
        }

        val fightId = intent.getStringExtra("fight_id")
        if (!fightId.isNullOrBlank()) {
            sharedPrefs.edit()
                .putBoolean("dismissed_fight_$fightId", true)
                .apply()

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    supabaseService.updateFightStatus(fightId, "DECLINED")
                    android.util.Log.i("NotificationDismissed", "Focus fight $fightId marked as DECLINED on dismissal.")
                } catch (e: Exception) {
                    android.util.Log.w("NotificationDismissed", "Failed to decline dismissed fight: ${e.message}")
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
