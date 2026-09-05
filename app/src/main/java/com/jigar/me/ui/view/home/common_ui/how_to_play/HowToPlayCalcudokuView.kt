package com.jigar.me.ui.view.home.common_ui.how_to_play

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
                .background(Color.White, RoundedCornerShape(AppDimens.Dimens20))
                .border(AppDimens.Dimens4, accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20))
                .padding(AppDimens.Dimens16)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.calcudoku_game),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = accentColors.base)
                    Spacer(Modifier.weight(1f))
                    KidsActionButton(
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
                            Text("A finished 3×3 puzzle", fontSize = 12.sp, color = Color.Gray,
                                fontFamily = FontFamily(Font(R.font.font_bold)))
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
                        ) {
                            CdBullet("Use the numbers 1 to 3. No number repeats in a row or column.")
                            CdBullet("Bold outlines are cages. The small clue shows the target + sign.")

                            Column(
                                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
                                modifier = Modifier.clip(RoundedCornerShape(AppDimens.Dimens10))
                                    .background(HIGHLIGHT).padding(AppDimens.Dimens10)
                            ) {
                                Text("Look at the yellow 6× cage:", fontSize = 15.sp, color = Color.Black,
                                    fontFamily = FontFamily(Font(R.font.font_bold)))
                                Text("2 and 3 go in it, because 2 × 3 = 6  ✔", fontSize = 15.sp, color = Color.Black,
                                    fontFamily = FontFamily(Font(R.font.font_regular)))
                                Text("3+ means the boxes add to 3  (1 + 2).", fontSize = 15.sp, color = Color.Black,
                                    fontFamily = FontFamily(Font(R.font.font_regular)))
                                Text("2− means the boxes differ by 2  (3 − 1).", fontSize = 15.sp, color = Color.Black,
                                    fontFamily = FontFamily(Font(R.font.font_regular)))
                            }

                            CdBullet("Tap a box, then tap a number below. Tap ⌫ to clear.")
                            CdBullet("Stuck? Tap a box — the bear tells you that cage's goal!")
                        }
                    }
                    Spacer(Modifier.height(AppDimens.Dimens16))
                    Text("A clever number puzzle that blends arithmetic with logic.",
                        fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_bold)))
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
            Text(clue, color = CAGE_LINE, fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = 11.sp,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 4.dp, top = 3.dp))
        }
        Text("${EX_VALUES[i]}", color = Color.Black,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 22.sp)
    }
}

@Composable
private fun CdBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("•  ", fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_bold)))
        Text(text, fontSize = 15.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_regular)))
    }
}
