package com.jigar.me.ui.view.home.common_ui.dialogs

import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.WindowInsets
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.BuildConfig
import com.revenuecat.purchases.models.Period
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.WorkspacePremium
import com.jigar.me.ui.view.ab.AbBeadsLoader
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbHeroBadge
import com.jigar.me.ui.view.ab.AbIconButton
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbSound
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abSquash
// Removed: the store provides the discount (no countdown pill on the paywall)
// import com.jigar.me.ui.view.home.common_ui.InlineCountdownPill
import com.jigar.me.ui.view.home.screens.purchase.components.getDurationTxt
import com.jigar.me.ui.view.home.screens.purchase.components.getPriceSuffix
import com.jigar.me.ui.view.home.screens.purchase.components.isYearly
// import com.jigar.me.ui.view.home.screens.purchase.components.originalPriceText
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.RcPlanItem
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.formatPlanPrice
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlin.math.roundToInt

/*
 * The kit look of the landscape paywall sheet (mirrors the themed branches of the iOS `FreemiumPaywallBottomSheet`):
 * a handle with a close button, the crown branding with "See Benefits" on the left, the plan cards on the right.
 */

/** The iPad multiplier of `responsiveDimenstion`, so the type grows the same way on tablets. */
private val responsive: Float get() = if (AppDimens.isLargeTablet) 1.7f else if (AppDimens.isTablet) 1.5f else 1f

@Composable
internal fun PaywallKitContent(
    headline: String,
    subtitle: String,
    socialProof: String,
    uiState: PurchaseUiState,
    onClose: () -> Unit,
    onSeeBenefits: () -> Unit,
    onRetry: () -> Unit,
    onSubscribe: (Int) -> Unit,
    privacyUrl: String = "",
    onOpenLink: (String) -> Unit = {},
    hideLifetime: Boolean = false,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Column(Modifier.fillMaxWidth()) {
        PaywallKitTopBar(onClose)
        Row(
            Modifier.fillMaxWidth().padding(start = AppDimens.Dimens16, end = AppDimens.Dimens16, top = AppDimens.Dimens8, bottom = AppDimens.Dimens16),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(0.35f), contentAlignment = Alignment.Center) {
                PaywallKitBranding(headline, subtitle, socialProof, onSeeBenefits)
            }
            Box(Modifier.width(2.dp).height(if (AppDimens.isLargeTablet) 160.dp else if (AppDimens.isTablet) 130.dp else 100.dp).background(colors.outline))
            PaywallKitPlans(
                uiState = uiState, onRetry = onRetry, onSubscribe = onSubscribe,
                privacyUrl = privacyUrl, onOpenLink = onOpenLink, modifier = Modifier.weight(0.65f), hideLifetime = hideLifetime,
            )
        }
    }
}

/** The plan cards with their loader / retry states and the billing note: the right side of the sheet and of the first-launch page. */
@Composable
internal fun PaywallKitPlans(
    uiState: PurchaseUiState,
    onRetry: () -> Unit,
    onSubscribe: (Int) -> Unit,
    privacyUrl: String = "",
    onOpenLink: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    firstLaunch: Boolean = false,
    /** Lifetime stays off the first N paywall sheets of a device (Remote Config lifetime_min_paywalls). */
    hideLifetime: Boolean = false,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
    ) {
        if (uiState.isLoading) {
            AbBeadsLoader(Modifier.padding(vertical = AppDimens.Dimens20))
        } else if (uiState.sortedPlanList.isEmpty()) {
            Column(
                Modifier.padding(vertical = AppDimens.Dimens20),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
            ) {
                Text(
                    stringResource(uiState.error ?: R.string.something_went_wrong),
                    style = AbTypography.body(s), color = colors.textSecondary, textAlign = TextAlign.Center,
                )
                AbButton(stringResource(R.string.retry), tone = AbTone.Primary, size = AbSize.Small, icon = Icons.Rounded.Refresh, onClick = onRetry)
            }
        } else {
            // the first-launch page leads with the yearly plan and does not offer lifetime; the index stays the plan's own place in the list
        val order = uiState.sortedPlanList.indices
            .filter { !((firstLaunch || hideLifetime) && uiState.sortedPlanList[it].sku.contains("all")) }
            .let { all -> if (firstLaunch) all.sortedBy { if (uiState.sortedPlanList[it].sku.contains("1year")) 0 else 1 } else all }
        order.forEach { index ->
            val plan = uiState.sortedPlanList[index]
                val isYearly = plan.sku.contains("1year")
                val isLifetime = plan.sku.contains("all")
                PaywallKitPlanCard(
                    icon = when {
                        isLifetime -> Icons.Rounded.AllInclusive
                        isYearly -> Icons.Rounded.WorkspacePremium
                        else -> Icons.Rounded.CalendarMonth
                    },
                    iconColor = when {
                        isLifetime -> Color(0xFF7B2FBE)
                        isYearly -> Color(0xFFFF8400)
                        else -> Color(0xFF0074D5)
                    },
                    title = plan.getDurationTxt(),
                    plan = plan,
                    isHighlighted = isYearly,
                    badge = when {
                        isYearly -> stringResource(R.string.paywall_badge_best_value)
                        isLifetime -> stringResource(R.string.paywall_badge_one_time)
                        else -> null
                    },
                    uiState = uiState,
                    onSubscribe = { onSubscribe(index) },
                )
            }
            val shown = order.map { uiState.sortedPlanList[it] }
            PaywallKitLegal(
                privacyUrl, onOpenLink,
                showRenewal = shown.any { !it.sku.contains("all") },
                showLifetimeNote = shown.any { it.sku.contains("all") },
            )
        }
    }
}

