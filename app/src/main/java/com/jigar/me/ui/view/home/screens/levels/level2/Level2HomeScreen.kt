package com.jigar.me.ui.view.home.screens.levels.level2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.jigar.me.ui.view.ab.AbLevelCard
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.levels.LevelScreenRoot
import com.jigar.me.ui.view.home.screens.levels.levelForeground
import com.jigar.me.ui.view.home.screens.levels.levelTopBarPadding
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun Level2HomeScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
    onNavigateToChapter: (Int) -> Unit,
) {
    val prefs        = LocalPreferencesHelper.current
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    var showPaywall  by remember { mutableStateOf(false) }

    LevelScreenRoot(AbWorld.Sunset) {
        Column(modifier = Modifier.levelForeground()) {
            AbTopBar(title = "Add & Subtract", onBack = onBackClick, modifier = Modifier.levelTopBarPadding())

            BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val cols    = 5
                val rows    = 2
                val spacing = AppDimens.Dimens10
                val hPad    = AppDimens.Dimens16
                val vPad    = AppDimens.Dimens12

                val cellW = (maxWidth - hPad * 2 - spacing * (cols - 1)) / cols
                val cellH = (maxHeight - spacing * (rows - 1) - vPad * 2) / rows

                Column(
                    modifier            = Modifier.fillMaxSize().padding(horizontal = hPad).padding(vertical = vPad),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        level2Chapters.take(cols).forEachIndexed { index, chapter ->
                            val isLocked   = !isSubscribed && index >= 3
                            val chapStars  = if (chapter.type != Level2ChapterType.FORMULA_REF)
                                prefs.getCustomParamInt("l2_quiz_stars_${chapter.id}", 0) else 0
                            L2ChapterCard(chapter, cellW, cellH, isLocked, chapStars) {
                                if (isLocked) showPaywall = true else onNavigateToChapter(chapter.id)
                            }
                        }
                    }
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        level2Chapters.drop(cols).forEach { chapter ->
                            val chapStars = if (chapter.type != Level2ChapterType.FORMULA_REF)
                                prefs.getCustomParamInt("l2_quiz_stars_${chapter.id}", 0) else 0
                            L2ChapterCard(chapter, cellW, cellH, isLocked = !isSubscribed, stars = chapStars) {
                                if (!isSubscribed) showPaywall = true else onNavigateToChapter(chapter.id)
                            }
                        }
                    }
                }
            }
        }

        if (showPaywall) {
            FreemiumPaywallBottomSheet(
                paywallContext = PaywallContext.LEVEL2,
                onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
                onDismiss = { showPaywall = false }
            )
        }
    }
}

@Composable
private fun L2ChapterCard(
    chapter: Level2ChapterData,
    cellW: Dp,
    cellH: Dp,
    isLocked: Boolean = false,
    stars: Int = 0,
    onClick: () -> Unit,
) {
    val isFormulaRef = chapter.type == Level2ChapterType.FORMULA_REF
    AbLevelCard(
        emoji     = chapter.emoji,
        title     = chapter.title,
        label     = if (isFormulaRef) "Reference" else "Lesson ${chapter.id}",
        number    = if (isFormulaRef) null else chapter.id,
        accent    = chapter.endColor,
        width     = cellW,
        height    = cellH,
        stars     = stars,
        isLocked  = isLocked,
        showStars = !isFormulaRef,
        tag       = if (chapter.columns == 2) "2 cols" else null,
        onClick   = onClick,
    )
}
