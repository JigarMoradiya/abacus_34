package com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components.SudokuBoxRules
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components.SudokuPlayViewModel
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components.SudokuSize
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.ceil

@AndroidEntryPoint
class SudokuPlayFragment : Fragment() {

    private val viewModel: SudokuPlayViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navController = findNavController()
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    SudokuPlayScreen(
                        navController = navController,
                        vm = viewModel,
                    )
                }
            }
        }
    }
}


@Composable
fun SudokuPlayScreen(
    navController: NavController,
    vm: SudokuPlayViewModel,
    modifier: Modifier = Modifier
) {
    if (vm.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val size = vm.size
    val difficulty = vm.difficulty
    Box(modifier = Modifier.fillMaxSize()) {
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

            // Board + Right column controls (responsive)
            Row(modifier = Modifier.fillMaxSize()) {
                // Board (left)
                Box(modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center) {
                    SudokuBoard(vm = vm)
                }

                // Number pad + actions (right)
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding12),
                        Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    // message
                    if (vm.message.isNullOrEmpty()){
                        Text("", color = Color.Red, modifier = Modifier.padding(8.dp))
                    }else{
                        Text(vm.message?:"", color = Color.Red, modifier = Modifier.padding(8.dp))
                    }

                    Row {
                        Button(
                            onClick = { vm.toggleCandidates() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),   // Green
                                contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp, vertical = 8.dp
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (vm.showCandidates) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )

                                Text(
                                    text = if (vm.showCandidates) stringResource(R.string.hide_options)
                                    else stringResource(R.string.show_options),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { vm.revealOneNumber() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800),   // Orange
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp, vertical = 8.dp
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                // 🔢 Top: used/limit
                                Text(
                                    text = "${vm.hintUsed}/${vm.hintLimit}",
                                    fontSize = dimensionResource(R.dimen.textSize18).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_bold)),
                                    modifier = Modifier.height(24.dp)
                                )

                                // 🧩 Bottom: word "Hints"
                                Text(
                                    text = stringResource(R.string.hints),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { vm.resetPuzzle() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935),   // Red
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp, vertical = 8.dp
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
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
}

@Composable
fun SudokuBoard(vm: SudokuPlayViewModel, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier.padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        val n = vm.puzzle.size.grid
        val (boxRows, boxCols) = SudokuBoxRules.boxSize(vm.puzzle.size)

        // board dimensions
        val side = min(maxWidth, maxHeight)
        val cellSize = side / n

        Box(
            modifier = Modifier.size(side),
            contentAlignment = Alignment.TopStart
        ) {
            // 1) DRAW CELLS ---------------------------------------------
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

            // 2) DRAW BOX STROKES (BIG SQUARES) -------------------------
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
                            style = Stroke(width = 1.dp.toPx())
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
            // check row & col & box
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

    Box(modifier = Modifier
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
                        .padding(2.dp)
                        .fillMaxWidth(),
                    text = cands.joinToString(" "),
                    fontSize = 10.sp,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.copy(
                        lineHeight = 11.sp,       // smaller line spacing
                        letterSpacing = 1.sp   // optional: tighten spacing between digits
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

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            row1.forEach { n ->
                NumberKey(n) { vm.enter(n) }
            }
            if (row2.isEmpty()) {
                EraseKey{ vm.eraseSelected() }
            }
        }
        if (row2.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row2.forEach { n -> NumberKey(n) { vm.enter(n) } }
                EraseKey{ vm.eraseSelected() }
            }
        }
    }
}

@Composable
fun EraseKey(onClick: () -> Unit) {
    Box(modifier = Modifier
        .size(width = 48.dp, height = 36.dp)
        .background(
            colorResource(R.color.red_400), shape = RoundedCornerShape(8.dp)
        )
        .clickable {
            onClick()
        },contentAlignment = Alignment.Center){
        Icon(
            painter = painterResource(R.drawable.ic_backspace), // Replace with your Backspace/Erase Icon
            contentDescription = "Erase",
            tint = colorResource(R.color.white),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun NumberKey(n: Int, onClick: () -> Unit) {
    Box(modifier = Modifier
        .size(width = 48.dp, height = 36.dp)
        .background(colorResource(R.color.colorPrimary), shape = RoundedCornerShape(8.dp))
        .clickable {
            onClick()
        },contentAlignment = Alignment.Center){
        Text(text = "$n",
            color = colorResource(R.color.white),
            fontSize = dimensionResource(id = R.dimen.textSize24).value.sp,
            fontFamily = FontFamily(Font(R.font.font_bold)))
    }
}