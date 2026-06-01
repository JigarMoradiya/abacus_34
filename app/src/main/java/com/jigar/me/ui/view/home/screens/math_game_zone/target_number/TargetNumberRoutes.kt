package com.jigar.me.ui.view.home.screens.math_game_zone.target_number

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.components.TargetNumberViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.viewmodel.TargetNumberPlayViewModel
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.utils.FreemiumManager

@Composable
fun TargetNumberHomeRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onStartPlay: (level: Int, diff: String) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: TargetNumberViewModel = hiltViewModel()
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()
    val isSubscribed = homeActivityViewModel.isPurchasedForModule(purchasedSKU)
    var showPaywall by remember { mutableStateOf(false) }

    TargetNumberHomeScreen(
        viewModel = viewModel,
        onStartGame = {
            if (FreemiumManager.gameGate(isSubscribed) == FreemiumManager.GateResult.ALLOW) {
                val state = viewModel.uiState.value
                onStartPlay(state.selectedLevel, state.selectedDifficulty.name)
            } else {
                showPaywall = true
            }
        },
        onBackClick = onBackClick
    )

    if (showPaywall) {
        FreemiumPaywallBottomSheet(
            purchasedSKU = purchasedSKU,
            onDismiss = { showPaywall = false }
        )
    }
}

@Composable
fun TargetNumberPlayRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: TargetNumberPlayViewModel = hiltViewModel()
    TargetNumberPlayScreen(
        viewModel = viewModel,
        onBackClick = onBackClick
    )
}
