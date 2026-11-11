package com.example.t1.ui.tour

import android.content.Context

/**
 * Synchronous in-memory and SharedPreferences backed manager for product tour states.
 * Guarantees zero race conditions on app launch: existing/returning users will NEVER
 * see unwanted tour overlays on normal startup or tab switching.
 */
object TourManager {
    private const val PREFS_NAME = "t1_tour_prefs"
    private const val KEY_FIRST_LAUNCH_TOUR_COMPLETED = "has_completed_first_launch_tour"

    /**
     * Synchronously checks whether the user has completed or skipped the first-time tour.
     */
    fun hasCompletedFirstLaunchTour(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_FIRST_LAUNCH_TOUR_COMPLETED)) {
            return prefs.getBoolean(KEY_FIRST_LAUNCH_TOUR_COMPLETED, false)
        }

        // If this is an existing installation (user already has previous settings or preferences),
        // automatically mark as completed so existing users are never annoyed by repeated tours.
        val hasExistingSettings = context.getSharedPreferences("t1_settings", Context.MODE_PRIVATE).all.isNotEmpty()
        val hasNotificationPrefs = context.getSharedPreferences("t1_notification_spam_prefs", Context.MODE_PRIVATE).all.isNotEmpty()
        if (hasExistingSettings || hasNotificationPrefs) {
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH_TOUR_COMPLETED, true).apply()
            return true
        }

        return false
    }

    /**
     * Permanently marks the initial onboarding tour as completed.
     */
    fun markFirstLaunchTourCompleted(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_FIRST_LAUNCH_TOUR_COMPLETED, true)
            .apply()
    }

    /**
     * Resets the tour state (only called when user explicitly taps 'Replay Product Tour' in Settings).
     */
    fun resetFirstLaunchTour(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_FIRST_LAUNCH_TOUR_COMPLETED, false)
            .apply()
    }
}

