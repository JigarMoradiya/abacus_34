package com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jigar.me.R
import com.jigar.me.ui.view.home.common.BackButtonWithText
import com.jigar.me.ui.view.home.common.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.viewmodels.NumberSequencePuzzleViewModel

@Composable
fun NumberSequencePuzzleJetpackScreen(
    navController: NavController,
    gridSize: Int,
    viewModel: NumberSequencePuzzleViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var tileColors by remember { mutableStateOf(listOf<List<Color>>()) }

    val spacing = if (gridSize == 5) { 8.dp } else if (gridSize == 4) { 10.dp } else { 12.dp }
    val opacity = 0.4f

    Box(modifier = Modifier.fillMaxSize()) {
        val title = when (gridSize) {
            3 -> stringResource(R.string._3_3_puzzle)
            4 -> stringResource(R.string._4_4_puzzle)
            else -> stringResource(R.string._5_5_puzzle)
        }
        BackButtonWithText(title = title, onBackClick = {
            navController.popBackStack()
        })
        Column(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val maxWidth = maxWidth

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LeftPanel(
                        moveCount = uiState.moveCount,
                        modifier = Modifier.width(maxWidth * 0.25f)
                    )

                    PuzzleBoard(
                        gridSize = gridSize,
                        tiles = uiState.tiles,
                        spacing = spacing,
                        opacity = opacity,
                        tileColors = tileColors,
                        onTileMove = { row, col -> viewModel.onTileMove(row, col) },
                        modifier = Modifier.width(maxWidth * 0.5f)
                    )

                    RightPanel(
                        soundOn = uiState.soundOn,
                        onSoundToggle = { viewModel.toggleSound() },
                        onRestart = { viewModel.restartGame() },
                        modifier = Modifier.width(maxWidth * 0.25f)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.isSolved,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CustomPopupView(
                title = stringResource(R.string.you_did_it),
                description = stringResource(R.string.completed_in_b_moves_b, uiState.moveCount),
                positiveButtonText = stringResource(R.string.continue_to_play),
                negativeButtonText = stringResource(R.string.no_i_want_to_close),
                icon = R.drawable.ic_complete,
                widthMultiplier = 0.5f,
                onPositiveTapped = { viewModel.playAgain() },
                onNegativeTapped = {
                    viewModel.closePopup()
                    navController.popBackStack()
                }
            )
        }
    }

    LaunchedEffect(gridSize) {
        viewModel.initialize(gridSize)
        tileColors = List(gridSize) {
            List(gridSize) {
                viewModel.randomTileColor(gridSize)
            }
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
            Text(
                "Start New Game", color = Color(0xFF006400),
                fontSize = dimensionResource(id = R.dimen.textSizeLarge).value.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PuzzleBoard(
    gridSize: Int,
    tiles: List<List<Int?>>,
    spacing: Dp,
    opacity: Float,
    tileColors: List<List<Color>>,
    onTileMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (gridSize) {
        3 -> Color(0xFFF33173).copy(alpha = opacity)
        4 -> Color(0xFFFF9800).copy(alpha = opacity)
        else -> Color(0xFF2196F3).copy(alpha = opacity)
    }.copy(alpha = 0.15f)

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val spacingPx = with(LocalDensity.current) { spacing.toPx() }
        val availableWidth = constraints.maxWidth.toFloat()
        val availableHeight = constraints.maxHeight.toFloat() * 0.8f

        val horizontalTileSizePx =
            (availableWidth - ((gridSize - 1) * spacingPx)) / gridSize
        val verticalTileSizePx =
            (availableHeight - ((gridSize - 1) * spacingPx)) / gridSize
        val tileSizePx = minOf(horizontalTileSizePx, verticalTileSizePx)

        val tileSize = with(LocalDensity.current) { tileSizePx.toDp() }

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
                                color = tileColors[row][col],
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
