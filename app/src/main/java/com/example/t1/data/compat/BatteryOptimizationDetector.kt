package com.example.t1.data.compat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class BatteryOptimizationState {
    object Optimized : BatteryOptimizationState()
    object Unrestricted : BatteryOptimizationState()
    object Unknown : BatteryOptimizationState()
}

@Singleton
class BatteryOptimizationDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return true
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun getBatteryOptimizationState(): BatteryOptimizationState {
        return if (isIgnoringBatteryOptimizations()) {
            BatteryOptimizationState.Unrestricted
        } else {
            BatteryOptimizationState.Optimized
        }
    }

    fun getRequestIntent(): Intent {
        return Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun openOptimizationSettings(ctx: Context) {
        try {
            val directIntent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ctx.startActivity(directIntent)
        } catch (_: Exception) {
            try {
                val listIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                ctx.startActivity(listIntent)
            } catch (_: Exception) {
                try {
                    val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    ctx.startActivity(appSettingsIntent)
                } catch (_: Exception) {}
            }
        }
    }
}
