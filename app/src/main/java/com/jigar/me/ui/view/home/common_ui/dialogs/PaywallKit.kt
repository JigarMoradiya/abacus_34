package com.jigar.me.ui.view.home.common_ui.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.jigar.me.ui.view.home.common_ui.InlineCountdownPill
import com.jigar.me.ui.view.home.screens.purchase.components.getDurationTxt
import com.jigar.me.ui.view.home.screens.purchase.components.getPriceSuffix
import com.jigar.me.ui.view.home.screens.purchase.components.isYearly
import com.jigar.me.ui.view.home.screens.purchase.components.originalPriceText
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.RcPlanItem
import com.jigar.me.ui.view.home.theme.AppDimens

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
    onOfferExpired: () -> Unit,
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
            Column(
                Modifier.weight(0.65f),
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
                    uiState.sortedPlanList.forEachIndexed { index, plan ->
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
                            onOfferExpired = onOfferExpired,
                        )
                    }
                }
            }
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
    onOfferExpired: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val k = responsive
    val tone = if (isHighlighted) colors.accentOrange else colors.outline
    val shape = RoundedCornerShape(AbShapes.md * s)
    val edge = if (isHighlighted) colors.accentOrange.abDarkened(0.2f) else colors.outline
    val originalPrice = originalPriceText(uiState.discountPer, plan, uiState.original1YearData, uiState.discountPerLifetime, uiState.originalLifetimeData)
    val savingsText = paywallSavingsText(plan, uiState)
    val showYearOff = plan.isYearly() && uiState.discountPer > 0
    val showLifetimeOff = !plan.isYearly() && plan.isLifeTimeOffer() && plan.sku.contains("offer") && uiState.discountPerLifetime > 0
    val countdownEnd = if (showYearOff) uiState.homeOfferYearlyEndMillis else if (showLifetimeOff) uiState.homeOfferLifetimeEndMillis else null

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
                    if (originalPrice != null) {
                        Text(
                            originalPrice, style = AbTypography.fixed(12f * k, FontWeight.SemiBold),
                            color = colors.textSecondary, textDecoration = TextDecoration.LineThrough,
                        )
                    }
                    Text(
                        "${plan.price ?: ""} ${plan.getPriceSuffix().trim()}", style = AbTypography.fixed(13f * k, FontWeight.ExtraBold),
                        color = colors.brandPrimary,
                    )
                }
                if (savingsText != null) {
                    Text(savingsText, style = AbTypography.fixed(11f * k, FontWeight.Bold), color = colors.textSecondary)
                }
                if (showYearOff || showLifetimeOff) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp * s)) {
                        Text(
                            if (showYearOff) stringResource(R.string.paywall_percent_off_extra, uiState.discountPer)
                            else stringResource(R.string.paywall_percent_off, uiState.discountPerLifetime),
                            style = AbTypography.fixed(11f * k, FontWeight.ExtraBold), color = colors.accentGreenInk,
                        )
                        countdownEnd?.let { InlineCountdownPill(it, onOfferExpired) }
                    }
                }
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
