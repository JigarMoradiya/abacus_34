package com.jigar.me.ui.view.ab

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Rounded surface with an outline ring and a soft tinted shadow; the tappable variant squashes on press. */
@Composable
fun AbCard(
    modifier: Modifier = Modifier,
    tint: Color? = null,
    tiltDegrees: Float = 0f,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.lg * s)
    val shadowTint = colors.outline.copy(alpha = if (colors.isDark) 0.25f else 0.9f)
    val press = remember { MutableInteractionSource() }
    val base = modifier
        .rotate(tiltDegrees)
        .shadow(14.dp * s, shape, ambientColor = shadowTint, spotColor = shadowTint)
        .background(tint ?: colors.bgSecondary, shape)
        .border(2.dp, colors.outline.copy(alpha = if (colors.isDark) 1f else 0.8f), shape)
    val face = if (onClick != null) base.abSquash(cornerRadius = AbShapes.lg * s, edgeHeight = 0.dp, onClick = onClick) else base
    Column(face.padding(16.dp * s).fillMaxWidth(), content = content)
}

/** Just the switch part (no label), for use inside setting rows: rounded track with a 3D edge and a bouncy white knob. */
@Composable
fun AbSwitch(isOn: Boolean, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val track = if (isOn) colors.accentGreen else colors.surfaceTint
    val knobX by animateDpAsState(
        if (isOn) 52.dp * s - 24.dp * s - 3.dp * s else 3.dp * s,
        spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium), label = "abSwitch",
    )
    Box(
        modifier
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                AbSound.Button.play(); onChange(!isOn)
            }
            .padding(bottom = 3.dp * s),
    ) {
        Box(Modifier.width(52.dp * s).height(30.dp * s).offset(y = 3.dp * s).background(track.abDarkened(0.25f), CircleShape))
        Box(Modifier.width(52.dp * s).height(30.dp * s).background(track, CircleShape))
        Box(
            Modifier.offset(x = knobX, y = 3.dp * s).size(24.dp * s)
                .shadow(2.dp * s, CircleShape).background(Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (isOn) Icons.Rounded.Check else Icons.Rounded.Close, null, Modifier.size(12.dp * s),
                tint = if (isOn) AbColors.Light.accentGreen.abDarkened(0.2f) else AbColors.Light.textSecondary,
            )
        }
    }
}

/** A labelled switch row. */
@Composable
fun AbToggle(isOn: Boolean, label: String, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(modifier.fillMaxWidth().padding(vertical = 6.dp * s), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp * s)) {
        Text(label, Modifier.weight(1f), style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
        AbSwitch(isOn, onChange)
    }
}

/** Friendly capsule toast that slides down from the top on a spring while [visible] (the caller owns the timing). */
@Composable
fun AbToast(message: String, visible: Boolean, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val y by animateDpAsState(if (visible) 0.dp else (-160).dp * s, spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium), label = "abToast")
    val a by animateFloatAsState(if (visible) 1f else 0f, label = "abToastAlpha")
    val shadowTint = (if (colors.isDark) Color.Black else colors.outline).copy(alpha = 0.8f)
    Row(
        modifier.offset(y = y).alpha(a)
            .shadow(10.dp * s, CircleShape, ambientColor = shadowTint, spotColor = shadowTint)
            .background(colors.bgElevated, CircleShape)
            .border(2.dp, colors.outline, CircleShape)
            .padding(start = if (icon == null) 20.dp * s else 8.dp * s, end = 20.dp * s, top = 8.dp * s, bottom = 8.dp * s),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp * s),
    ) {
        if (icon != null) {
            Box(Modifier.size(32.dp * s).background(colors.brandPrimary, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, Modifier.size(16.dp * s), tint = Color.White)
            }
        }
        Text(message, style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}
