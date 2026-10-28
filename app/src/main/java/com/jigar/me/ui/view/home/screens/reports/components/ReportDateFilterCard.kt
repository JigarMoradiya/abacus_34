package com.jigar.me.ui.view.home.screens.reports.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbMenuPicker
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryUiState
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryViewModel

/** The date range picker of the header (last 30 days, this month ...). */
@Composable
fun ReportDateFilterCard(
    uiState: ReportHistoryUiState,
    viewModel: ReportHistoryViewModel
) {
    AbMenuPicker(
        items = uiState.dateFilterList,
        selectedTitle = uiState.selectedDateFilter?.label ?: "",
        title = { it.label },
        onSelect = viewModel::onDateFilterSelected,
        placeholder = stringResource(R.string.select_date),
        icon = Icons.Rounded.CalendarMonth,
        minWidth = 150.dp * LocalAbScale.current.factor,
    )
}
