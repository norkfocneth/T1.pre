package com.example.t1.domain.permission

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface UsagePermissionManager {
    fun checkPermission(context: Context): UsagePermissionState
    fun observePermissionState(context: Context): Flow<UsagePermissionState>
    fun openPermissionSettings(context: Context)
}
