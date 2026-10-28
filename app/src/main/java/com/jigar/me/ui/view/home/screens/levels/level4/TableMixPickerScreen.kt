package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbLevelCard
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.levels.level3.components.ScreenTopBar
import com.jigar.me.ui.view.home.screens.levels.level3.components.screenLeftEdge
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8

@Composable
fun TableMixPickerScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onMixSelected: (List<Int>) -> Unit,
    onBackClick: () -> Unit,
) {
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    var showPaywall by remember { mutableStateOf(false) }

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Twilight)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .screenLeftEdge()
            ) {
                ScreenTopBar(title = "🎲 Mix Table Practice", onBack = onBackClick)

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = Dimens16, vertical = Dimens8),
                    horizontalArrangement = Arrangement.spacedBy(Dimens16)
                ) {
                    // LEFT — FREE card (35%)
                    val free = MIX_GROUPS.first()
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(0.35f)
                            .fillMaxHeight()
                    ) {
                        AbLevelCard(
                            emoji     = free.emoji,
                            title     = free.label,
                            label     = free.label,
                            accent    = free.endColor,
                            width     = maxWidth,
                            height    = maxHeight,
                            showStars = false,
                            tag       = "FREE FOREVER",
                            subtitle  = "${free.tables.size} tables mixed together",
                            onClick   = { onMixSelected(free.tables) },
                        )
                    }

                    // RIGHT — 3×2 premium grid (65%), fills full height
                    Column(
                        modifier = Modifier
                            .weight(0.65f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(Dimens12)
                    ) {
                        val premiumGroups = MIX_GROUPS.drop(1)
                        // Row 1: ×1–5, ×6–10, ×1–10 — Row 2: ×11–15, ×15–20, ×1–20
                        listOf(premiumGroups.take(3), premiumGroups.drop(3)).forEach { rowGroups ->
                            Row(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Dimens12)
                            ) {
                                rowGroups.forEach { group ->
                                    BoxWithConstraints(
                                        modifier = Modifier.weight(1f).fillMaxHeight()
                                    ) {
                                        AbLevelCard(
                                            emoji     = group.emoji,
                                            title     = group.label,
                                            label     = group.label,
                                            accent    = group.endColor,
                                            width     = maxWidth,
                                            height    = maxHeight,
                                            isLocked  = !isSubscribed,
                                            showStars = false,
                                            subtitle  = "${group.tables.size} tables",
                                            onClick   = {
                                                if (isSubscribed) onMixSelected(group.tables)
                                                else showPaywall = true
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showPaywall) {
                FreemiumPaywallBottomSheet(
                    paywallContext = PaywallContext.TABLES,
                    onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
                    onDismiss = { showPaywall = false }
                )
            }
        }
    }
}
