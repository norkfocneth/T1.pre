package com.example.t1.ui.viewmodel

import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import com.example.t1.domain.permission.UsagePermissionState

sealed class CollectionStatus {
    object Idle : CollectionStatus()
    object Collecting : CollectionStatus()
    object Success : CollectionStatus()
    data class Failed(val error: String) : CollectionStatus()
}

data class DashboardUiState(
    val permissionState: UsagePermissionState = UsagePermissionState.Unknown,
    val collectionStatus: CollectionStatus = CollectionStatus.Idle,
    val todayBehaviour: DailyBehaviourSummary? = null,
    val lastUpdated: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
