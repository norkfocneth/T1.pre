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
        // Productivity Messaging & Work
        "com.slack" to EngineCategory.PRODUCTIVITY,
        "com.microsoft.teams" to EngineCategory.PRODUCTIVITY,
        "com.zoom.videomeetings" to EngineCategory.PRODUCTIVITY,
        "com.google.android.gm" to EngineCategory.PRODUCTIVITY, // Gmail
        "com.google.android.apps.meetings" to EngineCategory.PRODUCTIVITY, // Google Meet
        "com.google.android.apps.tachyon" to EngineCategory.PRODUCTIVITY, // Google Meet
        
        // Social Messaging
        "com.whatsapp" to EngineCategory.SOCIAL,
        "com.whatsapp.w4b" to EngineCategory.SOCIAL,
        "org.telegram.messenger" to EngineCategory.SOCIAL,
        "com.discord" to EngineCategory.SOCIAL,
        
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
            
            AppCategory.ENTERTAINMENT,
            AppCategory.MEDIA,
            AppCategory.PHOTOGRAPHY -> EngineCategory.ENTERTAINMENT
            
            AppCategory.SOCIAL,
            AppCategory.COMMUNICATION -> EngineCategory.SOCIAL
            
            AppCategory.FINANCE,
            AppCategory.HEALTH,
            AppCategory.UTILITIES,
            AppCategory.SHOPPING,
            AppCategory.NAVIGATION,
            AppCategory.SYSTEM,
            AppCategory.OTHER -> EngineCategory.UTILITY
        }
    }
}
