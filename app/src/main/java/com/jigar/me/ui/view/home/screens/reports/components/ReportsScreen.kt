package com.jigar.me.ui.view.home.screens.reports.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import com.jigar.me.ui.view.ab.AbBeadsLoader
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryUiState
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryViewModel
import com.jigar.me.ui.view.home.theme.AppDimens

private const val FREE_HISTORY_LIMIT = 3

@Composable
fun ReportsScreen(
    uiState: ReportHistoryUiState,
    viewModel: ReportHistoryViewModel,
    isSubscribed: Boolean = true,
    onShowPaywall: () -> Unit = {},
    onCheckResult: (AllExamData) -> Unit
) {
    val colors = LocalAbColors.current
    val displayList = if (isSubscribed) uiState.list else uiState.list.take(FREE_HISTORY_LIMIT)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
        contentPadding = PaddingValues(
            start = DeviceInfo.screenHorizontalPadding(),
            end = AppDimens.Dimens16,
            top = AppDimens.Dimens12,
            bottom = AppDimens.Dimens12
        )
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
                    AbBeadsLoader()
                }
            }
        }
    }
}

/** "Upgrade to see your whole history": a gold strip with a button to the plans. */
@Composable
private fun ReportHistoryUpgradeBanner(onShowPaywall: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val gold = Color(0xFFFFB800)
    val shape = RoundedCornerShape(AbShapes.md * s)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .abChunkyFace(
                face = if (colors.isDark) gold.copy(alpha = 0.18f) else Color(0xFFFFF6D6),
                edge = gold.abDarkened(0.2f), stroke = null,
                cornerRadius = AbShapes.md * s, edgeHeight = 3.dp * s,
            )
            .border(2.dp, gold.copy(alpha = 0.8f), shape)
            .padding(horizontal = 14.dp * s, vertical = 10.dp * s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp * s)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp * s)
                .background(gold, CircleShape)
                .border(2.dp, Color.White.copy(alpha = 0.9f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size((18f * AB_SF_TO_VECTOR).dp * s)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Upgrade to Premium",
                style = AbTypography.fixed(15f * s, FontWeight.ExtraBold),
                color = colors.textPrimary
            )
            AbShrinkText(
                text = "See your complete report history",
                style = AbTypography.fixed(12f * s, FontWeight.SemiBold),
                color = colors.textSecondary,
                minScale = 0.8f,
                maxLines = 2
            )
        }
        AbButton(
            text = "View Plans",
            painterIcon = painterResource(R.drawable.crown),
            tone = AbTone.Warning,
            size = AbSize.Small,
            onClick = onShowPaywall
        )
    }
}
