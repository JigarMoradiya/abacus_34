package com.jigar.me.ui.view.home.screens.purchase.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbBeadsLoader
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbIconButton
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbSound
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.InlineCountdownPill
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.RcPlanItem
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.DateTimeUtils

/** The iPad multiplier of `responsiveDimenstion`, so the type grows the same way on tablets. */
private val responsive: Float get() = if (AppDimens.isLargeTablet) 1.7f else if (AppDimens.isTablet) 1.5f else 1f

/**
 * The paywall screen in the kit look (mirrors the iOS `SubscriptionView`): benefits on the left, the plan cards on the
 * right with one common button under them. Call it under [com.jigar.me.ui.view.ab.AbTheme].
 */
@Composable
fun PurchaseScreen(
    uiState: PurchaseUiState,
    onPlanSelected: (Int) -> Unit,
    onShowOldSubClick: () -> Unit,
    oldSubPopupCloseClick: () -> Unit,
    onSubscribe: () -> Unit,
    onClose: () -> Unit,
    onOfferExpired: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val s = LocalAbScale.current.factor
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(start = DeviceInfo.screenHorizontalPadding(), end = AppDimens.Dimens16)
                .padding(vertical = AppDimens.Dimens16),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
        ) {
            PurchaseKitBenefits(uiState.benefitList, Modifier.weight(0.6f).fillMaxHeight())
            PurchaseKitPlans(uiState, onPlanSelected, onSubscribe, onOfferExpired, onRetry, Modifier.weight(0.4f).fillMaxHeight())
        }

        if (uiState.isOldSubscriptionThere) {
            AbButton(
                stringResource(R.string.old_purchases),
                Modifier.align(Alignment.BottomCenter).padding(bottom = AppDimens.Dimens12),
                tone = AbTone.Success, size = AbSize.Compact, onClick = onShowOldSubClick,
            )
        }

        AbIconButton(
            Icons.Rounded.Close, tone = AbTone.Primary, sound = AbSound.Back, onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = DeviceInfo.screenHorizontalPadding(), top = DeviceInfo.screenTopPadding() + AppDimens.Dimens10)
                .padding(start = if (DeviceInfo.screenHorizontalPadding() == 0.dp) AppDimens.Dimens16 else 0.dp),
        )
    }

    // set Old Subscription popup
    AnimatedVisibility(
        visible = uiState.showOldSubscriptionPopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        OldPurchasesBottomSheet(
            uiState = uiState, onClose = {
                oldSubPopupCloseClick()
            }
        )
    }
}

// region left: the benefits

@Composable
private fun PurchaseKitBenefits(benefits: List<String>, modifier: Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val k = responsive
    val crown = (if (AppDimens.isLargeTablet) 130f else if (AppDimens.isTablet) 100f else 48f).dp
    Column(modifier.verticalScroll(rememberScrollState()).padding(top = if (AppDimens.isTablet) 0.dp else 50.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.crown), null, Modifier.height(crown), contentScale = ContentScale.Fit)
            // the sub title is pulled up under the title (Baloo leaves a tall empty band between two lines)
            Column(verticalArrangement = Arrangement.spacedBy((-7f * k).dp)) {
                Text(stringResource(R.string.purchase_headline), style = AbTypography.fixed(26f * k, FontWeight.ExtraBold), color = colors.brandPrimary)
                Text(
                    stringResource(R.string.purchase_subline), style = AbTypography.fixed(16f * k, FontWeight.Bold),
                    color = colors.textSecondary, maxLines = 2,
                )
            }
        }
        val shape = RoundedCornerShape(AbShapes.lg * s)
        Column(
            Modifier
                .padding(top = AppDimens.Dimens10)
                .fillMaxWidth()
                .background(colors.bgSecondary.copy(alpha = if (colors.isDark) 0.96f else 0.94f), shape)
                .border(2.dp, colors.outline, shape)
                .padding(if (AppDimens.isTablet) 16.dp else 10.dp),
            verticalArrangement = Arrangement.spacedBy(if (AppDimens.isTablet) 12.dp else 6.dp),
        ) {
            benefits.forEach { line ->
                // each line starts with its emoji; the rest keeps its <strong> runs in one kit font
                val emoji = line.substringBefore(' ')
                val text = line.substringAfter(' ')
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), verticalAlignment = Alignment.Top) {
                    Text(emoji, style = AbTypography.fixed(16f * k, FontWeight.Medium), modifier = Modifier.padding(top = AppDimens.Dimens2))
                    Text(markup(text, 14f * k), color = colors.textPrimary)
                }
            }
        }
    }
}

