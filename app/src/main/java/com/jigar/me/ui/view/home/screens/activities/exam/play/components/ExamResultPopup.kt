package com.jigar.me.ui.view.home.screens.activities.exam.play.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.base.abacus_base.utils.MathUtils
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimaryLight
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.QuestionResult
import com.jigar.me.ui.view.home.theme.AppDimens


@Composable
fun ExamResultPopup(
    items: List<QuestionResult>, numberOfColumns: Int = 6, onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(
                indication = null, interactionSource = remember { MutableInteractionSource() }) { onDismiss() }) {

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(AppDimens.Dimens16), shape = RoundedCornerShape(AppDimens.Dimens20), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(AppDimens.Dimens6)
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // Header
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.result_of_exam),
                        style = MaterialTheme.typography.titleLarge.copy(color = ColorPrimary, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_extra_bold))), modifier = Modifier.align(Alignment.Center)
                    )

                    TextButton(
                        onClick = onDismiss, modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null,tint = Color.DarkGray)
                        Spacer(Modifier.width(AppDimens.Dimens4))
                        Text(
                            stringResource(R.string.close), style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold)))
                        )
                    }
                }

                HorizontalDivider(
                    Modifier.padding(bottom = AppDimens.Dimens8), color = Color.Black.copy(alpha = 0.1f), thickness = AppDimens.Dimens1
                )

                // ✅ CORRECT GRID
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(numberOfColumns), modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalItemSpacing = AppDimens.Dimens12,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                    contentPadding = PaddingValues(bottom = AppDimens.Dimens12, start = AppDimens.Dimens12, end = AppDimens.Dimens12)
                ) {
                    items(
                        items = items, key = { item -> "${item.que}_${item.hashCode()}" }) { item ->
                        QuestionExamColumnItem(item)
                    }
                }

            }
        }
    }
}


@Composable
fun QuestionExamColumnItem(item: QuestionResult) {

    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(AppDimens.Dimens12), colors = CardDefaults.cardColors(containerColor = ColorPrimaryLight), elevation = CardDefaults.cardElevation(AppDimens.Dimens2)
    ) {

        Column(
            modifier = Modifier.padding(vertical = AppDimens.Dimens6), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val lines = remember(item.que) {
                MathUtils.splitQuestionIntoLines(item.que)
            }

            lines.forEach { part ->
                Text(
                    text = part.replace("x", " x ").replace("×", " x ").replace("/", " ÷ ").replace("÷", " ÷ "),
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))), textAlign = TextAlign.Center
                )
            }

            HorizontalDivider(Modifier.padding(vertical = AppDimens.Dimens4), color = Color.Black.copy(alpha = 0.1f), thickness = AppDimens.Dimens1)

            Text(
                text = item.userAnswer.toString(), style = MaterialTheme.typography.bodyLarge.copy(color = item.statusQue.color, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_bold)))
            )

            Icon(
                modifier = Modifier.size(AppDimens.Dimens16),
                imageVector = item.statusQue.symbol, contentDescription = null, tint = item.statusQue.color
            )
        }
    }
}
