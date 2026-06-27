package com.example.t1.domain.behaviour

import android.content.Context
import android.content.pm.PackageManager
import com.example.t1.domain.model.behaviour.AppCategory
import com.example.t1.domain.model.behaviour.AppUsageSummary
import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import com.example.t1.domain.model.usage.DailyUsageData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BehaviourProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appCategoryEngine: AppCategoryEngine
) {
    private val labelCache = ConcurrentHashMap<String, String>()
    private val packageManager: PackageManager = context.packageManager

    private fun getApplicationLabel(packageName: String): String {
        val cached = labelCache[packageName]
        if (cached != null) return cached

        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val label = packageManager.getApplicationLabel(appInfo).toString()
            labelCache[packageName] = label
            label
        } catch (e: Exception) {
            // Fallback: format package name suffix
            val segments = packageName.split(".")
            val name = segments.lastOrNull() ?: packageName
            val formatted = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            labelCache[packageName] = formatted
            formatted
        }
    }

    fun process(usageData: DailyUsageData): DailyBehaviourSummary {
        val categoryDurations = mutableMapOf<AppCategory, Long>()
        for (category in AppCategory.values()) {
            categoryDurations[category] = 0L
        }

        val appDurations = mutableMapOf<String, Long>()

        for (session in usageData.foregroundSessions) {
            val category = appCategoryEngine.getCategory(session.packageName)
            val currentDuration = categoryDurations[category] ?: 0L
            categoryDurations[category] = currentDuration + session.durationMs

            val currentAppDuration = appDurations[session.packageName] ?: 0L
            appDurations[session.packageName] = currentAppDuration + session.durationMs
        }

        // Build sorted top app usage list (limited to top 20)
        val topApps = appDurations.entries
            .map { (packageName, durationMs) ->
                AppUsageSummary(
                    packageName = packageName,
                    displayName = getApplicationLabel(packageName),
                    usageDurationMs = durationMs,
                    category = appCategoryEngine.getCategory(packageName)
                )
            }
            .sortedByDescending { it.usageDurationMs }
            .take(20)

        return DailyBehaviourSummary(
            date = usageData.date,
            totalScreenTimeMs = usageData.totalScreenTimeMs,
            socialTimeMs = categoryDurations[AppCategory.SOCIAL] ?: 0L,
            entertainmentTimeMs = categoryDurations[AppCategory.ENTERTAINMENT] ?: 0L,
            productiveTimeMs = categoryDurations[AppCategory.PRODUCTIVITY] ?: 0L,
            educationTimeMs = categoryDurations[AppCategory.EDUCATION] ?: 0L,
            communicationTimeMs = categoryDurations[AppCategory.COMMUNICATION] ?: 0L,
            financeTimeMs = categoryDurations[AppCategory.FINANCE] ?: 0L,
            healthTimeMs = categoryDurations[AppCategory.HEALTH] ?: 0L,
            developmentTimeMs = categoryDurations[AppCategory.DEVELOPMENT] ?: 0L,
            otherTimeMs = categoryDurations[AppCategory.OTHER] ?: 0L,
            unlockCount = usageData.unlockCount,
            appOpenCount = usageData.appOpenCount,
            foregroundSessionCount = usageData.foregroundSessions.size,
            topUsedApps = topApps,
            collectionTimestamp = System.currentTimeMillis()
        )
    }
}
