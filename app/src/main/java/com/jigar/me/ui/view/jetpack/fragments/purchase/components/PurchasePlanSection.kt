package com.jigar.me.ui.view.jetpack.fragments.purchase.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.BillingClient
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.components.PrimaryButton
import com.jigar.me.ui.view.jetpack.fragments.purchase.viewmodels.PurchaseUiState

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

        Icon(
            painterResource(R.drawable.crown),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = Color.Unspecified
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding16)))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(end = dimensionResource(R.dimen.activity_padding12)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding4)),
        ) {
            itemsIndexed(uiState.sortedSkuList) { index, plan ->
                PurchasePlanCard(
                    uiState = uiState,
                    plan = plan,
                    isSelected = uiState.selectedIndex == index,
                    onClick = {
                        onPlanSelected(index)
                    }
                )
            }
        }

        if (uiState.showSubmitButton && uiState.sortedSkuList.isNotEmpty()){
            Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding8)))
            val isSubs = uiState.sortedSkuList[uiState.selectedIndex].type == BillingClient.ProductType.SUBS
            PrimaryButton(text = if (isSubs)stringResource(R.string.txt_subscribe_Now) else stringResource(R.string.txt_purchase_Now), onClick = onSubscribe)
        }
    }
}
