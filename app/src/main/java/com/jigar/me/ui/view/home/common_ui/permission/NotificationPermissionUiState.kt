package com.jigar.me.ui.view.home.common_ui.permission

sealed interface NotificationPermissionUiState {
    data object Idle : NotificationPermissionUiState
    data object NotRequired : NotificationPermissionUiState
    data object Requesting : NotificationPermissionUiState
    data object Granted : NotificationPermissionUiState
    data object Denied : NotificationPermissionUiState
}
