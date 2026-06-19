package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8

@Composable
fun TableMixPickerScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onMixSelected: (List<Int>) -> Unit,
    onBackClick: () -> Unit,
) {
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    var showPaywall by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            BackButtonWithText(title = "🎲 Mix Table Practice", onBackClick = onBackClick)

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Dimens16, vertical = Dimens8),
                horizontalArrangement = Arrangement.spacedBy(Dimens16)
            ) {
                // LEFT — FREE card (35%)
                FreeMixCard(
                    group = MIX_GROUPS.first(),
                    modifier = Modifier
                        .weight(0.35f)
                        .fillMaxHeight(),
                    onClick = {
                        AudioPlayerManager.playSoundBtnClick()
                        onMixSelected(MIX_GROUPS.first().tables)
                    }
                )

                // RIGHT — 3×2 premium grid (65%), fills full height like iOS
                Column(
                    modifier = Modifier
                        .weight(0.65f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(Dimens12)
                ) {
                    val premiumGroups = MIX_GROUPS.drop(1)
                    // Row 1: ×1–5, ×6–10, ×1–10
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens12)
                    ) {
                        premiumGroups.take(3).forEach { group ->
                            PremiumMixCard(
                                group = group,
                                isLocked = !isSubscribed,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                onClick = {
                                    AudioPlayerManager.playSoundBtnClick()
                                    if (isSubscribed) onMixSelected(group.tables)
                                    else showPaywall = true
                                }
                            )
                        }
                    }
                    // Row 2: ×11–15, ×15–20, ×1–20
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens12)
                    ) {
                        premiumGroups.drop(3).forEach { group ->
                            PremiumMixCard(
                                group = group,
                                isLocked = !isSubscribed,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                onClick = {
                                    AudioPlayerManager.playSoundBtnClick()
                                    if (isSubscribed) onMixSelected(group.tables)
                                    else showPaywall = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPaywall) {
        FreemiumPaywallBottomSheet(
            onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
            onDismiss = { showPaywall = false }
        )
    }
}

@Composable
private fun FreeMixCard(
    group: MixGroup,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isTablet = DeviceInfo.isTablet
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens24),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(group.startColor, group.endColor)))
                .padding(Dimens16)
        ) {
            // FREE FOREVER badge top-right
            Surface(
                color = Color(0xFFFFEB3B),
                shape = RoundedCornerShape(Dimens8),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = "FREE FOREVER",
                    style = MaterialTheme.typography.labelLarge.scaled(),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.padding(horizontal = Dimens8, vertical = Dimens4)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = group.emoji,
                    style = if (isTablet) MaterialTheme.typography.displayLarge.scaled()
                            else MaterialTheme.typography.displayMedium.scaled()
                )
                Spacer(Modifier.height(Dimens12))
                Text(
                    text = group.label,
                    style = if (isTablet) MaterialTheme.typography.displaySmall.scaled()
                            else MaterialTheme.typography.headlineLarge.scaled(),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Dimens4))
                Text(
                    text = "${group.tables.size} tables mixed together",
                    style = if (isTablet) MaterialTheme.typography.titleMedium.scaled()
                            else MaterialTheme.typography.bodyLarge.scaled(),
                    color = Color.White.copy(alpha = 0.88f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Dimens16))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    group.tables.forEach { n ->
                        Surface(
                            color = Color.White.copy(alpha = 0.25f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "×$n",
                                style = MaterialTheme.typography.titleSmall.scaled(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = Dimens8, vertical = Dimens6)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumMixCard(
    group: MixGroup,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isTablet = DeviceInfo.isTablet
    val alpha = if (isLocked) 0.65f else 1f
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens20),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            group.startColor.copy(alpha = alpha),
                            group.endColor.copy(alpha = alpha)
                        )
                    )
                )
                .padding(Dimens12)
        ) {
            if (isLocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(Dimens24)
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(AppDimens.Dimens14)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = group.emoji,
                    style = if (isTablet) MaterialTheme.typography.headlineLarge.scaled()
                            else MaterialTheme.typography.headlineMedium.scaled()
                )
                Spacer(Modifier.height(Dimens4))
                Text(
                    text = group.label,
                    style = if (isTablet) MaterialTheme.typography.headlineSmall.scaled()
                            else MaterialTheme.typography.titleLarge.scaled(),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = if (isLocked) 0.75f else 1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${group.tables.size} tables",
                    style = if (isTablet) MaterialTheme.typography.bodyLarge.scaled()
                            else MaterialTheme.typography.bodyMedium.scaled(),
                    color = Color.White.copy(alpha = if (isLocked) 0.55f else 0.85f)
                )
            }
        }
    }
}
