package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator.SudokuBoxRules
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens

/**
 * The Sudoku board on a purple toy tray (Android twin of iOS `SudokuBoardView`): candy cells inside, a soft rounded outline
 * around every box, and a thick rim + 3D edge tray under it all. The cells keep exactly the sizes they had; the tray is drawn
 * outward into the margin around the board.
 */
@Composable
fun SudokuBoard(vm: SudokuPlayViewModel, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val accent: Color = colors.accentPurple

    BoxWithConstraints(
        modifier = modifier.padding(AppDimens.Dimens12),
        contentAlignment = Alignment.Center
    ) {

        val n = vm.puzzle.size.grid
        val (boxRows, boxCols) = SudokuBoxRules.boxSize(vm.puzzle.size)

        val side = min(maxWidth, maxHeight)
        val cellSize = side / n

        // the tray's rim hugs the cells (it is the grid's outer border) and the tray drops 4dp at the bottom
        val rim = 5.dp
        val reach = rim
        val drop = 4.dp
        val trayCorner = 14.dp
        val trayShape = RoundedCornerShape(trayCorner)

        Box(
            modifier = Modifier.size(side),
            contentAlignment = Alignment.Center
        ) {

            // 🔹 TOY TRAY (behind the cells; bigger than the board, so it ignores the incoming size)
            Box(
                modifier = Modifier
                    .requiredSize(side + reach * 2)
                    .drawBehind {
                        drawRoundRect(
                            color = accent.abDarkened(0.3f),
                            topLeft = Offset(0f, drop.toPx()),
                            size = this.size,
                            cornerRadius = CornerRadius(trayCorner.toPx())
                        )
                    }
                    .shadow(12.dp, trayShape, clip = false)
                    .background(Color.White.copy(alpha = 0.94f), trayShape)
                    .border(rim, accent, trayShape)
            )

            Box(modifier = Modifier.size(side)) {

                // 🔹 GRID CELLS (no spacing!): square cells that meet edge to edge; the lines below draw the grid
                Column(modifier = Modifier.clip(RoundedCornerShape(trayCorner - rim))) {
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

                // 🔹 GRID LINES (drawn on top, never touched): a thin line between EVERY pair of cells so every row and column
                // shows all its boxes, a thick ONE line on each box boundary (a rounded outline per box drew two parallel lines
                // wherever two boxes meet) and the tray rim around it as the outer border.
                Canvas(modifier = Modifier.matchParentSize()) {

                    val cell = this.size.width / n

                    val boxW = cell * boxCols
                    val boxH = cell * boxRows

                    val rows = n / boxRows
                    val cols = n / boxCols

                    val thinW = 2.dp.toPx()
                    val boxLineW = 3.5.dp.toPx()
                    val thin = accent.copy(alpha = 0.8f)
                    val thick = accent
                    val full = this.size.width

                    for (k in 1 until n) {
                        val p = k * cell
                        drawLine(thin, Offset(p, 0f), Offset(p, full), thinW)
                        drawLine(thin, Offset(0f, p), Offset(full, p), thinW)
                    }
                    for (bc in 1 until cols) {
                        val x = bc * boxW
                        drawLine(thick, Offset(x, 0f), Offset(x, full), boxLineW)
                    }
                    for (br in 1 until rows) {
                        val y = br * boxH
                        drawLine(thick, Offset(0f, y), Offset(full, y), boxLineW)
                    }
                }
            }
        }
    }
}
