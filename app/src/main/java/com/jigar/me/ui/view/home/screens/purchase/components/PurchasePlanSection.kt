package com.jigar.me.ui.view.home.screens.purchase.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorRed
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState

@Composable
fun PurchasePlanSection(
    modifier: Modifier = Modifier,
    uiState: PurchaseUiState,
    onPlanSelected: (Int) -> Unit,
    onSubscribe: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(end = AppDimens.Dimens12, top = AppDimens.Dimens2, bottom = AppDimens.Dimens2),
        ) {
            uiState.sortedPlanList.forEachIndexed { index, plan ->
                if (index > 0) Spacer(Modifier.height(AppDimens.Dimens8))
                PurchasePlanCard(
                    uiState = uiState,
                    plan = plan,
                    isSelected = uiState.selectedIndex == index,
                    onClick = { onPlanSelected(index) },
                    onSubscribe = onSubscribe
                )
            }
        }

        val selected = uiState.sortedPlanList.getOrNull(uiState.selectedIndex)
        // Exact SKU match, not .contains() -- arrangeData() only removeExact()s the
        // plain SKU when a plan is admin-assigned, so a real purchasable ".offer"
        // variant of the same plan type can still be in the list and selected.
        val isSelectedAssignedFromAdmin = selected != null && (
            (selected.sku == "com.abacus.puzzle.1year" && uiState.yearPlanAssignFromAdmin != null) ||
                (selected.sku == "com.abacus.all" && uiState.allPlanAssignFromAdmin != null)
            )
        val showCancelText = selected != null && !selected.isLifeTimeOffer() && !selected.isPurchase &&
            !isSelectedAssignedFromAdmin
        if (showCancelText) {
            Spacer(Modifier.height(AppDimens.Dimens8))
            Text(
                text = stringResource(R.string.cancel_subscription_anytime),
                style = MaterialTheme.typography.labelSmall.scaled().copy(
                    color = ColorRed, fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily(Font(R.font.font_regular))
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(end = AppDimens.Dimens12)
            )
        }
    }
}
