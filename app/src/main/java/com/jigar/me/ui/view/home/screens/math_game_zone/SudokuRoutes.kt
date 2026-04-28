package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.SudokuHomeScreen
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.SudokuPlayScreen
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components.SudokuHomeViewModel
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components.SudokuPlayViewModel
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel

@Composable
fun SudokuHomeRoute(
    navController: NavHostController,
    homeActivityViewModel: HomeActivityViewModel,
    onStartPlay: (size: String, difficulty: String, isNewPuzzle: Boolean) -> Unit,
    onPurchase: () -> Unit,
) {
    val viewModel: SudokuHomeViewModel = hiltViewModel()
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()

    SudokuHomeScreen(
        viewModel = viewModel,
        navController = navController,
        onStart = {
            if (homeActivityViewModel.isPurchasedForModule(purchasedSKU)) {
                val state = viewModel.uiState.value
                onStartPlay(
                    state.selectedSizeFinal.name,
                    state.selectedDifficultyFinal.name,
                    state.isNewGame
                )
            } else {
                onPurchase()
            }
        }
    )
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
