package com.jigar.me.ui.view.home.screens.reports.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimaryLight
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryUiState
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryViewModel
import com.jigar.me.ui.view.home.theme.AppDimens


@Composable
fun ReportDateFilterCard(
    uiState: ReportHistoryUiState,
    viewModel: ReportHistoryViewModel
) {
    Box {

        Card(
            onClick = { viewModel.onDateFilterClick() },
            colors = CardDefaults.cardColors(containerColor = ColorPrimaryLight),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(
                dimensionResource(R.dimen.card_elevation3)
            ),
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = dimensionResource(R.dimen.activity_padding6))
                .padding(end = dimensionResource(R.dimen.activity_padding16))
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.activity_padding12),
                        end = dimensionResource(R.dimen.activity_padding6)
                    ).padding(vertical = dimensionResource(R.dimen.activity_padding4))
            ) {

                Text(
                    text = uiState.selectedDateFilter?.label ?: stringResource(R.string.select_date),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = Color.Black
                    ),
                    modifier = Modifier.width(90.dp),
                    maxLines = 1
                )

                Icon(
                    modifier = Modifier.size(AppDimens.Dimens16),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }

        DropdownMenu(
            expanded = uiState.isDateDropdownExpanded,
            onDismissRequest = { viewModel.onDateFilterDismiss() },
            containerColor = ColorPrimaryLight
        ) {
            uiState.dateFilterList.forEach { item ->
                DropdownMenuItem(
                    modifier = Modifier.height(36.dp),
                    text = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily(Font(R.font.font_semibold))
                            )
                        )
                    },
                    onClick = {
                        viewModel.onDateFilterSelected(item)
                    }
                )
            }
        }
    }
}
