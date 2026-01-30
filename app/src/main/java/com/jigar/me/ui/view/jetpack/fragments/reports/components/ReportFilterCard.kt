package com.jigar.me.ui.view.jetpack.fragments.reports.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorPrimaryLight
import com.jigar.me.ui.view.jetpack.fragments.reports.viewmodels.ReportHistoryUiState
import com.jigar.me.ui.view.jetpack.fragments.reports.viewmodels.ReportHistoryViewModel

@Composable
fun ReportFilterCard(
    uiState: ReportHistoryUiState,
    viewModel: ReportHistoryViewModel
) {
    Box {

        Card(
            onClick = { viewModel.onFilterClick() },
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(
                dimensionResource(R.dimen.card_elevation3)
            ),
            colors = CardDefaults.cardColors(containerColor = ColorPrimaryLight),
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = dimensionResource(R.dimen.activity_padding6))
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.activity_padding12),
                    end = dimensionResource(R.dimen.activity_padding6),
                ).padding(vertical = dimensionResource(R.dimen.activity_padding2))
            ) {

                Text(
                    text = uiState.selectedFilter?.label?:"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = Color.Black
                    ),
                    modifier = Modifier.width(200.dp),
                    maxLines = 1
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }

        // 🔽 Dropdown (Spinner replacement)
        DropdownMenu(
            expanded = uiState.isDropdownExpanded,
            onDismissRequest = { viewModel.onFilterDismiss() },
            containerColor = ColorPrimaryLight
        ) {
            uiState.filterList.forEachIndexed { index, item ->
                DropdownMenuItem(
                    modifier = Modifier.height(36.dp),
                    text = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold,fontFamily = FontFamily(Font(R.font.font_semibold))),
                        )
                    },
                    onClick = {
                        if (index > 0) {
                            viewModel.onFilterSelected(item)
                        } else {
                            viewModel.onFilterDismiss()
                        }
                    }
                )
            }
        }
    }
}
