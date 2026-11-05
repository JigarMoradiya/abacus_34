package com.jigar.me.ui.view.ab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/** A small chevron that points down, drawn (like [AbChevron]) so its weight matches the kit. */
@Composable
private fun AbDownChevron(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.16f, h * 0.34f)
            lineTo(w * 0.5f, h * 0.68f)
            lineTo(w * 0.84f, h * 0.34f)
        }
        drawPath(path, color, style = Stroke(width = w * 0.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * A dropdown for the kit: a chunky pill that shows the current choice and opens a menu of [items]. Replaces the
 * per-screen purple capsules of the report screens. The iOS kit opens the system menu; here it is a Material dropdown
 * painted in the kit's colours and font, as wide as the pill at least.
 *
 * @param selectedTitle the current choice (empty = show [placeholder]).
 * @param icon an optional leading icon (orange, like the iOS kit).
 * @param minWidth the pill is at least this wide (0 = hug the text).
 * @param title the text of an item.
 */
@Composable
fun <Item> AbMenuPicker(
    items: List<Item>,
    selectedTitle: String,
    title: (Item) -> String,
    onSelect: (Item) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    icon: ImageVector? = null,
    minWidth: Dp = 0.dp,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val density = LocalDensity.current
    var expanded by remember { mutableStateOf(false) }
    var pillWidthPx by remember { mutableIntStateOf(0) }
    val press = remember { MutableInteractionSource() }
    val pressed by press.collectIsPressedAsState()

    Box(modifier) {
        // the pill: face, a 3 dp edge in the outline colour under it, and the outline ring
        Row(
            Modifier
                .alpha(if (pressed) 0.7f else 1f)
                .clickable(interactionSource = press, indication = null) { expanded = true }
                .abChunkyFace(colors.bgSecondary, colors.outline, null, AbShapes.pill, 3.dp * s)
                .border(2.dp, colors.outline, CircleShape)
                .widthIn(min = minWidth)
                .heightIn(min = 40.dp * s)
                // measured before the horizontal padding, so it is the whole pill's width (the menu is at least that wide)
                .onSizeChanged { pillWidthPx = it.width }
                .padding(horizontal = 14.dp * s),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(Modifier.weight(1f, fill = false), verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, null, Modifier.size((14f * AB_SF_TO_VECTOR).dp * s), tint = colors.accentOrange)
                    Spacer(Modifier.width(8.dp * s))
                }
                AbShrinkText(
                    text = if (selectedTitle.isEmpty()) placeholder else selectedTitle,
                    style = AbTypography.fixed(14f * s, FontWeight.ExtraBold),
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f, fill = false),
                    minScale = 0.7f,
                    maxLines = 1,
                )
            }
            // 8 + the flexible gap (at least 4) + 8 of the iOS stack, then the chevron
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(20.dp * s))
                AbDownChevron(colors.textSecondary, Modifier.size(12.dp * s))
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(min = with(density) { pillWidthPx.toDp() }),
            offset = DpOffset(0.dp, 4.dp * s),
            shape = RoundedCornerShape(AbShapes.md * s),
            containerColor = colors.bgSecondary,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp,
            border = BorderStroke(2.dp, colors.outline),
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            title(item),
                            style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary,
                            maxLines = 1,
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelect(item)
                    },
                )
            }
        }
    }
}
