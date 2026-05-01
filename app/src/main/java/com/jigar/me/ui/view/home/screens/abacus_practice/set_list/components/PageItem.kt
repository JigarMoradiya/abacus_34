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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled

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
        shape = RoundedCornerShape(AppDimens.Dimens16),
        elevation = CardDefaults.cardElevation(AppDimens.Dimens4),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        )
    ) {
        Column {

            PageHeader(page)

            HorizontalDivider(
                color = Color(0xFFFFCC80),
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

        Box(
            modifier = Modifier
                .size(AppDimens.Dimens32)
                .background(
                    Color.White,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.StickyNote2,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(AppDimens.Dimens24)
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
                    color = Color(0xFF5D4037)
                ),
                maxLines = 2
            )

            if (!page.description.isNullOrEmpty()) {
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodySmall.scaled().copy(
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.font_medium)),
                        color = Color(0xFF8D6E63)
                    ),
                    maxLines = 2
                )
            }
        }

        if (!page.is_active) {
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


