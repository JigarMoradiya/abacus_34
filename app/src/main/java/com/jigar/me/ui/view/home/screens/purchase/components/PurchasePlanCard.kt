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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Subscriptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.Black
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorAccent
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorAccentLight
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorGreen
// Removed: the store provides the discount (no countdown pill)
// import com.jigar.me.ui.view.home.common_ui.InlineCountdownPill
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.InlineCountdownPill
import com.jigar.me.ui.view.home.common_ui.dialogs.storeIntroOffer
import com.jigar.me.ui.view.home.common_ui.dialogs.badgeText
import com.jigar.me.ui.view.home.common_ui.dialogs.storeOffer
import com.jigar.me.ui.view.home.common_ui.dialogs.timedOffer
import com.jigar.me.utils.StoreOneTimeOffer
import com.jigar.me.ui.view.home.common_ui.dialogs.thenLine
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.RcPlanItem
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.DateTimeUtils
import androidx.compose.ui.res.pluralStringResource

// ── RcPlanItem helpers ──────────────────────────────────────────────────────

@Composable
fun RcPlanItem.getDurationTxt(): String = when {
    sku.contains("week") -> stringResource(R.string.plan_weekly)
    sku.contains("1month") && !sku.contains("trial") -> stringResource(R.string.plan_monthly)
    sku.contains("3month") -> stringResource(R.string.plan_3_month)
    sku.contains("6month") -> stringResource(R.string.plan_6_month)
    sku.contains("1year") -> stringResource(R.string.plan_yearly)
    sku.contains("all") -> stringResource(R.string.plan_lifetime)
    else -> sku
}

fun RcPlanItem.isYearly(): Boolean = sku.contains("1year")

@Composable
fun RcPlanItem.getPriceSuffix(): String = " " + when {
    sku.contains("week")   -> stringResource(R.string.plan_per_week)
    sku.contains("1month") -> stringResource(R.string.plan_per_month)
    sku.contains("1year")  -> stringResource(R.string.plan_per_year)
    else                   -> stringResource(R.string.plan_one_time)
}

fun RcPlanItem.calculateSavings(monthlyMicros: Long, yearlyMicros: Long): Long =
    yearlySavingsPercent(monthlyMicros, 12, yearlyMicros) ?: 0L

/** Savings % of the yearly price vs paying [baselineMicros] every period, [periodsPerYear] times a year. */
fun yearlySavingsPercent(baselineMicros: Long?, periodsPerYear: Int, yearlyMicros: Long?): Long? {
    if (baselineMicros == null || baselineMicros <= 0L || yearlyMicros == null || yearlyMicros <= 0L) return null
    val baselineYear = baselineMicros * periodsPerYear
    return ((baselineYear - yearlyMicros).toDouble() / baselineYear * 100).toLong().coerceAtLeast(0L)
}

// ── Composables ─────────────────────────────────────────────────────────────

