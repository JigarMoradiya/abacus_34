package com.jigar.me.ui.view.home.screens.activities.exercise.components.pager

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSound
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.ui.view.home.screens.activities.ActivityDimens
import com.jigar.me.ui.view.home.screens.activities.exercise.exercise_generator.GridItemModel
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlin.math.ceil

/** The numbered circles of one exercise type: two centred rows. */
@Composable
fun ExerciseWrapGrid(
    items: List<GridItemModel>,
    selectedItem: GridItemModel?,
    isItemLocked: (itemIndex: Int) -> Boolean = { false },
    onLockedItemClick: () -> Unit = {},
    onItemSelected: (GridItemModel) -> Unit
) {
    val rows = 2
    val columns = ceil(items.size / rows.toFloat()).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = (ActivityDimens.exerciseGridSize + AppDimens.Dimens6) * rows),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)
    ) {
        for (row in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < items.size) {
                        val item = items[index]
                        val isSelected = selectedItem?.id == item.id
                        val isLocked = isItemLocked(index)

                        KidsGridItem(
                            index = index,
                            isSelected = isSelected,
                            isLocked = isLocked,
                            onClick = {
                                if (isLocked) onLockedItemClick()
                                else onItemSelected(item)
                            }
                        )
                    }
                }
            }
        }
    }
}

/** One chunky numbered circle: orange when picked, pale and padlocked when it needs the paid version. */
@Composable
fun KidsGridItem(
    index: Int,
    isSelected: Boolean,
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    val colors = LocalAbColors.current
    val size = ActivityDimens.exerciseGridSize
    val face = if (isLocked) colors.surfaceTint else if (isSelected) colors.accentOrange else colors.bgSecondary
    val edge = if (isLocked) colors.outline else if (isSelected) colors.accentOrange.abDarkened(0.3f) else colors.outline
    val ink = if (isLocked) colors.textSecondary else if (isSelected) Color.White else colors.textPrimary
    val rimSelected = isSelected && !isLocked

    Box(Modifier.padding(bottom = size * 0.08f), contentAlignment = Alignment.TopEnd) {
        Box(
            Modifier
                .abSquash(face, edge, null, size, size * 0.08f, sound = AbSound.Button, onClick = onClick)
                .size(size)
                .border(
                    if (rimSelected) 2.5.dp else 2.dp,
                    if (rimSelected) Color.White.copy(alpha = 0.85f) else colors.outline,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            AbShrinkText(
                text = "${index + 1}",
                style = AbTypography.fixed(size.value * 0.42f, FontWeight.ExtraBold),
                color = ink,
                minScale = 0.6f,
                maxLines = 1,
            )
        }

        if (isLocked) {
            Box(
                Modifier
                    .offset(x = size * 0.06f, y = -(size * 0.06f))
                    .size(size * 0.38f)
                    .background(Color(0xFFFF9800), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Lock, null, Modifier.size(size * 0.2f * AB_SF_TO_VECTOR), tint = Color.White)
            }
        }
    }
}