/** The first-launch page: the page's own left column and the plans side by side (inside the camera cut-out), no sheet. */
@Composable
fun PaywallKitEmbeddedLayout(left: @Composable () -> Unit, footer: (@Composable () -> Unit)? = null, plans: @Composable () -> Unit) {
    Row(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens12),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(0.44f).fillMaxHeight(), contentAlignment = Alignment.CenterStart) { left() }
        // the plans stay centred as before; a footer row (the tutorial's "Continue free for now") sits at the very bottom of the right side
        Box(Modifier.weight(0.56f).fillMaxHeight()) {
            Box(Modifier.align(Alignment.Center)) { plans() }
            footer?.let { Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth()) { it() } }
        }
    }
}

/** The handle in the middle and the close button on the right. */
@Composable
private fun PaywallKitTopBar(onClose: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Box(Modifier.fillMaxWidth().padding(top = AppDimens.Dimens8, bottom = AppDimens.Dimens4), contentAlignment = Alignment.Center) {
        Box(Modifier.size(44.dp * s, 5.dp * s).clip(RoundedCornerShape(3.dp * s)).background(colors.outline))
        Box(Modifier.align(Alignment.CenterEnd).padding(end = AppDimens.Dimens12).size(36.dp * s), contentAlignment = Alignment.Center) {
            AbIconButton(Icons.Rounded.Close, Modifier.requiredSize(48.dp * s).scale(0.7f), tone = AbTone.Ghost, sound = AbSound.Back, onClick = onClose)
        }
    }
}

@Composable
private fun PaywallKitBranding(headline: String, subtitle: String, socialProof: String, onSeeBenefits: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val k = responsive
    val crown = (if (AppDimens.isLargeTablet) 130f else if (AppDimens.isTablet) 100f else 48f).dp
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp * s)) {
        Box(Modifier.padding(bottom = 5.dp * s).size(crown * 1.7f), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val d = size.minDimension
                drawCircle(Color(0xFFE08A00), d / 2, center + Offset(0f, 5.dp.toPx() * s))
                drawCircle(Brush.verticalGradient(listOf(Color(0xFFFFE27A), Color(0xFFFFAE2E))), d / 2, center)
                val ring = 3.dp.toPx()
                drawCircle(Color.White.copy(alpha = 0.9f), d / 2 - ring / 2, center, style = Stroke(ring))
            }
            Image(painterResource(R.drawable.crown), null, Modifier.height(crown * 0.9f), contentScale = ContentScale.Fit)
        }
        // the headline and its line below sit close: Baloo's line box leaves a wide empty band between two lines
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy((-5).dp * k)) {
            Text(
                headline, style = AbTypography.fixed(22f * k, FontWeight.ExtraBold),
                color = colors.accentOrange.abDarkened(if (colors.isDark) 0f else 0.2f), textAlign = TextAlign.Center,
            )
            Text(subtitle, style = AbTypography.fixed(13f * k, FontWeight.SemiBold), color = colors.textSecondary, textAlign = TextAlign.Center)
        }
        if (socialProof.isNotBlank()) {
            Text(socialProof, style = AbTypography.fixed(12f * k, FontWeight.Bold), color = colors.accentOrange.abDarkened(if (colors.isDark) 0f else 0.38f), textAlign = TextAlign.Center) // light orange on white is hard to read
        }
        AbButton(
            stringResource(R.string.see_benefits), Modifier.padding(top = 4.dp * s),
            tone = AbTone.Purple, size = AbSize.Compact, icon = Icons.Rounded.FormatListBulleted, onClick = onSeeBenefits,
        )
    }
}

