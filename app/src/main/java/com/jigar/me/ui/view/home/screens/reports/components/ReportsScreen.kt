package com.jigar.me.ui.view.home.screens.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.R
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryUiState
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType

private const val FREE_HISTORY_LIMIT = 3

@Composable
fun ReportsScreen(
    uiState: ReportHistoryUiState,
    viewModel: ReportHistoryViewModel,
    isSubscribed: Boolean = true,
    onShowPaywall: () -> Unit = {},
    onCheckResult: (AllExamData) -> Unit
) {
    val displayList = if (isSubscribed) uiState.list else uiState.list.take(FREE_HISTORY_LIMIT)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        contentPadding = PaddingValues(vertical = AppDimens.Dimens12)
    ) {

        items(
            items = displayList,
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

            if (isSubscribed) {
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
        }

        if (!isSubscribed) {
            item {
                ReportHistoryUpgradeBanner(onShowPaywall = onShowPaywall)
            }
        }

        if (isSubscribed && uiState.isPagingLoader) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.Dimens16),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ReportHistoryUpgradeBanner(onShowPaywall: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            .background(Color(0xFFFFF8E1), RoundedCornerShape(AppDimens.Dimens16))
            .padding(AppDimens.Dimens20),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color(0xFFFF9800),
            modifier = Modifier.size(AppDimens.Dimens32)
        )
        Spacer(Modifier.height(AppDimens.Dimens8))
        Text(
            text = "Upgrade to Premium",
            style = MaterialTheme.typography.titleMedium.scaled().copy(
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                color = Color(0xFF5D4037)
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(AppDimens.Dimens4))
        Text(
            text = "See your complete report history",
            style = MaterialTheme.typography.bodySmall.scaled().copy(
                fontFamily = FontFamily(Font(R.font.font_medium)),
                color = Color(0xFF8D6E63)
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(AppDimens.Dimens16))
        KidsActionButton(
            text = "View Plans",
            icon = Icons.Default.Star,
            type = ButtonType.ORANGE,
            onClick = onShowPaywall
        )
    }
}
