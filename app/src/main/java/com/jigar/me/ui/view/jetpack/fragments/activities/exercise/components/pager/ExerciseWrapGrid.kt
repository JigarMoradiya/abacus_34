package com.jigar.me.ui.view.jetpack.fragments.activities.exercise.components.pager

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.activities.exercise.exercise_generator.GridItemModel
import kotlin.math.ceil

@Composable
fun ExerciseWrapGrid(
    items: List<GridItemModel>, selectedItem: GridItemModel?, onItemSelected: (GridItemModel) -> Unit
) {
    val rows = 2
    val columns = ceil(items.size / rows.toFloat()).toInt()

    Column(
        modifier = Modifier.wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding6))
    ) {
        for (row in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding6)),
                modifier = Modifier.wrapContentWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < items.size) {
                        val item = items[index]
                        val isSelected = selectedItem?.id == item.id

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                )
                                .border(
                                    width = 1.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape
                                )
                                .clickable { onItemSelected(item) }, contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}", style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
