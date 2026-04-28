package com.jigar.me.ui.view.home.common_ui.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun NotificationPermissionHandler(
    viewModel: NotificationPermissionViewModel = hiltViewModel(),
    onResult: (granted: Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(granted)
        onResult(granted)
    }

    LaunchedEffect(Unit) {
        val isRuntimePermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        val isAlreadyGranted = if (isRuntimePermissionRequired) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        viewModel.onCheckInitial(isRuntimePermissionRequired, isAlreadyGranted)
    }

    LaunchedEffect(uiState) {
        if (uiState is NotificationPermissionUiState.Requesting &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
