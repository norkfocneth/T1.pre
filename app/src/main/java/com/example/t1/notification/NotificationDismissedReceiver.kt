package com.example.t1.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationDismissedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: return
        android.util.Log.d("NotificationDismissed", "Notification of type $type dismissed by user.")

        val sharedPrefs = context.getSharedPreferences("t1_notification_spam_prefs", Context.MODE_PRIVATE)
        val key = "dismiss_count_$type"
        val currentCount = sharedPrefs.getInt(key, 0)
        
        sharedPrefs.edit()
            .putInt(key, currentCount + 1)
            .apply()
            
        android.util.Log.d("NotificationDismissed", "Dismissal count for $type incremented to ${currentCount + 1}")
    }
}
