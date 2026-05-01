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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Subscriptions
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
import com.android.billingclient.api.BillingClient
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorRed
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.theme.ButtonType

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
            uiState.sortedSkuList.forEachIndexed { index, plan ->
                if (index > 0) Spacer(Modifier.height(AppDimens.Dimens8))
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
            Spacer(Modifier.height(AppDimens.Dimens8))
            val selected = uiState.sortedSkuList[uiState.selectedIndex]

            val isSubs = selected.type == BillingClient.ProductType.SUBS
            if (isSubs){
                val text = if (selected.getFreeTrialDays() > 0){
                    stringResource(R.string.subscription_free_trial_msg)
                }else{
                    stringResource(R.string.cancel_subscription_anytime)
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall.scaled().copy(
                        color = ColorRed,fontWeight = FontWeight.Normal,fontFamily = FontFamily(Font(R.font.font_regular))
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(AppDimens.Dimens8))
            }

            KidsActionButton(
                text = if (isSubs)stringResource(R.string.txt_subscribe_Now) else stringResource(R.string.txt_purchase_Now),
                icon = Icons.Default.Subscriptions,
                type = ButtonType.BLUE,
                onClick = onSubscribe,
                isSmall = true
            )
        }
    }
}
