package com.example.t1.data.repository

import com.example.t1.data.database.dao.AppCategoryOverrideDao
import com.example.t1.data.database.entity.AppCategoryOverrideEntity
import com.example.t1.domain.behaviour.AppCategoryEngine
import com.example.t1.domain.model.behaviour.AppCategory
import com.example.t1.domain.repository.AppCategoryRepository
import com.example.t1.domain.repository.EngineCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCategoryRepositoryImpl @Inject constructor(
    private val appCategoryEngine: AppCategoryEngine,
    private val appCategoryOverrideDao: AppCategoryOverrideDao
) : AppCategoryRepository {

    private val customOverridesCache = ConcurrentHashMap<String, EngineCategory>()

    init {
        // Asynchronously preload user overrides into memory cache
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val overrides = appCategoryOverrideDao.getAllOverrides()
                for (override in overrides) {
                    val category = parseCategory(override.category)
                    if (category != null) {
                        customOverridesCache[override.packageName] = category
                    }
                }
            } catch (e: Exception) {
                // Ignore initial load failure
            }
        }
    }

    fun updateCache(overrides: Map<String, EngineCategory>) {
        customOverridesCache.putAll(overrides)
    }

    // ─── Fixed Apps (Locked - Cannot be customized) ───
    private val lockedDistractions = setOf(
        "com.instagram.android",
        "com.facebook.katana",
        "com.facebook.lite",
        "com.snapchat.android",
        "com.netflix.mediaclient",
        "com.zhiliaoapp.musically", // TikTok
        "com.ss.android.ugc.trill", // TikTok
        "com.twitter.android",
        "com.x.android",
        "com.pinterest",
        "com.tinder",
        "com.bumble.app",
        "com.king.candycrushsaga",
        "com.supercell.clashofclans",
        "com.dts.freefireth",
        "com.pubg.imobile"
    )

    // Default package overrides
    private val defaultOverrides = mapOf(
        // Communication (default)
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

        // Messaging
        "com.whatsapp" to EngineCategory.COMMUNICATION,
        "com.whatsapp.w4b" to EngineCategory.COMMUNICATION,
        "org.telegram.messenger" to EngineCategory.COMMUNICATION,
        "org.thunderdog.challegram" to EngineCategory.COMMUNICATION,
        "com.discord" to EngineCategory.COMMUNICATION,

        // Utilities
        "com.android.settings" to EngineCategory.UTILITY,
        "com.android.camera" to EngineCategory.UTILITY,
        "com.sec.android.app.camera" to EngineCategory.UTILITY,
        "com.google.android.apps.photos" to EngineCategory.UTILITY,
        "com.sec.android.gallery3d" to EngineCategory.UTILITY
    )

    override fun getCategory(packageName: String): EngineCategory {
        // 1. Locked distractions always take precedence (anti-cheat)
        if (lockedDistractions.contains(packageName)) {
            val legacy = appCategoryEngine.getCategory(packageName)
            return if (legacy == AppCategory.ENTERTAINMENT) EngineCategory.ENTERTAINMENT else EngineCategory.SOCIAL
        }

        // 2. User-customized classification from Room cache
        val customOverride = customOverridesCache[packageName]
        if (customOverride != null) {
            return customOverride
        }

        // 3. Check default static overrides
        val defaultOverride = defaultOverrides[packageName]
        if (defaultOverride != null) {
            return defaultOverride
        }

        // 4. Fallback to default AppCategoryEngine mapping
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

    private fun parseCategory(name: String): EngineCategory? {
        return try {
            EngineCategory.valueOf(name.uppercase())
        } catch (e: Exception) {
            null
        }
    }
}
