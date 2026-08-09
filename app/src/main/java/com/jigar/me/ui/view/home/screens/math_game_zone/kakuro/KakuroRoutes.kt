package com.jigar.me.ui.view.home.screens.math_game_zone.kakuro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroHomeViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.viewmodel.KakuroPlayViewModel

@Composable
fun KakuroHomeRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onStartPlay: (diff: String, level: Int) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: KakuroHomeViewModel = hiltViewModel()
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    var showPaywall by remember { mutableStateOf(false) }

    // Re-read stars whenever we come back from a play session.
    LaunchedEffect(Unit) { viewModel.refreshProgress() }

    KakuroHomeScreen(
        viewModel = viewModel,
        isSubscribed = isSubscribed,
        onLevelClick = { difficulty, level ->
            if (difficulty == CommonDifficulty4.easy || isSubscribed) {
                onStartPlay(difficulty.name, level)
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
fun KakuroPlayRoute(
    onBackClick: () -> Unit,
    onNextLevel: (diff: String, level: Int) -> Unit
) {
    val viewModel: KakuroPlayViewModel = hiltViewModel()
    KakuroPlayScreen(
        viewModel = viewModel,
        onBackClick = onBackClick,
        onNextLevel = { onNextLevel(viewModel.difficulty.name, viewModel.level + 1) }
    )
}
