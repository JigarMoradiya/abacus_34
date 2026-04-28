package com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
import com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.viewmodels.NumberSequencePuzzleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NumberSequencePuzzleJetpackFragment : Fragment() {

    private val viewModel by viewModels<NumberSequencePuzzleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val gridSize = NumberSequencePuzzleJetpackFragmentArgs.fromBundle(requireArguments()).type
        return ComposeView(requireContext()).apply {
            setContent {
                AbacusTheme {
                    val navController = findNavController()
                    NumberSequencePuzzleJetpackScreen(
                        navController = navController,
                        gridSize = gridSize,viewModel = viewModel
                    )
                }
            }
        }
    }
}


@Composable
fun NumberSequencePuzzleJetpackScreen(
    navController: NavController,
    gridSize: Int,
    viewModel: NumberSequencePuzzleViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val spacing = if (gridSize == 5) {8.dp}else if (gridSize == 4) {10.dp} else {12.dp}
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

                    if (uiState.tiles.size == gridSize && uiState.tileColors.size == gridSize) {
                        PuzzleBoard(
                            gridSize = gridSize,
                            tiles = uiState.tiles,
                            spacing = spacing,
                            opacity = opacity,
                            tileColors = uiState.tileColors,
                            onTileMove = { row, col -> viewModel.onTileMove(row, col) },
                            modifier = Modifier.width(maxWidth * 0.5f)
                        )
                    } else {
                        Box(modifier = Modifier.width(maxWidth * 0.5f).fillMaxHeight())
                    }

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
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { onSoundToggle() }
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = if (soundOn) R.drawable.ic_volume_on else R.drawable.ic_volume_off),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(35.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(if (soundOn) "Sound On" else "Sound Off", color = Color.Black.copy(alpha = 0.7f),
                fontSize = dimensionResource(id = R.dimen.textSizeSmall).value.sp,
                fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.size(20.dp))

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { onRestart() }
                .background(Color(0xFF90EE90).copy(alpha = 0.3f))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
    tileColors: List<List<Color>>,
    onTileMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (gridSize) {
        3 -> Color(0xFFF33173).copy(alpha = opacity)
        4 -> Color(0xFFFF9800).copy(alpha = opacity)
        else -> Color(0xFF2196F3).copy(alpha = opacity)
    }.copy(alpha = 0.15f)

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
            .clip(RoundedCornerShape(size * 0.15f))
            .background(
                if (number != null) color else Color.Transparent
            )
            .clickable(enabled = number != null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (number != null) {
            val fontSize = when (size) {
                in 0.dp..60.dp -> 20.sp
                in 60.dp..80.dp -> 24.sp
                else -> 30.sp
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
