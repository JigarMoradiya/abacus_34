package com.jigar.me.ui.view.home.common_ui.how_to_play

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.CalcudokuGridLines
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abHowToCard

private val CAGE_LINE = Color(0xFF455A64)
private val HIGHLIGHT = Color(0xFFFFF3C4)

// A tiny worked 3×3 example (valid Latin square + a few cages).
private val EX_VALUES = listOf(1, 2, 3, 2, 3, 1, 3, 1, 2)
private val EX_CAGE = listOf(0, 0, 1, 2, 3, 3, 2, 4, 4)
private val EX_CLUES = mapOf(0 to "3+", 2 to "3", 3 to "6×", 4 to "2−", 7 to "2×")
private const val EX_HIGHLIGHT = 2   // the 6× cage (cells 3 & 6)

@Composable
fun HowToPlayCalcudokuView(
    widthMultiplier: Float = 0.85f,
    heightMultiplier: Float = 0.9f,
    onClose: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenWidth = with(LocalDensity.current) { windowInfo.containerSize.width.toDp() }
    val screenHeight = with(LocalDensity.current) { windowInfo.containerSize.height.toDp() }
    val accentColors = getButtonColors(ButtonType.BLUE)

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).padding(AppDimens.Dimens16),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.width(screenWidth * widthMultiplier).height(screenHeight * heightMultiplier)
                .abHowToCard(ButtonType.BLUE)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.calcudoku_game),
                        fontFamily = AbTypography.Baloo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = com.jigar.me.ui.view.home.screens.math_game_zone.common.howToInk(accentColors.base))
                    Spacer(Modifier.weight(1f))
                    GameButton(
                        text = stringResource(R.string.close),
                        type = ButtonType.BLUE,
                        onClick = onClose,
                        isSmall = true
                    )
                }
                Spacer(Modifier.height(AppDimens.Dimens8))

                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens24)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            MiniGrid()
                            Spacer(Modifier.height(AppDimens.Dimens8))
                            Text(stringResource(R.string.htp_calcudoku_1), fontSize = 12.sp, color = Color.Gray,
                                fontFamily = AbTypography.Baloo)
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
                        ) {
                            CdBullet(stringResource(R.string.htp_calcudoku_2))
                            CdBullet(stringResource(R.string.htp_calcudoku_3))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
                                modifier = Modifier.clip(RoundedCornerShape(AppDimens.Dimens10))
                                    .background(HIGHLIGHT).padding(AppDimens.Dimens10)
                            ) {
                                Text(stringResource(R.string.htp_calcudoku_4), fontSize = 15.sp, color = Color(0xFF3B2A00),
                                    fontFamily = AbTypography.Baloo)
                                Text(stringResource(R.string.htp_calcudoku_5), fontSize = 15.sp, color = Color(0xFF3B2A00),
                                    fontFamily = AbTypography.Baloo)
                                Text(stringResource(R.string.htp_calcudoku_6), fontSize = 15.sp, color = Color(0xFF3B2A00),
                                    fontFamily = AbTypography.Baloo)
                                Text(stringResource(R.string.htp_calcudoku_7), fontSize = 15.sp, color = Color(0xFF3B2A00),
                                    fontFamily = AbTypography.Baloo)
                            }

                            CdBullet(stringResource(R.string.htp_calcudoku_8))
                            CdBullet(stringResource(R.string.htp_calcudoku_9))
                        }
                    }
                    Spacer(Modifier.height(AppDimens.Dimens16))
                    Text(stringResource(R.string.htp_calcudoku_10),
                        fontSize = 16.sp, color = LocalAbColors.current.textPrimary, fontFamily = AbTypography.Baloo)
                }
            }
        }
    }
}

@Composable
private fun MiniGrid() {
    val cell = 46.dp
    val g = cell * 3
    Box(modifier = Modifier.size(g).clip(RoundedCornerShape(AppDimens.Dimens10))) {
        Column {
            for (r in 0 until 3) {
                Row {
                    for (c in 0 until 3) MiniCell(r * 3 + c, cell)
                }
            }
        }
        CalcudokuGridLines(3, cell, EX_CAGE)
    }
}

@Composable
private fun MiniCell(i: Int, cell: androidx.compose.ui.unit.Dp) {
    val bg = if (EX_CAGE[i] == EX_HIGHLIGHT) HIGHLIGHT else Color.White
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(cell).background(bg)
    ) {
        EX_CLUES[i]?.let { clue ->
            Text(clue, color = CAGE_LINE, fontFamily = AbTypography.Baloo, fontSize = 11.sp,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 4.dp, top = 3.dp))
        }
        Text("${EX_VALUES[i]}", color = Color(0xFF3B2A00),
            fontFamily = AbTypography.Baloo, fontSize = 22.sp)
    }
}

@Composable
private fun CdBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("•  ", fontSize = 16.sp, color = LocalAbColors.current.textPrimary, fontFamily = AbTypography.Baloo)
        Text(text, fontSize = 15.sp, color = LocalAbColors.current.textPrimary, fontFamily = AbTypography.Baloo)
    }
}
