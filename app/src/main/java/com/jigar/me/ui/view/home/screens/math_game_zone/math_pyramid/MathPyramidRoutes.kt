package com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.home.MathPyramidHomeJetpackScreen
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.home.components.MathPyramidViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.play.MathPyramidPlayJetpackScreen
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.utils.FreemiumManager

@Composable
fun MathPyramidHomeRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onStartPlay: (levels: Int, difficulty: String) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: MathPyramidViewModel = hiltViewModel()
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    var showPaywall by remember { mutableStateOf(false) }

    MathPyramidHomeJetpackScreen(
        viewModel = viewModel,
        onStartGame = {
            val state = viewModel.uiState.value
            // 2-level Easy pyramid is free forever — everything else is premium
            val gate = FreemiumManager.mathPyramidGate(
                isSubscribed = isSubscribed,
                levels = state.selectedLevel,
                isEasiestDifficulty = state.selectedDifficulty == CommonDifficulty4.easy
            )
            if (gate == FreemiumManager.GateResult.ALLOW) {
                onStartPlay(state.selectedLevel, state.selectedDifficulty.name)
            } else {
                showPaywall = true
            }
        },
        onBackClick = onBackClick
    )

    if (showPaywall) {
        FreemiumPaywallBottomSheet(
            paywallContext = PaywallContext.GAMES,
            onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
            onDismiss = { showPaywall = false }
        )
    }
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
