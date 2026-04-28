package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.dialogs.CommonLoadingView
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuBoxRules
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuPlayViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuSize
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlin.math.ceil

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
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(
                    title = "${size.displayName} • ${difficulty.displayName}",
                    onBackClick = { navController.popBackStack() }
                )
            }

            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    SudokuBoard(vm = vm)
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
                        Text("", color = Color.Red, modifier = Modifier.padding(AppDimens.Dimens8))
                    } else {
                        Text(vm.message ?: "", color = Color.Red, modifier = Modifier.padding(AppDimens.Dimens8))
                    }

                    Row {
                        Button(
                            onClick = { vm.toggleCandidates() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(AppDimens.Dimens8),
                            contentPadding = PaddingValues(
                                horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (vm.showCandidates) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(AppDimens.Dimens24)
                                )

                                Text(
                                    text = if (vm.showCandidates) stringResource(R.string.hide_options)
                                    else stringResource(R.string.show_options),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }

                        Spacer(Modifier.width(AppDimens.Dimens8))
                        Button(
                            onClick = { vm.revealOneNumber() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(AppDimens.Dimens8),
                            contentPadding = PaddingValues(
                                horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${vm.hintUsed}/${vm.hintLimit}",
                                    fontSize = dimensionResource(R.dimen.textSize18).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_bold)),
                                    modifier = Modifier.height(AppDimens.Dimens24)
                                )

                                Text(
                                    text = stringResource(R.string.hints),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }

                        Spacer(Modifier.width(AppDimens.Dimens8))
                        Button(
                            onClick = { vm.resetPuzzle() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(AppDimens.Dimens8),
                            contentPadding = PaddingValues(
                                horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(AppDimens.Dimens24)
                                )

                                Text(
                                    text = stringResource(R.string.restart),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }

                    }

                    NumberPad(vm = vm)
                }
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

@Composable
fun SudokuBoard(vm: SudokuPlayViewModel, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier.padding(AppDimens.Dimens12),
        contentAlignment = Alignment.Center
    ) {
        val n = vm.puzzle.size.grid
        val (boxRows, boxCols) = SudokuBoxRules.boxSize(vm.puzzle.size)

        val side = min(maxWidth, maxHeight)
        val cellSize = side / n

        Box(
            modifier = Modifier.size(side),
            contentAlignment = Alignment.TopStart
        ) {
            Column {
                for (r in 0 until n) {
                    Row {
                        for (c in 0 until n) {
                            SudokuCell(
                                vm = vm,
                                row = r,
                                col = c,
                                sizeDp = cellSize
                            )
                        }
                    }
                }
            }

            Canvas(modifier = Modifier.matchParentSize()) {
                val cell = size.width / n

                val boxW = cell * boxCols
                val boxH = cell * boxRows

                val rows = n / boxRows
                val cols = n / boxCols

                for (br in 0 until rows) {
                    for (bc in 0 until cols) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(bc * boxW, br * boxH),
                            size = Size(boxW, boxH),
                            style = Stroke(width = AppDimens.Dimens1.toPx())
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SudokuCell(vm: SudokuPlayViewModel, row: Int, col: Int, sizeDp: Dp) {
    val puzzleGiven = vm.puzzle.startBoard[row][col] != 0
    val isSelected = vm.selected == row to col
    val value = vm.board[row][col]
    val isConflict = remember(vm.board) {
        if (value == 0) false
        else {
            val n = vm.puzzle.size.grid
            for (i in 0 until n) {
                if (i != col && vm.board[row][i] == value) return@remember true
                if (i != row && vm.board[i][col] == value) return@remember true
            }
            val (br, bc) = SudokuBoxRules.boxSize(vm.puzzle.size)
            val sr = (row / br) * br
            val sc = (col / bc) * bc
            for (r in 0 until br) for (c in 0 until bc) {
                val rr = sr + r; val cc = sc + c
                if (!(rr == row && cc == col) && vm.board[rr][cc] == value) return@remember true
            }
            false
        }
    }

    val inSameRow = vm.selected?.first == row
    val inSameCol = vm.selected?.second == col
    val brc = SudokuBoxRules.boxSize(vm.puzzle.size)
    val inSameBox = vm.selected?.let { (sr, sc) ->
        (sr / brc.first) == (row / brc.first) && (sc / brc.second) == (col / brc.second)
    } ?: false

    val bgColor = when {
        isSelected -> Color(0xFFBBDEFB)
        inSameRow || inSameCol || inSameBox -> Color(0xFFFFF9C4)
        puzzleGiven -> Color.White
        else -> colorResource(R.color.grey_200)
    }

    Box(
        modifier = Modifier
            .size(sizeDp)
            .background(bgColor)
            .border(0.5.dp, colorResource(R.color.grey_300))
            .clickable(enabled = !puzzleGiven) { vm.selectCell(row, col) },
        contentAlignment = Alignment.Center
    ) {
        if (value != 0) {
            Text(
                text = value.toString(),
                fontSize = when (vm.puzzle.size) {
                    SudokuSize.FOUR -> 28.sp
                    SudokuSize.SIX -> 20.sp
                    SudokuSize.NINE -> 16.sp
                },
                fontFamily = FontFamily(Font(if (puzzleGiven) R.font.font_semibold else R.font.font_bold)),
                color = if (puzzleGiven) Color.Black else if (isConflict) Color.Red else Color.Blue
            )
        } else {
            if (vm.showCandidates && vm.selected?.first == row && vm.selected?.second == col) {
                val cands = vm.candidatesForSelected()
                Text(
                    modifier = Modifier
                        .padding(AppDimens.Dimens2)
                        .fillMaxWidth(),
                    text = cands.joinToString(" "),
                    fontSize = 10.sp,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.copy(
                        lineHeight = 11.sp,
                        letterSpacing = 1.sp
                    ),
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    maxLines = 3,
                    softWrap = true
                )

            }
        }
    }
}

@Composable
fun NumberPad(vm: SudokuPlayViewModel) {
    val max = vm.puzzle.size.grid
    val numbers = (1..max).toList()
    val mid = ceil(numbers.size / 2.0).toInt()
    val row1 = numbers.take(mid)
    val row2 = numbers.drop(mid)

    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
            row1.forEach { n ->
                NumberKey(n) { vm.enter(n) }
            }
            if (row2.isEmpty()) {
                EraseKey { vm.eraseSelected() }
            }
        }
        if (row2.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
                row2.forEach { n -> NumberKey(n) { vm.enter(n) } }
                EraseKey { vm.eraseSelected() }
            }
        }
    }
}

@Composable
fun EraseKey(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = AppDimens.Dimens48, height = AppDimens.Dimens36)
            .background(
                colorResource(R.color.red_400), shape = RoundedCornerShape(AppDimens.Dimens8)
            )
            .clickable {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_backspace),
            contentDescription = "Erase",
            tint = colorResource(R.color.white),
            modifier = Modifier.size(AppDimens.Dimens24)
        )
    }
}

@Composable
fun NumberKey(n: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = AppDimens.Dimens48, height = AppDimens.Dimens36)
            .background(colorResource(R.color.colorPrimary), shape = RoundedCornerShape(AppDimens.Dimens8))
            .clickable {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$n",
            color = colorResource(R.color.white),
            fontSize = dimensionResource(id = R.dimen.textSize24).value.sp,
            fontFamily = FontFamily(Font(R.font.font_bold))
        )
    }
}
