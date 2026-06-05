package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.home.SudokuHomeScreen
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.home.SudokuHomeViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.SudokuPlayScreen
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPlayViewModel
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.utils.FreemiumManager

@Composable
fun SudokuHomeRoute(
    navController: NavHostController,
    homeActivityViewModel: HomeActivityViewModel,
    onStartPlay: (size: String, difficulty: String, isNewPuzzle: Boolean) -> Unit,
) {
    val viewModel: SudokuHomeViewModel = hiltViewModel()
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    var showPaywall by remember { mutableStateOf(false) }

    SudokuHomeScreen(
        viewModel = viewModel,
        navController = navController,
        onStart = {
            if (FreemiumManager.gameGate(isSubscribed) == FreemiumManager.GateResult.ALLOW) {
                val state = viewModel.uiState.value
                onStartPlay(
                    state.selectedSizeFinal.name,
                    state.selectedDifficultyFinal.name,
                    state.isNewGame
                )
            } else {
                showPaywall = true
            }
        }
    )

    if (showPaywall) {
        FreemiumPaywallBottomSheet(
            onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
            onDismiss = { showPaywall = false }
        )
    }
}

@Composable
fun SudokuPlayRoute(
    navController: NavHostController,
) {
    val viewModel: SudokuPlayViewModel = hiltViewModel()
    SudokuPlayScreen(
        navController = navController,
        vm = viewModel
    )
}
