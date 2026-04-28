package com.jigar.me.ui.view.home.screens.reports.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryUiState
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryViewModel

@Composable
fun ReportsScreen(
    uiState: ReportHistoryUiState,
    viewModel: ReportHistoryViewModel,
    onCheckResult: (AllExamData) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding8)),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.activity_padding12))
    ) {

        items(
            items = uiState.list,
            key = { item ->
                item.id ?: "${item.created_at}_${item.hashCode()}"
            }
        ) { item ->

            ReportCard(
                item = item,
                onCheckResultTapped = {
                    onCheckResult(it)
                }
            )

            // 🔹 Pagination trigger
            val lastItem = uiState.list.lastOrNull()

            if (
                item == lastItem &&
                uiState.list.size < uiState.totalRecord &&
                !uiState.isLoading
            ) {
                LaunchedEffect(Unit) {
                    viewModel.loadNextPage()
                }
            }
        }

        // 🔹 Loader at bottom
        if (uiState.isPagingLoader) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

    }
}
