package com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jigar.me.R
import com.jigar.me.ui.view.home.common.BackButtonWithText
import com.jigar.me.ui.view.home.common.HowToPlayButton
import com.jigar.me.ui.view.home.common.how_to_play.HowToPlayNumberSequenceView

@Composable
fun NumberSequencePuzzleHomeJetpackScreen(
    navController: NavController,
    onPuzzleSelect: (Int) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var showHelp by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            BackButtonWithText(title = stringResource(R.string.number_sequence_puzzle), onBackClick = onBackClick)
            Spacer(Modifier.weight(1f))
            HowToPlayButton {
                showHelp = true
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.number_sequence_puzzle),
                    contentDescription = "Number Puzzle Logo",
                    modifier = Modifier
                        .height(dimensionResource(R.dimen._120dp))
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = stringResource(R.string.select_the_puzzle_dashboard),
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    fontSize = dimensionResource(id = R.dimen.textSize20).value.sp,
                    color = colorResource(id = R.color.colorBlueDark),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.activity_padding16))
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.activity_padding16))
                ) {
                    PuzzleOptionView(
                        gridSize = 3,
                        color = Color(0xFFF33173),
                        onClick = { onPuzzleSelect(3) }
                    )
                    PuzzleOptionView(
                        gridSize = 4,
                        color = Color(0xFFFF9800),
                        onClick = { onPuzzleSelect(4) }
                    )
                    PuzzleOptionView(
                        gridSize = 5,
                        color = Color(0xFF2196F3),
                        onClick = { onPuzzleSelect(5) }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }

        AnimatedVisibility(
            visible = showHelp,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            HowToPlayNumberSequenceView {
                showHelp = false
            }
        }
    }
}

@Composable
fun PuzzleOptionView(
    gridSize: Int,
    color: Color,
    onClick: () -> Unit
) {
    val boxSize = dimensionResource(R.dimen._72dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(boxSize)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRoundRect(
                    color = color,
                    size = size,
                    style = Stroke(width = 4f)
                )
            }

            val cellSize = with(LocalDensity.current) {
                (boxSize.toPx() / gridSize).toDp()
            }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                repeat(gridSize) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(gridSize) {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .border(
                                        width = 0.5.dp,
                                        color = color.copy(alpha = 1f)
                                    )
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "$gridSize x $gridSize Puzzle",
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            fontSize = dimensionResource(id = R.dimen.textSizeRegular).value.sp,
            color = color,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.activity_padding8))
        )
    }
}
