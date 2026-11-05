package com.jigar.me.ui.view.ab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * A compact chunky tile with one big number ("x7"), used for the times-table picker. Same family as [AbLevelCard]: glossy
 * gradient face, 3D edge that the press sinks onto, lock badge when locked. Mirrors `AbTableTile.swift`.
 */
@Composable
fun AbTableTile(
    number: Int,
    accent: Color,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    isLocked: Boolean = false,
    onClick: () -> Unit,
) {
    val colors = LocalAbColors.current
    val dens = LocalDensity.current.density
    val w = width.value
    val h = height.value
    val edgeH = max(4f, h * 0.07f)
    val faceH = h - edgeH
    val radius = min(AbShapes.md.value, faceH * 0.3f).dp
    val shape = RoundedCornerShape(radius)
    Box(
        modifier
            .size(width, height)
            .abSquash(
                face = Color.Transparent, edge = accent.abDarkened(0.32f), stroke = null,
                cornerRadius = radius, edgeHeight = edgeH.dp, onClick = onClick,
            )
            .clearAndSetSemantics {
                contentDescription = "× $number"
                role = Role.Button
            },
    ) {
        Box(Modifier.fillMaxSize().clip(shape), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                drawRect(
                    Brush.verticalGradient(listOf(accent.abBlended(Color.White, 0.24f), accent, accent.abDarkened(0.12f))),
                )
                // A few faint beads along the bottom, the same motif as the level cards.
                val bw = size.height * 0.34f
                val bh = size.height * 0.21f
                val gap = size.height * 0.05f
                val rowW = 4 * bw + 3 * gap
                val x0 = (size.width - rowW) / 2f
                val y0 = size.height / 2f + size.height * 0.36f - bh / 2f
                for (i in 0 until 4) {
                    val x = x0 + i * (bw + gap)
                    drawPath(abBeadPath(Rect(x, y0, x + bw, y0 + bh)), Color.White.copy(alpha = 0.13f))
                }
                // Glossy highlight from the top-left corner.
                val ew = size.width * 1.1f
                val eh = size.height * 0.7f
                val cx = size.width * 0.5f - size.width * 0.22f
                val cy = size.height * 0.5f - size.height * 0.5f
                drawOval(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.36f), Color.White.copy(alpha = 0f)),
                        startY = cy - eh / 2f, endY = cy + eh / 2f,
                    ),
                    topLeft = Offset(cx - ew / 2f, cy - eh / 2f), size = Size(ew, eh),
                )
            }
            AbMinScaleText(
                text = "×$number",
                style = abFixedStyle(faceH * 0.46f, FontWeight.ExtraBold).copy(shadow = abShadow(dens, Color.Black.copy(alpha = 0.28f), dy = 2f)),
                color = Color.White,
                modifier = Modifier.padding(horizontal = (w * 0.06f).dp),
                minScale = 0.6f,
                maxLines = 1,
            )
            if (isLocked) {
                val glyph = faceH * 0.2f
                val pad = faceH * 0.07f
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(pad.dp)
                        .size((glyph + 2f * pad).dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Lock, null, Modifier.size((glyph * 1.15f).dp), tint = Color.White)
                }
            }
            Box(Modifier.fillMaxSize().border(1.5.dp, Color.White.copy(alpha = if (colors.isDark) 0.2f else 0.45f), shape))
        }
    }
}
