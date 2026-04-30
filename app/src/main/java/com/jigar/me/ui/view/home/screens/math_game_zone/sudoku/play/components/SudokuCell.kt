package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator.SudokuBoxRules
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPlayViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuSize
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens3
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.PrimaryBlue

@Composable
fun SudokuCell(vm: SudokuPlayViewModel, row: Int, col: Int, sizeDp: Dp) {

    val puzzleGiven = vm.puzzle.startBoard[row][col] != 0
    val isSelected = vm.selected == row to col
    val value = vm.board[row][col]

    val isConflict = remember(vm.board) {
        if (value == 0) false else {
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
        (sr / brc.first) == (row / brc.first) &&
                (sc / brc.second) == (col / brc.second)
    } ?: false

    val bgColor = when {
        isSelected -> Color(0xFF90CAF9)
        inSameRow || inSameCol || inSameBox -> Color(0xFFFFF59D)
        puzzleGiven -> Color.White
        else -> Color(0xFFE3F2FD)
    }

    Box(
        modifier = Modifier
            .size(sizeDp) // ✅ DO NOT TOUCH THIS
            .border(0.5.dp, Color(0x596D4C41))
            .clickable(enabled = !puzzleGiven) {
                vm.selectCell(row, col)
            },
        contentAlignment = Alignment.Center
    ) {

        // 🔥 Inner styling (safe)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens3) // 👈 inside only
                .clip(RoundedCornerShape(when (vm.puzzle.size) {
                    SudokuSize.FOUR -> Dimens10
                    SudokuSize.SIX -> Dimens8
                    SudokuSize.NINE -> Dimens4
                }))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {

            if (value != 0) {
                Text(
                    text = value.toString(),
                    fontSize = when (vm.puzzle.size) {
                        SudokuSize.FOUR -> 40.sp * appScale()
                        SudokuSize.SIX -> 28.sp * appScale()
                        SudokuSize.NINE -> 18.sp * appScale()
                    },
                    fontFamily = FontFamily(
                        Font(if (puzzleGiven) R.font.font_semibold else R.font.font_extra_bold)
                    ),
                    color = when {
                        puzzleGiven -> Color.Black
                        isConflict -> Color.Red
                        else -> PrimaryBlue
                    }
                )
            } else {
                if (vm.showCandidates &&
                    vm.selected?.first == row &&
                    vm.selected?.second == col
                ) {
                    val cands = vm.candidatesForSelected()
                    Text(
                        text = cands.joinToString(" "),
                        fontSize = when (vm.puzzle.size) {
                            SudokuSize.FOUR -> 24.sp * appScale()
                            SudokuSize.SIX -> 14.sp * appScale()
                            SudokuSize.NINE -> 10.sp * appScale()
                        },
                        fontFamily = FontFamily(
                            Font( R.font.font_medium)
                        ),
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens2),
                        maxLines = 3,
                        softWrap = true
                    )
                }
            }
        }
    }
}
