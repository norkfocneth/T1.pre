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
    fun getBatteryOptimizationState(): BatteryOptimizationState {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return BatteryOptimizationState.Unknown
        return if (powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            BatteryOptimizationState.Unrestricted
        } else {
            BatteryOptimizationState.Optimized
        }
    }

    fun getRequestIntent(): Intent {
        return Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
