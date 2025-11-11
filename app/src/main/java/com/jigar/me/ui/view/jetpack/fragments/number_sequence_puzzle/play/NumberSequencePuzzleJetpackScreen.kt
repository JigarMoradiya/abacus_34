package com.jigar.me.ui.view.jetpack.fragments.number_sequence_puzzle.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.PlaySound


@Composable
fun NumberSequencePuzzleJetpackScreen(
    navController: NavController,prefManager : AppPreferencesHelper,
    gridSize: Int
) {
    val spacing = 12.dp
    val opacity = 0.4f
    val context = LocalContext.current
    var tiles by remember { mutableStateOf(generateSolvableGrid(gridSize)) }
    var moveCount by remember { mutableIntStateOf(0) }
    var isSolved by remember { mutableStateOf(false) }
    var soundOn by remember { mutableStateOf(prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_NumberPuzzleVolume,true)) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 🔹 Header
        HeaderToolbar(
            title = when (gridSize) {
                3 -> "3×3 Puzzle"
                4 -> "4×4 Puzzle"
                else -> "5×5 Puzzle"
            },
            onBackClick = { navController.popBackStack() }
        )
        Column(modifier = Modifier.fillMaxSize()) {

            // 🔹 Main content area
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val maxWidth = maxWidth

                // Divide layout like SwiftUI: left (moves), center (puzzle), right (buttons)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT SIDE – Move Count
                    LeftPanel(moveCount = moveCount, modifier = Modifier.width(maxWidth * 0.25f))

                    // CENTER – Puzzle
                    PuzzleBoard(
                        gridSize = gridSize,
                        tiles = tiles,
                        spacing = spacing,
                        opacity = opacity,
                        onTileMove = { row, col ->
                            val (newTiles, moved) = moveTile(tiles, row, col)
                            if (moved) {
                                tiles = newTiles
                                moveCount++
                                val isSolve = checkSolved(newTiles)
                                if (isSolve) {
                                    if (soundOn){
                                        PlaySound.play(context,PlaySound.number_puzzle_win)
                                    }
                                    isSolved = true
                                }else{
                                    if (soundOn){
                                        PlaySound.play(context,PlaySound.swap_sound)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .width(maxWidth * 0.5f)
                    )

                    // RIGHT SIDE – Controls
                    RightPanel(
                        soundOn = soundOn,
                        onSoundToggle = {
                            soundOn = !soundOn
                            prefManager.setCustomParamBoolean(AppConstants.Settings.Setting_NumberPuzzleVolume,soundOn)
                                        },
                        onRestart = {
                            tiles = generateSolvableGrid(gridSize)
                            moveCount = 0
                            isSolved = false
                        },
                        modifier = Modifier.width(maxWidth * 0.25f)
                    )
                }
            }
        }

        // 🔹 Solved Popup Overlay
        AnimatedVisibility(
            visible = isSolved,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CustomPopupView(
                title = "🎉 You Did It! 🎉",
                description = "Completed in <b>$moveCount moves!</b>",
                positiveButtonText = "Continue to Play",
                negativeButtonText = "No, I want to close",
                icon = R.drawable.ic_complete,
                widthMultiplier = 0.5f,
                onPositiveTapped = {
                    isSolved = false
                    tiles = generateSolvableGrid(gridSize)
                    moveCount = 0
                },
                onNegativeTapped = {
                    isSolved = false
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun HeaderToolbar(
    title: String = "Number Sequences Puzzle", onBackClick: () -> Unit
) {
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                onClick = { onBackClick() },
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.light_back)
                ),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.menu_icons_corner)),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = dimensionResource(id = R.dimen.card_elevation)
                ),
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.icons_margin2))
                    .size(dimensionResource(id = R.dimen.menu_icons))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.menu_icons))
                        .padding(dimensionResource(id = R.dimen.menu_icons_padding))
                )
            }

            Text(
                text = title,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                fontSize = dimensionResource(id = R.dimen.textSizeSuperExtraLarge).value.sp,
                color = colorResource(id = R.color.colorPrimaryDark),
            )

        }
    }
}

@Composable
private fun LeftPanel(moveCount: Int, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = moveCount.toString(),
            fontSize = dimensionResource(id = R.dimen.textSize36).value.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Text(
            text = "Current Moves",
            fontSize = dimensionResource(id = R.dimen.textSizeLarge).value.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.7f)
        )
    }
}


