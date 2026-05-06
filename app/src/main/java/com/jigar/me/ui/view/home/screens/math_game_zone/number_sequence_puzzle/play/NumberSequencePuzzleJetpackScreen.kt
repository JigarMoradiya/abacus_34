package com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.play

import com.jigar.me.ui.view.home.theme.AppDimens

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorYellowOrange
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.viewmodels.NumberSequencePuzzleViewModel
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun NumberSequencePuzzleJetpackScreen(
    navController: NavController,
    gridSize: Int,
    viewModel: NumberSequencePuzzleViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var tileColors by remember { mutableStateOf(listOf<List<Color>>()) }

    val spacing = if (gridSize == 5) { AppDimens.Dimens8 } else if (gridSize == 4) { AppDimens.Dimens10 } else { AppDimens.Dimens12 }
    val opacity = 0.4f

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
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
                val leftRightWidth = 0.25f
                val centerWidth = 0.50f
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LeftPanel(
                        moveCount = uiState.moveCount,
                        modifier = Modifier.width(maxWidth * leftRightWidth)
                    )

                    if (uiState.tiles.size == gridSize && tileColors.size == gridSize) {
                        PuzzleBoard(
                            gridSize = gridSize,
                            tiles = uiState.tiles,
                            spacing = spacing,
                            opacity = opacity,
                            tileColors = tileColors,
                            onTileMove = { row, col -> viewModel.onTileMove(row, col) },
                            modifier = Modifier.width(maxWidth * centerWidth)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(maxWidth * centerWidth))
                    }

                    RightPanel(
                        soundOn = uiState.soundOn,
                        onSoundToggle = { viewModel.toggleSound() },
                        onRestart = { viewModel.restartGame() },
                        modifier = Modifier.width(maxWidth * leftRightWidth)
                    )


                }
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

    if (uiState.isSolved){
        ConfettiRainEffect()
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
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxHeight()
    ) {
        Card(
            modifier = modifier
                .wrapContentSize(),
            shape = RoundedCornerShape(Dimens16),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3CD) // soft yellow
            ),
            elevation = CardDefaults.cardElevation(Dimens4)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = Dimens24, vertical = Dimens16),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Fun emoji/icon
                Text(
                    text = "🎯",
                    style = MaterialTheme.typography.displaySmall.scaled(),
                )

                Spacer(modifier = Modifier.height(Dimens8))

                // Move count
                Text(
                    text = moveCount.toString(),
                    style = MaterialTheme.typography.displayLarge.scaled(),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFF6B00) // playful orange
                )

                Spacer(modifier = Modifier.height(Dimens4))

                // Label
                Text(
                    text = stringResource(R.string.current_moves),
                    style = MaterialTheme.typography.titleMedium.scaled(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4E4E4E)
                )
            }
        }
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
        KidsActionButton(
            text = if (soundOn) stringResource(R.string.volume_on) else stringResource(R.string.volume_off),
            icon = if (soundOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
            type = ButtonType.ORANGE,
            isSmall = true,
            onClick = onSoundToggle,
        )

        Spacer(Modifier.height(AppDimens.Dimens12))

        KidsActionButton(
            text = stringResource(R.string.start_new_game),
            icon = Icons.Filled.SportsEsports,
            type = ButtonType.GREEN,
            isSmall = true,
            onClick = onRestart,
        )
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
        4 -> ColorYellowOrange.copy(alpha = opacity)
        else -> Color(0xFF2196F3).copy(alpha = opacity)
    }.copy(alpha = 0.15f)

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val spacingPx = with(LocalDensity.current) { spacing.toPx() }
        val availableWidth = constraints.maxWidth.toFloat()
        val availableHeight = constraints.maxHeight.toFloat() * 0.8f

        val horizontalTileSizePx = (availableWidth - ((gridSize - 1) * spacingPx)) / gridSize
        val verticalTileSizePx = (availableHeight - ((gridSize - 1) * spacingPx)) / gridSize
        val tileSizePx = minOf(horizontalTileSizePx, verticalTileSizePx)

        val tileSize = with(LocalDensity.current) { tileSizePx.toDp() }

        Box(
            modifier = Modifier
                .background(backgroundColor, RoundedCornerShape(AppDimens.Dimens20))
                .padding(spacing)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until gridSize) {
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                        for (col in 0 until gridSize) {
                            val num = tiles.getOrNull(row)?.getOrNull(col)
                            TileView(
                                number = num,
                                size = tileSize,
                                color = tileColors.getOrNull(row)?.getOrNull(col) ?: Color.Transparent,
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
                    .clip(RoundedCornerShape(AppDimens.Dimens10))
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
                        width = AppDimens.Dimens1,
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(AppDimens.Dimens12)
                    )
            )
        }
    }
}