/** `<strong>`/`<b>` runs in one kit font: the same size throughout, heavier where bold. */
@Composable
private fun markup(source: String, size: Float): AnnotatedString {
    val regular = AbTypography.fixed(size, FontWeight.SemiBold).toSpanStyle()
    val bold = AbTypography.fixed(size, FontWeight.ExtraBold).toSpanStyle()
    return remember(source, size) {
        buildAnnotatedString {
            val pattern = Regex("<(strong|b)>(.*?)</\\1>")
            var last = 0
            for (m in pattern.findAll(source)) {
                withStyle(regular) { append(source.substring(last, m.range.first)) }
                withStyle(bold) { append(m.groupValues[2]) }
                last = m.range.last + 1
            }
            withStyle(regular) { append(source.substring(last)) }
        }
    }
}

// endregion

// region right: the plans

@Composable
private fun PurchaseKitPlans(
    uiState: PurchaseUiState,
    onPlanSelected: (Int) -> Unit,
    onSubscribe: () -> Unit,
    onOfferExpired: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        // Plans come from a network fetch (RevenueCat offerings) that can fail (no connectivity, no signed-in store
        // account, etc.): say so and offer a retry instead of rendering nothing.
        if (uiState.isLoading) {
            AbBeadsLoader()
        } else if (uiState.sortedPlanList.isEmpty()) {
            // Three different reasons for an empty list: the plans could not be loaded (an error was set), the user was
            // given a plan by the admin (it takes the store's plans off the list, so what is shown instead is that plan,
            // the same card the list would have carried), or the store returned no plans. Only the first is an error.
            val adminPlan = uiState.allPlanAssignFromAdmin ?: uiState.yearPlanAssignFromAdmin
            if (uiState.error == null && adminPlan != null) {
                PurchaseKitAdminPlanCard(isLifetime = uiState.allPlanAssignFromAdmin != null, startDate = adminPlan.start_date)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                    Text(
                        stringResource(uiState.error ?: R.string.purchase_no_plans),
                        style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold), color = colors.textSecondary,
                        textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = AppDimens.Dimens16),
                    )
                    AbButton(stringResource(R.string.retry), tone = AbTone.Primary, size = AbSize.Small, icon = Icons.Rounded.Refresh, onClick = onRetry)
                }
            }
        } else {
            Column(Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                uiState.sortedPlanList.forEachIndexed { index, plan ->
                    PurchaseKitPlanCard(
                        uiState = uiState,
                        plan = plan,
                        isSelected = uiState.selectedIndex == index,
                        onClick = { onPlanSelected(index) },
                        onOfferExpired = onOfferExpired,
                    )
                }
            }

            val selected = uiState.sortedPlanList.getOrNull(uiState.selectedIndex)
            // startsWith, not == -- arrangeData() only removeExact()s the plain SKU when a plan is admin-assigned, so
            // only the ".offer" variant of that same plan type ever remains selectable in that case.
            val isSelectedAssignedFromAdmin = selected != null && (
                (selected.sku.startsWith("com.abacus.puzzle.1year") && uiState.yearPlanAssignFromAdmin != null) ||
                    (selected.sku.startsWith("com.abacus.all") && uiState.allPlanAssignFromAdmin != null)
                )
            if (selected != null && !selected.isPurchase && !isSelectedAssignedFromAdmin) {
                if (!selected.isLifeTimeOffer()) {
                    Text(
                        stringResource(R.string.cancel_subscription_anytime),
                        Modifier.fillMaxWidth().padding(top = AppDimens.Dimens8),
                        style = AbTypography.fixed(14f * s, FontWeight.SemiBold), color = colors.textSecondary, textAlign = TextAlign.Center,
                    )
                }
                // the common button below the list
                if (uiState.isPurchasing) {
                    Box(Modifier.fillMaxWidth().padding(top = AppDimens.Dimens8), contentAlignment = Alignment.Center) { AbBeadsLoader() }
                } else {
                    AbButton(
                        stringResource(if (selected.isLifeTimeOffer()) R.string.txt_purchase_Now else R.string.txt_subscribe_Now),
                        Modifier.padding(horizontal = AppDimens.Dimens16).padding(top = AppDimens.Dimens8, bottom = AppDimens.Dimens8),
                        tone = AbTone.Warning, size = AbSize.Medium, icon = Icons.Rounded.WorkspacePremium, fillWidth = true,
                        onClick = onSubscribe,
                    )
                }
            }
        }
    }
}

