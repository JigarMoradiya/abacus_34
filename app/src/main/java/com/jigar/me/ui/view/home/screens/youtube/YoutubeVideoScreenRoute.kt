package com.jigar.me.ui.view.home.screens.youtube

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.dialogs.ParentalGateDialog
import com.jigar.me.ui.view.home.screens.youtube.components.YoutubeVideoGrid
import com.jigar.me.ui.view.home.screens.youtube.viewmodels.YoutubeVideoViewModel
import com.jigar.me.utils.ParentalGateSessionCache

@Composable
fun YoutubeVideoScreenRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: YoutubeVideoViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showURLGate by remember { mutableStateOf(false) }
    var pendingUrlAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    // Tracked locally (not via ParentalGateSessionCache) because opening a video
    // backgrounds the app, which resets that session cache -- relying on it here
    // would re-ask the gate on every single video tap instead of once per visit.
    var gateResolvedThisVisit by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!gateResolvedThisVisit) showURLGate = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            BackButtonWithText(
                title = stringResource(R.string.video_tutorials),
                onBackClick = onBackClick
            )
            YoutubeVideoGrid(
                videos = uiState.videoList,
                gateResolvedThisVisit = gateResolvedThisVisit,
                onRequestGate = { action ->
                    pendingUrlAction = action
                    showURLGate = true
                }
            )
        }

        // Rendered at this outer level (not inside YoutubeVideoGrid) so the scrim
        // covers the whole screen, including the header bar above the grid.
        if (showURLGate) {
            ParentalGateDialog(
                onPassed = {
                    showURLGate = false
                    gateResolvedThisVisit = true
                    ParentalGateSessionCache.markPassed()
                    pendingUrlAction?.invoke()
                    pendingUrlAction = null
                },
                onCancelled = { showURLGate = false; pendingUrlAction = null }
            )
        }
    }
}
