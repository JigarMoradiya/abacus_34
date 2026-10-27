package com.jigar.me.ui.view.ab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** The hexagonal abacus bead (pointed left and right), same silhouette as the beads on the Free Mode abacus. */
fun abBeadPath(rect: Rect): Path {
    val w = rect.width
    return Path().apply {
        moveTo(rect.left, rect.center.y)
        lineTo(rect.left + w * 0.24f, rect.top)
        lineTo(rect.right - w * 0.24f, rect.top)
        lineTo(rect.right, rect.center.y)
        lineTo(rect.right - w * 0.24f, rect.bottom)
        lineTo(rect.left + w * 0.24f, rect.bottom)
        close()
    }
}

/** One glossy bead. `lit` beads carry colour; unlit beads are a muted tint of the surface. */
@Composable
fun AbBead(color: Color, modifier: Modifier = Modifier, lit: Boolean = true, size: Pair<Dp, Dp> = 34.dp to 22.dp) {
    val colors = LocalAbColors.current
    val face = if (lit) color else colors.bgSecondary.copy(alpha = if (colors.isDark) 0.55f else 0.9f)
    val rim = if (lit) face.abDarkened(0.28f) else colors.textSecondary.copy(alpha = 0.45f)
    Canvas(modifier.size(size.first, size.second)) {
        val rect = Rect(Offset.Zero, this.size)
        val path = abBeadPath(rect)
        drawPath(path, Brush.verticalGradient(listOf(if (lit) face.abBlended(Color.White, 0.28f) else face, face)))
        drawPath(path, rim, style = Stroke(width = 1.5.dp.toPx(), join = StrokeJoin.Round))
        // Gloss highlight: the strip that makes it read as a bead and not a flat chip.
        val gw = this.size.width * 0.42f
        val gh = this.size.height * 0.16f
        drawRoundRect(
            Color.White.copy(alpha = if (lit) 0.45f else 0.25f),
            topLeft = Offset((this.size.width - gw) / 2f, this.size.height * 0.14f),
            size = Size(gw, gh), cornerRadius = CornerRadius(gh / 2f, gh / 2f),
        )
    }
}

/** Star rating as beads: earned stars are gold beads carrying a star, the rest are muted. The middle bead is larger. */
@Composable
fun AbBeadStars(earned: Int, modifier: Modifier = Modifier, total: Int = 3, sizeFactor: Float = 1f) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor * sizeFactor
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp * s), verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until total) {
            val big = total == 3 && i == 1
            val w = (if (big) 54 else 42).dp * s
            val h = (if (big) 36 else 28).dp * s
            Box(Modifier.offset(y = if (big) (-4).dp * s else 0.dp), contentAlignment = Alignment.Center) {
                AbBead(colors.accentYellow, lit = i < earned, size = w to h)
                Icon(
                    Icons.Rounded.Star, null, Modifier.size(h * 0.58f),
                    tint = if (i < earned) Color.White else colors.outline,
                )
            }
        }
    }
}