/** The plan the admin gave this user, drawn like a plan card ("Lifetime Plan : Assigned", who gave it and when). */
@Composable
private fun PurchaseKitAdminPlanCard(isLifetime: Boolean, startDate: String?) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val k = responsive
    val shape = RoundedCornerShape(AbShapes.md * s)
    val ink = colors.textPrimary
    Box(Modifier.padding(AppDimens.Dimens4)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 3.dp * s)
                .drawBehind {
                    val r = AbShapes.md.toPx() * s
                    drawRoundRect(colors.outline, Offset(0f, 3.dp.toPx() * s), size, CornerRadius(r, r))
                }
                .background(colors.bgSecondary, shape)
                .border(2.dp, colors.outline, shape)
                .padding(AppDimens.Dimens8),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens2), verticalAlignment = Alignment.CenterVertically) {
                Text("${if (isLifetime) "Lifetime" else "Yearly"} Plan :", style = AbTypography.fixed(14f * k, FontWeight.SemiBold), color = ink)
                Text(
                    stringResource(R.string.assigned),
                    Modifier
                        .background(colors.accentGreen.abReadable(4.5f), CircleShape)
                        .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens4),
                    style = AbTypography.fixed(12f * k, FontWeight.SemiBold), color = Color.White,
                )
            }
            Column(Modifier.offset(y = 4.dp * s)) {
                Text(
                    CommonUtils.htmlToAnnotatedString(stringResource(R.string.plan_assigned_from_admin)),
                    style = AbTypography.fixed(10f * k, FontWeight.SemiBold), color = ink,
                )
                Text(
                    CommonUtils.htmlToAnnotatedString("<strong>Assigned ON : </strong>${
                        DateTimeUtils.convertDateFormat(startDate ?: "", DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz, DateTimeUtils.dd_MMMM_yyyy)}"),
                    style = AbTypography.fixed(10f * k, FontWeight.SemiBold), color = ink,
                )
            }
        }
    }
}

