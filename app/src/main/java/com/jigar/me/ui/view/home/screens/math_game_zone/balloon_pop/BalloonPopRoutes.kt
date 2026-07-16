package com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop

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
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components.BalloonPopHomeViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.viewmodel.BalloonPopPlayViewModel

@Composable
fun BalloonPopHomeRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onStartPlay: (diff: String) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: BalloonPopHomeViewModel = hiltViewModel()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    var showPaywall by remember { mutableStateOf(false) }

    BalloonPopHomeScreen(
        viewModel = viewModel,
        onStartGame = {
            // Easy is free forever — only the harder modes are premium.
            if (selectedDifficulty == CommonDifficulty4.easy || isSubscribed) {
                onStartPlay(selectedDifficulty.name)
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
fun BalloonPopPlayRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: BalloonPopPlayViewModel = hiltViewModel()
    BalloonPopPlayScreen(
        viewModel = viewModel,
        onBackClick = onBackClick
    )
}
