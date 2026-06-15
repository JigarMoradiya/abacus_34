package com.jigar.me.ui.view.home.screens.levels.level2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun Level2HomeScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
    onNavigateToChapter: (Int) -> Unit,
) {
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    var showPaywall  by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            BackButtonWithText(title = "Add & Subtract", onBackClick = onBackClick)

            BoxWithConstraints(
                modifier         = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val cols       = 5
                val rows       = 2
                val spacing    = AppDimens.Dimens10
                val hPad       = AppDimens.Dimens16
                val vPad       = AppDimens.Dimens10
                val shadowRoom = AppDimens.Dimens8

                val cellW = (maxWidth - hPad * 2 - spacing * (cols - 1)) / cols
                val cellH = (maxHeight - vPad * 2 - spacing * (rows - 1) - shadowRoom) / rows

                Column(
                    modifier            = Modifier.width(maxWidth - hPad * 2).padding(vertical = vPad),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        level2Chapters.take(cols).forEachIndexed { index, chapter ->
                            val isLocked = !isSubscribed && index >= 3
                            L2ChapterCard(chapter, cellW, cellH, isLocked) {
                                if (isLocked) showPaywall = true else onNavigateToChapter(chapter.id)
                            }
                        }
                    }
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        level2Chapters.drop(cols).forEach { chapter ->
                            L2ChapterCard(chapter, cellW, cellH, isLocked = !isSubscribed) {
                                if (!isSubscribed) showPaywall = true else onNavigateToChapter(chapter.id)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(shadowRoom))
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
private fun L2ChapterCard(
    chapter: Level2ChapterData,
    cellW: Dp,
    cellH: Dp,
    isLocked: Boolean = false,
    onClick: () -> Unit,
) {
    val isFormulaRef = chapter.type == Level2ChapterType.FORMULA_REF
    val shape = RoundedCornerShape(AppDimens.Dimens20)

    Box(
        modifier = Modifier
            .size(width = cellW, height = cellH)
            .shadow(AppDimens.Dimens6, shape,
                ambientColor = chapter.endColor.copy(alpha = 0.4f),
                spotColor    = chapter.endColor.copy(alpha = 0.4f))
            .background(
                Brush.linearGradient(listOf(chapter.startColor, chapter.endColor)),
                shape
            )
            .then(
                if (isFormulaRef)
                    Modifier.border(AppDimens.Dimens2, Color.White.copy(0.50f), shape)
                else
                    Modifier
            )
            .clickable(remember { MutableInteractionSource() }, null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier.padding(AppDimens.Dimens8)
        ) {
            Text(
                text  = chapter.emoji,
                style = if (DeviceInfo.isTablet) MaterialTheme.typography.displayLarge.scaled() else MaterialTheme.typography.displaySmall.scaled()
            )
            Spacer(Modifier.height(if (DeviceInfo.isTablet) AppDimens.Dimens8 else AppDimens.Dimens4))

            val label = when (chapter.type) {
                Level2ChapterType.FORMULA_REF -> "Reference"
                else                          -> "Lesson ${chapter.id}"
            }
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(AppDimens.Dimens100))
                    .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens3)
            ) {
                Text(
                    text       = label,
                    style      = MaterialTheme.typography.labelSmall.scaled(),
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(AppDimens.Dimens4))
            Text(
                text       = chapter.title,
                style      = MaterialTheme.typography.labelLarge.scaled(),
                color      = Color.White,
                fontWeight = FontWeight.Black,
                textAlign  = TextAlign.Center,
                maxLines   = 2
            )
            if (chapter.columns == 2) {
                Spacer(Modifier.height(AppDimens.Dimens2))
                Text(
                    text  = "2 columns",
                    style = MaterialTheme.typography.labelSmall.scaled(),
                    color = Color.White.copy(0.70f)
                )
            }
        }

        if (isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppDimens.Dimens6)
                    .size(AppDimens.Dimens24)
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
    }
}