/** A plan: name and price on one line, the savings / offer lines under it; the selected plan wears the brand rim. */
@Composable
private fun PurchaseKitPlanCard(
    uiState: PurchaseUiState,
    plan: RcPlanItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onOfferExpired: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val k = responsive
    val originalYearly = uiState.original1YearData
    val discountPer = uiState.discountPer
    val discountPerLifetime = uiState.discountPerLifetime
    val isAssigned = (plan.sku.contains("1year") && uiState.yearPlanAssignFromAdmin != null) ||
        (plan.sku.contains("all") && uiState.allPlanAssignFromAdmin != null)
    val isPurchased = plan.isPurchase
    val showsBestValue = plan.isYearly() && !isPurchased && !isAssigned
    val shape = RoundedCornerShape(AbShapes.md * s)
    val ink = colors.textPrimary
    val original = originalPriceText(discountPer, plan, originalYearly, discountPerLifetime, uiState.originalLifetimeData)

    Box(Modifier.padding(AppDimens.Dimens4).padding(top = if (showsBestValue) 8.dp * s else 0.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 3.dp * s)
                .drawBehind {
                    val r = AbShapes.md.toPx() * s
                    drawRoundRect(if (isSelected) colors.brandPrimaryDark else colors.outline, Offset(0f, 3.dp.toPx() * s), size, CornerRadius(r, r))
                }
                .background(colors.bgSecondary, shape)
                .then(if (isSelected) Modifier.background(colors.brandPrimary.copy(alpha = if (colors.isDark) 0.20f else 0.09f), shape) else Modifier)
                .border(if (isSelected) 3.dp else 2.dp, if (isSelected) colors.brandPrimary else colors.outline, shape)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = !isPurchased && !isAssigned) { onClick() }
                .padding(AppDimens.Dimens8),
        ) {
            // name + price (or the "Subscribed" / "Assigned" pill)
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens2), verticalAlignment = Alignment.CenterVertically) {
                Text("${plan.getDurationTxt()} Plan :", style = AbTypography.fixed(14f * k, FontWeight.SemiBold), color = ink)
                if (!isPurchased && !isAssigned) {
                    if (original != null) {
                        Text(
                            original, style = AbTypography.fixed(15f * k, FontWeight.SemiBold), color = colors.brandPrimary,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }
                    Text(
                        plan.price ?: "", style = AbTypography.fixed(20f * k, FontWeight.ExtraBold), color = colors.brandPrimary,
                        modifier = Modifier.padding(start = AppDimens.Dimens4),
                    )
                    Text(plan.getPriceSuffix(), style = AbTypography.fixed(14f * k, FontWeight.SemiBold), color = colors.brandPrimary)
                } else {
                    Text(
                        stringResource(
                            when {
                                isAssigned -> R.string.assigned
                                !plan.isLifeTimeOffer() -> R.string.txt_subscribed
                                else -> R.string.txt_purchased
                            }
                        ),
                        Modifier
                            .background(colors.accentGreen.abReadable(4.5f), CircleShape)
                            .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens4),
                        style = AbTypography.fixed(12f * k, FontWeight.SemiBold), color = Color.White,
                    )
                }
            }

            // Baloo's line boxes are tall, so the rows under the price line are pulled up: the group under the price line,
            // and each row under the one above it (the gaps measured about 12 and 10 dp before)
            Column(if (isPurchased || isAssigned) Modifier.offset(y = 4.dp * s) else Modifier.tuckUp(12.dp * s)) {
                when {
                    isAssigned && plan.sku.contains("1year") && uiState.yearPlanAssignFromAdmin != null -> {
                        Text(stringResource(R.string.plan_assigned_from_admin), style = AbTypography.fixed(10f * k, FontWeight.SemiBold), color = ink)
                        Text(
                            CommonUtils.htmlToAnnotatedString("<strong>Assigned ON : </strong>${
                                DateTimeUtils.convertDateFormat(
                                    uiState.yearPlanAssignFromAdmin.start_date ?: "",
                                    DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz, DateTimeUtils.dd_MMMM_yyyy
                                )}"),
                            style = AbTypography.fixed(10f * k, FontWeight.SemiBold), color = ink,
                        )
                    }
                    isAssigned && plan.sku.contains("all") && uiState.allPlanAssignFromAdmin != null -> {
                        Text(
                            CommonUtils.htmlToAnnotatedString(stringResource(R.string.plan_assigned_from_admin)),
                            style = AbTypography.fixed(10f * k, FontWeight.SemiBold), color = ink,
                        )
                        Text(
                            CommonUtils.htmlToAnnotatedString("<strong>Assigned ON : </strong>${
                                DateTimeUtils.convertDateFormat(
                                    uiState.allPlanAssignFromAdmin.start_date ?: "",
                                    DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz, DateTimeUtils.dd_MMMM_yyyy
                                )}"),
                            style = AbTypography.fixed(10f * k, FontWeight.SemiBold), color = ink,
                        )
                    }
                    isPurchased -> {
                        Text(
                            CommonUtils.htmlToAnnotatedString(CommonUtils.getPurchaseTime(plan.type, plan.purchaseTime, plan.billingPeriod) ?: ""),
                            style = AbTypography.fixed(10f * k, FontWeight.SemiBold), color = ink,
                        )
                    }
                    else -> {
                        val monthly = uiState.original1MonthData
                        if (plan.isYearly() && monthly != null) {
                            val yearlyMicros = (originalYearly?.price_amount_micros ?: plan.price_amount_micros) ?: 0L
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("save", style = AbTypography.fixed(12f * k, FontWeight.SemiBold), color = ink)
                                Text(
                                    " ~${plan.calculateSavings(monthly.price_amount_micros ?: 0L, yearlyMicros)}% ",
                                    style = AbTypography.fixed(18f * k, FontWeight.ExtraBold), color = ink,
                                )
                                Text("vs monthly", style = AbTypography.fixed(12f * k, FontWeight.SemiBold), color = ink)
                            }
                            // "extra ~% OFF" + countdown always on their own row below the savings line
                            if (discountPer > 0) {
                                Row(Modifier.tuckUp(6.dp * s), horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens2), verticalAlignment = Alignment.CenterVertically) {
                                    Text("(extra ~$discountPer% OFF)", style = AbTypography.fixed(14f * k, FontWeight.SemiBold), color = colors.accentGreenInk)
                                    uiState.homeOfferYearlyEndMillis?.let { InlineCountdownPill(it, onOfferExpired) }
                                }
                            }
                        } else if (plan.isLifeTimeOffer() && discountPerLifetime > 0) {
                            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens2), verticalAlignment = Alignment.CenterVertically) {
                                Text("~$discountPerLifetime% OFF", style = AbTypography.fixed(14f * k, FontWeight.SemiBold), color = colors.accentGreenInk)
                                uiState.homeOfferLifetimeEndMillis?.let { InlineCountdownPill(it, onOfferExpired) }
                            }
                        }
                    }
                }
            }
        }

        // the same "Best Value!" badge the premium sheet puts on the yearly plan
        if (showsBestValue) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp * s, y = (-9).dp * s)
                    .height(22.dp * s)
                    .background(Brush.horizontalGradient(listOf(Color(0xFF22A927), Color(0xFF049D06))), CircleShape)
                    .border(1.5.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                    .padding(horizontal = 10.dp * s),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(R.string.paywall_badge_best_value), style = AbTypography.fixed(12f * s, FontWeight.ExtraBold), color = Color.White,
                    maxLines = 1, modifier = Modifier.wrapContentHeight(unbounded = true),
                )
            }
        }
    }
}

// endregion

/** Pulls a row up by [by] and gives that room back, so stacked Baloo lines (tall line boxes) sit close together. */
private fun Modifier.tuckUp(by: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val pull = by.roundToPx().coerceAtMost(placeable.height)
    layout(placeable.width, placeable.height - pull) { placeable.place(0, -pull) }
}
