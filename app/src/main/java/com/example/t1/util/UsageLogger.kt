package com.example.t1.util

import android.util.Log
import com.example.t1.BuildConfig
import java.time.LocalDate

object UsageLogger {
    private const val TAG = "T1_UsageStats"

    fun logPermissionGranted() {
        Log.i(TAG, "UsageStats permission granted.")
    }

    fun logPermissionRevoked() {
        Log.w(TAG, "UsageStats permission revoked!")
    }

    fun logCollectionStarted(date: LocalDate) {
        Log.i(TAG, "Starting usage collection for date: $date")
    }

    fun logCollectionCompleted(date: LocalDate, durationMs: Long, sessionsCount: Int) {
        Log.i(TAG, "Usage collection completed for date: $date. Found $sessionsCount sessions in ${durationMs}ms.")
    }

    fun logCollectionFailed(date: LocalDate, reason: String) {
        Log.e(TAG, "Usage collection failed for date: $date. Reason: $reason")
    }

    fun logPackageScanned(packageName: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Scanning package: $packageName")
        }
    }
}
