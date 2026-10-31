package com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.play

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

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
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.offset
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
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
                            isSolved = uiState.isSolved,
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

    // Debug builds only (`--ez ab_show_result true`): shows the real result overlay without solving a puzzle.
    if (AbDebugFlags.showResultEnabled && !uiState.isSolved) {
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
    val colors = LocalAbColors.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxHeight()
    ) {
        // The "Current Moves" box: the count pops every time it changes.
        AbGlassPanel {
            Column(
                modifier = Modifier.padding(horizontal = Dimens12, vertical = Dimens16),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens8)
            ) {
                Text(
                    text = moveCount.toString(),
                    style = AbTypography.fixed(30f * appScale(), FontWeight.Black),
                    color = colors.accentOrange.abReadable(3f),
                    modifier = Modifier.abBounce(moveCount)
                )
                Text(
                    text = stringResource(R.string.current_moves),
                    style = AbTypography.fixed(17f * appScale(), FontWeight.ExtraBold),
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
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
        GameButton(
            text = if (soundOn) stringResource(R.string.volume_on) else stringResource(R.string.volume_off),
            icon = if (soundOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
            type = ButtonType.ORANGE,
            isSmall = true,
            onClick = onSoundToggle,
        )

        Spacer(Modifier.height(AppDimens.Dimens12))

        GameButton(
            text = stringResource(R.string.start_new_game),
            icon = Icons.Filled.SportsEsports,
            type = ButtonType.GREEN,
            isSmall = true,
            onClick = onRestart,
        )
    }
}

// Lets a decoration reach past its content: the content keeps the layout size it would have had, but the node is
// measured and placed bigger (the reverse of padding), so the tray's 3D edge can hang below the board without growing it.
private fun Modifier.puzzleOutset(bottom: Dp): Modifier = layout { measurable, constraints ->
    val b = bottom.roundToPx()
    val placeable = measurable.measure(constraints.offset(vertical = b))
    layout(placeable.width, placeable.height - b) { placeable.place(0, 0) }
}

// The board is a toy tray in the game's blue. Underneath, one recessed slot per position takes the taps exactly as
// before (a tile's slot calls onTileMove for its row and column). On top, every numbered tile is keyed by its number and
// placed by an offset, so when the tiles change each tile glides to its new spot on a spring.
@Composable
private fun PuzzleBoard(
    gridSize: Int,
    tiles: List<List<Int?>>,
    spacing: Dp,
    isSolved: Boolean,
    onTileMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAbColors.current
    val accent: Color = gameCardStyle(GameCategoryType.NUMBER_SEQUENCE_PUZZLE).light
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val spacingPx = with(density) { spacing.toPx() }
        val availableWidth = constraints.maxWidth.toFloat()
        val availableHeight = constraints.maxHeight.toFloat() * 0.8f

        val horizontalTileSizePx = (availableWidth - ((gridSize - 1) * spacingPx)) / gridSize
        val verticalTileSizePx = (availableHeight - ((gridSize - 1) * spacingPx)) / gridSize
        val tileSizePx = minOf(horizontalTileSizePx, verticalTileSizePx)

        val tileSize = with(density) { tileSizePx.toDp() }
        // Slots lay out at rounded px sizes, so the tiles above them use the same rounded step.
        val pitchPx = with(density) { tileSize.roundToPx() + spacing.roundToPx() }

        // Where every numbered tile is right now: number -> (row, col).
        val positions = HashMap<Int, Pair<Int, Int>>()
        for (r in tiles.indices) {
            for (c in tiles[r].indices) {
                val n = tiles[r][c]
                if (n != null) positions[n] = Pair(r, c)
            }
        }
        // One colour per row of the finished puzzle, so the solved board is a rainbow of rows and a tile's colour tells
        // the kid which row it belongs to.
        val rowPalette = listOf(colors.accentPink, colors.accentOrange, colors.accentGreen, colors.brandSecondary, colors.accentPurple)

        Box(
            modifier = Modifier
                .puzzleOutset(bottom = 6.dp)
                .abToyTray(accent, spacing)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until gridSize) {
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                        for (col in 0 until gridSize) {
                            val hasTile = tiles.getOrNull(row)?.getOrNull(col) != null
                            val slotShape = RoundedCornerShape(tileSize * 0.22f)
                            Box(
                                modifier = Modifier
                                    .size(tileSize)
                                    .background(accent.copy(alpha = 0.16f), slotShape)
                                    .border(2.dp, accent.copy(alpha = 0.28f), slotShape)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        enabled = hasTile
                                    ) { onTileMove(row, col) }
                            )
                        }
                    }
                }
            }

            for (n in 1 until gridSize * gridSize) {
                val pos = positions[n] ?: continue
                PuzzleSlidingTile(
                    number = n, row = pos.first, col = pos.second, size = tileSize, pitchPx = pitchPx,
                    face = rowPalette[((n - 1).coerceAtLeast(0) / gridSize.coerceAtLeast(1)) % rowPalette.size].abReadable(3f),
                    solved = isSolved
                )
            }
        }
    }
}

// One sliding tile: a candy square in its row colour with a big white number. It fills its whole square (face plus the 3D
// edge), glides to its new spot on a spring, and does a little hop when the puzzle is solved. It never takes a tap.
@Composable
private fun PuzzleSlidingTile(number: Int, row: Int, col: Int, size: Dp, pitchPx: Int, face: Color, solved: Boolean) {
    val target = IntOffset(col * pitchPx, row * pitchPx)
    val animated by animateIntOffsetAsState(
        targetValue = target,
        animationSpec = spring<IntOffset>(dampingRatio = 0.62f, stiffness = 340f),
        label = "puzzleTileSlide"
    )
    Box(
        modifier = Modifier
            .offset { animated }
            .size(size)
            .abPopIn(minOf(number * 30, 400))
            .abBounce(solved, 1.12f)
            .abToyTile(face, size * 0.22f, maxOf(3.dp, size * 0.06f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            color = Color.White, maxLines = 1, softWrap = false,
            style = AbTypography.fixed(size.value * 0.5f, FontWeight.Black).copy(
                shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 2f), 0f)
            )
        )
    }
}

// "Candy Pop" celebration card: the shared glossy accent panel with the bouncing badge, stars and confetti. A solved
// puzzle is always a win, so the card shows all three stars.
@Composable
private fun PuzzleResultOverlay(
    moveCount: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    val s = gameScale()
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { AudioPlayerManager.playSoundClap() }
        GameResultCard(
            title = stringResource(R.string.you_did_it),
            emoji = "🧩",
            accent = ButtonType.ORANGE,
            stars = 3,
            stats = {
                Text(
                    stringResource(R.string.completed_in_b_moves_b, moveCount).replace("<b>", "").replace("</b>", ""),
                    color = Color.White,
                    style = AbTypography.fixed(20f * s, FontWeight.ExtraBold).copy(
                        shadow = Shadow(Color.Black.copy(alpha = 0.22f), Offset(0f, 4f), 0f)
                    ),
                    textAlign = TextAlign.Center
                )
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    GameButton(text = stringResource(R.string.continue_to_play), type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() })
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            }
        )
    }
}
