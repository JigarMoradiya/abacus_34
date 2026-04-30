package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.dialogs.CommonLoadingView
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.components.NumberPad
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.components.SudokuBoard
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun SudokuPlayScreen(
    navController: NavController,
    vm: SudokuPlayViewModel,
    modifier: Modifier = Modifier
) {
    if (vm.isLoading) {
        CommonLoadingView(
            title = stringResource(R.string.please_wait),
            text = stringResource(R.string.making_your_sudoku_ready)
        )
        return
    }

    val size = vm.size
    val difficulty = vm.difficulty
    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.wrapContentWidth()) {
                BackButtonWithText(
                    title = "${size.displayName} • ${difficulty.displayName}",
                    onBackClick = { navController.popBackStack() },
                    modifier = Modifier.wrapContentWidth()
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    SudokuBoard(vm = vm, modifier = Modifier.fillMaxSize())
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    AppDimens.Dimens12,
                    Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (vm.message.isNullOrEmpty()) {
                    Text("", color = Color.Red, modifier = Modifier.padding(Dimens8))
                } else {
                    Text(vm.message ?: "", color = Color.Red, modifier = Modifier.padding(Dimens8))
                }

                Row {
                    KidsActionButton(
                        text = if (vm.showCandidates) stringResource(R.string.hide_options) else stringResource(R.string.show_options),
                        icon = if (vm.showCandidates) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        type = ButtonType.TEAL,
                        isSmall = true,
                        onClick = { vm.toggleCandidates() }
                    )

                    Spacer(Modifier.width(Dimens8))

                    KidsActionButton(
                        text = "💡 Hint ${vm.hintUsed}/${vm.hintLimit}",
                        type = ButtonType.ORANGE,
                        isSmall = true,
                        onClick = { vm.revealOneNumber() }
                    )

                    Spacer(Modifier.width(Dimens8))

                    KidsActionButton(
                        text = stringResource(R.string.restart),
                        icon = Icons.Filled.Refresh,
                        type = ButtonType.PINK,
                        isSmall = true,
                        onClick = { vm.resetPuzzle() }
                    )

                }

                NumberPad(vm = vm)
            }
        }
    }

    AnimatedVisibility(
        visible = vm.isSolved,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        CustomPopupView(
            title = stringResource(R.string.you_are_a_genius),
            description = stringResource(R.string.you_completed_this_sudoku),
            positiveButtonText = stringResource(R.string.play_again),
            negativeButtonText = stringResource(R.string.no_i_will_play_letter),
            icon = R.drawable.ic_complete,
            widthMultiplier = 0.5f,
            onPositiveTapped = {
                vm.isSolved = false
                vm.loadNewPuzzle(size, difficulty)
            },
            onNegativeTapped = {
                vm.isSolved = false
                navController.popBackStack()
            }
        )
    }
}
