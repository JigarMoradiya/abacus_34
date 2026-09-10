package com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.play

import com.jigar.me.ui.view.home.navigation.safePopBackStack
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.shadow
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
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.viewmodels.NumberSequencePuzzleViewModel
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.offset
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import kotlinx.coroutines.delay

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
            navController.safePopBackStack()
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

    if (uiState.isSolved) {
        PuzzleResultOverlay(
            moveCount = uiState.moveCount,
            onPlayAgain = { viewModel.playAgain() },
            onBack = {
                viewModel.closePopup()
                navController.safePopBackStack()
            }
        )
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

// "Candy Pop" celebration card — matches Number Snake's result popup style
// (gradient card, white badge overlapping the top edge, two symmetric filled
// buttons) instead of the generic CustomPopupView celebration theme.
@Composable
private fun PuzzleResultOverlay(
    moveCount: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    val accentColors = getButtonColors(ButtonType.ORANGE)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { AudioPlayerManager.playSoundClap() }
        Box(modifier = Modifier.fillMaxWidth(0.6f), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(AppDimens.Dimens20 * 1.2f), ambientColor = accentColors.base, spotColor = accentColors.base)
                    .background(accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20 * 1.2f))
                    .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens20),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Spacer(Modifier.height(AppDimens.Dimens20))
                Text(
                    stringResource(R.string.you_did_it),
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    fontSize = 26.sp.scaled(),
                    style = TextStyle(shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)),
                    textAlign = TextAlign.Center
                )
                Text(
                    stringResource(R.string.completed_in_b_moves_b, moveCount).replace("<b>", "").replace("</b>", ""),
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                    fontSize = 18.sp.scaled(),
                    textAlign = TextAlign.Center
                )
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = stringResource(R.string.continue_to_play), type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = stringResource(R.string.no_i_want_to_close), type = ButtonType.NEGATIVE,
                        onClick = { if (enabled) onBack() })
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = (-28).dp)
                    .size(64.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🧩", fontSize = (32f * gameScale()).sp)
            }
        }
    }
}
