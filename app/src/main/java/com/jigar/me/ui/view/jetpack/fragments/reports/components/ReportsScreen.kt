package com.jigar.me.ui.view.jetpack.fragments.reports.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.jigar.me.R
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.ui.view.jetpack.fragments.reports.viewmodels.ReportHistoryUiState
import com.jigar.me.ui.view.jetpack.fragments.reports.viewmodels.ReportHistoryViewModel

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
            key = {  item ->
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
//            LaunchedEffect(item) {
//                viewModel.fetchNextPageIfNeeded(item)
//            }
        }

        // 🔹 Loading footer
//        if (uiState.canLoadMore) {
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            }
//        }
    }
}
