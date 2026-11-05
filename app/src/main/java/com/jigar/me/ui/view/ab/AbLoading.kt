package com.jigar.me.ui.view.ab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.sin

/** Three coloured beads that bob one after another: the loading indicator of the kit (iOS `AbBeadsLoader`). */
@Composable
fun AbBeadsLoader(modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val tones = listOf(colors.accentOrange, colors.accentGreen, colors.brandSecondary)
    val seconds by produceState(0f) {
        while (true) withFrameNanos { value = (it / 1_000_000_000.0).toFloat() }
    }
    Row(
        modifier.height(30.dp * s),
        horizontalArrangement = Arrangement.spacedBy(8.dp * s),
        verticalAlignment = Alignment.Bottom,
    ) {
        tones.forEachIndexed { i, tone ->
            val phase = sin(seconds * 5f - i * 0.8f)
            val shape = RoundedCornerShape(50)
            Box(
                Modifier
                    .offset(y = (-max(0f, phase) * 9f).dp * s)
                    .size(26.dp * s, 16.dp * s)
                    .background(Brush.verticalGradient(listOf(tone.abBlended(Color.White, 0.3f), tone)), shape)
                    .border(1.5.dp, tone.abDarkened(0.3f), shape),
            )
        }
    }
}

/** The kit's loading card: the bobbing beads over "Loading", centred on the screen. */
@Composable
fun AbLoadingCard(modifier: Modifier = Modifier, text: String = "Loading") {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.lg * s)
    val edge = 5.dp * s
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .shadow(14.dp * s, shape, clip = false, ambientColor = Color.Black.copy(alpha = 0.2f), spotColor = Color.Black.copy(alpha = 0.2f))
                .padding(bottom = edge)
                .drawBehind {
                    val r = AbShapes.lg.toPx() * s
                    drawRoundRect(colors.outline, Offset(0f, edge.toPx()), size, CornerRadius(r, r))
                }
                .background(colors.bgSecondary, shape)
                .border(3.dp, colors.outline, shape)
                .padding(horizontal = 28.dp * s, vertical = 18.dp * s),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp * s),
        ) {
            AbBeadsLoader()
            Text(text, style = AbTypography.fixed(15f * s, FontWeight.ExtraBold), color = colors.textPrimary)
        }
    }
}
