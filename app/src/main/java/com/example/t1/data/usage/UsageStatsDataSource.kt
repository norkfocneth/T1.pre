package com.example.t1.data.usage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.t1.domain.model.usage.DailyUsageData
import com.example.t1.domain.model.usage.ForegroundSession
import com.example.t1.util.T1Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageStatsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

    fun queryDailyUsage(date: LocalDate): DailyUsageData {
        val zoneId = ZoneId.systemDefault()
        val startOfDayMs = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDayMs = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

        if (usageStatsManager == null) {
            return emptyDailyUsage(date)
        }

        try {
            val events = usageStatsManager.queryEvents(startOfDayMs, endOfDayMs) ?: return emptyDailyUsage(date)
            val eventList = mutableListOf<LightweightUsageEvent>()
            val tempEvent = UsageEvents.Event()
            while (events.hasNextEvent()) {
                events.getNextEvent(tempEvent)
                eventList.add(
                    LightweightUsageEvent(
                        packageName = tempEvent.packageName ?: "",
                        timestamp = tempEvent.timeStamp,
                        eventType = tempEvent.eventType
                    )
                )
            }

            // Sort events by timestamp to guarantee chronological processing
            eventList.sortBy { it.timestamp }

            val sessions = mutableListOf<ForegroundSession>()
            var activeApp: String? = null
            var activeAppStart: Long = 0L
            var unlockCount = 0
            var appOpenCount = 0
            var firstAppOpenTime: Long? = null
            var lastAppCloseTime: Long? = null

            for (event in eventList) {
                when (event.eventType) {
                    1 -> { // ACTIVITY_RESUMED (value = 1)
                        appOpenCount++
                        if (firstAppOpenTime == null) {
                            firstAppOpenTime = event.timestamp
                        }
                        lastAppCloseTime = event.timestamp

                        if (activeApp != null && activeApp != event.packageName) {
                            // Close existing session
                            val duration = event.timestamp - activeAppStart
                            if (duration > 0) {
                                sessions.add(
                                    ForegroundSession(
                                        packageName = activeApp,
                                        startTime = activeAppStart,
                                        endTime = event.timestamp,
                                        durationMs = duration
                                    )
                                )
                            }
                        }
                        activeApp = event.packageName
                        activeAppStart = event.timestamp
                    }
                    2 -> { // ACTIVITY_PAUSED (value = 2)
                        if (activeApp == event.packageName) {
                            val duration = event.timestamp - activeAppStart
                            if (duration > 0) {
                                sessions.add(
                                    ForegroundSession(
                                        packageName = activeApp,
                                        startTime = activeAppStart,
                                        endTime = event.timestamp,
                                        durationMs = duration
                                    )
                                )
                            }
                            activeApp = null
                        }
                        lastAppCloseTime = event.timestamp
                    }
                    16 -> { // KEYGUARD_DISMISSED (value = 16)
                        unlockCount++
                    }
                }
            }

            // Close trailing active session if present at end of window
            if (activeApp != null) {
                val endLimit = Math.min(System.currentTimeMillis(), endOfDayMs)
                val duration = endLimit - activeAppStart
                if (duration > 0) {
                    sessions.add(
                        ForegroundSession(
                            packageName = activeApp,
                            startTime = activeAppStart,
                            endTime = endLimit,
                            durationMs = duration
                        )
                    )
                }
            }

            val totalScreenTimeMs = sessions.sumOf { it.durationMs }

            return DailyUsageData(
                date = date,
                totalScreenTimeMs = totalScreenTimeMs,
                unlockCount = unlockCount,
                appOpenCount = appOpenCount,
                foregroundSessions = sessions,
                firstAppOpenTime = firstAppOpenTime,
                lastAppCloseTime = lastAppCloseTime,
                collectionTimestamp = System.currentTimeMillis(),
                isVerified = true
            )
        } catch (e: SecurityException) {
            T1Logger.e("SecurityException querying UsageEvents - permission revoked?", e)
            return emptyDailyUsage(date)
        } catch (e: Exception) {
            T1Logger.e("Error querying daily usage stats", e)
            return emptyDailyUsage(date)
        }
    }

    private fun emptyDailyUsage(date: LocalDate): DailyUsageData {
        return DailyUsageData(
            date = date,
            totalScreenTimeMs = 0L,
            unlockCount = 0,
            appOpenCount = 0,
            foregroundSessions = emptyList(),
            firstAppOpenTime = null,
            lastAppCloseTime = null,
            collectionTimestamp = System.currentTimeMillis(),
            isVerified = false
        )
    }

    private data class LightweightUsageEvent(
        val packageName: String,
        val timestamp: Long,
        val eventType: Int
    )
}
