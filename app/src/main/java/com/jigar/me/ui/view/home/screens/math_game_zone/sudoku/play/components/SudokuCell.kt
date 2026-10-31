package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abWobble
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator.SudokuBoxRules
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPlayViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuSize
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2

/**
 * One candy cell of the toy board (Android twin of iOS `SudokuCellView`): given digits sit on white, empty cells carry a
 * pastel that alternates box by box, the selected cell glows and its row / column / box are washed yellow, a digit bounces
 * in when it is placed and shakes when it clashes.
 */
@Composable
fun SudokuCell(vm: SudokuPlayViewModel, row: Int, col: Int, sizeDp: Dp) {
    val colors = LocalAbColors.current

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

    // given digits sit on white; the empty cells carry a pastel that alternates box by box, so the boxes read without lines
    val oddBox: Boolean = ((row / brc.first) + (col / brc.second)) % 2 == 1
    val emptyFill: Color = if (oddBox) colors.accentPurple.copy(alpha = 0.16f) else colors.brandSecondary.copy(alpha = 0.18f)
    val baseFill: Color = if (puzzleGiven) Color.White else emptyFill

    // the selected cell is washed blue, the cells that share its row / column / box yellow
    val related: Boolean = inSameRow || inSameCol || inSameBox
    val highlightColor: Color = if (isSelected) colors.brandPrimary.copy(alpha = 0.22f) else colors.accentYellow.copy(alpha = 0.35f)
    val highlightAmount: Float by animateFloatAsState(
        targetValue = if (isSelected || related) 1f else 0f,
        animationSpec = tween(180),
        label = "sudokuHighlight"
    )
    val selectionAmount: Float by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "sudokuSelection"
    )
    val primary: Color = colors.brandPrimary

    // square cells that meet edge to edge: the board draws the grid lines over them
    val tileCorner: Dp = 0.dp
    val tilePad: Dp = 0.dp
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(sizeDp) // ✅ DO NOT TOUCH THIS
            .clickable(interactionSource = interaction, indication = null, enabled = !puzzleGiven) {
                vm.selectCell(row, col)
            },
        contentAlignment = Alignment.Center
    ) {

        // 🔥 Inner styling (safe): the candy tile, its wash and the selection glow are drawn inside the cell
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(tilePad)
                .drawBehind {
                    val corner = tileCorner.toPx()
                    val radius = CornerRadius(corner, corner)
                    val sel: Float = selectionAmount.coerceIn(0f, 1f)
                    val hi: Float = highlightAmount.coerceIn(0f, 1f)

                    // soft glow rings just outside the selected tile
                    if (sel > 0.01f) {
                        for (k in 1..2) {
                            val grow = (k * 2f - 1f).dp.toPx()
                            drawRoundRect(
                                color = primary.copy(alpha = (0.34f / k) * sel),
                                topLeft = Offset(-grow, -grow),
                                size = Size(this.size.width + grow * 2f, this.size.height + grow * 2f),
                                cornerRadius = CornerRadius(corner + grow, corner + grow),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }

                    drawRoundRect(color = baseFill, cornerRadius = radius)
                    if (hi > 0f) {
                        drawRoundRect(color = highlightColor.copy(alpha = highlightColor.alpha * hi), cornerRadius = radius)
                    }

                    // the bright outline of the selected cell
                    if (sel > 0.01f) {
                        val w = 3.dp.toPx()
                        drawRoundRect(
                            color = primary.copy(alpha = sel),
                            topLeft = Offset(w / 2f, w / 2f),
                            size = Size(this.size.width - w, this.size.height - w),
                            cornerRadius = CornerRadius(corner - w / 2f, corner - w / 2f),
                            style = Stroke(width = w)
                        )
                    }
                }
        )

        // The digit (or the candidates) lives in a box that is always there, so a digit placed in an empty cell bounces too.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(tilePad)
                .abBounce(value)
                .abWobble(isConflict),
            contentAlignment = Alignment.Center
        ) {

            if (value != 0) {
                Text(
                    text = value.toString(),
                    style = AbTypography.fixed(
                        when (vm.puzzle.size) {
                            SudokuSize.FOUR -> 40f * appScale()
                            SudokuSize.SIX -> 28f * appScale()
                            SudokuSize.NINE -> 18f * appScale()
                        },
                        if (puzzleGiven) FontWeight.ExtraBold else FontWeight.Black
                    ),
                    color = when {
                        puzzleGiven -> colors.textPrimary
                        isConflict -> colors.danger
                        else -> colors.brandPrimary
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
                        style = AbTypography.fixed(
                            when (vm.puzzle.size) {
                                SudokuSize.FOUR -> 24f * appScale()
                                SudokuSize.SIX -> 14f * appScale()
                                SudokuSize.NINE -> 10f * appScale()
                            },
                            FontWeight.Bold
                        ),
                        color = colors.accentGreenInk,
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
