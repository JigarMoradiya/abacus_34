package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.min
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator.SudokuBoxRules
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2

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
            modifier = Modifier
                .size(side)
                .clip(RoundedCornerShape(Dimens16)) // outer board shape
                .background(Color(0xFFFFF8E1))   // soft kids bg
        ) {

            // 🔹 GRID CELLS (no spacing!)
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

            // 🔹 BOX BORDERS (draw on top)
            Canvas(modifier = Modifier.matchParentSize()) {

                val cell = size.width / n

                val boxW = cell * boxCols
                val boxH = cell * boxRows

                val rows = n / boxRows
                val cols = n / boxCols

                val radius = Dimens16.toPx()

                for (br in 0 until rows) {
                    for (bc in 0 until cols) {

                        val isTop = br == 0
                        val isBottom = br == rows - 1
                        val isLeft = bc == 0
                        val isRight = bc == cols - 1

                        val roundRect = RoundRect(
                            rect = Rect(
                                offset = Offset(bc * boxW, br * boxH),
                                size = Size(boxW, boxH)
                            ),
                            topLeft = if (isTop && isLeft) CornerRadius(radius) else CornerRadius.Zero,
                            topRight = if (isTop && isRight) CornerRadius(radius) else CornerRadius.Zero,
                            bottomLeft = if (isBottom && isLeft) CornerRadius(radius) else CornerRadius.Zero,
                            bottomRight = if (isBottom && isRight) CornerRadius(radius) else CornerRadius.Zero
                        )

                        drawPath(
                            path = Path().apply { addRoundRect(roundRect) },
                            color = Color(0xFF6D4C41),
                            style = Stroke(width = Dimens2.toPx())
                        )
                    }
                }
            }
        }
    }
}
