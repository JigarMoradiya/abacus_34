package com.jigar.me.ui.view.ab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** A small chunky label: a legend entry, a timer, a status ("Step by step", "02:15"). Same 3D edge as buttons, not tappable. */
@Composable
fun AbChip(text: String, face: Color, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    val s = LocalAbScale.current.factor
    Row(
        modifier
            .abChunkyFace(face, face.abDarkened(0.3f), null, AbShapes.pill, 3.dp * s)
            .heightIn(min = 28.dp * s)
            .padding(horizontal = 12.dp * s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) { Icon(icon, null, Modifier.size(13.dp * s), tint = Color.White); Spacer(Modifier.width(4.dp * s)) }
        Text(text, style = AbTypography.fixed(13f * s, FontWeight.ExtraBold), color = Color.White, maxLines = 1)
    }
}

@Composable
fun AbChip(text: String, tone: AbTone, modifier: Modifier = Modifier, icon: ImageVector? = null) =
    AbChip(text, AbTonePalette.of(tone, AbColors.Light).face, modifier, icon)

/**
 * "Question 3 of 10", "Step 2 of 5": the counter at the top of a question or lesson card. On a coloured hero panel it is
 * a frosted pill ([onColor]); on a plain surface it is a solid chip in the brand colour.
 */
@Composable
fun AbCountPill(text: String, modifier: Modifier = Modifier, icon: ImageVector? = null, onColor: Boolean = true) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val bg = if (onColor) Modifier.background(Color.White.copy(alpha = 0.22f), CircleShape).border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
    else Modifier.background(colors.brandPrimary, CircleShape)
    Row(
        modifier.then(bg).height(30.dp * s).padding(horizontal = 14.dp * s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) { Icon(icon, null, Modifier.size(13.dp * s), tint = Color.White); Spacer(Modifier.width(6.dp * s)) }
        Text(text, style = AbTypography.fixed(14f * s, FontWeight.ExtraBold), color = Color.White, maxLines = 1)
    }
}
