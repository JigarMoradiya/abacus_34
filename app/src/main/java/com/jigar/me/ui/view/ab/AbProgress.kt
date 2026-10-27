package com.jigar.me.ui.view.ab

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Progress for headers and lesson cards: a chunky rounded bar that fills left to right, with a glossy highlight and a
 * "3/10" count. Deliberately plain (no abacus imagery) so it is never mistaken for an abacus.
 */
@Composable
fun AbProgressBar(
    filled: Int,
    total: Int,
    modifier: Modifier = Modifier,
    /** Sitting on a coloured hero panel: white track and fill instead of the neutral track and green fill. */
    onColor: Boolean = false,
    accent: Color? = null,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val count = maxOf(total, 1)
    val lit = filled.coerceIn(0, count)
    val value by animateFloatAsState(
        lit.toFloat() / count, spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium), label = "abProgress",
    )
    val h = 16.dp * s
    val base = accent ?: colors.accentGreen
    val fillTop = if (onColor) Color.White else base.abBlended(Color.White, 0.3f)
    val fillBottom = if (onColor) Color(0xFFE3E6F5) else base
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(10.dp * s), verticalAlignment = Alignment.CenterVertically) {
        BoxWithConstraints(Modifier.weight(1f).height(h)) {
            val track = if (onColor) Color.White.copy(alpha = 0.26f) else colors.bgSecondary.copy(alpha = if (colors.isDark) 0.6f else 0.92f)
            val ring = if (onColor) Color.White.copy(alpha = 0.4f) else colors.outline
            Box(Modifier.fillMaxHeight().background(track, CircleShape).border(2.dp, ring, CircleShape))
            Box(
                Modifier
                    .width(maxOf(h, maxWidth * value))
                    .fillMaxHeight()
                    .background(Brush.verticalGradient(listOf(fillTop, fillBottom)), CircleShape),
            ) {
                if (!onColor) {
                    Box(
                        Modifier.align(Alignment.TopCenter).padding(top = h * 0.16f, start = h * 0.4f, end = h * 0.4f)
                            .fillMaxHeight(0.2f).fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.45f), CircleShape),
                    )
                }
            }
        }
        Text("$lit/$count", style = AbTypography.fixed(15f * s, FontWeight.ExtraBold, tabular = true),
            color = if (onColor) Color.White else colors.textPrimary, maxLines = 1)
    }
}
