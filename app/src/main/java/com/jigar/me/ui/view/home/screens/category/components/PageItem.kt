package com.jigar.me.ui.view.home.screens.category.components

import com.jigar.me.ui.view.home.theme.AppDimens
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set

@Composable
fun PageItem(
    page: DisplayPages,
    allSets: List<Set>,
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

    Card(
        modifier = Modifier
            .padding(AppDimens.Dimens6)
            .fillMaxWidth(),
        shape = RoundedCornerShape(AppDimens.Dimens8),
        elevation = CardDefaults.cardElevation(AppDimens.Dimens2),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {

            // Header
            PageHeader(page = page)

            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = AppDimens.Dimens1
            )

            // ✅ REAL GRID
            Column(
                modifier = Modifier.padding(AppDimens.Dimens6),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                rows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        row.forEach { set ->
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                SetItem(
                                    set = set,
                                    onClick = { onSetClick(set) },
                                    onLongClick = { onSetLongClick(set) }
                                )
                            }
                        }

                        // 🔹 Fill empty columns (VERY IMPORTANT)
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
    page: DisplayPages
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppDimens.Dimens8),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔹 Left icon (imgIcon)
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.StickyNote2,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen._28dp))
        )


        // 🔹 Title + Description
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = AppDimens.Dimens8)
        ) {

            // Title
            Text(
                text = page.name,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 18.sp,fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))),
                maxLines = 2
            )

            // Description (only if not empty)
            if (!page.description.isNullOrEmpty()) {
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 16.sp,fontWeight = FontWeight.SemiBold,fontFamily = FontFamily(Font(R.font.font_semibold))),
                    maxLines = 2
                )
            }
        }

        // 🔹 Disabled indicator (imgActive)
        if (!page.is_active) {
            Image(
                painter = painterResource(R.drawable.ic_disable),
                contentDescription = null,
                modifier = Modifier
                    .size(AppDimens.Dimens12)
                    .padding(start = AppDimens.Dimens4)
            )
        }
    }
}


fun <T> List<T>.chunkedGrid(columns: Int): List<List<T>> {
    return this.chunked(columns)
}


