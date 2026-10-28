package com.jigar.me.ui.view.home.screens.my_account.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.utils.DateTimeUtils.formatTo
import com.jigar.me.utils.DateTimeUtils.toDate

/** One count of the account (exams, exercises, custom challenges): the number in a coloured badge, a title and the last time. */
@Composable
fun StatisticsCard(
    title: String,
    count: String,
    time: String,
    tone: Color,
    modifier: Modifier = Modifier
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val lastAttend = if (time.isEmpty()) "-" else time.toDate().formatTo("'at' dd MMM-yy hh:mm a")

    Row(
        modifier
            .fillMaxWidth()
            .abChunkyFace(
                face = colors.bgSecondary.copy(alpha = if (colors.isDark) 0.96f else 0.94f),
                edge = colors.outline, stroke = colors.outline,
                cornerRadius = AbShapes.md * s, edgeHeight = 3.dp * s,
            )
            .padding(horizontal = 12.dp * s, vertical = 8.dp * s),
        horizontalArrangement = Arrangement.spacedBy(12.dp * s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // the count in a coloured badge
        Box(Modifier.size(52.dp * s), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val r = size.minDimension / 2f
                val centre = Offset(size.width / 2f, size.height / 2f)
                drawCircle(tone.abDarkened(0.3f), r, centre + Offset(0f, 3.dp.toPx() * s))
                drawCircle(Brush.verticalGradient(listOf(tone.abBlended(Color.White, 0.25f), tone)), r, centre)
                val ring = 2.5.dp.toPx()
                drawCircle(Color.White.copy(alpha = 0.9f), r - ring / 2f, centre, style = Stroke(ring))
            }
            AbShrinkText(
                text = count,
                style = AbTypography.fixed(22f * s, FontWeight.ExtraBold),
                color = Color.White,
                minScale = 0.5f,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 4.dp * s)
            )
        }

        Column(Modifier.weight(1f)) {
            Box(Modifier.height(20.dp * s), contentAlignment = Alignment.CenterStart) {
                AbShrinkText(
                    text = title,
                    style = AbTypography.fixed(15f * s, FontWeight.ExtraBold),
                    color = colors.textPrimary,
                    minScale = 0.7f,
                    maxLines = 1
                )
            }
            AbShrinkText(
                text = "Last attend $lastAttend",
                style = AbTypography.fixed(11f * s, FontWeight.SemiBold),
                color = colors.textSecondary,
                minScale = 0.8f,
                maxLines = 2
            )
        }
    }
}
