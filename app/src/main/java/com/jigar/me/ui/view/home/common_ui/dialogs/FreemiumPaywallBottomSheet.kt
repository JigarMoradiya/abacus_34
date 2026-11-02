package com.jigar.me.ui.view.home.common_ui.dialogs

import com.jigar.me.utils.AppConstants
// Removed: the store provides the discount (no timed Home offer to start on dismiss)
// import com.jigar.me.utils.HomeOfferManager
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.FreemiumWindow
import androidx.compose.ui.platform.LocalUriHandler
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
import com.jigar.me.ui.view.ab.AbBeadsLoader
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
// import com.jigar.me.ui.view.home.common_ui.InlineCountdownPill
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.purchase.components.getDurationTxt
import com.jigar.me.ui.view.home.screens.purchase.components.isYearly
import com.jigar.me.ui.view.home.screens.purchase.components.yearlySavingsPercent
import com.jigar.me.ui.view.home.common_ui.sheets.KidsBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.ParentalGateDialog
import com.jigar.me.ui.view.home.screens.purchase.components.PriceUi
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseViewModel
import com.jigar.me.ui.view.ab.LocalAbThemed
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppAnalytics
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun FreemiumPaywallBottomSheet(
    paywallContext: PaywallContext = PaywallContext.GENERIC,
    onSubscriptionActivated: () -> Unit = {},
    /** The first-launch page's own left column: when set, the plans are drawn on that page instead of in a bottom sheet. */
    embeddedLeft: (@Composable () -> Unit)? = null,
    embeddedFooter: (@Composable () -> Unit)? = null,
    /** The grown-up question was already answered just before this sheet opened (the day 3 / day 7 reminder). */
    gateAlreadyPassed: Boolean = false,
    onDismiss: () -> Unit,
) {
    val viewModel: PurchaseViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastContext = LocalContext.current
    LaunchedEffect(uiState.message) {
        uiState.message?.let { android.widget.Toast.makeText(toastContext, toastContext.getString(it), android.widget.Toast.LENGTH_LONG).show(); viewModel.clearMessage() }
    }
    val activity = LocalContext.current as Activity
    val prefs = com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper.current
    // Lifetime shows from the paywall sheet number `lifetime_min_paywalls` + 1 (0 = from the first, as before); the embedded
    // first-launch page does not count as a sheet
    val lifetimeHidden = remember {
        if (embeddedLeft != null) false
        else {
            val opened = prefs.getCustomParamInt(AppConstants.Freemium.paywallOpenCount, 0)
            prefs.setCustomParamInt(AppConstants.Freemium.paywallOpenCount, opened + 1)
            opened < prefs.getCustomParamInt(AppConstants.RemoteConfig.lifetimeMinPaywalls, 0)
        }
    }
    // Which price variant this user sees (RevenueCat Experiments swap the current offering)
    LaunchedEffect(uiState.offeringId) {
        val yearly = uiState.sortedPlanList.firstOrNull { it.sku.contains("1year") && !it.sku.contains("offer") }
        FreemiumWindow.notePriceVariant(
            uiState.offeringId, yearly?.sku,
            yearly?.price_amount_micros?.let { it / 1_000_000.0 }, yearly?.rcPackage?.product?.price?.currencyCode,
        )
    }

    val ctx = paywallContext.analyticsName
    // Once the free week is over every locked tap says so (the item's own headline otherwise)
    val headlineContext = if (FreemiumWindow.freeTierExpired(viewModel.isUserSubscribed()) &&
        paywallContext != PaywallContext.ONBOARDING && paywallContext != PaywallContext.FREE_ENDING
    ) PaywallContext.FREE_ENDED else paywallContext
    LaunchedEffect(Unit) {
        viewModel.loadData()
        FreemiumWindow.notePaywallShown(prefs)
        AppAnalytics.log(AppAnalytics.PAYWALL_VIEWED, "surface" to (if (embeddedLeft != null) "page" else "sheet"), "context" to ctx, "window" to FreemiumWindow.phase.name.lowercase(), "logged_in" to viewModel.isUserLoggedIn())
    }

    // Purchase succeeded — dismiss and notify caller so background screen removes locks
    LaunchedEffect(uiState.purchaseSuccess) {
        if (uiState.purchaseSuccess) {
            onDismiss()
            onSubscriptionActivated()
        }
    }

    // Was `uiState.sortedPlanList.isEmpty()` -- that never turns false again once the
    // offerings fetch fails (no connectivity, no signed-in store account, etc.), so a
    // real failure showed an infinite spinner forever instead of an error. isLoading
    // now reflects whether the fetch actually finished.
    val isLoading = uiState.isLoading
    var showBenefits by remember { mutableStateOf(false) }
    var showLoginSheet by remember { mutableStateOf(false) }
    var pendingPlanIndex by remember { mutableStateOf<Int?>(null) }
    var parentalGatePassed by remember { mutableStateOf(gateAlreadyPassed) }
    var showGate by remember { mutableStateOf(false) }               // the grown-up gate is asked when a parent TAPS a plan or link (price is visible first)
    var pendingGateAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val uriHandler = LocalUriHandler.current
    fun requireGate(action: () -> Unit) {
        if (parentalGatePassed) { action(); return }
        pendingGateAction = action
        showGate = true
        AppAnalytics.log(AppAnalytics.GATE_SHOWN, "surface" to "sheet", "context" to ctx)
    }
    DisposableEffect(Unit) {
        onDispose {
            // Removed: the store provides the discount (a one-time home_offer used to start here when a parent who saw the prices closed the sheet)
            // if (parentalGatePassed && !CommonUtils.checkPurchaseForExerciseExamCCM(prefs)) HomeOfferManager.startOnDismissIfNeeded(prefs)
            AppAnalytics.log(AppAnalytics.PAYWALL_DISMISSED, "surface" to "sheet", "context" to ctx, "gate_passed" to parentalGatePassed) }
    }
    fun planSku(index: Int) = uiState.sortedPlanList.getOrNull(index)?.sku ?: ""

    // After login — re-check subscription fresh (RC + admin plans) then proceed or purchase
    fun handleAfterLogin() {
        AppAnalytics.log(AppAnalytics.LOGIN_COMPLETED, "surface" to "sheet", "context" to ctx)
        showLoginSheet = false
        if (viewModel.isUserSubscribed()) {
            onDismiss(); onSubscriptionActivated()
        } else {
            pendingPlanIndex?.let { index ->
                pendingPlanIndex = null
                viewModel.onPlanSelected(index)
                viewModel.makePurchase(activity, "sheet", ctx)
            }
        }
    }

    // the login sheet and the grown-up gate: over the bottom sheet, or over the first-launch page
    val loginAndGate: @Composable () -> Unit = {
    // Login sheet overlay — paywall gate was already passed, skip gate inside login
    if (showLoginSheet) {
        FreemiumLoginBottomSheet(
            showContinueWithoutSaving = false,
            skipParentalGate = true,
            onLoginSuccess = { handleAfterLogin() },
            onDismiss = { showLoginSheet = false }
        )
    }

    // Parental gate — asked once per opening, when a parent taps a plan or the privacy link
    if (showGate) {
        ParentalGateDialog(
            onPassed = {
                parentalGatePassed = true
                showGate = false
                AppAnalytics.log(AppAnalytics.GATE_PASSED, "surface" to "sheet", "context" to ctx)
                val action = pendingGateAction
                pendingGateAction = null
                action?.invoke()
            },
            onCancelled = { showGate = false; pendingGateAction = null }
        )
    }
    }

    // a tap on a plan: gate first, then login, then the store
    val subscribeTapped: (Int) -> Unit = { index ->
        AppAnalytics.log(AppAnalytics.PLAN_SELECTED, "surface" to "sheet", "context" to ctx, "product_id" to planSku(index))
        requireGate {
            if (!viewModel.isUserLoggedIn()) {
                pendingPlanIndex = index
                showLoginSheet = true
                AppAnalytics.log(AppAnalytics.LOGIN_PROMPTED, "surface" to "sheet", "context" to ctx, "product_id" to planSku(index))
            } else if (viewModel.isUserSubscribed()) {
                onDismiss(); onSubscriptionActivated()
            } else {
                viewModel.onPlanSelected(index)
                viewModel.makePurchase(activity, "sheet", ctx)
            }
        }
    }

    val themed = LocalAbThemed.current
    if (embeddedLeft != null && themed) {
        Box(Modifier.fillMaxSize()) {
            PaywallKitEmbeddedLayout(left = embeddedLeft, footer = embeddedFooter) {
                PaywallKitPlans(
                    uiState = uiState,
                    onRetry = { viewModel.loadData() },
                    onSubscribe = subscribeTapped,
                    privacyUrl = viewModel.privacyPolicyUrl(),
                    onOpenLink = { url -> requireGate { uriHandler.openUri(url) } },
                    firstLaunch = true,
                )
            }
            loginAndGate()
        }
        return
    }
    KidsBottomSheet(
        visible = true,
        onDismiss = onDismiss,
        widthFraction = if (themed) 0.85f else 0.75f,
        showHandle = !themed,
        overlay = {
            if (showBenefits && themed) {
                PaywallKitBenefits(onClose = { showBenefits = false })
            } else if (showBenefits) {
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

            loginAndGate()
        }
    ) {
      if (themed) {
        PaywallKitContent(
            headline = stringResource(headlineContext.headlineRes),
            subtitle = stringResource(headlineContext.subtitleRes),
            socialProof = uiState.socialProofText,
            uiState = uiState,
            onClose = onDismiss,
            onSeeBenefits = { showBenefits = true },
            onRetry = { viewModel.loadData() },
            onSubscribe = subscribeTapped,
            privacyUrl = viewModel.privacyPolicyUrl(),
            onOpenLink = { url -> requireGate { uriHandler.openUri(url) } },
            hideLifetime = lifetimeHidden,
        )
      } else {
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
                            text = stringResource(headlineContext.headlineRes),
                            color = Color.Black.copy(alpha = 0.2f),
                            style = MaterialTheme.typography.titleLarge.scaled(),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.offset(2.dp, 2.dp)
                        )
                        Text(
                            text = stringResource(headlineContext.headlineRes),
                            color = Color(0xFFE65100),
                            style = MaterialTheme.typography.titleLarge.scaled(),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimens.Dimens6))

                    Text(
                        text = stringResource(headlineContext.subtitleRes),
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
                        AbBeadsLoader()
                    } else if (uiState.sortedPlanList.isEmpty()) {
                        Text(
                            text = stringResource(uiState.error ?: R.string.something_went_wrong),
                            style = MaterialTheme.typography.bodyMedium.scaled(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = AppDimens.Dimens12)
                        )
                        KidsActionButton(
                            text = stringResource(R.string.retry),
                            type = ButtonType.BLUE,
                            isSmall = true,
                            onClick = { viewModel.loadData() }
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
                                    AppAnalytics.log(AppAnalytics.PLAN_SELECTED, "surface" to "sheet", "context" to ctx, "product_id" to planSku(index))
                                    requireGate {
                                        if (!viewModel.isUserLoggedIn()) {
                                            pendingPlanIndex = index
                                            showLoginSheet = true
                                            AppAnalytics.log(AppAnalytics.LOGIN_PROMPTED, "surface" to "sheet", "context" to ctx, "product_id" to planSku(index))
                                        } else if (viewModel.isUserSubscribed()) {
                                            onDismiss(); onSubscriptionActivated()
                                        } else {
                                            viewModel.onPlanSelected(index)
                                            viewModel.makePurchase(activity, "sheet", ctx)
                                        }
                                    }
                                },
                            )
                        }
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
                    PriceUi(plan)
                }
                // a paid intro price the store offers: "first year, then ₹1,200 / year" and the green N% OFF
                val intro = plan.storeIntroOffer()
                if (intro != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
                        Text(
                            text = intro.thenLine(plan),
                            color = Color.Black,
                            style = MaterialTheme.typography.labelSmall.scaled(),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Text(
                            text = stringResource(R.string.paywall_percent_off_store, intro.percentOff),
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.labelSmall.scaled(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Removed: the store provides the discount ("(extra ~N% OFF)" / "~N% OFF" rows and the countdown pill)
                // if (plan.isYearly()) {
                //     // "save ~N% vs monthly" was removed from the plan card earlier (too much data): paywallSavingsText(plan, uiState)
                //     if (uiState.discountPer > 0) {
                //         Text(text = stringResource(R.string.paywall_percent_off_extra, uiState.discountPer), ...)
                //         // uiState.homeOfferYearlyEndMillis?.let { endMillis -> InlineCountdownPill(endMillis, onOfferExpired) }
                //     }
                // } else if (plan.isLifeTimeOffer() && plan.sku.contains("offer") && uiState.discountPerLifetime > 0) {
                //     Text(text = stringResource(R.string.paywall_percent_off, uiState.discountPerLifetime), ...)
                //     // uiState.homeOfferLifetimeEndMillis?.let { endMillis -> InlineCountdownPill(endMillis, onOfferExpired) }
                // }
            }

            KidsActionButton(
                text = if (plan.isLifeTimeOffer()) stringResource(R.string.txt_purchase_Now) else stringResource(R.string.paywall_subscribe),
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
/** "Save N% vs monthly / weekly" for the yearly plan, computed against the original (non-offer) yearly price. */
@Composable
internal fun paywallSavingsText(plan: com.jigar.me.ui.view.home.screens.purchase.viewmodels.RcPlanItem, uiState: PurchaseUiState): String? {
    if (!plan.isYearly()) return null
    val yearlyMicros = uiState.original1YearData?.price_amount_micros ?: plan.price_amount_micros
    val monthlyPct = yearlySavingsPercent(uiState.original1MonthData?.price_amount_micros, 12, yearlyMicros)
    val weeklyPct = if (monthlyPct == null)
        yearlySavingsPercent(uiState.original1WeekData?.price_amount_micros, 52, yearlyMicros) else null
    return when {
        monthlyPct != null && monthlyPct > 0 -> stringResource(R.string.paywall_save_vs_monthly, monthlyPct)
        weeklyPct != null && weeklyPct > 0 -> stringResource(R.string.paywall_save_vs_weekly, weeklyPct)
        else -> null
    }
}
