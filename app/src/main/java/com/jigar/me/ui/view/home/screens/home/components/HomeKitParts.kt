package com.jigar.me.ui.view.home.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abChunkyFace
import kotlin.math.min

/*
 * Small pieces of the restyled Home screen (mirror the private views at the bottom of the iOS `HomeView.swift`):
 * the mascot's greeting bubble, the line that introduces the menu, the streak pill and the little "NEW" tag.
 */

/** The speech bubble outline: a rounded box with a small tail under it. */
private fun speechBubblePath(w: Float, h: Float, cornerRadius: Float, tailW: Float, tailH: Float, tailOffsetX: Float): Path {
    val bH = h - tailH
    val cr = min(cornerRadius, bH / 2)
    val midX = w / 2 + tailOffsetX
    val tailL = midX - tailW / 2
    val tailR = midX + tailW / 2
    return Path().apply {
        moveTo(cr, 0f)
        lineTo(w - cr, 0f)
        arcTo(Rect(w - 2 * cr, 0f, w, 2 * cr), -90f, 90f, false)
        lineTo(w, bH - cr)
        arcTo(Rect(w - 2 * cr, bH - 2 * cr, w, bH), 0f, 90f, false)
        lineTo(tailR, bH)
        lineTo(midX, h)
        lineTo(tailL, bH)
        lineTo(cr, bH)
        arcTo(Rect(0f, bH - 2 * cr, 2 * cr, bH), 90f, 90f, false)
        lineTo(0f, cr)
        arcTo(Rect(0f, 0f, 2 * cr, 2 * cr), 180f, 90f, false)
        close()
    }
}

/** A four-pointed sparkle, drawn (the iOS `sparkle` symbol). */
@Composable
private fun Sparkle(color: Color, size: androidx.compose.ui.unit.Dp) {
    Box(
        Modifier.size(size).drawBehind {
            val c = Offset(this.size.width / 2, this.size.height / 2)
            val r = this.size.minDimension / 2
            val inner = r * 0.28f
            val p = Path().apply {
                moveTo(c.x, c.y - r)
                quadraticTo(c.x + inner * 0.4f, c.y - inner * 0.4f, c.x + r, c.y)
                quadraticTo(c.x + inner * 0.4f, c.y + inner * 0.4f, c.x, c.y + r)
                quadraticTo(c.x - inner * 0.4f, c.y + inner * 0.4f, c.x - r, c.y)
                quadraticTo(c.x - inner * 0.4f, c.y - inner * 0.4f, c.x, c.y - r)
                close()
            }
            drawPath(p, color)
        },
    )
}

/** The mascot's greeting bubble, drawn from the kit colours so it follows Light / Dark. */
@Composable
fun HomeSpeechBubble(modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val tone = colors.accentOrange
    Column(
        modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
            .drawBehind {
                val path = speechBubblePath(size.width, size.height, 18.dp.toPx(), 18.dp.toPx(), 10.dp.toPx(), 40.dp.toPx())
                // a 3D edge under the bubble, then a face that warms toward the top, then the outline
                translate(0f, 4.dp.toPx()) { drawPath(path, tone.copy(alpha = if (colors.isDark) 0.55f else 0.45f)) }
                drawPath(
                    path,
                    Brush.verticalGradient(listOf(colors.bgSecondary, colors.bgSecondary.abBlended(tone, if (colors.isDark) 0.10f else 0.12f))),
                )
                drawPath(path, tone.copy(alpha = if (colors.isDark) 0.8f else 0.6f), style = Stroke(width = 2.5.dp.toPx()))
            }
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Sparkle(tone, (9 * s).dp)
            Box(Modifier.height((18 * s).dp), contentAlignment = Alignment.Center) {
                Text(
                    "Hi Champ!", color = colors.textPrimary, style = AbTypography.fixed(16f * s, FontWeight.ExtraBold),
                    maxLines = 1, modifier = Modifier.wrapContentHeight(unbounded = true),
                )
            }
            Text("👋", fontSize = (15 * s).sp)
        }
        Box(Modifier.height((13 * s).dp), contentAlignment = Alignment.Center) {
            Text(
                "Let's count & calculate!", color = colors.textSecondary, style = AbTypography.fixed(10.5f * s, FontWeight.Bold),
                maxLines = 2, textAlign = TextAlign.Center, modifier = Modifier.wrapContentHeight(unbounded = true),
            )
        }
    }
}

