package com.jigar.me.ui.view.home.common_ui.dialogs

import android.app.Activity
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorAccent
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.purchase.components.getDurationTxt
import com.jigar.me.ui.view.home.screens.purchase.components.isYearly
import com.jigar.me.ui.view.home.screens.purchase.components.yearlySavingsPercent
import com.jigar.me.ui.view.home.common_ui.sheets.KidsBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.ParentalGateDialog
import com.jigar.me.ui.view.home.screens.purchase.components.PriceUi
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun FreemiumPaywallBottomSheet(
    paywallContext: PaywallContext = PaywallContext.GENERIC,
    onSubscriptionActivated: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    val viewModel: PurchaseViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    // Purchase succeeded — dismiss and notify caller so background screen removes locks
    LaunchedEffect(uiState.purchaseSuccess) {
        if (uiState.purchaseSuccess) {
            onDismiss()
            onSubscriptionActivated()
        }
    }

    val isLoading = uiState.sortedPlanList.isEmpty()
    var showBenefits by remember { mutableStateOf(false) }
    var showLoginSheet by remember { mutableStateOf(false) }
    var pendingPlanIndex by remember { mutableStateOf<Int?>(null) }
    var parentalGatePassed by remember { mutableStateOf(false) }

    // After login — re-check subscription fresh (RC + admin plans) then proceed or purchase
    fun handleAfterLogin() {
        showLoginSheet = false
        if (viewModel.isUserSubscribed()) {
            onDismiss(); onSubscriptionActivated()
        } else {
            pendingPlanIndex?.let { index ->
                pendingPlanIndex = null
                viewModel.onPlanSelected(index)
                viewModel.makePurchase(activity)
            }
        }
    }

    KidsBottomSheet(
        visible = true,
        onDismiss = onDismiss,
        overlay = {
            if (showBenefits) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showBenefits = false },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .clip(RoundedCornerShape(AppDimens.Dimens16))
                            .background(Color.White)
                    ) {
                        // Purple gradient header band -- "Sticker Book" treatment
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Brush.horizontalGradient(listOf(Color(0xFF9374EF), Color(0xFF6446CC))))
                                .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens12),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.premium_benefits_title),
                                style = MaterialTheme.typography.titleSmall.scaled(),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.font_bold)),
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .size(AppDimens.Dimens24)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { showBenefits = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(AppDimens.Dimens16)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.padding(AppDimens.Dimens20),
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
                        ) {
                            BenefitRow(text = stringResource(R.string.benefit_all_levels))
                            BenefitRow(text = stringResource(R.string.benefit_all_exams))
                            BenefitRow(text = stringResource(R.string.benefit_all_exercises))
                            BenefitRow(text = stringResource(R.string.benefit_ccm))
                            BenefitRow(text = stringResource(R.string.benefit_all_games))
                            BenefitRow(text = stringResource(R.string.benefit_report_history))
                        }
                    }
                }
            }

            // Login sheet overlay — paywall gate was already passed, skip gate inside login
            if (showLoginSheet) {
                FreemiumLoginBottomSheet(
                    showContinueWithoutSaving = false,
                    skipParentalGate = true,
                    onLoginSuccess = { handleAfterLogin() },
                    onDismiss = { showLoginSheet = false }
                )
            }

            // Parental gate — always shown first, every time the paywall opens
            if (!parentalGatePassed) {
                ParentalGateDialog(
                    onPassed = { parentalGatePassed = true },
                    onCancelled = { onDismiss() }
                )
            }
        }
    ) {
        // ── Main landscape layout ──────────────────────────────────
        Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Left: branding ─────────────────────────────────────
                Column(
                    modifier = Modifier.weight(0.32f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(AppDimens.Dimens56)
                            .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = Color(0xFFC77601), spotColor = Color(0xFFC77601))
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFFB8C00)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(AppDimens.Dimens6))

                    Box {
                        Text(
                            text = stringResource(paywallContext.headlineRes),
                            color = Color.Black.copy(alpha = 0.2f),
                            style = MaterialTheme.typography.titleLarge.scaled(),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.offset(2.dp, 2.dp)
                        )
                        Text(
                            text = stringResource(paywallContext.headlineRes),
                            color = Color(0xFFE65100),
                            style = MaterialTheme.typography.titleLarge.scaled(),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimens.Dimens6))

                    Text(
                        text = stringResource(paywallContext.subtitleRes),
                        color = Color(0xFF666666),
                        style = MaterialTheme.typography.bodySmall.scaled(),
                        textAlign = TextAlign.Center
                    )

                    if (uiState.socialProofText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(AppDimens.Dimens6))
                        Text(
                            text = uiState.socialProofText,
                            color = Color(0xFFE65100),
                            style = MaterialTheme.typography.labelSmall.scaled(),
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimens.Dimens10))

                    // See Benefits button -- filled purple gradient pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100f))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF9374EF), Color(0xFF6446CC))))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { showBenefits = true }
                            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.see_benefits),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.scaled(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Vertical divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(120.dp)
                        .background(Color(0xFFEEEEEE))
                )

                // ── Right: plan cards ───────────────────────────────────
                Column(
                    modifier = Modifier.weight(0.68f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = ColorAccent,
                            modifier = Modifier.size(AppDimens.Dimens40)
                        )
                    } else {
                        uiState.sortedPlanList.forEachIndexed { index, plan ->
                            val isYearly = plan.sku.contains("1year")
                            val isWeekly = plan.sku.contains("week")
                            val isLifetime = plan.sku.contains("all")
                            val emoji = when {
                                isWeekly -> "📆"
                                isLifetime -> "⭐"
                                isYearly -> "🏆"
                                else -> "📅"
                            }
                            val badge = when {
                                isYearly -> stringResource(R.string.paywall_badge_best_value)
                                isLifetime -> stringResource(R.string.paywall_badge_one_time)
                                else -> null
                            }
                            PaywallPlanCard(
                                emoji = emoji,
                                title = plan.getDurationTxt(),
                                plan = plan,
                                isHighlighted = isYearly,
                                badge = badge,
                                uiState = uiState,
                                onSubscribe = {
                                    if (!viewModel.isUserLoggedIn()) {
                                        pendingPlanIndex = index
                                        showLoginSheet = true
                                    } else if (viewModel.isUserSubscribed()) {
                                        onDismiss(); onSubscriptionActivated()
                                    } else {
                                        viewModel.onPlanSelected(index)
                                        viewModel.makePurchase(activity)
                                    }
                                }
                            )
                        }
                    }
                }
            }
    }
}

