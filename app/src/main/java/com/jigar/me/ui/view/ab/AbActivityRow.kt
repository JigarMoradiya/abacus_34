package com.jigar.me.ui.view.ab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A wide chunky row for choosing an activity (Learn & Drill, Flashcards, Fill the Blanks): an emoji badge, a title and one
 * line of help, and a chevron, on a glossy [accent] gradient with a 3D edge. Same press feel as every kit button.
 */
@Composable
fun AbActivityRow(
    emoji: String,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val density = LocalDensity.current.density
    val shape = RoundedCornerShape(AbShapes.lg * s)

    Row(
        modifier
            .fillMaxWidth()
            .abSquash(
                face = Color.Transparent, edge = accent.abDarkened(0.32f), stroke = null,
                cornerRadius = AbShapes.lg * s, edgeHeight = 6.dp * s, onClick = onClick,
            )
            .clip(shape)
            .background(Brush.verticalGradient(listOf(accent.abBlended(Color.White, 0.22f), accent, accent.abDarkened(0.1f))))
            .drawBehind {
                // soft highlight: a big pale ellipse bleeding in from the upper left, clipped by the row's shape
                val top = -size.height * 0.62f
                val ovalHeight = size.height * 1.1f
                drawOval(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.28f), Color.White.copy(alpha = 0f)),
                        startY = top, endY = top + ovalHeight,
                    ),
                    topLeft = Offset(-size.width * 0.2f, top),
                    size = Size(size.width * 0.9f, ovalHeight),
                )
            }
            .border(2.dp, Color.White.copy(alpha = if (colors.isDark) 0.2f else 0.4f), shape)
            .padding(14.dp * s)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(16.dp * s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // emoji badge (the system emoji font, not Baloo)
        Box(
            Modifier
                .size(58.dp * s)
                .shadow(3.dp, CircleShape)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(emoji, fontSize = (30f * s).sp)
        }

        // the title and its help line sit close: Baloo's line box leaves a wide empty band between two lines
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy((-6).dp * s)) {
            Text(
                title,
                style = AbTypography.fixed(21f * s, FontWeight.ExtraBold).copy(
                    shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 1.5f * density), 0f),
                ),
                color = Color.White,
            )
            Text(
                subtitle,
                style = AbTypography.fixed(14f * s, FontWeight.SemiBold),
                color = Color.White.copy(alpha = 0.92f),
            )
        }

        Box(Modifier.size(34.dp * s).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
            AbChevron(accent.abDarkened(0.2f), Modifier.size(21.dp * s), pointingLeft = false)
        }
    }
}
