package com.jigar.me.ui.view.home.screens.activities.exercise.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbChip
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.ExerciseQuestionDecor
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.home.screens.activities.ActivityTopBar
import com.jigar.me.ui.view.home.screens.activities.exercise.components.pager.ExercisePagerScreen
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.exerciseWidth
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.secToCountDown

/**
 * The Exercise screen: the abacus on one side, a tall rounded panel on the other. Before an exercise starts the panel
 * holds the pager that picks one; while it runs the panel shows a calm notebook-grid scene with the keypad, the
 * question and the Answer / Next row. On a phone the hint about the two ways to answer sits between the two.
 */
@Composable
fun ExerciseScreen(
    uiState: ExerciseUiState,
    viewModel: ExerciseViewModel,
    isLoggedIn: Boolean = true,
    onShowLogin: () -> Unit = {},
    onShowPaywall: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val scale = LocalAbScale.current
    val abacusOnLeft = uiState.isAbacusOnLeftHand
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    val leftInset = insets.calculateLeftPadding(LayoutDirection.Ltr)
    val rightInset = insets.calculateRightPadding(LayoutDirection.Ltr)

    // The width of the box around the (7-rod) abacus on a phone: the abacus at its own size plus the screen edge, but no
    // more than leaves the hint text 130 dp beside the panel. A little less (95%): the fit around the abacus scales the
    // abacus and its answer bar together down to the box.
    val context = LocalContext.current
    val dim = remember(context) { AbacusTheme.dimensionPreset(context, screenType = AppConstants.AbacusScreen.screenTypeExercise) }
    val naturalWidth = dim.beadWidth * 7 + dim.rectLineWidth * 2 + dim.columnSpaces * 7 * 2
    val edge = if (abacusOnLeft) DeviceInfo.screenHorizontalPadding() else Dimens16

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val abacusBoxWidth = minOf((naturalWidth + edge) * 0.95f, maxWidth - exerciseWidth - 130.dp).coerceAtLeast(0.dp)

        // the panel runs to the top, bottom and outer edge of the device; its content stays inside the safe area
        if (abacusOnLeft) {
            ExercisePanelFill(
                shape = RoundedCornerShape(topStart = 40.dp, bottomStart = 40.dp),
                scenePage = if (uiState.isExerciseStarted) uiState.currentPage else null,
                modifier = Modifier.align(Alignment.CenterEnd).width(exerciseWidth + rightInset).fillMaxHeight(),
            )
        } else {
            ExercisePanelFill(
                shape = RoundedCornerShape(topEnd = 40.dp, bottomEnd = 40.dp),
                scenePage = if (uiState.isExerciseStarted) uiState.currentPage else null,
                modifier = Modifier.align(Alignment.CenterStart).width(exerciseWidth + leftInset).fillMaxHeight(),
            )
        }

        Row(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            val questionPanel: @Composable () -> Unit = {
                Box(Modifier.width(exerciseWidth).fillMaxHeight()) {
                    if (uiState.isExerciseStarted) {
                        ExerciseQuestions(uiState, viewModel)
                    } else {
                        ExercisePagerScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            isLoggedIn = isLoggedIn,
                            onShowLogin = onShowLogin,
                            onShowPaywall = onShowPaywall
                        ) { _, _ ->
                            viewModel.generateExercise()
                        }
                    }
                }
            }
            val abacus: @Composable () -> Unit = { ExerciseAbacusRow(uiState, viewModel, abacusBoxWidth) }

            if (abacusOnLeft) {
                abacus()
                if (scale.isTablet) Box(Modifier.weight(1f)) else ExerciseInstruction(uiState, Modifier.weight(1f))
                questionPanel()
            } else {
                questionPanel()
                if (scale.isTablet) Box(Modifier.weight(1f)) else ExerciseInstruction(uiState, Modifier.weight(1f))
                abacus()
            }
        }

        ExerciseHeader(uiState, onBackClick)
    }
}

/** The tall side panel (question and keypad), drawn from the kit colours. */
@Composable
private fun ExercisePanelFill(shape: RoundedCornerShape, scenePage: Int?, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val shadowTint = colors.outline.copy(alpha = if (colors.isDark) 0.4f else 0.9f)
    Box(
        modifier
            .shadow(10.dp, shape, clip = false, ambientColor = shadowTint, spotColor = shadowTint)
            .background(colors.bgSecondary.copy(alpha = if (colors.isDark) 0.96f else 0.94f), shape)
            .border(3.dp, colors.outline, shape)
    ) {
        // The scene is cut to the panel's own shape, so it follows the same rounded corners
        if (scenePage != null) {
            Box(Modifier.fillMaxSize().clip(shape)) { ExerciseQuestionDecor(scenePage) }
        }
    }
}

/** The back button and title on the screen edge, and, while an exercise runs, the timer in the middle at the top. */
@Composable
private fun ExerciseHeader(uiState: ExerciseUiState, onBackClick: () -> Unit) {
    val s = LocalAbScale.current.factor
    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        if (uiState.isExerciseStarted) {
            Box(
                Modifier.align(Alignment.TopCenter).padding(top = DeviceInfo.screenTopPadding()).height(48.dp * s),
                contentAlignment = Alignment.Center,
            ) {
                AbChip(text = "Time : " + uiState.elapsedSeconds.secToCountDown(), tone = AbTone.Purple, icon = Icons.Filled.Schedule)
            }
        }
        ActivityTopBar(title = stringResource(R.string.title_exercises), onBack = onBackClick)
    }
}
