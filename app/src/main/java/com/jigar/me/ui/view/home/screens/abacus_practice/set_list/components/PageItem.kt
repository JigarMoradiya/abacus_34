package com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components

import com.jigar.me.ui.view.home.theme.AppDimens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorLightCoffee
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorLightYellow
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorYellowOrange
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled

@Composable
fun PageItem(
    page: DisplayPages,
    allSets: List<Set>,
    isLocked: Boolean = false,
    onSetClick: (Set) -> Unit,
    onSetLongClick: (Set) -> Unit
) {
    val sets = remember(page, allSets) {
        allSets.filter { it.page_id == page.id }
    }

    val columns = when (sets.size) {
        1, 2, 3 -> sets.size
        4 -> 2
        else -> 3
    }

    val rows = remember(sets, columns) {
        sets.chunkedGrid(columns)
    }

//    val bgColor = if (isLocked) Color(0x14808080) else ColorLightYellow
    val bgColor = ColorLightYellow
    val shadowColor = if (isLocked) Color.Gray.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.5f)
    val shape = RoundedCornerShape(AppDimens.Dimens16)

    Box(
        modifier = Modifier
            .padding(AppDimens.Dimens6)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isLocked) AppDimens.Dimens2 else AppDimens.Dimens4,
                    shape = shape,
                    ambientColor = shadowColor,
                    spotColor = shadowColor
                )
                .background(bgColor, shape)
        ) {
            PageHeader(page, isLocked)

            HorizontalDivider(
                color = if (isLocked) Color.Gray.copy(alpha = 0.3f) else Color(0xFFFFCC80),
                thickness = AppDimens.Dimens1
            )

            Column(
                modifier = Modifier.padding(AppDimens.Dimens6),
                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens2)
            ) {
                rows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens2)
                    ) {
                        row.forEach { set ->
                            Box(modifier = Modifier.weight(1f)) {
                                SetItem(
                                    set = set,
                                    onClick = { onSetClick(set) },
                                    onLongClick = { onSetLongClick(set) },
                                    isLocked = isLocked
                                )
                            }
                        }
                        repeat(columns - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PageHeader(
    page: DisplayPages,
    isLocked: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppDimens.Dimens8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(AppDimens.Dimens32)
                .shadow(elevation = AppDimens.Dimens4, shape = CircleShape, ambientColor = Color.Gray.copy(alpha = 0.35f), spotColor = Color.Gray.copy(alpha = 0.35f))
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLocked) Icons.Default.Lock else Icons.AutoMirrored.Outlined.StickyNote2,
                contentDescription = null,
                tint = if (isLocked) Color.Gray else ColorYellowOrange,
                modifier = Modifier.size(AppDimens.Dimens20)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = AppDimens.Dimens8)
        ) {
            Text(
                text = page.name,
                style = MaterialTheme.typography.bodyLarge.scaled().copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    color = if (isLocked) Color.Gray else Color(0xFF5D4037)
                ),
                maxLines = 2
            )

            if (!page.description.isNullOrEmpty()) {
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodySmall.scaled().copy(
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.font_medium)),
                        color = if (isLocked) Color.Gray.copy(alpha = 0.7f) else ColorLightCoffee
                    ),
                    maxLines = 2
                )
            }
        }

        if (isLocked) {
            Icon(
                imageVector = Icons.Filled.WorkspacePremium,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(AppDimens.Dimens16)
            )
        } else if (!page.is_active) {
            Image(
                painter = painterResource(R.drawable.ic_disable),
                contentDescription = null,
                modifier = Modifier.size(AppDimens.Dimens14)
            )
        }
    }
}

fun <T> List<T>.chunkedGrid(columns: Int): List<List<T>> {
    return this.chunked(columns)
}
