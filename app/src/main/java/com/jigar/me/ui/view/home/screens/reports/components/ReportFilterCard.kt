package com.jigar.me.ui.view.home.screens.reports.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbMenuPicker
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryUiState
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryViewModel

/** The report type picker of the header (all reports, exam, exercise ...). The first filter is only a placeholder, so it is not listed. */
@Composable
fun ReportFilterCard(
    uiState: ReportHistoryUiState,
    viewModel: ReportHistoryViewModel
) {
    AbMenuPicker(
        items = uiState.filterList.drop(1),
        selectedTitle = uiState.selectedFilter?.label ?: "",
        title = { it.label },
        onSelect = viewModel::onFilterSelected,
        placeholder = stringResource(R.string.select_report_type),
        icon = Icons.Rounded.FilterList,
        minWidth = 150.dp * LocalAbScale.current.factor,
    )
}
