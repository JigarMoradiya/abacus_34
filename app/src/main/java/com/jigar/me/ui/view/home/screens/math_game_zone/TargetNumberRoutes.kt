package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.TargetNumberHomeScreen
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.TargetNumberPlayScreen
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.components.TargetNumberViewModel
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.viewmodel.TargetNumberPlayViewModel
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel

@Composable
fun TargetNumberHomeRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onStartPlay: (level: Int, diff: String) -> Unit,
    onPurchase: () -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: TargetNumberViewModel = hiltViewModel()
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()

    TargetNumberHomeScreen(
        viewModel = viewModel,
        onStartGame = {
            if (homeActivityViewModel.isPurchasedForModule(purchasedSKU)) {
                val state = viewModel.uiState.value
                onStartPlay(state.selectedLevel, state.selectedDifficulty.name)
            } else {
                onPurchase()
            }
        },
        onBackClick = onBackClick
    )
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
