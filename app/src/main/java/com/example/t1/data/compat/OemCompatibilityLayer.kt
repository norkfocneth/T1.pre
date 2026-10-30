package com.example.t1.data.compat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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

    /**
     * Attempts to open the OEM-specific autostart or background activity screen
     * across Xiaomi (HyperOS/MIUI), Samsung, Oppo, Realme, Vivo, and OnePlus.
     */
    fun openOemBackgroundSettings(context: Context): Boolean {
        val oem = (Build.MANUFACTURER ?: "").lowercase().trim()
        val intents = mutableListOf<Intent>()

        when {
            oem.contains("xiaomi") || oem.contains("redmi") || oem.contains("poco") -> {
                intents.add(
                    Intent().setComponent(
                        ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"
                        )
                    )
                )
                intents.add(Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT))
            }
            oem.contains("samsung") -> {
                intents.add(
                    Intent().setComponent(
                        ComponentName(
                            "com.samsung.android.lool",
                            "com.samsung.android.sm.ui.battery.BatteryActivity"
                        )
                    )
                )
                intents.add(
                    Intent().setComponent(
                        ComponentName(
                            "com.samsung.android.sm",
                            "com.samsung.android.sm.ui.battery.BatteryActivity"
                        )
                    )
                )
            }
            oem.contains("oppo") || oem.contains("realme") -> {
                intents.add(
                    Intent().setComponent(
                        ComponentName(
                            "com.coloros.safecenter",
                            "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                        )
                    )
                )
                intents.add(
                    Intent().setComponent(
                        ComponentName(
                            "com.oppo.safe",
                            "com.oppo.safe.permission.startup.StartupAppListActivity"
                        )
                    )
                )
            }
            oem.contains("vivo") -> {
                intents.add(
                    Intent().setComponent(
                        ComponentName(
                            "com.iqoo.secure",
                            "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                        )
                    )
                )
                intents.add(
                    Intent().setComponent(
                        ComponentName(
                            "com.vivo.permissionmanager",
                            "com.vivo.permissionmanager.activity.PurviewTabActivity"
                        )
                    )
                )
            }
            oem.contains("oneplus") -> {
                intents.add(
                    Intent().setComponent(
                        ComponentName(
                            "com.oneplus.security",
                            "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
                        )
                    )
                )
            }
        }

        // Generic Android Battery Optimization Request
        intents.add(
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        )

        // General App Info Details
        intents.add(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        )

        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            } catch (_: Exception) {}
        }
        return false
    }
}
