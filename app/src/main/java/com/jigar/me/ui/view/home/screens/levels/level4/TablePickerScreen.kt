package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16

private val tablePickerColors = listOf(
    Color(0xFFE53935), Color(0xFFF57C00), Color(0xFFF9A825), Color(0xFF43A047), Color(0xFF00ACC1),
    Color(0xFF00897B), Color(0xFF1976D2), Color(0xFF7B1FA2), Color(0xFFC2185B), Color(0xFF5E35B1),
    Color(0xFFE64A19), Color(0xFF00695C), Color(0xFF1565C0), Color(0xFF2E7D32), Color(0xFF0288D1),
    Color(0xFFAD1457), Color(0xFF6A1B9A), Color(0xFF558B2F), Color(0xFF4E342E), Color(0xFF37474F),
)

@Composable
fun TablePickerScreen(
    onTableSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

            BackButtonWithText(title = "Choose a table to practise", onBackClick = onBackClick)

            // Remaining space after header — calculate grid to fit exactly
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val cols       = 5
                val rows       = 4
                val hPad       = AppDimens.Dimens16
                val spacing    = AppDimens.Dimens12

                // cell width from available width
                val cellWfW = (maxWidth - hPad * 2 - spacing * (cols - 1)) / cols

                // cell height: budget = maxHeight - inter-row gaps - bottom contentPadding for shadow room
                val cellHfH = (maxHeight - spacing * (rows - 1) - AppDimens.Dimens12) / rows
                val cellWfH = cellHfH * 1.5f

                // use whichever fits both axes
                val cellW   = min(cellWfW, cellWfH)
                val cellH   = cellW / 1.5f
                val gridW   = cellW * cols + spacing * (cols - 1)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(cols),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                        verticalArrangement = Arrangement.spacedBy(spacing),
                        userScrollEnabled = false,
                        contentPadding = PaddingValues(bottom = AppDimens.Dimens12),
                        modifier = Modifier
                            .width(gridW)
                            .height(cellH * rows + spacing * (rows - 1) + AppDimens.Dimens12)
                    ) {
                        items((1..20).toList()) { n ->
                            val color = tablePickerColors[(n - 1) % tablePickerColors.size]
                            TablePickerButton(
                                n = n, color = color,
                                cellW = cellW, cellH = cellH,
                                onClick = { onTableSelected(n) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TablePickerButton(n: Int, color: Color, cellW: androidx.compose.ui.unit.Dp, cellH: androidx.compose.ui.unit.Dp, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens16)
//    Box(
//        modifier = Modifier
//            .size(width = cellW, height = cellH)
//            .shadow(elevation = AppDimens.Dimens6, shape = shape)
//            .background(Brush.linearGradient(listOf(color, color.copy(alpha = 0.78f))))
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    )
     Card(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens16),
        elevation = CardDefaults.cardElevation(AppDimens.Dimens4),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
         Row(
             modifier = Modifier
                 .size(width = cellW, height = cellH)
                 .background(Brush.linearGradient(listOf(color, color.copy(alpha = 0.78f)))),
             verticalAlignment = Alignment.CenterVertically,
             horizontalArrangement = Arrangement.Center
         ){
             Text(
                 text = "×$n",
                 style = MaterialTheme.typography.displaySmall.scaled(),
                 fontWeight = FontWeight.Black,
                 color = Color.White
             )
         }
    }
}
