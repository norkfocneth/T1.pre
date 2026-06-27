package com.example.t1.domain.permission

sealed class UsagePermissionState {
    object Unknown : UsagePermissionState()
    object Loading : UsagePermissionState()
    object Granted : UsagePermissionState()
    object Denied : UsagePermissionState()
    object PermanentlyDenied : UsagePermissionState()
}
