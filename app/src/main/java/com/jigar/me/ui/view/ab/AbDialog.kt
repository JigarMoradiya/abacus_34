package com.jigar.me.ui.view.ab

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.pow

// region Popup entrance

/** SwiftUI `.easeOut`. */
internal val AbEaseOut = CubicBezierEasing(0f, 0f, 0.58f, 1f)

/** SwiftUI `.spring(response:dampingFraction:)` as a Compose spring: stiffness = (2 pi / response)^2, unit mass. */
private fun abEntranceSpring(response: Float, dampingFraction: Float) = spring<Float>(
    dampingRatio = dampingFraction,
    stiffness = ((2.0 * PI / response).pow(2)).toFloat(),
)

/**
 * `false` on the first composition, `true` right after it: feed it to [abScrimEntrance] / [abCardEntrance] /
 * [abSheetEntrance]. A popup starts its entrance by itself this way, so it looks the same wherever a screen shows it
 * (it does not depend on the caller animating anything).
 */
@Composable
fun rememberAbEntranceShown(): Boolean {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    return shown
}

/**
 * How every kit popup comes in: the dimmed backdrop only FADES (ease-out, 0.22 s) while only the card pops up from a
 * slightly smaller size. (Scaling the whole popup made the dim layer visibly shrink into the middle of the screen.)
 */
fun Modifier.abScrimEntrance(shown: Boolean): Modifier = composed {
    val fade by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(AbMotion.NORMAL, easing = AbEaseOut),
        label = "abScrimEntrance",
    )
    graphicsLayer { alpha = fade }
}

/** The card of a popup: scales up from [from] to 1 while fading in, on a spring (response 0.34, damping 0.8). */
fun Modifier.abCardEntrance(shown: Boolean, from: Float = 0.9f): Modifier = composed {
    val progress by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = abEntranceSpring(0.34f, 0.8f),
        label = "abCardEntrance",
    )
    graphicsLayer {
        val sc = from + (1f - from) * progress
        scaleX = sc
        scaleY = sc
        alpha = progress.coerceIn(0f, 1f)
    }
}

/** A sheet that rises from the bottom edge of the screen by [distance], on a spring (response 0.4, damping 0.85). */
fun Modifier.abSheetEntrance(shown: Boolean, distance: Dp = 420.dp): Modifier = composed {
    val progress by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = abEntranceSpring(0.4f, 0.85f),
        label = "abSheetEntrance",
    )
    val distancePx = with(LocalDensity.current) { distance.toPx() }
    graphicsLayer { translationY = (1f - progress) * distancePx }
}

/**
 * The full-screen layer of a kit popup: the dimmed scrim (fades in, swallows every tap, tapping it calls [onScrimTap])
 * with the popup centred over it. [content] receives the layer's size and whether the entrance has started.
 * Android's back press does what a scrim tap does (and is swallowed when the popup cannot be dismissed).
 * [scrim] overrides the kit scrim for the screens whose iOS popup dims with plain black 45%.
 */
@Composable
internal fun AbPopupLayer(
    onScrimTap: (() -> Unit)?,
    scrim: Color? = null,
    content: @Composable (maxWidth: Dp, maxHeight: Dp, shown: Boolean) -> Unit,
) {
    val colors = LocalAbColors.current
    val shown = rememberAbEntranceShown()
    // modal: Back is always swallowed, and does what a scrim tap does when the popup can be dismissed
    BackHandler { onScrimTap?.invoke() }
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val layerWidth = maxWidth
        val layerHeight = maxHeight
        Box(
            Modifier
                .fillMaxSize()
                .abScrimEntrance(shown)
                .background(scrim ?: colors.overlayScrim)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onScrimTap?.invoke() },
        )
        content(layerWidth, layerHeight, shown)
    }
}

/**
 * The chunky card shared by the kit popups: face, rim in [tone], a 6 dp 3D edge under it in a darker [tone], a soft
 * shadow, and the card entrance. [lightBorderAlpha] is the rim opacity in Light mode (0.7 in Dark mode). Swallows taps so
 * a tap on the card never reaches the scrim.
 */
