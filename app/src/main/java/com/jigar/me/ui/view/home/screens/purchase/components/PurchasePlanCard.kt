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
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.RcPlanItem
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.DateTimeUtils

// ── RcPlanItem helpers ──────────────────────────────────────────────────────

fun RcPlanItem.getDurationTxt(): String = when {
    sku.contains("week") -> "Weekly"
    sku.contains("1month") && !sku.contains("trial") -> "Monthly"
    sku.contains("3month") -> "3 Month"
    sku.contains("6month") -> "6 Month"
    sku.contains("1year") -> "Yearly"
    sku.contains("all") -> "Lifetime"
    else -> sku
}

fun RcPlanItem.isYearly(): Boolean = sku.contains("1year")

fun RcPlanItem.getPriceSuffix(): String = when {
    sku.contains("week")   -> " / week"
    sku.contains("1month") -> " / month"
    sku.contains("1year")  -> " / year"
    else                   -> " one time"
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
    onSubscribe: () -> Unit = {}
) {
    val originalYearly = uiState.original1YearData
    val originalLifetime = uiState.originalLifetimeData
    val original1MonthData = uiState.original1MonthData
    val discountPer = uiState.discountPer
    val discountPerLifetime = uiState.discountPerLifetime
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
                        PriceUi(discountPer, plan, originalYearly, discountPerLifetime, originalLifetime)
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
                        text = CommonUtils.htmlToAnnotatedString("<strong>Assigned ON : </strong>${
                            DateTimeUtils.convertDateFormat(
                                uiState.yearPlanAssignFromAdmin.start_date ?: "",
                                DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz, DateTimeUtils.dd_MMMM_yyyy
                            )}"),
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
                        text = CommonUtils.htmlToAnnotatedString("<strong>Assigned ON : </strong>${
                            DateTimeUtils.convertDateFormat(
                                uiState.allPlanAssignFromAdmin.start_date ?: "",
                                DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz, DateTimeUtils.dd_MMMM_yyyy
                            )}"),
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
                            CommonUtils.getPurchaseTime(plan.type, plan.purchaseTime, plan.billingPeriod) ?: ""
                        ),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Black, fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily(Font(R.font.font_regular))
                        )
                    )
                }
                else -> {
                    Row {
                        if (plan.isYearly()) {
                            if (original1MonthData != null) {
                                val yearlyMicros = (originalYearly?.price_amount_micros ?: plan.price_amount_micros) ?: 0L
                                Text(
                                    text = "save ~${plan.calculateSavings(original1MonthData.price_amount_micros ?: 0L, yearlyMicros)}% vs monthly",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Black, fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily(Font(R.font.font_medium))
                                    )
                                )
                                if (discountPer > 0) {
                                    Text(
                                        text = "(extra ~$discountPer% OFF)",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = ColorGreen, fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily(Font(R.font.font_bold))
                                        )
                                    )
                                }
                            }
                        } else if (plan.isLifeTimeOffer() && discountPerLifetime > 0) {
                            Text(
                                text = "~ ${discountPerLifetime}% OFF",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = ColorGreen, fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.font_bold))
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriceUi(
    discountPer: Int, plan: RcPlanItem,
    originalYearly: RcPlanItem?, discountPerLifetime: Int, originalLifetime: RcPlanItem?
) {
    if (discountPer > 0 && plan.sku == "com.abacus.puzzle.1year.offer" && originalYearly != null) {
        if ((originalYearly.price_amount_micros ?: 0) > (plan.price_amount_micros ?: 0)) {
            Text(
                text = originalYearly.price ?: "",
                style = MaterialTheme.typography.bodySmall.copy(
                    textDecoration = TextDecoration.LineThrough, color = ColorAccent,
                    fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))
                )
            )
        }
    } else if (discountPerLifetime > 0 && plan.sku == "com.abacus.all.offer") {
        val originalPrice = if (originalLifetime != null && (originalLifetime.price_amount_micros ?: 0) > (plan.price_amount_micros ?: 0)) {
            originalLifetime.price
        } else {
            // com.abacus.all not in offerings — calculate original from discount %
            val offerMicros = plan.price_amount_micros ?: 0L
            val originalMicros = (offerMicros * 100.0 / (100 - discountPerLifetime)).toLong()
            val formatted = try {
                val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault())
                format.currency = java.util.Currency.getInstance(plan.rcPackage.product.price.currencyCode)
                format.maximumFractionDigits = 0
                format.format(originalMicros / 1_000_000.0)
            } catch (e: Exception) { null }
            formatted
        }
        if (originalPrice != null) {
            Text(
                text = originalPrice,
                style = MaterialTheme.typography.bodySmall.copy(
                    textDecoration = TextDecoration.LineThrough, color = ColorAccent,
                    fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))
                )
            )
        }
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
