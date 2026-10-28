package com.jigar.me.ui.view.home.screens.activities.exercise.components.pager

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbFitToSpace
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.ExercisePageArt
import com.jigar.me.ui.view.ab.ExercisePageDecor
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.screens.activities.ActivityDimens
import com.jigar.me.ui.view.home.screens.activities.exercise.exercise_generator.GridItemModel
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.utils.FreemiumManager

/**
 * The three exercise types as pages: the page's picture, its name, the numbered circles (which set to practise), what the
 * chosen set is, and the Start button. Faint symbols in the page's colours sit behind the writing.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExercisePagerScreen(
    uiState: ExerciseUiState,
    viewModel: ExerciseViewModel,
    isLoggedIn: Boolean = true,
    onShowLogin: () -> Unit = {},
    onShowPaywall: () -> Unit = {},
    onStart: (GridItemModel, Int) -> Unit
) {
    val colors = LocalAbColors.current
    val scale = LocalAbScale.current
    val s = scale.factor
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage,
        pageCount = { uiState.exercises.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onPageChanged(pagerState.currentPage)
    }

    // Nothing sits above the pager on a phone, so it needs less room there than on a tablet
    Column(Modifier.fillMaxSize().padding(top = if (scale.isTablet) 48.dp * s else 24.dp)) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            val exercise = uiState.exercises[page]
            val selectedItem = uiState.selectedItems[page]

            Box(Modifier.fillMaxSize()) {
                ExercisePageDecor(page)

                AbFitToSpace(Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(ActivityDimens.exerciseItemGap),
                    ) {
                        // The page's picture: + and - on the first page, x on the second, ÷ on the third
                        ExercisePageArt(page)

                        // TITLE + SUBTITLE
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Dimens4)) {
                            AbShrinkText(
                                text = exercise.title,
                                // iOS squeezes this heading to about 0.78 of its size on a phone (the page is short there)
                                style = AbTypography.title(if (LocalAbScale.current.isTablet) s else s * 0.78f),
                                color = colors.textPrimary,
                                minScale = 0.7f,
                                maxLines = 2,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = stringResource(R.string.select_your_exercise),
                                style = AbTypography.label(s),
                                color = colors.textSecondary,
                                textAlign = TextAlign.Center,
                            )
                        }

                        // GRID
                        ExerciseWrapGrid(
                            items = exercise.gridItems,
                            selectedItem = selectedItem,
                            isItemLocked = { itemIndex ->
                                FreemiumManager.exerciseGate(isLoggedIn, uiState.isPurchased, page, itemIndex) == FreemiumManager.GateResult.REQUIRE_PAYWALL
                            },
                            onLockedItemClick = { onShowPaywall() },
                            onItemSelected = { viewModel.onGridItemSelected(page, it) }
                        )

                        // DESCRIPTION (two full lines)
                        Text(
                            text = selectedItem?.selectedItemDescription(page) ?: "",
                            style = AbTypography.label(s),
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(Modifier.height(Dimens4))

                        // BUTTON
                        AbButton(
                            text = stringResource(R.string.start_exercise),
                            tone = AbTone.Warning,
                            size = AbSize.Small,
                            icon = Icons.Default.Bolt,
                            onClick = {
                                selectedItem?.let { item ->
                                    val itemIdx = exercise.gridItems.indexOfFirst { it.id == item.id }
                                    when (FreemiumManager.exerciseGate(isLoggedIn, uiState.isPurchased, page, itemIdx)) {
                                        FreemiumManager.GateResult.ALLOW -> onStart(item, page)
                                        FreemiumManager.GateResult.REQUIRE_LOGIN -> onShowLogin()
                                        FreemiumManager.GateResult.REQUIRE_LOGIN_THEN_PAYWALL -> onShowLogin()
                                        FreemiumManager.GateResult.REQUIRE_PAYWALL -> onShowPaywall()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Page indicator: the current page is a longer orange pill
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = AppDimens.Dimens8)
        ) {
            repeat(uiState.exercises.size) { idx ->
                val current = idx == pagerState.currentPage
                val width by animateDpAsState(
                    targetValue = if (current) AppDimens.Dimens16 else AppDimens.Dimens8,
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
                    label = "pageDot",
                )
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(AppDimens.Dimens8)
                        .background(if (current) colors.accentOrange else colors.outline, CircleShape)
                )
            }
        }
    }
}
