package com.jigar.me.ui.view.home.screens.purchase.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.model.dbtable.inapp.isLifeTimeOffer
import com.jigar.me.data.model.dbtable.inapp.isYearly
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.jetpack.core.presentation.theme.Black
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorAccent
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorAccentLight
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorGreen
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.DateTimeUtils

@Composable
fun PurchasePlanCard(
    uiState: PurchaseUiState,
    plan: InAppSkuDetails,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val originalYearly = uiState.original1YearData
    val originalLifetime = uiState.originalLifetimeData
    val original1MonthData = uiState.original1MonthData
    val discountPer = uiState.discountPer
    val discountPerLifetime = uiState.discountPerLifetime
    val isAssignedPlan = (plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Year) && uiState.yearPlanAssignFromAdmin != null) || plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_All) && uiState.allPlanAssignFromAdmin != null

    val cardShape = RoundedCornerShape(AppDimens.Dimens12)
    val selectedBorder = isSelected && !plan.isPurchase && !isAssignedPlan
    Card(
        onClick = {
            if (!plan.isPurchase && !isAssignedPlan){
                onClick()
            }
        },
//        enabled = !plan.isPurchase && !isAssignedPlan,
        shape = cardShape,
        border = if (selectedBorder)
            BorderStroke(AppDimens.Dimens1, ColorAccent)
        else
            BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.06f)),
        colors = CardDefaults.cardColors(containerColor = ColorAccentLight),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = plan.getDurationTxt(),
                    style = MaterialTheme.typography.labelLarge.scaled().copy(color = Color.DarkGray,fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold)))
                )

                if ((plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Year) && uiState.yearPlanAssignFromAdmin != null) || plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_All) && uiState.allPlanAssignFromAdmin != null){
                    Spacer(Modifier.width(AppDimens.Dimens8))
                    Surface(
                        shape = RoundedCornerShape(AppDimens.Dimens4),
                        color = colorResource(R.color.green_500),
                        modifier = Modifier.padding(top = AppDimens.Dimens2),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Text(
                            lineHeight = 10.sp,
                            text = stringResource(R.string.assigned),
                            style = MaterialTheme.typography.labelSmall.scaled().copy(color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
                            modifier = Modifier.padding(
                                horizontal = AppDimens.Dimens8,
                                vertical = AppDimens.Dimens2
                            )
                        )
                    }
                }else if (plan.isPurchase){
                    Spacer(Modifier.width(AppDimens.Dimens8))
                    Surface(
                        shape = RoundedCornerShape(AppDimens.Dimens4),
                        color = colorResource(R.color.green_500),
                        modifier = Modifier.padding(top = AppDimens.Dimens2),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Text(
                            lineHeight = 10.sp,
                            text = stringResource(R.string.txt_purchased),
                            style = MaterialTheme.typography.labelSmall.scaled().copy(color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
                            modifier = Modifier.padding(
                                horizontal = AppDimens.Dimens8,
                                vertical = AppDimens.Dimens2
                            )
                        )
                    }
                }else{
                    Text(
                        text = " : ",
                        style = MaterialTheme.typography.bodyLarge.scaled().copy(color = Black,fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold)))
                    )

                    if(plan.getFreeTrialDays() > 0){
                        Text(
                            text = "${plan.getFreeTrialDays()} Days FREE",
                            style = MaterialTheme.typography.bodyLarge.scaled().copy(color = Black,fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold)))
                        )
                    }else{
                        PriceUi(discountPer,plan,originalYearly,discountPerLifetime,originalLifetime)
                    }

                }
            }

            if (!(plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Year) && uiState.yearPlanAssignFromAdmin != null || plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_All) && uiState.allPlanAssignFromAdmin != null) && !plan.isPurchase) {
                if (plan.getFreeTrialDays() > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "then",
                            style = MaterialTheme.typography.labelLarge.scaled().copy(color = Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold)))
                        )

                        PriceUi(discountPer, plan, originalYearly, discountPerLifetime, originalLifetime)
                    }
                }
            }


            if (plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Year) && uiState.yearPlanAssignFromAdmin != null){
                Spacer(Modifier.height(AppDimens.Dimens4))
                Text(
                    text = stringResource(R.string.plan_assigned_from_admin),
                    style = MaterialTheme.typography.labelMedium.scaled().copy(
                        color = Color.Black,fontWeight = FontWeight.Normal,fontFamily = FontFamily(Font(R.font.font_regular))
                    )
                )
                Text(
                    text = CommonUtils.htmlToAnnotatedString("<strong>Assigned ON : </strong>${
                        DateTimeUtils.convertDateFormat(uiState.yearPlanAssignFromAdmin.start_date?:"",
                            DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz,
                            DateTimeUtils.dd_MMMM_yyyy)}"),
                    style = MaterialTheme.typography.labelMedium.scaled().copy(
                        color = Color.Black,fontWeight = FontWeight.Normal,fontFamily = FontFamily(Font(R.font.font_regular))
                    )
                )
            }else if (plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_All) && uiState.allPlanAssignFromAdmin != null){
                Spacer(Modifier.height(AppDimens.Dimens4))
                Text(
                    text = CommonUtils.htmlToAnnotatedString(stringResource(R.string.plan_assigned_from_admin)),
                    style = MaterialTheme.typography.labelMedium.scaled().copy(
                        color = Color.Black,fontWeight = FontWeight.Normal,fontFamily = FontFamily(Font(R.font.font_regular))
                    )
                )
                Text(
                    text = CommonUtils.htmlToAnnotatedString("<strong>Assigned ON : </strong>${
                        DateTimeUtils.convertDateFormat(uiState.allPlanAssignFromAdmin.start_date?:"",
                            DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz,
                            DateTimeUtils.dd_MMMM_yyyy)}"),
                    style = MaterialTheme.typography.labelMedium.scaled().copy(
                        color = Color.Black,fontWeight = FontWeight.Normal,fontFamily = FontFamily(Font(R.font.font_regular))
                    )
                )
            }else if (plan.isPurchase){
                Spacer(Modifier.height(AppDimens.Dimens4))
                Text(
                    text = CommonUtils.htmlToAnnotatedString("<strong>Order Id : </strong>${plan.orderId}"),
                    style = MaterialTheme.typography.labelMedium.scaled().copy(
                        color = Color.Black,fontWeight = FontWeight.Normal,fontFamily = FontFamily(Font(R.font.font_regular))
                    )
                )
                Text(
                    text = CommonUtils.htmlToAnnotatedString(CommonUtils.getPurchaseTime(plan.type,plan.purchaseTime,plan.billingPeriod,)?:""),
                    style = MaterialTheme.typography.labelMedium.scaled().copy(
                        color = Color.Black,fontWeight = FontWeight.Normal,fontFamily = FontFamily(Font(R.font.font_regular))
                    )
                )
            }else{
                Row {
                    if (plan.isYearly()) {
                        if (original1MonthData != null) {
                            val yearlyMicros = if (originalYearly != null ){ originalYearly.price_amount_micros }else { plan.price_amount_micros }

                            Text(
                                text = "save ~${plan.calculateSavings(original1MonthData.price_amount_micros?:0L, yearlyMicros?:0)}% vs monthly",
                                style = MaterialTheme.typography.labelSmall.scaled().copy(
                                    color = Black,fontWeight = FontWeight.Medium,fontFamily = FontFamily(Font(R.font.font_medium))
                                )
                            )
                            if (discountPer > 0) {
                                Text(
                                    text = "(extra ~$discountPer% OFF)",
                                    style = MaterialTheme.typography.labelSmall.scaled().copy(
                                        color = ColorGreen,fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))
                                    )
                                )
                            }
                        }
                    }else if (plan.isLifeTimeOffer() && discountPerLifetime > 0){
                        Text(
                            text = "~ ${discountPerLifetime}% OFF",
                            style = MaterialTheme.typography.labelSmall.scaled().copy(
                                color = ColorGreen,fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))
                            )
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun PriceUi(discountPer: Int, plan: InAppSkuDetails, originalYearly: InAppSkuDetails?, discountPerLifetime: Int, originalLifetime: InAppSkuDetails?) {
    // price in strike
    if (discountPer > 0 && plan.sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer && originalYearly != null){
        if ((originalYearly.price_amount_micros ?: 0) > (plan.price_amount_micros?:0)){
            Text(
                text = originalYearly.price?:"",
                style = MaterialTheme.typography.bodySmall.scaled().copy(
                    textDecoration = TextDecoration.LineThrough,
                    color = ColorAccent,fontWeight = FontWeight.SemiBold,fontFamily = FontFamily(Font(R.font.font_semibold))
                )
            )
        }
    }else if (discountPerLifetime > 0 && plan.sku == BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_offer && originalLifetime != null){
        if ((originalLifetime.price_amount_micros ?: 0) > (plan.price_amount_micros?:0)){
            Text(
                text = originalLifetime.price?:"",
                style = MaterialTheme.typography.bodySmall.scaled().copy(
                    textDecoration = TextDecoration.LineThrough,
                    color = ColorAccent,fontWeight = FontWeight.SemiBold,fontFamily = FontFamily(Font(R.font.font_semibold))
                )
            )
        }
    }

    Spacer(Modifier.width(AppDimens.Dimens4))

    Text(
        text = plan.getDisplayPrice(),
        style = MaterialTheme.typography.bodyLarge.scaled().copy(
            color = ColorAccent,fontWeight = FontWeight.SemiBold,fontFamily = FontFamily(Font(R.font.font_semibold))
        )
    )
}
