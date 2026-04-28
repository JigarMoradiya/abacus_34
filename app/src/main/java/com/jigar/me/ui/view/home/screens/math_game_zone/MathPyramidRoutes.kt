package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.ui.view.home.common.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.home.MathPyramidHomeJetpackScreen
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.home.components.MathPyramidViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.play.MathPyramidPlayJetpackScreen
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel

@Composable
fun MathPyramidHomeRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onStartPlay: (levels: Int, difficulty: String) -> Unit,
    onPurchase: () -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: MathPyramidViewModel = hiltViewModel()
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()

    MathPyramidHomeJetpackScreen(
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
fun MathPyramidPlayRoute(
    levels: Int,
    difficultyName: String?,
    onBackClick: () -> Unit,
) {
    val difficulty = CommonDifficulty4.fromName(difficultyName)
    MathPyramidPlayJetpackScreen(
        levels = levels,
        difficulty = difficulty,
        onBackClick = onBackClick
    )
}
