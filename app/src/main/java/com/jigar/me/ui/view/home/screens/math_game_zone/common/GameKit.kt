package com.jigar.me.ui.view.home.screens.math_game_zone.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbThemeMode
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.home.theme.ButtonType

// The Math Game Zone's bridge to the Ab UI kit (Android twin of iOS Main/Games/GameKit.swift). The games were written against
// the old Kids* components; these keep the same call shapes so a screen can switch look without touching its logic.

/** The kit tone that stands in for each of the old gradient button colours. */
fun ButtonType.abTone(): AbTone = when (this) {
    ButtonType.BLUE -> AbTone.Primary
    ButtonType.PINK -> AbTone.Pink
    ButtonType.ORANGE -> AbTone.Warning
    ButtonType.PURPLE -> AbTone.Purple
    ButtonType.GREEN, ButtonType.POSITIVE -> AbTone.Success
    ButtonType.RED, ButtonType.NEGATIVE -> AbTone.Danger
    ButtonType.TEAL -> AbTone.Secondary
    ButtonType.DISABLE -> AbTone.Primary
    ButtonType.OPTIONS -> AbTone.Ghost
}

/** `KidsActionButton` with the kit's chunky 3D look and sound. Same parameters. */
@Composable
fun GameButton(
    text: String,
    icon: ImageVector? = null,
    painterIcon: Painter? = null,
    type: ButtonType,
    onClick: () -> Unit,
    isIconStart: Boolean = true,
    isSmall: Boolean = false,
    modifier: Modifier = Modifier,
) {
    AbButton(
        text = text,
        modifier = modifier,
        tone = type.abTone(),
        size = if (isSmall) AbSize.Compact else AbSize.Medium,
        enabled = type != ButtonType.DISABLE,
        icon = icon,
        painterIcon = painterIcon,
        iconAtEnd = !isIconStart,
        onClick = onClick,
    )
}

/** The button on a result card that returns to the game's own home screen: a house, in the friendly blue of the kit. */
@Composable
fun GameHomeButton(modifier: Modifier = Modifier, isSmall: Boolean = false, onClick: () -> Unit) {
    GameButton(text = "Home", icon = Icons.Filled.Home, type = ButtonType.BLUE, isSmall = isSmall, modifier = modifier, onClick = onClick)
}

/**
 * Every Game Zone route sits in this frame: the light kit theme (the Game Zone has no dark design yet) over the kit's quiet
 * sky and hills in the game's own [world], instead of the old global bokeh.
 */
@Composable
fun GameFrame(world: AbWorld, content: @Composable () -> Unit) {
    // follows the app's Light/Dark toggle (it used to be pinned to light)
    AbTheme {
        Box(Modifier.fillMaxSize()) {
            AbBackground(world, Modifier.fillMaxSize())
            content()
        }
    }
}

/** A soft white pill for a short line of text (a tagline, a best score, a score counter). [outline] colours its rim. */
fun Modifier.abGamePill(outline: Color? = null): Modifier = composed {
    val colors = LocalAbColors.current
    this
        .background(colors.bgSecondary.copy(alpha = if (colors.isDark) 0.92f else 0.9f), CircleShape)
        .border(if (outline == null) 2.dp else 3.dp, outline ?: colors.outline, CircleShape)
}

/** The soft white tray behind a row of choices (the difficulty chips). */
fun Modifier.abGameTray(): Modifier = composed {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.md * s)
    this
        .background(colors.bgSecondary.copy(alpha = if (colors.isDark) 0.92f else 0.88f), shape)
        .border(2.dp, colors.outline, shape)
}

/** The frame every "How to play" card shares: a kit card with a rim and 3D edge in the game's colour. */
fun Modifier.abHowToCard(accent: ButtonType, inset: Dp = 16.dp): Modifier = composed {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.lg * s)
    val face = com.jigar.me.ui.view.ab.AbTonePalette.of(accent.abTone(), colors).face
    val edge = face.abDarkened(0.3f)
    val drop = 5.dp * s
    this
        .padding(bottom = drop)
        .drawBehind {
            drawRoundRect(edge, Offset(0f, drop.toPx()), size, CornerRadius(AbShapes.lg.toPx() * s))
        }
        .shadow(14.dp * s, shape, clip = false)
        .background(colors.bgSecondary, shape)
        .border(3.dp, face, shape)
        .clip(shape)
        .padding(inset)
}

