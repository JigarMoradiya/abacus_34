package com.jigar.me.ui.view.home.screens.activities.exercise.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.utils.AppConstants

@Composable
fun ExerciseAbacusRow(uiState: ExerciseUiState, viewModel: ExerciseViewModel, modifier: Modifier, onBackClick: () -> Unit) {
    Box(modifier = modifier) {

        Column(modifier = modifier) {
            if (uiState.isAbacusOnLeftHand){
                BackButtonWithText(title = stringResource(R.string.title_exercises), onBackClick = {
                    onBackClick()
                })
            }
            Spacer(Modifier.weight(1f))
            if (uiState.isAbacusOnLeftHand){
                Spacer(Modifier.height(AppDimens.Dimens4))
            }else{
                Spacer(Modifier.height(AppDimens.Dimens32))
            }
            Row {
                if (!uiState.isAbacusOnLeftHand){
                    Spacer(Modifier.weight(1f))
                }
                ExerciseAbacus(viewModel)
            }
            Spacer(Modifier.weight(1f))
        }
        // bottom label for to use keypad or abacus
        if (uiState.isExerciseStarted) {
            UseWhichHandTextUi(
                highlightColor = uiState.currentColorPresetModel.buttonColor,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppDimens.Dimens16)
            )
        }
    }
}

@Composable
fun ExerciseAbacus(viewModel: ExerciseViewModel) {
    Box {
        AbacusWithDecimalCanvas(
            selectedTheme = viewModel.selectedTheme,
            screenType = AppConstants.AbacusScreen.screenTypeExercise,
            isBeadSoundOn = viewModel.isBeadSoundEnabled,
            isDisplayCurrentAbacusInput = viewModel.isDisplayCurrentAbacusInput,
            abacusData = viewModel.abacusCalc,
            numberOfColumns = 7,
            rodMovement = viewModel.rodMovements,
            showDirectionHint = viewModel.showDirectionHints,
            isNextButtonEnable = true,
            onRodMovementChange = { viewModel.updateRodMovements(it) },
            onShowDirectionHintsChange = { viewModel.updateShowDirectionHints(it) },
            onShowHighlighterChange = {},
            onReset = {},
            onNext = {
                viewModel.nextQuestion()
            })
    }
}