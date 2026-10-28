package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.min
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbLevelCard
import com.jigar.me.ui.view.ab.AbTableTile
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.levels.level3.components.ScreenTopBar
import com.jigar.me.ui.view.home.screens.levels.level3.components.screenLeftEdge
import com.jigar.me.ui.view.home.theme.AppDimens

private val tablePickerColors = listOf(
    Color(0xFFE53935), Color(0xFFF57C00), Color(0xFFF9A825), Color(0xFF43A047), Color(0xFF00ACC1),
    Color(0xFF00897B), Color(0xFF1976D2), Color(0xFF7B1FA2), Color(0xFFC2185B), Color(0xFF5E35B1),
    Color(0xFFE64A19), Color(0xFF00695C), Color(0xFF1565C0), Color(0xFF2E7D32), Color(0xFF0288D1),
    Color(0xFFAD1457), Color(0xFF6A1B9A), Color(0xFF558B2F), Color(0xFF4E342E), Color(0xFF37474F),
)

@Composable
fun TablePickerScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onTableSelected: (Int) -> Unit,
    onMixPractice: () -> Unit = {},
    onBackClick: () -> Unit,
) {
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    var showPaywall  by remember { mutableStateOf(false) }

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Twilight)
            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()) {

                ScreenTopBar(title = "Choose a table to practise", onBack = onBackClick)

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    val areaW     = maxWidth
                    val areaH     = maxHeight

                    // LEFT 70% — table grid
                    val gridAreaW = areaW * 0.70f
                    val cols      = 5
                    val rows      = 4
                    val hPad      = AppDimens.Dimens16
                    val spacing   = AppDimens.Dimens12

                    val availW  = gridAreaW - hPad * 2
                    val cellWfW = (availW - spacing * (cols - 1)) / cols
                    val cellHfH = (areaH - spacing * (rows + 2)) / rows
                    val cellWfH = cellHfH * 1.5f
                    val cellW   = min(cellWfW, cellWfH)
                    val cellH   = cellW / 1.5f

                    Row(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.width(gridAreaW).fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically)
                        ) {
                            for (r in 0 until rows) {
                                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                                    for (c in 0 until cols) {
                                        val n = r * cols + c + 1
                                        val isLocked = n > 3 && !isSubscribed
                                        AbTableTile(
                                            number = n,
                                            accent = tablePickerColors[n - 1],
                                            width = cellW,
                                            height = cellH,
                                            isLocked = isLocked,
                                            onClick = { if (isLocked) showPaywall = true else onTableSelected(n) },
                                        )
                                    }
                                }
                            }
                        }

                        // RIGHT 30% — Mix Practice card
                        Box(
                            modifier = Modifier.width(areaW * 0.30f).fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            AbLevelCard(
                                emoji     = "🎲",
                                title     = "Mix Table Practice",
                                label     = "Start",
                                accent    = Color(0xFF0288D1),
                                width     = areaW * 0.30f - AppDimens.Dimens16,
                                height    = areaH - AppDimens.Dimens16,
                                showStars = false,
                                subtitle  = "Practise multiple tables together!",
                                cta       = "Start",
                                onClick   = onMixPractice,
                            )
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
