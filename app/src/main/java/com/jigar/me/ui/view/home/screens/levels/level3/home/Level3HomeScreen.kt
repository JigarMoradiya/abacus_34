package com.jigar.me.ui.view.home.screens.levels.level3.home

import com.jigar.me.ui.view.home.screens.levels.level3.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun Level3HomeScreen(
    onBackClick:       () -> Unit,
    onNavigateToMode:  (L3Mode) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            BackButtonWithText(title = "Speed & Anzan", onBackClick = onBackClick)

            BoxWithConstraints(
                modifier         = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val spacing = AppDimens.Dimens10
                val hPad    = AppDimens.Dimens16
                val vPad    = AppDimens.Dimens10

                // Layout: top row 3 cards, bottom row 2 centered
                val topModes    = L3Mode.entries.take(3)
                val bottomModes = L3Mode.entries.drop(3)

                val cardH = (maxHeight - vPad * 2 - spacing - AppDimens.Dimens8) / 2
                val topW  = (maxWidth - hPad * 2 - spacing * 2) / 3
                val botW  = (maxWidth - hPad * 2 - spacing) / 2

                Column(
                    modifier            = Modifier.fillMaxSize().padding(horizontal = hPad, vertical = vPad),
                    verticalArrangement = Arrangement.spacedBy(spacing),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                    ) {
                        topModes.forEach { mode ->
                            L3ModeCard(mode, topW, cardH) { onNavigateToMode(mode) }
                        }
                    }
                    Row(
                        modifier              = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                    ) {
                        bottomModes.forEach { mode ->
                            L3ModeCard(mode, topW, cardH) { onNavigateToMode(mode) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun L3ModeCard(mode: L3Mode, w: Dp, h: Dp, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens20)
    Box(
        modifier = Modifier
            .size(width = w, height = h)
            .shadow(AppDimens.Dimens6, shape,
                ambientColor = mode.endColor.copy(0.4f),
                spotColor    = mode.endColor.copy(0.4f))
            .background(Brush.linearGradient(listOf(mode.startColor, mode.endColor)), shape)
            .border(AppDimens.Dimens1, Color.White.copy(0.25f), shape)
            .clickable(remember { MutableInteractionSource() }, null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier.padding(AppDimens.Dimens8)
        ) {
            Text(text = mode.emoji, style = MaterialTheme.typography.titleLarge.scaled())
            Spacer(Modifier.height(AppDimens.Dimens6))
            Text(
                text       = mode.title,
                style      = MaterialTheme.typography.titleMedium.scaled(),
                color      = Color.White,
                fontWeight = FontWeight.Black,
                textAlign  = TextAlign.Center,
                maxLines   = 2,
            )
            Spacer(Modifier.height(AppDimens.Dimens4))
            Box(
                modifier = Modifier
                    .background(Color.White.copy(0.22f), RoundedCornerShape(AppDimens.Dimens100))
                    .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens3)
            ) {
                Text(
                    text       = mode.subtitle,
                    style      = MaterialTheme.typography.labelSmall.scaled(),
                    color      = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign  = TextAlign.Center,
                )
            }
        }
    }
}