@Composable
private fun RightPanel(
    soundOn: Boolean,
    onSoundToggle: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxHeight()
    ) {
        // Sound toggle
        Button(
            onClick = onSoundToggle,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f)),
            shape = CircleShape
        ) {
            val icon = if (soundOn) R.drawable.ic_volume_on else R.drawable.ic_volume_off
            val label = if (soundOn) "Volume On" else "Volume Off"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(label, color = Color.Red, fontSize = dimensionResource(id = R.dimen.textSizeLarge).value.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Restart
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green.copy(alpha = 0.1f)),
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_start_new_game),
                contentDescription = null,
                tint = Color(0xFF006400),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Start New Game", color = Color(0xFF006400),
                fontSize = dimensionResource(id = R.dimen.textSizeLarge).value.sp,
                fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun PuzzleBoard(
    gridSize: Int,
    tiles: List<List<Int?>>,
    spacing: Dp,
    opacity: Float,
    onTileMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tileColor = when (gridSize) {
        3 -> Color(0xFF9C27B0).copy(alpha = opacity)
        4 -> Color(0xFFFF9800).copy(alpha = opacity)
        else -> Color(0xFF2196F3).copy(alpha = opacity)
    }

    val backgroundColor = tileColor.copy(alpha = 0.15f)

    // ✅ Equivalent to SwiftUI's GeometryReader
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val spacingPx = with(LocalDensity.current) { spacing.toPx() }
        val availableWidth = constraints.maxWidth.toFloat()
        val availableHeight = constraints.maxHeight.toFloat() * 0.8f

        // Same math as your SwiftUI code:
        val horizontalTileSizePx =
            (availableWidth - ((gridSize - 1) * spacingPx)) / gridSize
        val verticalTileSizePx =
            (availableHeight - ((gridSize - 1) * spacingPx)) / gridSize
        val tileSizePx = minOf(horizontalTileSizePx, verticalTileSizePx)

        // Convert back to Dp for Compose UI
        val tileSize = with(LocalDensity.current) { tileSizePx.toDp() }

        // ✅ Now draw the board centered with dynamic tile size
        Box(
            modifier = Modifier
                .background(backgroundColor, RoundedCornerShape(20.dp))
                .padding(spacing)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until gridSize) {
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                        for (col in 0 until gridSize) {
                            val num = tiles[row][col]
                            TileView(
                                number = num,
                                size = tileSize,
                                color = tileColor,
                                onClick = { onTileMove(row, col) }
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
private fun TileView(number: Int?, size: Dp, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size)
            .clickable(enabled = number != null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (number != null) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    fontSize = (size.value * 0.6f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold))
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(size)
                    .border(
                        width = 1.dp,
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
            )


        }
    }
}


private fun generateSolvableGrid(gridSize: Int, scrambleMoves: Int = 200): List<List<Int?>> {
    val grid = goalGrid(gridSize).map { it.toMutableList() }
    var empty = gridSize - 1 to gridSize - 1

    repeat(scrambleMoves) {
        val neighbors = listOfNotNull(
            (empty.first - 1).takeIf { it >= 0 }?.let { it to empty.second },
            (empty.first + 1).takeIf { it < gridSize }?.let { it to empty.second },
            (empty.second - 1).takeIf { it >= 0 }?.let { empty.first to it },
            (empty.second + 1).takeIf { it < gridSize }?.let { empty.first to it }
        )
        val pick = neighbors.random()
        grid[empty.first][empty.second] = grid[pick.first][pick.second]
        grid[pick.first][pick.second] = null
        empty = pick
    }

    return grid
}

private fun goalGrid(gridSize: Int): List<List<Int?>> {
    var num = 1
    return List(gridSize) { r ->
        List(gridSize) { c ->
            if (r == gridSize - 1 && c == gridSize - 1) null else num++
        }
    }
}

private fun moveTile(
    tiles: List<List<Int?>>,
    row: Int,
    col: Int
): Pair<List<List<Int?>>, Boolean> {
    val grid = tiles.map { it.toMutableList() }
    val emptyPos = findEmpty(grid) ?: return tiles to false
    val (er, ec) = emptyPos
    var moved = false

    if (row == er) {
        val dir = if (col < ec) 1 else -1
        var current = ec
        while (current != col) {
            val next = current - dir
            grid[er][current] = grid[er][next]
            current = next
            moved = true
        }
        grid[row][col] = null
    } else if (col == ec) {
        val dir = if (row < er) 1 else -1
        var current = er
        while (current != row) {
            val next = current - dir
            grid[current][ec] = grid[next][ec]
            current = next
            moved = true
        }
        grid[row][col] = null
    }

    return grid to moved
}

private fun findEmpty(grid: List<List<Int?>>): Pair<Int, Int>? {
    for (r in grid.indices) {
        for (c in grid[r].indices) {
            if (grid[r][c] == null) return r to c
        }
    }
    return null
}

private fun checkSolved(grid: List<List<Int?>>): Boolean {
    val flat = grid.flatten()
    val correct = (1 until grid.size * grid.size).map { it } + listOf(null)
    return flat == correct
}