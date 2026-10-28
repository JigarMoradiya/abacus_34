package com.jigar.me.ui.view.home.screens.activities.exercise.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbFitToSpace
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.screens.activities.rememberToyBarRoom
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.utils.AppConstants

/**
 * The abacus side of the Exercise screen. On a phone it sits in a box exactly [boxWidth] wide (the abacus at its own size,
 * a little less), shrunk with its answer bar to the room below the header; on a tablet it keeps its size and the hint
 * about the two ways to answer sits under it.
 */
@Composable
fun ExerciseAbacusRow(uiState: ExerciseUiState, viewModel: ExerciseViewModel, boxWidth: Dp, modifier: Modifier = Modifier) {
    val scale = LocalAbScale.current
    // the abacus keeps the standard screen edge on the side it sits against
    val edge = if (uiState.isAbacusOnLeftHand) DeviceInfo.screenHorizontalPadding() else Dimens16
    val edgePadding = if (uiState.isAbacusOnLeftHand) Modifier.padding(start = edge) else Modifier.padding(end = edge)

    if (!scale.isTablet) {
        Box(
            modifier
                .width(boxWidth)
                .fillMaxHeight()
                // the header is about this tall on a phone (top gap + back button + its edge)
                .padding(top = DeviceInfo.screenTopPadding() + 44.dp + 14.dp, bottom = 8.dp)
        ) {
            AbFitToSpace(Modifier.fillMaxSize(), alignment = Alignment.CenterStart) {
                Box(edgePadding) { ExerciseAbacus(viewModel, uiState) }
            }
        }
    } else {
        Column(modifier.fillMaxHeight().then(edgePadding), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.weight(1f))
            ExerciseAbacus(viewModel, uiState)
            Spacer(Modifier.weight(1f))
            // on a tablet the hint sits at the bottom, under the abacus
            Row(
                Modifier.padding(bottom = Dimens16).alpha(if (uiState.isExerciseStarted) 1f else 0f),
                horizontalArrangement = Arrangement.spacedBy(Dimens10),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ExerciseHintBadges(30.dp * scale.factor)
                UseWhichHandTextUi(highlightColor = uiState.currentColorPresetModel.buttonColor)
            }
        }
    }
}

/** On a phone the hint about the two ways to answer sits between the abacus and the question panel. */
@Composable
fun ExerciseInstruction(uiState: ExerciseUiState, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier.padding(horizontal = Dimens12).alpha(if (uiState.isExerciseStarted) 1f else 0f),
            verticalArrangement = Arrangement.spacedBy(Dimens8),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ExerciseHintBadges(34.dp * LocalAbScale.current.factor)
            UseWhichHandTextUi(highlightColor = uiState.currentColorPresetModel.buttonColor)
        }
    }
}

@Composable
fun ExerciseAbacus(viewModel: ExerciseViewModel, uiState: ExerciseUiState) {
    // the toy answer bar hangs above the frame, so the box leaves room for it
    Box(Modifier.padding(top = rememberToyBarRoom(AppConstants.AbacusScreen.screenTypeExercise))) {
        AbacusWithDecimalCanvas(
            selectedTheme = viewModel.selectedTheme,
            screenType = AppConstants.AbacusScreen.screenTypeExercise,
            isBeadSoundOn = viewModel.isBeadSoundEnabled,
            isDisplayCurrentAbacusInput = viewModel.isDisplayCurrentAbacusInput,
            abacusData = viewModel.abacusCalc,
            numberOfColumns = 7,
            rodMovement = viewModel.rodMovements,
            showDirectionHint = viewModel.showDirectionHints,
            frameStyle = AbacusFrameStyle.Toy,
            isNextButtonEnable = uiState.isExerciseStarted,
            onRodMovementChange = { viewModel.updateRodMovements(it) },
            onShowDirectionHintsChange = { viewModel.updateShowDirectionHints(it) },
            onShowHighlighterChange = {},
            onReset = {},
            onNext = {
                viewModel.nextQuestion()
            })
    }
}