/** The full-screen "Premium benefits" card over a scrim; the sheet underneath stays open. */
@Composable
internal fun PaywallKitBenefits(onClose: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.lg * s)
    val tone = colors.accentPurple
    val none = remember { MutableInteractionSource() }
    Box(
        Modifier.fillMaxSize().background(colors.overlayScrim).clickable(none, indication = null) { onClose() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .fillMaxWidth(0.51f)
                .padding(bottom = 5.dp * s)
                .drawBehind {
                    val r = AbShapes.lg.toPx() * s
                    drawRoundRect(tone.abDarkened(0.3f), Offset(0f, 5.dp.toPx() * s), size, CornerRadius(r, r))
                }
                .clip(shape)
                .background(colors.bgSecondary)
                .drawWithContent {
                    drawContent()
                    val stroke = 3.dp.toPx()
                    val r = AbShapes.lg.toPx() * s - stroke / 2
                    drawRoundRect(tone.copy(alpha = 0.6f), Offset(stroke / 2, stroke / 2), Size(size.width - stroke, size.height - stroke), CornerRadius(r), style = Stroke(stroke))
                }
                .clickable(none, indication = null) { },
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(tone, tone.abDarkened(0.25f))))
                    .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens10),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.premium_benefits_title), Modifier.weight(1f), style = AbTypography.headline(s), color = Color.White)
                Box(
                    Modifier
                        .abSquash(cornerRadius = 16.dp * s, sound = AbSound.Back, onClick = onClose)
                        .size(32.dp * s)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape)
                        .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Close, null, Modifier.size(18.dp * s), tint = Color.White)
                }
            }
            Column(Modifier.padding(AppDimens.Dimens20), verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                listOf(
                    R.string.benefit_all_levels, R.string.benefit_all_exams, R.string.benefit_all_exercises,
                    R.string.benefit_ccm, R.string.benefit_all_games, R.string.benefit_report_history,
                ).forEach { res ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                        Icon(Icons.Rounded.Verified, null, Modifier.size(AppDimens.Dimens14), tint = Color(0xFF34C759))
                        Text(stringResource(res), style = AbTypography.fixed(12f * responsive, FontWeight.Medium), color = colors.textPrimary)
                    }
                }
            }
        }
    }
}