/**
 * The line that introduces the menu: rods that fade out to the sides (each ending in a bead) and a coloured pill with the
 * sentence in the middle.
 */
@Composable
fun HomeSectionDivider(text: String, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val h = (26 * s).dp
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        DividerRod(leading = true, Modifier.weight(1f))
        Row(
            Modifier
                .padding(bottom = 3.dp)
                .height(h)
                .drawBehind {
                    val corner = CornerRadius(size.height / 2)
                    drawRoundRect(colors.brandPrimaryDark, topLeft = Offset(0f, 3.dp.toPx()), size = size, cornerRadius = corner)
                    drawRoundRect(Brush.verticalGradient(listOf(colors.brandSecondary, colors.brandPrimary)), size = size, cornerRadius = corner)
                    val sw = 2.dp.toPx()
                    drawRoundRect(
                        Color.White.copy(alpha = 0.85f), topLeft = Offset(sw / 2, sw / 2),
                        size = androidx.compose.ui.geometry.Size(size.width - sw, size.height - sw),
                        cornerRadius = CornerRadius(size.height / 2 - sw / 2), style = Stroke(width = sw),
                    )
                }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painterResource(R.drawable.fireworks), null, Modifier.size(h * 0.7f), contentScale = ContentScale.Fit)
            Text(
                text, color = Color.White,
                style = AbTypography.fixed(12f * s, FontWeight.ExtraBold).copy(shadow = Shadow(Color.Black.copy(alpha = 0.2f), Offset(0f, 1f), 0f)),
                maxLines = 1, overflow = TextOverflow.Clip,
            )
        }
        DividerRod(leading = false, Modifier.weight(1f))
    }
}

/** A thin rod that fades away from the pill, with a small bead at its far end. */
@Composable
private fun DividerRod(leading: Boolean, modifier: Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val bead = (9 * s).dp
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        if (leading) Box(Modifier.size(bead).drawBehind { drawCircle(colors.accentOrange) })
        Box(
            Modifier.weight(1f).height(3.dp).drawBehind {
                val a = if (leading) 0.05f else 0.55f
                val b = if (leading) 0.55f else 0.05f
                drawRoundRect(
                    Brush.horizontalGradient(listOf(colors.brandPrimary.copy(alpha = a), colors.brandPrimary.copy(alpha = b))),
                    cornerRadius = CornerRadius(size.height / 2),
                )
            },
        )
        if (!leading) Box(Modifier.size(bead).drawBehind { drawCircle(colors.accentOrange) })
    }
}

/** The streak pill: flame, "3 Days" and the best streak. */
@Composable
fun HomeStreakChip(title: String, subtitle: String, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(
        modifier
            .abChunkyFace(colors.bgSecondary, colors.outline, colors.outline, AbShapes.pill, 4.dp)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painterResource(R.drawable.fire), null, Modifier.size((28 * s).dp), contentScale = ContentScale.Fit)
        // The streak count is the headline of this chip; its two lines sit close together (Baloo's line box is tall).
        Column {
            Box(Modifier.height((21 * s).dp), contentAlignment = Alignment.CenterStart) {
                Text(
                    title, color = colors.textPrimary, style = AbTypography.fixed(20f * s, FontWeight.ExtraBold),
                    maxLines = 1, softWrap = false, modifier = Modifier.wrapContentHeight(unbounded = true),
                )
            }
            Box(Modifier.height((12 * s).dp), contentAlignment = Alignment.CenterStart) {
                Text(
                    subtitle, color = colors.textSecondary, style = AbTypography.fixed(11f * s, FontWeight.SemiBold),
                    maxLines = 1, softWrap = false, modifier = Modifier.wrapContentHeight(unbounded = true),
                )
            }
        }
    }
}

/** The small "NEW" style tag over a menu badge: a chunky yellow pill. */
@Composable
fun HomeTagBadge(text: String, size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Text(
        text, color = Color(0xFF5A3A00),
        style = AbTypography.fixed(maxOf(11f, size.value * 0.075f), FontWeight.ExtraBold),
        modifier = modifier
            .abChunkyFace(Color(0xFFFFD23F), Color(0xFFC98A00), Color.White.copy(alpha = 0.9f), AbShapes.pill, 3.dp)
            .padding(horizontal = size * 0.05f, vertical = size * 0.015f),
    )
}
