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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import com.jigar.me.ui.view.home.common_ui.sheets.KidsBottomSheet
import com.jigar.me.ui.view.home.screens.purchase.components.PriceUi
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun FreemiumPaywallBottomSheet(
    onSubscriptionActivated: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    val viewModel: PurchaseViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val isLoading = uiState.sortedPlanList.isEmpty()
    var showBenefits by remember { mutableStateOf(false) }
    var showLoginSheet by remember { mutableStateOf(false) }
    var pendingPlanIndex by remember { mutableStateOf<Int?>(null) }

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
                            .padding(AppDimens.Dimens20),
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.premium_benefits_title),
                                style = MaterialTheme.typography.titleSmall.scaled(),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.font_bold)),
                                color = Color(0xFF0074D5),
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .size(AppDimens.Dimens24)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0F0F0))
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { showBenefits = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFF666666),
                                    modifier = Modifier.size(AppDimens.Dimens16)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(AppDimens.Dimens4))

                        BenefitRow(text = stringResource(R.string.benefit_all_levels))
                        BenefitRow(text = stringResource(R.string.benefit_all_exams))
                        BenefitRow(text = stringResource(R.string.benefit_all_exercises))
                        BenefitRow(text = stringResource(R.string.benefit_ccm))
                        BenefitRow(text = stringResource(R.string.benefit_all_games))
                        BenefitRow(text = stringResource(R.string.benefit_report_history))
                    }
                }
            }

            // Login sheet overlay — covers the whole sheet when login is required
            if (showLoginSheet) {
                FreemiumLoginBottomSheet(
                    showContinueWithoutSaving = false,
                    onLoginSuccess = { handleAfterLogin() },
                    onDismiss = { showLoginSheet = false }
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
                            .clip(CircleShape)
                            .background(Color(0xFFFFF8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(AppDimens.Dimens6))

                    Box {
                        Text(
                            text = "Go Premium!",
                            color = Color.Black.copy(alpha = 0.2f),
                            style = MaterialTheme.typography.titleLarge.scaled(),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.offset(2.dp, 2.dp)
                        )
                        Text(
                            text = "Go Premium!",
                            color = Color(0xFFE65100),
                            style = MaterialTheme.typography.titleLarge.scaled(),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimens.Dimens6))

                    Text(
                        text = "Unlock ALL levels\nNo limits\nLearn without limits!",
                        color = Color(0xFF666666),
                        style = MaterialTheme.typography.bodySmall.scaled(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(AppDimens.Dimens10))

                    // See Benefits button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100f))
                            .border(
                                width = 1.dp,
                                color = Color(0xFF0074D5).copy(alpha = 0.6f),
                                shape = RoundedCornerShape(100f)
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { showBenefits = true }
                            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.see_benefits),
                            color = Color(0xFF0074D5),
                            style = MaterialTheme.typography.labelSmall.scaled(),
                            fontWeight = FontWeight.Medium
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
                                isYearly -> "Best Value!"
                                isLifetime -> "One Time"
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
            }

            KidsActionButton(
                text = "Subscribe",
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