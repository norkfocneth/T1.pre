package com.example.t1.data.compat

import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

data class OemProfile(
    val manufacturer: String,
    val hasAggressiveTaskKiller: Boolean,
    val requiresManualBatteryOptimizationGuidance: Boolean
)

@Singleton
class OemCompatibilityLayer @Inject constructor() {

    fun getOemProfile(): OemProfile {
        val rawManufacturer = Build.MANUFACTURER ?: "unknown"
        val manufacturer = rawManufacturer.lowercase().trim()
        val hasAggressiveTaskKiller = when (manufacturer) {
            "xiaomi", "redmi", "poco", "oneplus", "oppo", "vivo", "realme", "huawei" -> true
            else -> false
        }
        val requiresManualBatteryOptimizationGuidance = when (manufacturer) {
            "samsung", "xiaomi", "redmi", "poco", "oneplus", "oppo", "vivo", "realme" -> true
            else -> false
        }
        return OemProfile(
            manufacturer = rawManufacturer,
            hasAggressiveTaskKiller = hasAggressiveTaskKiller,
            requiresManualBatteryOptimizationGuidance = requiresManualBatteryOptimizationGuidance
        )
    }
}