@Composable
internal fun AbPopupCard(
    width: Dp,
    shown: Boolean,
    tone: Color,
    lightBorderAlpha: Float,
    padding: Dp,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.lg * s)
    val edgeHeight = 6.dp * s
    val edgeColor = tone.abDarkened(0.3f)
    val rim = tone.copy(alpha = if (colors.isDark) 0.7f else lightBorderAlpha)
    Column(
        modifier
            .width(width)
            .abCardEntrance(shown)
            .pointerInput(Unit) { detectTapGestures(onTap = { }) }
            // shadow first, so its silhouette includes the 3D edge that hangs below the face
            .shadow(18.dp * s, shape, clip = false, ambientColor = Color.Black, spotColor = Color.Black)
            .padding(bottom = edgeHeight)
            .drawBehind {
                val r = AbShapes.lg.toPx() * s
                drawRoundRect(edgeColor, Offset(0f, edgeHeight.toPx()), size, CornerRadius(r, r))
            }
            .background(colors.bgSecondary, shape)
            .border(3.dp, rim, shape)
            .padding(padding),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

// endregion

/**
 * The kit's modal: a dimmed scrim and a chunky card with an icon badge, a title, scrolling content and a trailing slot.
 * Replaces the old per-screen popups on restyled screens. Compose it as the last child of a full-screen box; it fills
 * that box. It starts its own entrance.
 *
 * @param icon the badge in front of the title (null = no badge). Kit callers pass a Material icon where the iOS kit
 * passes an SF Symbol name.
 * @param onDismiss tapping the scrim (or Android's back) calls this; null means the dialog can only be closed by its
 * own buttons.
 * @param widthFraction fraction of the screen width the card may use.
 * @param footer shown at the trailing end of the header row, after the title (that is where the iOS kit puts it).
 */
@Composable
fun AbDialog(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accent: Color? = null,
    onDismiss: (() -> Unit)? = null,
    widthFraction: Float = 0.7f,
    footer: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val tone = accent ?: colors.brandPrimary
    AbPopupLayer(onScrimTap = onDismiss) { layerWidth, layerHeight, shown ->
        AbPopupCard(
            width = minOf(layerWidth * widthFraction, 620.dp * s),
            shown = shown,
            tone = tone,
            lightBorderAlpha = 0.45f,
            padding = 16.dp * s,
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp * s),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp * s),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    Box(
                        Modifier
                            .size(34.dp * s)
                            .background(tone, CircleShape)
                            .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(icon, null, Modifier.size((16f * AB_SF_TO_VECTOR).dp * s), tint = Color.White)
                    }
                }
                AbShrinkText(
                    text = title,
                    style = AbTypography.headline(s),
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                    minScale = 0.7f,
                    maxLines = 2,
                )
                Spacer(Modifier.width(8.dp * s))
                footer()
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxOf(120.dp, layerHeight * 0.62f))
                    .verticalScroll(rememberScrollState()),
                content = content,
            )
        }
    }
}

/**
 * A rounded text field for the kit: chunky outline, kit font, numeric by default. The outline thickens and turns brand
 * blue while it has focus; tapping anywhere on the field focuses it.
 */
@Composable
fun AbTextField(
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Number,
    isSecure: Boolean = false,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.md * s)
    var focused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val ring by animateColorAsState(
        if (focused) colors.brandPrimary else colors.outline,
        tween(AbMotion.FAST, easing = AbEaseOut), label = "abTextFieldRing",
    )
    val ringWidth by animateDpAsState(
        if (focused) 3.dp else 2.dp,
        tween(AbMotion.FAST, easing = AbEaseOut), label = "abTextFieldRingWidth",
    )
    val textStyle = AbTypography.body(s).copy(fontWeight = FontWeight.Bold, color = colors.textPrimary)

    Box(
        modifier
            .heightIn(min = 44.dp * s)
            .background(colors.surfaceTint.copy(alpha = if (colors.isDark) 0.5f else 0.6f), shape)
            .border(ringWidth, ring, shape)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { focusRequester.requestFocus() }
            .padding(horizontal = 14.dp * s),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focused = it.isFocused },
            textStyle = textStyle,
            singleLine = true,
            cursorBrush = SolidColor(colors.brandPrimary),
            keyboardOptions = KeyboardOptions(keyboardType = if (isSecure) KeyboardType.Password else keyboardType),
            visualTransformation = if (isSecure) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (text.isEmpty()) {
                        Text(placeholder, style = textStyle, color = colors.textSecondary.copy(alpha = 0.7f), maxLines = 1)
                    }
                    inner()
                }
            },
        )
    }
}

