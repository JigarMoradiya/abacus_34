package com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.home.NumberSequencePuzzleHomeJetpackScreen
import com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.play.NumberSequencePuzzleJetpackScreen
import com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.viewmodels.NumberSequencePuzzleViewModel
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.utils.FreemiumManager

@Composable
fun NumberSequencePuzzleHomeRoute(
    navController: NavHostController,
    homeActivityViewModel: HomeActivityViewModel,
    onPuzzleSelect: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()
    val isSubscribed = homeActivityViewModel.isPurchasedForModule(purchasedSKU)
    var showPaywall by remember { mutableStateOf(false) }

    NumberSequencePuzzleHomeJetpackScreen(
        navController = navController,
        isSubscribed = isSubscribed,
        onPuzzleSelect = { type ->
            if (FreemiumManager.numberSequenceGate(isSubscribed, type) == FreemiumManager.GateResult.ALLOW) {
                onPuzzleSelect(type)
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
fun NumberSequencePuzzlePlayRoute(
    navController: NavHostController,
    gridSize: Int,
) {
    val viewModel: NumberSequencePuzzleViewModel = hiltViewModel()
    NumberSequencePuzzleJetpackScreen(
        navController = navController,
        gridSize = gridSize,
        viewModel = viewModel
    )
}
