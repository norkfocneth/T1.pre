package com.example.t1.data.permission

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import com.example.t1.domain.permission.UsagePermissionManager
import com.example.t1.domain.permission.UsagePermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsagePermissionManagerImpl @Inject constructor() : UsagePermissionManager {

    override fun checkPermission(context: Context): UsagePermissionState {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return if (mode == AppOpsManager.MODE_ALLOWED) {
            UsagePermissionState.Granted
        } else {
            UsagePermissionState.Denied
        }
    }

    override fun observePermissionState(context: Context): Flow<UsagePermissionState> = flow {
        while (true) {
            emit(checkPermission(context))
            delay(2000)
        }
    }

    override fun openPermissionSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (ex: Exception) {
                // Safe fallback if settings cannot be opened
            }
        }
    }
}
