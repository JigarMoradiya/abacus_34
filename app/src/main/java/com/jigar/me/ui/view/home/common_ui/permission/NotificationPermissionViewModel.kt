package com.jigar.me.ui.view.home.common_ui.permission

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationPermissionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationPermissionUiState>(
        NotificationPermissionUiState.Idle
    )
    val uiState: StateFlow<NotificationPermissionUiState> = _uiState.asStateFlow()

    fun onCheckInitial(isRuntimePermissionRequired: Boolean, isAlreadyGranted: Boolean) {
        _uiState.value = when {
            !isRuntimePermissionRequired -> NotificationPermissionUiState.NotRequired
            isAlreadyGranted -> NotificationPermissionUiState.Granted
            else -> NotificationPermissionUiState.Requesting
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.value = if (granted) {
            NotificationPermissionUiState.Granted
        } else {
            NotificationPermissionUiState.Denied
        }
    }
}