@Composable
private fun BenefitRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
    ) {
        Box(
            modifier = Modifier
                .size(AppDimens.Dimens16)
                .clip(CircleShape)
                .background(Color(0xFF2E7D32).copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.labelSmall.scaled(),
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.scaled(),
            fontWeight = FontWeight.Medium,
            color = Color(0xFF5D4037)
        )
    }
}

@Composable
private fun PaywallPlanCard(
    emoji: String,
    title: String,
    plan: com.jigar.me.ui.view.home.screens.purchase.viewmodels.RcPlanItem,
    isHighlighted: Boolean,
    badge: String?,
    uiState: PurchaseUiState,
    onSubscribe: () -> Unit,
) {
    val borderColor = if (isHighlighted) Color(0xFFFF8400) else Color(0xFFDDDDDD)
    val bgColor = if (isHighlighted) Color(0xFFFFF8F0) else Color(0xFFFAFAFA)

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppDimens.Dimens16))
                .background(bgColor)
                .border(
                    width = if (isHighlighted) 2.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(AppDimens.Dimens16)
                )
                .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens10),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
        ) {
            Text(text = emoji, style = MaterialTheme.typography.headlineLarge.scaled())

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isHighlighted) Color(0xFFE65100) else Color(0xFF333333),
                    style = MaterialTheme.typography.titleSmall.scaled(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold))
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriceUi(
                        discountPer = uiState.discountPer,
                        plan = plan,
                        originalYearly = uiState.original1YearData,
                        discountPerLifetime = uiState.discountPerLifetime,
                        originalLifetime = uiState.originalLifetimeData
                    )
                }
                if (plan.isYearly()) {
                    val yearlyMicros = uiState.original1YearData?.price_amount_micros ?: plan.price_amount_micros
                    val monthlyPct = yearlySavingsPercent(uiState.original1MonthData?.price_amount_micros, 12, yearlyMicros)
                    val weeklyPct = if (monthlyPct == null)
                        yearlySavingsPercent(uiState.original1WeekData?.price_amount_micros, 52, yearlyMicros) else null
                    val savingsText = when {
                        monthlyPct != null && monthlyPct > 0 -> stringResource(R.string.paywall_save_vs_monthly, monthlyPct)
                        weeklyPct != null && weeklyPct > 0 -> stringResource(R.string.paywall_save_vs_weekly, weeklyPct)
                        else -> null
                    }
                    if (savingsText != null) {
                        Text(
                            text = savingsText,
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.labelSmall.scaled(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (plan.isLifeTimeOffer() && plan.sku.contains("offer") && uiState.discountPerLifetime > 0) {
                    Text(
                        text = stringResource(R.string.paywall_percent_off, uiState.discountPerLifetime),
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.labelSmall.scaled(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            KidsActionButton(
                text = stringResource(R.string.paywall_subscribe),
                type = if (isHighlighted) ButtonType.ORANGE else ButtonType.BLUE,
                onClick = onSubscribe,
                isSmall = true
            )
        }

        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = (-10).dp)
                    .clip(RoundedCornerShape(100f))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF22A927), Color(0xFF049D06)))
                    )
                    .padding(horizontal = AppDimens.Dimens8, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.scaled(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold))
                )
            }
        }
    }
}