/** A plan: icon badge, title, price lines and a subscribe button; the highlighted plan wears an orange rim. */
@Composable
private fun PaywallKitPlanCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    plan: RcPlanItem,
    isHighlighted: Boolean,
    badge: String?,
    uiState: PurchaseUiState,
    onSubscribe: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val k = responsive
    val tone = if (isHighlighted) colors.accentOrange else colors.outline
    val shape = RoundedCornerShape(AbShapes.md * s)
    val edge = if (isHighlighted) colors.accentOrange.abDarkened(0.2f) else colors.outline
    // Removed: the store provides the discount (no Remote Config %, no struck-through ".offer" anchor, no countdown):
    // val originalPrice = originalPriceText(uiState.discountPer, plan, uiState.original1YearData, uiState.discountPerLifetime, uiState.originalLifetimeData)
    // val showYearOff = plan.isYearly() && uiState.discountPer > 0
    // val showLifetimeOff = !plan.isYearly() && plan.isLifeTimeOffer() && plan.sku.contains("offer") && uiState.discountPerLifetime > 0
    // val countdownEnd = if (showYearOff) uiState.homeOfferYearlyEndMillis else if (showLifetimeOff) uiState.homeOfferLifetimeEndMillis else null
    val savingsText = paywallSavingsText(plan, uiState)
    val trialDays = plan.freeTrialDays()
    val trialPrefix = if (trialDays > 0) stringResource(R.string.paywall_trial_then, trialDays) + " " else ""
    // a paid intro price the store offers (a Play offer on the base plan): the intro price leads, the regular one is struck through
    val intro = plan.storeIntroOffer()
    val perMonth = if (plan.isYearly()) plan.rcPackage.product.pricePerMonth()?.formatted else null
    val perMonthText = perMonth?.let { stringResource(R.string.paywall_per_month_approx, it) }

    Box(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 3.dp * s)
                .drawBehind {
                    val r = AbShapes.md.toPx() * s
                    drawRoundRect(edge, Offset(0f, 3.dp.toPx() * s), size, CornerRadius(r, r))
                }
                .background(colors.bgSecondary, shape)
                .then(if (isHighlighted) Modifier.background(colors.accentOrange.copy(alpha = if (colors.isDark) 0.16f else 0.08f), shape) else Modifier)
                .border(if (isHighlighted) 3.dp else 2.dp, tone, shape)
                .padding(horizontal = 12.dp * s, vertical = 8.dp * s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp * s),
        ) {
            AbHeroBadge(icon, accent = iconColor, size = 38.dp * s)
            Column(Modifier.weight(1f)) {
                Text(
                    title, style = AbTypography.fixed(14f * k, FontWeight.ExtraBold),
                    color = if (isHighlighted) colors.accentOrange.abDarkened(if (colors.isDark) 0f else 0.2f) else colors.textPrimary,
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp * s)) {
                    if (intro != null) {
                        Text(
                            intro.regularPrice, style = AbTypography.fixed(12f * k, FontWeight.SemiBold),
                            color = colors.textSecondary, textDecoration = TextDecoration.LineThrough, maxLines = 1,
                        )
                        Text(intro.introPrice, style = AbTypography.fixed(13f * k, FontWeight.ExtraBold), color = colors.brandPrimary, maxLines = 1)
                        Text(
                            stringResource(R.string.paywall_percent_off_store, intro.percentOff),
                            style = AbTypography.fixed(11f * k, FontWeight.ExtraBold), color = colors.accentGreenInk, maxLines = 1,
                        )
                    } else {
                        // a store free trial reads "7 Days Free, then ₹1,799 / year" on the SAME line, so it adds no height
                        Text(
                            buildAnnotatedString {
                                if (trialPrefix.isNotEmpty()) withStyle(SpanStyle(color = colors.accentGreenInk)) { append(trialPrefix) }
                                withStyle(SpanStyle(color = colors.brandPrimary)) { append("${plan.price ?: ""} ${plan.getPriceSuffix().trim()}") }
                            },
                            style = AbTypography.fixed(13f * k, FontWeight.ExtraBold), maxLines = 1,
                        )
                    }
                }
                // "save ~N% vs monthly" removed from the card (too much data). To bring it back use:
                // val infoLine = listOfNotNull(perMonthText, savingsText).joinToString(" · ")
                // the intro line ("first year, then ₹1,200 / year") replaces the per-month line: that one is worked out from the regular price
                val infoLine = if (intro != null) intro.thenLine(plan) else listOfNotNull(perMonthText).joinToString(" · ")
                if (infoLine.isNotEmpty()) {
                    Text(infoLine, style = AbTypography.fixed(11f * k, FontWeight.Bold), color = colors.textSecondary, maxLines = 1)
                }
                // Removed: the store provides the discount ("(extra ~N% OFF)" / "~N% OFF" rows and the countdown pill)
                // if (showYearOff || showLifetimeOff) {
                //     Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp * s)) {
                //         Text(
                //             if (showYearOff) stringResource(R.string.paywall_percent_off_extra, uiState.discountPer)
                //             else stringResource(R.string.paywall_percent_off, uiState.discountPerLifetime),
                //             style = AbTypography.fixed(11f * k, FontWeight.ExtraBold), color = colors.accentGreenInk,
                //         )
                //         // countdownEnd?.let { InlineCountdownPill(it, onOfferExpired) }
                //     }
                // }
            }
            AbButton(
                if (plan.isLifeTimeOffer()) stringResource(R.string.txt_purchase_Now) else stringResource(R.string.paywall_subscribe),
                tone = if (isHighlighted) AbTone.Warning else AbTone.Primary, size = AbSize.Compact, onClick = onSubscribe,
            )
        }
        if (badge != null) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp * s, y = (-10).dp * s)
                    .height(22.dp * s)
                    .background(Brush.horizontalGradient(listOf(Color(0xFF22A927), Color(0xFF049D06))), CircleShape)
                    .border(1.5.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                    .padding(horizontal = 10.dp * s),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    badge, style = AbTypography.fixed(12f * s, FontWeight.ExtraBold), color = Color.White, maxLines = 1,
                    modifier = Modifier.wrapContentHeight(unbounded = true),
                )
            }
        }
    }
}


