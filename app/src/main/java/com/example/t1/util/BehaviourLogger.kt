package com.example.t1.util

import android.util.Log
import com.example.t1.BuildConfig
import java.time.LocalDate

object BehaviourLogger {
    private const val TAG = "T1_Behaviour"

    fun logCollectionStarted() {
        Log.i(TAG, "Behaviour aggregation pipeline started.")
    }

    fun logCollectionFinished(date: LocalDate, topAppsCount: Int) {
        Log.i(TAG, "Behaviour processing finished for date: $date. Processed top $topAppsCount apps.")
    }

    fun logCategoryMappingCompleted(count: Int) {
        Log.d(TAG, "Mapped categories for $count apps.")
    }

    fun logRoomSaveCompleted(date: LocalDate) {
        Log.i(TAG, "Saved behavior summary to Room cache for date: $date")
    }

    fun logCollectionFailed(reason: String) {
        Log.e(TAG, "Behaviour collection failed: $reason")
    }

    fun logCategoryMapped(packageName: String, category: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Mapped package $packageName to category $category")
        }
    }
}
