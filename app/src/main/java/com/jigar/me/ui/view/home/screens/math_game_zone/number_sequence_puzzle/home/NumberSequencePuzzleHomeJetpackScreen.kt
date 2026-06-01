package com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.home

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorYellowOrange
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayNumberSequenceView
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.SudokuHomeIconsBox
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun NumberSequencePuzzleHomeJetpackScreen(
    navController: NavController,
    isSubscribed: Boolean = true,
    onPuzzleSelect: (Int) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var showHelp by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(title = stringResource(R.string.number_sequence_puzzle), modifier = Modifier.weight(1f),onBackClick = onBackClick)
            KidsActionButton(
                modifier = Modifier.padding(end = Dimens16),
                text = stringResource(R.string.how_to_play),
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                type = ButtonType.PINK,
                isSmall = true,
                onClick = {
                    showHelp = true
                }
            )
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
                        .fillMaxHeight(if (DeviceInfo.isTablet) 0.4f else 0.35f)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = stringResource(R.string.select_the_puzzle_dashboard),
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    color = colorResource(id = R.color.colorBlueDark),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.scaled(),
                    modifier = Modifier.padding(top = Dimens16)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens24),
                    modifier = Modifier.padding(top = Dimens16)
                ) {
                    PuzzleOptionView(
                        gridSize = 3,
                        color = Color(0xFFF33173),
                        isLocked = false,
                        onClick = { onPuzzleSelect(3) }
                    )
                    PuzzleOptionView(
                        gridSize = 4,
                        color = ColorYellowOrange,
                        isLocked = !isSubscribed,
                        onClick = { onPuzzleSelect(4) }
                    )
                    PuzzleOptionView(
                        gridSize = 5,
                        color = Color(0xFF2196F3),
                        isLocked = !isSubscribed,
                        onClick = { onPuzzleSelect(5) }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }

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

@Composable
fun PuzzleOptionView(
    gridSize: Int,
    color: Color,
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    val boxSize = SudokuHomeIconsBox
    val displayColor = if (isLocked) Color.Gray.copy(alpha = 0.4f) else color

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(modifier = Modifier.size(boxSize)) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRoundRect(
                        color = displayColor,
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
                                        .border(width = 0.5.dp, color = displayColor)
                                )
                            }
                        }
                    }
                }
            }

            if (isLocked) {
                Box(
                    modifier = Modifier
                        .offset(x = AppDimens.Dimens4, y = (-AppDimens.Dimens4))
                        .size(AppDimens.Dimens18)
                        .background(Color(0xFFFF9800), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(AppDimens.Dimens10)
                    )
                }
            }
        }

        Text(
            text = "$gridSize x $gridSize Puzzle",
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            style = MaterialTheme.typography.labelLarge.scaled(),
            color = displayColor,
            modifier = Modifier.padding(top = AppDimens.Dimens8)
        )
    }
}
