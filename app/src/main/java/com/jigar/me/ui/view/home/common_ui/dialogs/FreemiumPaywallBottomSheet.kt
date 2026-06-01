package com.jigar.me.ui.view.home.common_ui.dialogs

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorAccent
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.sheets.KidsBottomSheet
import com.jigar.me.ui.view.home.screens.purchase.components.PriceUi
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun FreemiumPaywallBottomSheet(
    purchasedSKU: List<InAppSkuDetails>,
    onDismiss: () -> Unit,
) {
    val viewModel: PurchaseViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity

    LaunchedEffect(purchasedSKU) {
        viewModel.loadInitialData(purchasedSKU)
    }

    val isLoading = uiState.sortedSkuList.isEmpty()

    KidsBottomSheet(
        visible = true,
        onDismiss = onDismiss
    ) {
        // Main landscape layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: branding
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
            }

            // Vertical divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(120.dp)
                    .background(Color(0xFFEEEEEE))
            )

            // Right: plan cards
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
                    uiState.sortedSkuList.forEachIndexed { index, plan ->
                        val isYearly = plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Year)
                        val isWeekly = plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_Week)
                        val isLifetime = plan.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_All)
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
                                viewModel.onPlanSelected(index)
                                viewModel.makePurchase(activity)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaywallPlanCard(
    emoji: String,
    title: String,
    plan: InAppSkuDetails,
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
