package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import androidx.compose.material.icons.filled.ChevronRight
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import androidx.compose.ui.text.style.TextAlign

private val tablePickerColors = listOf(
    Color(0xFFE53935), Color(0xFFF57C00), Color(0xFFF9A825), Color(0xFF43A047), Color(0xFF00ACC1),
    Color(0xFF00897B), Color(0xFF1976D2), Color(0xFF7B1FA2), Color(0xFFC2185B), Color(0xFF5E35B1),
    Color(0xFFE64A19), Color(0xFF00695C), Color(0xFF1565C0), Color(0xFF2E7D32), Color(0xFF0288D1),
    Color(0xFFAD1457), Color(0xFF6A1B9A), Color(0xFF558B2F), Color(0xFF4E342E), Color(0xFF37474F),
)

@Composable
fun TablePickerScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onTableSelected: (Int) -> Unit,
    onMixPractice: () -> Unit = {},
    onBackClick: () -> Unit,
) {
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    var showPaywall  by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

            BackButtonWithText(title = "Choose a table to practise", onBackClick = onBackClick)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // LEFT 70% — table grid
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(0.70f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    val cols    = 5
                    val rows    = 4
                    val hPad    = AppDimens.Dimens16
                    val spacing = AppDimens.Dimens12

                    val cellWfW = (maxWidth - hPad * 2 - spacing * (cols - 1)) / cols
                    val cellHfH = (maxHeight - spacing * (rows - 1) - AppDimens.Dimens12) / rows
                    val cellWfH = cellHfH * 1.5f
                    val cellW   = min(cellWfW, cellWfH)
                    val cellH   = cellW / 1.5f
                    val gridW   = cellW * cols + spacing * (cols - 1)

                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(cols),
                            horizontalArrangement = Arrangement.spacedBy(spacing),
                            verticalArrangement = Arrangement.spacedBy(spacing),
                            userScrollEnabled = false,
                            contentPadding = PaddingValues(bottom = AppDimens.Dimens12),
                            modifier = Modifier
                                .width(gridW)
                                .height(cellH * rows + spacing * (rows - 1) + AppDimens.Dimens12)
                        ) {
                            items((1..20).toList()) { n ->
                                val color = tablePickerColors[(n - 1) % tablePickerColors.size]
                                TablePickerButton(
                                    n = n, color = color,
                                    cellW = cellW, cellH = cellH,
                                    isLocked = n > 3 && !isSubscribed,
                                    onClick = { AudioPlayerManager.playSoundBtnClick(); if (isSubscribed || n <= 3) onTableSelected(n) else showPaywall = true }
                                )
                            }
                        }
                    }
                }

                // RIGHT 30% — Mix Practice card
                MixPracticeCard(
                    modifier = Modifier
                        .weight(0.30f)
                        .fillMaxHeight()
                        .padding(end = Dimens16, top = Dimens8, bottom = Dimens8),
                    onClick = { AudioPlayerManager.playSoundBtnClick(); onMixPractice() }
                )
            }
        }
    }

    if (showPaywall) {
        FreemiumPaywallBottomSheet(
            paywallContext = PaywallContext.TABLES,
            onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
            onDismiss = { showPaywall = false }
        )
    }
}

@Composable
private fun MixPracticeCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val isTablet = DeviceInfo.isTablet
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens16),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1565C0), Color(0xFF0288D1), Color(0xFF00ACC1))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens8),
                modifier = Modifier.padding(Dimens12)
            ) {
                Text(
                    text = "🎲",
                    style = if (isTablet) MaterialTheme.typography.displayLarge.scaled()
                            else MaterialTheme.typography.displayMedium.scaled()
                )
                Text(
                    text = "Mix\nTable\nPractice",
                    style = if (isTablet) MaterialTheme.typography.titleLarge.scaled()
                            else MaterialTheme.typography.titleMedium.scaled(),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Practise multiple tables together!",
                    style = MaterialTheme.typography.labelSmall.scaled(),
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Dimens4))
                Row(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(AppDimens.Dimens24))
                        .padding(horizontal = Dimens12, vertical = Dimens8),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens4)
                ) {
                    Text(
                        text = "Start",
                        style = if (isTablet) MaterialTheme.typography.titleMedium.scaled()
                                else MaterialTheme.typography.titleSmall.scaled(),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(AppDimens.Dimens20)
                    )
                }
            }
        }
    }
}

@Composable
private fun TablePickerButton(
    n: Int,
    color: Color,
    cellW: androidx.compose.ui.unit.Dp,
    cellH: androidx.compose.ui.unit.Dp,
    isLocked: Boolean = false,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(Dimens16)
    Card(
        onClick = onClick,
        shape = shape,
        elevation = CardDefaults.cardElevation(AppDimens.Dimens4),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .size(width = cellW, height = cellH)
                .background(Brush.linearGradient(listOf(color, color.copy(alpha = 0.78f)))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "×$n",
                style = MaterialTheme.typography.displaySmall.scaled(),
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            if (isLocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(AppDimens.Dimens4)
                        .size(AppDimens.Dimens20)
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(AppDimens.Dimens12)
                    )
                }
            }
        }
    }
}