/** Free-trial length of a plan, in days (0 = none). Debug builds can force it for renders. */
internal fun RcPlanItem.freeTrialDays(): Int {
    if (BuildConfig.DEBUG && AbDebugFlags.paywallTrialDays > 0 && !isLifeTimeOffer()) return AbDebugFlags.paywallTrialDays
    val period = rcPackage.product.defaultOption?.freePhase?.billingPeriod ?: return 0
    return when (period.unit) {
        Period.Unit.DAY -> period.value
        Period.Unit.WEEK -> period.value * 7
        Period.Unit.MONTH -> period.value * 30
        Period.Unit.YEAR -> period.value * 365
        else -> 0
    }
}

/**
 * A paid intro price the store offers on a subscription (a Google Play offer on the base plan, read through RevenueCat):
 * the intro price for the first [periodCount] x [periodUnit], then the regular price.
 */
internal data class StoreIntroOffer(
    val introPrice: String,
    val regularPrice: String,
    /** round((regular - intro) / regular * 100), from the two store prices */
    val percentOff: Int,
    val periodUnit: Period.Unit,
    /** the intro phase's billing period times its cycle count, e.g. 1 for "first year", 3 for "first 3 months" */
    val periodCount: Int,
)

/**
 * The store's paid intro offer of this plan (`defaultOption`'s intro phase, cheaper than its regular price), or null: a plan
 * with a free trial keeps the trial line (see [freeTrialDays]), a plan without an intro phase and a one-time lifetime
 * product just show their regular price.
 */
internal fun RcPlanItem.storeIntroOffer(): StoreIntroOffer? {
    if (type == "inapp" || isLifeTimeOffer() || freeTrialDays() > 0) return null
    val option = rcPackage.product.defaultOption ?: return null
    val intro = option.introPhase ?: return null
    val regular = option.fullPricePhase ?: option.pricingPhases.lastOrNull() ?: return null
    val introMicros = intro.price.amountMicros
    val regularMicros = regular.price.amountMicros
    if (introMicros <= 0L || regularMicros <= 0L || introMicros >= regularMicros) return null
    val unit = intro.billingPeriod.unit
    if (unit == Period.Unit.UNKNOWN) return null
    val percent = ((regularMicros - introMicros) * 100.0 / regularMicros).roundToInt()
    if (percent <= 0) return null
    return StoreIntroOffer(
        introPrice = formatPlanPrice(introMicros, intro.price.currencyCode),
        regularPrice = formatPlanPrice(regularMicros, regular.price.currencyCode),
        percentOff = percent,
        periodUnit = unit,
        periodCount = intro.billingPeriod.value * (intro.billingCycleCount ?: 1).coerceAtLeast(1),
    )
}

/** "first year", "first 3 months" ... */
@Composable
internal fun StoreIntroOffer.periodWords(): String = when (periodUnit) {
    Period.Unit.DAY -> pluralStringResource(R.plurals.paywall_intro_first_days, periodCount, periodCount)
    Period.Unit.WEEK -> pluralStringResource(R.plurals.paywall_intro_first_weeks, periodCount, periodCount)
    Period.Unit.MONTH -> pluralStringResource(R.plurals.paywall_intro_first_months, periodCount, periodCount)
    Period.Unit.YEAR -> pluralStringResource(R.plurals.paywall_intro_first_years, periodCount, periodCount)
    else -> ""
}

/** "first year, then ₹1,200 / year" */
@Composable
internal fun StoreIntroOffer.thenLine(plan: RcPlanItem): String =
    stringResource(R.string.paywall_intro_then, periodWords(), regularPrice + plan.getPriceSuffix())

/**
 * Billing wording and the privacy link under the plans, in as many lines as it needs. The renewal sentence is only there when a
 * subscription is on offer, and a lifetime plan gets its own "one-time purchase, does not renew" sentence instead.
 */
@Composable
internal fun PaywallKitLegal(
    privacyUrl: String,
    onOpenLink: (String) -> Unit,
    showRenewal: Boolean = true,
    showLifetimeNote: Boolean = false,
) {
    val colors = LocalAbColors.current
    val k = responsive
    val text = listOfNotNull(
        if (showRenewal) stringResource(R.string.paywall_billing_note) else null,
        if (showLifetimeNote) stringResource(R.string.paywall_billing_note_lifetime) else null,
    ).joinToString(" ")
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (text.isNotEmpty()) {
            Text(text, style = AbTypography.fixed(11f * k, FontWeight.SemiBold), color = colors.textSecondary, textAlign = TextAlign.Center)
        }
        if (privacyUrl.isNotBlank()) {
            Text(
                stringResource(R.string.paywall_privacy_link),
                style = AbTypography.fixed(11f * k, FontWeight.ExtraBold), color = colors.brandPrimary, textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onOpenLink(privacyUrl) },
            )
        }
    }
}
