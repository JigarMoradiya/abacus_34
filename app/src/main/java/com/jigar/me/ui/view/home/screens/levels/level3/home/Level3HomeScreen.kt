package com.jigar.me.ui.view.home.screens.levels.level3.home

import com.jigar.me.ui.view.home.screens.levels.level3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbLevelCard
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.levels.level3.components.ScreenTopBar
import com.jigar.me.ui.view.home.screens.levels.level3.components.screenLeftEdge
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun Level3HomeScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick:      () -> Unit,
    onNavigateToMode: (L3Mode) -> Unit,
) {
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    var showPaywall  by remember { mutableStateOf(false) }

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon)
            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()) {
                ScreenTopBar(title = "Speed & Anzan", onBack = onBackClick)

                BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    val spacing = AppDimens.Dimens10
                    val hPad    = AppDimens.Dimens16
                    val vPad    = AppDimens.Dimens12

                    val topModes    = L3Mode.entries.take(3)
                    val bottomModes = L3Mode.entries.drop(3)

                    val cardH = (maxHeight - spacing - vPad * 2) / 2
                    val topW  = (maxWidth  - hPad * 2 - spacing * 2) / 3

                    Column(
                        modifier            = Modifier.fillMaxSize().padding(horizontal = hPad).padding(vertical = vPad),
                        verticalArrangement = Arrangement.spacedBy(spacing),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                            topModes.forEach { mode ->
                                ModeCard(mode, topW, cardH, isSubscribed) {
                                    if (isSubscribed) onNavigateToMode(mode) else showPaywall = true
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                            bottomModes.forEach { mode ->
                                ModeCard(mode, topW, cardH, isSubscribed) {
                                    if (isSubscribed) onNavigateToMode(mode) else showPaywall = true
                                }
                            }
                        }
                    }
                }
            }

            if (showPaywall) {
                FreemiumPaywallBottomSheet(
                    paywallContext = PaywallContext.LEVEL3,
                    onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
                    onDismiss = { showPaywall = false }
                )
            }
        }
    }
}

@Composable
private fun ModeCard(mode: L3Mode, width: Dp, height: Dp, isSubscribed: Boolean, onClick: () -> Unit) {
    AbLevelCard(
        emoji     = mode.emoji,
        title     = mode.title,
        label     = mode.subtitle,
        accent    = mode.endColor,
        width     = width,
        height    = height,
        isLocked  = !isSubscribed,
        showStars = false,
        subtitle  = mode.subtitle,
        onClick   = onClick,
    )
}
