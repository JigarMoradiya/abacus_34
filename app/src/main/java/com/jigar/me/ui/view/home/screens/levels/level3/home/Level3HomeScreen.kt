package com.jigar.me.ui.view.home.screens.levels.level3.home

import com.jigar.me.ui.view.home.screens.levels.level3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LevelHomeCard
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun Level3HomeScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick:      () -> Unit,
    onNavigateToMode: (L3Mode) -> Unit,
) {
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    var showPaywall  by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            BackButtonWithText(title = "Speed & Anzan", onBackClick = onBackClick)

            BoxWithConstraints(
                modifier         = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                val spacing = AppDimens.Dimens10
                val hPad    = AppDimens.Dimens16
                val vPad    = AppDimens.Dimens12

                val topModes    = L3Mode.entries.take(3)
                val bottomModes = L3Mode.entries.drop(3)

                val cardH = (maxHeight - spacing - vPad) / 2
                val topW  = (maxWidth  - hPad * 2 - spacing * 2) / 3

                Column(
                    modifier            = Modifier.fillMaxSize().padding(horizontal = hPad).padding(bottom = vPad),
                    verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                    ) {
                        topModes.forEach { mode ->
                            LevelHomeCard(
                                emoji      = mode.emoji,
                                title      = mode.title,
                                label      = mode.subtitle,
                                startColor = mode.startColor,
                                endColor   = mode.endColor,
                                width      = topW,
                                height     = cardH,
                                hasBorder  = true,
                                isLocked   = !isSubscribed,
                                onClick    = { AudioPlayerManager.playSoundBtnClick(); if (isSubscribed) onNavigateToMode(mode) else showPaywall = true },
                            )
                        }
                    }
                    Row(
                        modifier              = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                    ) {
                        bottomModes.forEach { mode ->
                            LevelHomeCard(
                                emoji      = mode.emoji,
                                title      = mode.title,
                                label      = mode.subtitle,
                                startColor = mode.startColor,
                                endColor   = mode.endColor,
                                width      = topW,
                                height     = cardH,
                                hasBorder  = true,
                                isLocked   = !isSubscribed,
                                onClick    = { AudioPlayerManager.playSoundBtnClick(); if (isSubscribed) onNavigateToMode(mode) else showPaywall = true },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPaywall) {
        FreemiumPaywallBottomSheet(
            onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
            onDismiss = { showPaywall = false }
        )
    }
}
