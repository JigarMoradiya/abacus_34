package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.home.NumberSequencePuzzleHomeJetpackScreen
import com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.play.NumberSequencePuzzleJetpackScreen
import com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.viewmodels.NumberSequencePuzzleViewModel
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel

@Composable
fun NumberSequencePuzzleHomeRoute(
    navController: NavHostController,
    homeActivityViewModel: HomeActivityViewModel,
    onPuzzleSelect: (Int) -> Unit,
    onPurchase: () -> Unit,
    onBackClick: () -> Unit,
) {
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()

    NumberSequencePuzzleHomeJetpackScreen(
        navController = navController,
        onPuzzleSelect = { type ->
            if (homeActivityViewModel.isPurchasedForModule(purchasedSKU)) {
                onPuzzleSelect(type)
            } else {
                onPurchase()
            }
        },
        onBackClick = onBackClick
    )
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
