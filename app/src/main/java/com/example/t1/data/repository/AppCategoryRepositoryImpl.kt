package com.example.t1.data.repository

import com.example.t1.domain.behaviour.AppCategoryEngine
import com.example.t1.domain.model.behaviour.AppCategory
import com.example.t1.domain.repository.AppCategoryRepository
import com.example.t1.domain.repository.EngineCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCategoryRepositoryImpl @Inject constructor(
    private val appCategoryEngine: AppCategoryEngine
) : AppCategoryRepository {

    // Dynamic package overrides for precision
    private val overrides = mapOf(
        // Communication (half-positive weight: +0.10)
        // These apps were previously PRODUCTIVITY but are now COMMUNICATION
        // to prevent score inflation from keeping email/chat open all day.
        "com.slack" to EngineCategory.COMMUNICATION,
        "com.microsoft.teams" to EngineCategory.COMMUNICATION,
        "us.zoom.videomeetings" to EngineCategory.COMMUNICATION,
        "com.zoom.videomeetings" to EngineCategory.COMMUNICATION,
        "com.google.android.gm" to EngineCategory.COMMUNICATION,             // Gmail
        "com.microsoft.office.outlook" to EngineCategory.COMMUNICATION,      // Outlook
        "com.google.android.apps.meetings" to EngineCategory.COMMUNICATION,  // Google Meet
        "com.google.android.apps.tachyon" to EngineCategory.COMMUNICATION,   // Google Duo/Meet
        "com.google.android.apps.dynamite" to EngineCategory.COMMUNICATION,  // Google Chat
        "com.skype.raider" to EngineCategory.COMMUNICATION,
        "com.viber.voip" to EngineCategory.COMMUNICATION,
        "com.imo.android.imoim" to EngineCategory.COMMUNICATION,

        // Communication (messaging apps)
        "com.whatsapp" to EngineCategory.COMMUNICATION,
        "com.whatsapp.w4b" to EngineCategory.COMMUNICATION,
        "org.telegram.messenger" to EngineCategory.COMMUNICATION,
        "org.thunderdog.challegram" to EngineCategory.COMMUNICATION,
        "com.discord" to EngineCategory.COMMUNICATION,

        // Utilities (Neutral)
        "com.android.settings" to EngineCategory.UTILITY,
        "com.android.camera" to EngineCategory.UTILITY,
        "com.sec.android.app.camera" to EngineCategory.UTILITY,
        "com.google.android.apps.photos" to EngineCategory.UTILITY,
        "com.sec.android.gallery3d" to EngineCategory.UTILITY
    )

    override fun getCategory(packageName: String): EngineCategory {
        // 1. Check direct overrides first
        val overridden = overrides[packageName]
        if (overridden != null) return overridden

        // 2. Fallback to default AppCategoryEngine mapping
        val legacyCategory = appCategoryEngine.getCategory(packageName)
        return when (legacyCategory) {
            AppCategory.PRODUCTIVITY,
            AppCategory.DEVELOPMENT -> EngineCategory.PRODUCTIVITY
            
            AppCategory.EDUCATION -> EngineCategory.EDUCATION

            AppCategory.COMMUNICATION -> EngineCategory.COMMUNICATION
            
            AppCategory.ENTERTAINMENT -> EngineCategory.ENTERTAINMENT
            
            AppCategory.SOCIAL -> EngineCategory.SOCIAL
            
            AppCategory.FINANCE,
            AppCategory.HEALTH,
            AppCategory.UTILITIES,
            AppCategory.SHOPPING,
            AppCategory.NAVIGATION,
            AppCategory.SYSTEM,
            AppCategory.MEDIA,
            AppCategory.PHOTOGRAPHY,
            AppCategory.OTHER -> EngineCategory.UTILITY
        }
    }
}