@Composable
fun PurchasePlanCard(
    uiState: PurchaseUiState,
    plan: RcPlanItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onSubscribe: () -> Unit = {},
) {
    val isAssignedPlan = (plan.sku.contains("1year") && uiState.yearPlanAssignFromAdmin != null) ||
            (plan.sku.contains("all") && uiState.allPlanAssignFromAdmin != null)

    val cardShape = RoundedCornerShape(AppDimens.Dimens12)
    val selectedBorder = isSelected && !plan.isPurchase && !isAssignedPlan

    Card(
        onClick = { if (!plan.isPurchase && !isAssignedPlan) onClick() },
        shape = cardShape,
        border = if (selectedBorder) BorderStroke(AppDimens.Dimens1, ColorAccent)
                 else BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.06f)),
        colors = CardDefaults.cardColors(containerColor = ColorAccentLight),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8)) {

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                // Plan name + price
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.getDurationTxt(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.DarkGray, fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold))
                        )
                    )
                    if (!isAssignedPlan && !plan.isPurchase) {
                        Text(
                            text = " : ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Black, fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.font_bold))
                            )
                        )
                        PriceUi(plan, plan.timedOffer(uiState)?.originalPrice, plan.storeOffer(uiState))
                    }
                }

                // Action button on right
                val isLifetime = plan.isLifeTimeOffer()
                val isPurchased = plan.isPurchase || isAssignedPlan
                Column(horizontalAlignment = Alignment.End) {
                    KidsActionButton(
                        text = when {
                            isAssignedPlan -> stringResource(R.string.assigned)
                            isPurchased && !isLifetime -> stringResource(R.string.txt_subscribed)
                            isPurchased -> stringResource(R.string.txt_purchased)
                            isLifetime -> stringResource(R.string.txt_purchase_Now)
                            else -> stringResource(R.string.txt_subscribe_Now)
                        },
                        icon = Icons.Default.Subscriptions,
                        type = if (isPurchased) ButtonType.GREEN else ButtonType.BLUE,
                        onClick = { if (!isPurchased) onSubscribe() },
                        isSmall = true
                    )
                }
            }

            when {
                isAssignedPlan && plan.sku.contains("1year") && uiState.yearPlanAssignFromAdmin != null -> {
                    Spacer(Modifier.height(AppDimens.Dimens4))
                    Text(
                        text = stringResource(R.string.plan_assigned_from_admin),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Black, fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily(Font(R.font.font_regular))
                        )
                    )
                    Text(
                        text = CommonUtils.htmlToAnnotatedString(stringResource(R.string.purchase_assigned_on, 
                            DateTimeUtils.convertDateFormat(
                                uiState.yearPlanAssignFromAdmin.start_date ?: "",
                                DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz, DateTimeUtils.dd_MMMM_yyyy
                            ))),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Black, fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily(Font(R.font.font_regular))
                        )
                    )
                }
                isAssignedPlan && plan.sku.contains("all") && uiState.allPlanAssignFromAdmin != null -> {
                    Spacer(Modifier.height(AppDimens.Dimens4))
                    Text(
                        text = CommonUtils.htmlToAnnotatedString(stringResource(R.string.plan_assigned_from_admin)),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Black, fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily(Font(R.font.font_regular))
                        )
                    )
                    Text(
                        text = CommonUtils.htmlToAnnotatedString(stringResource(R.string.purchase_assigned_on, 
                            DateTimeUtils.convertDateFormat(
                                uiState.allPlanAssignFromAdmin.start_date ?: "",
                                DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz, DateTimeUtils.dd_MMMM_yyyy
                            ))),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Black, fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily(Font(R.font.font_regular))
                        )
                    )
                }
                plan.isPurchase -> {
                    Spacer(Modifier.height(AppDimens.Dimens4))
                    Text(
                        text = CommonUtils.htmlToAnnotatedString(
                            CommonUtils.getPurchaseTime(androidx.compose.ui.platform.LocalContext.current, plan.type, plan.purchaseTime, plan.billingPeriod) ?: ""
                        ),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Black, fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily(Font(R.font.font_regular))
                        )
                    )
                }
                else -> {
                    Column {
                        // a paid intro price the store offers: "first year, then ₹1,200 / year" and the green N% OFF
                        val intro = plan.storeIntroOffer()
                        if (intro != null) {
                            Row {
                                Text(
                                    text = intro.thenLine(plan),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Black, fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily(Font(R.font.font_medium))
                                    )
                                )
                                Spacer(Modifier.width(AppDimens.Dimens6))
                                Text(
                                    text = stringResource(R.string.paywall_percent_off_store, intro.percentOff),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = ColorGreen, fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily(Font(R.font.font_bold))
                                    )
                                )
                            }
                        }
                        // the lifetime plan's Google Play discount offer: "30% OFF" or "₹500 OFF" (as set in the Play Console)
                        val storeOffer = plan.storeOffer(uiState)
                        if (storeOffer != null && storeOffer.badgeText().isNotEmpty()) {
                            Row {
                                Text(
                                    text = storeOffer.badgeText(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = ColorGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))
                                    )
                                )
                            }
                        }
                        // the timed Home offer: "(extra ~N% OFF)" / "~N% OFF" and the live countdown of the ".offer" plan
                        val timed = plan.timedOffer(uiState)
                        if (timed != null && (timed.percentOff > 0 || timed.endMillis != null)) {
                            Row {
                                if (timed.percentOff > 0) {
                                    Text(
                                        text = if (timed.isYearly) stringResource(R.string.paywall_percent_off_extra, timed.percentOff)
                                               else stringResource(R.string.paywall_percent_off, timed.percentOff),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = ColorGreen, fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily(Font(R.font.font_bold))
                                        )
                                    )
                                }
                                timed.endMillis?.let { endMillis ->
                                    Spacer(Modifier.width(AppDimens.Dimens6))
                                    InlineCountdownPill(endMillis, {})   // the plans reload when the window ends (PurchaseViewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * The price line of a plan: the regular price, or (a paid intro offer from the store) the intro price with the regular one struck
 * through. [timedOriginalPrice] is the regular plan's price while the timed Home offer shows this ".offer" plan instead of it.
 */
@Composable
fun PriceUi(plan: RcPlanItem, timedOriginalPrice: String? = null, storeOffer: StoreOneTimeOffer? = null) {
    // the lifetime plan's Google Play discount offer: the regular price struck through, the offer price in its place
    if (storeOffer != null) {
        Text(
            text = storeOffer.fullPriceText,
            style = MaterialTheme.typography.bodySmall.copy(
                textDecoration = TextDecoration.LineThrough, color = ColorAccent,
                fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))
            )
        )
        Spacer(Modifier.width(AppDimens.Dimens4))
        Text(
            text = storeOffer.offerPriceText,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = ColorAccent, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))
            )
        )
        val storeSuffix = plan.getPriceSuffix()
        if (storeSuffix.isNotEmpty()) {
            Text(
                text = storeSuffix,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ColorAccent, fontWeight = FontWeight.Medium, fontFamily = FontFamily(Font(R.font.font_medium))
                )
            )
        }
        return
    }
    val intro = if (timedOriginalPrice == null) plan.storeIntroOffer() else null
    if (timedOriginalPrice != null) {
        Text(
            text = timedOriginalPrice,
            style = MaterialTheme.typography.bodySmall.copy(
                textDecoration = TextDecoration.LineThrough, color = ColorAccent,
                fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))
            )
        )
    }
    if (intro != null) {
        Text(
            text = intro.regularPrice,
            style = MaterialTheme.typography.bodySmall.copy(
                textDecoration = TextDecoration.LineThrough, color = ColorAccent,
                fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))
            )
        )
        Spacer(Modifier.width(AppDimens.Dimens4))
        // the period the intro price covers is in the "first year, then ..." line, so no "/ year" suffix here
        Text(
            text = intro.introPrice,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = ColorAccent, fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            )
        )
        return
    }

    Spacer(Modifier.width(AppDimens.Dimens4))

    Text(
        text = plan.price ?: "",
        style = MaterialTheme.typography.bodyMedium.copy(
            color = ColorAccent, fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.font_bold))
        )
    )

    val suffix = plan.getPriceSuffix()
    if (suffix.isNotEmpty()) {
        Text(
            text = suffix,
            style = MaterialTheme.typography.labelSmall.copy(
                color = ColorAccent, fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.font_medium))
            )
        )
    }
}
