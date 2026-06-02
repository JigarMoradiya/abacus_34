package com.jigar.me.ui.view.home.screens.activities.exercise.components.pager

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.activities.exercise.exercise_generator.GridItemModel
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlin.math.ceil

@Composable
fun ExerciseWrapGrid(
    items: List<GridItemModel>,
    selectedItem: GridItemModel?,
    isItemLocked: (itemIndex: Int) -> Boolean = { false },
    onItemSelected: (GridItemModel) -> Unit
) {
    val rows = 2
    val columns = ceil(items.size / rows.toFloat()).toInt()

    Column(
        modifier = Modifier.wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)
    ) {
        for (row in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                modifier = Modifier.wrapContentWidth(), verticalAlignment = Alignment.CenterVertically
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
                            onClick = { if (!isLocked) onItemSelected(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KidsGridItem(
    index: Int,
    isSelected: Boolean,
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed && !isLocked) 0.9f else 1f,
        label = ""
    )

    val backgroundColor = when {
        isLocked -> Color(0xFFEEEEEE)
        isSelected -> Color(0xFFFFB300)
        else -> Color(0xFFFFF3E0)
    }
    val borderColor = when {
        isLocked -> Color(0xFFBDBDBD)
        isSelected -> Color(0xFFFF8F00)
        else -> Color(0xFFFFB74D)
    }

    Box(contentAlignment = Alignment.TopEnd) {
        Box(
            modifier = Modifier
                .size(AppDimens.Dimens28)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .drawBehind {
                    drawCircle(
                        color = Color.Black.copy(alpha = if (isLocked) 0.05f else 0.15f),
                        radius = size.minDimension / 2,
                        center = center.copy(y = center.y + 2f)
                    )
                }
                .clip(CircleShape)
                .background(backgroundColor)
                .border(
                    width = AppDimens.Dimens1,
                    color = borderColor,
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interaction,
                    indication = null
                ) {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.bodyMedium.scaled().copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    color = when {
                        isLocked -> Color(0xFFBDBDBD)
                        isSelected -> Color.White
                        else -> Color(0xFF6D4C41)
                    },
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = if (isLocked) 0.05f else 0.2f),
                        offset = Offset(1f, 1f),
                        blurRadius = 0f
                    )
                )
            )
        }

        if (isLocked) {
            Box(
                modifier = Modifier
                    .offset(x = 0.dp, y = (-AppDimens.Dimens2))
                    .size(AppDimens.Dimens10)
                    .background(Color(0xFFFF9800), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(AppDimens.Dimens6)
                )
            }
        }
    }
}