/**
 * Keeps a board and its keys fully on screen. The content takes its natural size and is centred; ONLY when it is taller or wider
 * than the room it is given is it shrunk (never enlarged) to fit, so on a short phone nothing is cut off at the bottom.
 */
@Composable
fun GameFitBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    androidx.compose.ui.layout.Layout(content = content, modifier = modifier) { measurables, constraints ->
        val w = constraints.maxWidth
        val h = constraints.maxHeight
        val placeables = measurables.map { it.measure(androidx.compose.ui.unit.Constraints()) }   // natural size
        val cw = placeables.maxOfOrNull { it.width } ?: 0
        val ch = placeables.maxOfOrNull { it.height } ?: 0
        val fit = minOf(1f, w.toFloat() / cw.coerceAtLeast(1), h.toFloat() / ch.coerceAtLeast(1))
        layout(w, h) {
            placeables.forEach {
                it.placeWithLayer((w - it.width) / 2, (h - it.height) / 2) {
                    scaleX = fit
                    scaleY = fit
                }
            }
        }
    }
}

/**
 * The frame every Game Zone home screen shares: [content] sits in the exact vertical middle of the screen, the [header] is
 * pinned to the top and the [bottom] row (difficulty chips + start) to the bottom. The content is NOT squeezed between the two:
 * on a short phone screen it shrinks to the room that is left, so the bottom row always gets its full height (the chips used
 * to collapse into thin bars). On a tall tablet nothing is scaled.
 */
@Composable
fun GameHomeLayout(
    modifier: Modifier = Modifier,
    /** true: the content sizes itself FROM the free height (fillMaxHeight / weight inside it work); false: it takes its natural size and shrinks to fit. */
    fillHeight: Boolean = false,
    header: @Composable () -> Unit,
    bottom: @Composable () -> Unit,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    androidx.compose.ui.layout.Layout(
        content = {
            androidx.compose.foundation.layout.Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, content = content)
            Box { header() }
            Box { bottom() }
        },
        modifier = modifier.fillMaxSize(),
    ) { measurables, constraints ->
        val w = constraints.maxWidth
        val h = constraints.maxHeight
        val loose = androidx.compose.ui.unit.Constraints(maxWidth = w, maxHeight = h)
        val head = measurables[1].measure(loose)
        val foot = measurables[2].measure(loose)
        // the content may use at most the room left once the taller of the two bars is mirrored above and below the middle
        val avail = (h - 2 * maxOf(head.height, foot.height)).coerceAtLeast(1)
        val body = measurables[0].measure(
            if (fillHeight) androidx.compose.ui.unit.Constraints(maxWidth = w, minHeight = avail, maxHeight = avail)
            else androidx.compose.ui.unit.Constraints(maxWidth = w, maxHeight = androidx.compose.ui.unit.Constraints.Infinity)
        )
        val fit = minOf(1f, avail.toFloat() / body.height.coerceAtLeast(1), w.toFloat() / body.width.coerceAtLeast(1))
        layout(w, h) {
            body.placeWithLayer((w - body.width) / 2, (h - body.height) / 2) {
                scaleX = fit
                scaleY = fit
            }
            head.place(0, 0)
            foot.place(0, h - foot.height)
        }
    }
}


/** A how-to card's accent colour, brightened on the dark page so a title in it stays readable (same hue, keeps its colour). */
@androidx.compose.runtime.Composable
fun howToInk(c: Color): Color {
    if (!com.jigar.me.ui.view.ab.LocalAbColors.current.isDark) return c
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(android.graphics.Color.argb(255, (c.red * 255).toInt(), (c.green * 255).toInt(), (c.blue * 255).toInt()), hsv)
    hsv[1] = minOf(hsv[1], 0.6f); hsv[2] = maxOf(hsv[2], 0.97f)
    return Color(android.graphics.Color.HSVToColor(hsv))
}