/**
 * A labelled settings row: the label on the left and any control (switch, field) on the right, on a soft tinted plate.
 * Use [AbSwitch] (AbSurfaces.kt) as the control for a toggle.
 */
@Composable
fun AbSettingRow(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    control: @Composable RowScope.() -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(
        modifier
            .fillMaxWidth()
            .background(
                colors.surfaceTint.copy(alpha = if (colors.isDark) 0.35f else 0.55f),
                RoundedCornerShape(AbShapes.md * s),
            )
            .padding(horizontal = 14.dp * s, vertical = 8.dp * s),
        horizontalArrangement = Arrangement.spacedBy(10.dp * s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Box(Modifier.width(26.dp * s), contentAlignment = Alignment.Center) {
                Icon(icon, null, Modifier.size((15f * AB_SF_TO_VECTOR).dp * s), tint = colors.accentOrange)
            }
        }
        Text(
            label,
            modifier = Modifier.weight(1f),
            style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
            textAlign = TextAlign.Start,
        )
        Spacer(Modifier.width(8.dp * s))
        control()
    }
}

/**
 * A yes / no question in a dialog: an icon, the question and two chunky buttons. [destructive] colours the main button
 * and icon as a warning (log out, delete); otherwise they use the brand colour. [message] and [note] may carry `<b>` runs
 * (every other tag is dropped, `<br>` becomes a line break).
 *
 * @param accent overrides the icon / header colour (a success message wants green rather than the brand blue).
 * @param note a line in the warning colour under the message (the old popups' "notes").
 * @param negativeText null = a single button, and the scrim no longer dismisses.
 */
@Composable
fun AbConfirmDialog(
    title: String,
    message: String,
    positiveText: String,
    modifier: Modifier = Modifier,
    negativeText: String? = null,
    icon: ImageVector = Icons.Rounded.Warning,
    destructive: Boolean = true,
    accent: Color? = null,
    note: String? = null,
    onNegative: () -> Unit = {},
    onPositive: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    AbDialog(
        title = title,
        modifier = modifier,
        icon = icon,
        accent = accent ?: (if (destructive) colors.danger else colors.brandPrimary),
        onDismiss = if (negativeText != null) onNegative else null,
        widthFraction = 0.55f,
    ) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp * s)) {
            Text(
                AbTypography.markup(message, regular = FontWeight.SemiBold, bold = FontWeight.ExtraBold),
                modifier = Modifier.fillMaxWidth(),
                style = AbTypography.fixed(17f * s, FontWeight.SemiBold),
                color = colors.textSecondary,
                textAlign = TextAlign.Start,
            )
            if (!note.isNullOrEmpty()) {
                Text(
                    AbTypography.markup(note, regular = FontWeight.Bold, bold = FontWeight.ExtraBold),
                    modifier = Modifier.fillMaxWidth(),
                    style = AbTypography.fixed(13f * s, FontWeight.Bold),
                    color = colors.danger,
                    textAlign = TextAlign.Start,
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp * s)) {
                if (negativeText != null) {
                    AbButton(
                        text = negativeText, modifier = Modifier.weight(1f), tone = AbTone.Ghost,
                        size = AbSize.Medium, fillWidth = true, onClick = onNegative,
                    )
                }
                AbButton(
                    text = positiveText, modifier = Modifier.weight(1f),
                    tone = if (destructive) AbTone.Danger else AbTone.Primary,
                    size = AbSize.Medium, fillWidth = true, onClick = onPositive,
                )
            }
        }
    }
}
