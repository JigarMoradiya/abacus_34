package com.jigar.me.ui.view.home.screens.activities.exercise.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbMenuIcon
import com.jigar.me.ui.view.ab.AbMenuKind
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import kotlin.math.max

@Composable
fun UseWhichHandTextUi(highlightColor: Color,
                       modifier: Modifier = Modifier,) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor

    // the preset's button colour is a deep tone made for the light page: on the dark page it is pulled toward white so it still reads
    val shown = if (colors.isDark) highlightColor.abBlended(Color.White, 0.62f) else highlightColor
    val annotatedText = remember(shown, s) {
        buildAnnotatedString {
            val highlight = SpanStyle(fontWeight = FontWeight.Bold, color = shown, fontSize = (13f * s).sp)
            append("Use ")
            withStyle(highlight) { append("the keypad") }
            append(" or ")
            withStyle(highlight) { append("the abacus") }
            append(" to give your answer!")
        }
    }

    Text(
        text = annotatedText,
        style = AbTypography.fixed(12f * s, FontWeight.SemiBold),
        color = colors.textPrimary,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

/** "Keypad  or  abacus": two round badges, the two ways to give an answer. */
@Composable
fun ExerciseHintBadges(size: Dp, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(size * 0.22f), verticalAlignment = Alignment.CenterVertically) {
        HintBadge(colors.brandPrimary, size) {
            Icon(Icons.Filled.Apps, null, Modifier.size(size * 0.46f * AB_SF_TO_VECTOR), tint = Color.White)
        }
        Text(stringResource(R.string.or), style = AbTypography.fixed(size.value * 0.42f, FontWeight.ExtraBold), color = colors.textSecondary)
        HintBadge(colors.accentOrange, size) {
            AbMenuIcon(AbMenuKind.FreeMode, Modifier.size(size * 0.74f))
        }
    }
}

@Composable
private fun HintBadge(tone: Color, size: Dp, content: @Composable () -> Unit) {
    Box(Modifier.padding(bottom = size * 0.06f).size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val d = this.size.minDimension
            val centre = Offset(this.size.width / 2f, this.size.height / 2f)
            drawCircle(tone.abDarkened(0.3f), d / 2f, centre + Offset(0f, d * 0.06f))
            drawCircle(Brush.verticalGradient(listOf(tone.abBlended(Color.White, 0.25f), tone)), d / 2f, centre)
            val ring = max(2.dp.toPx(), d * 0.06f)
            drawCircle(Color.White.copy(alpha = 0.9f), d / 2f - ring / 2f, centre, style = Stroke(ring))
        }
        content()
    }
}
