package com.jigar.me.ui.view.home.screens.levels.level3.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbFitToSpace
import com.jigar.me.ui.view.ab.AbHeroPanel
import com.jigar.me.ui.view.ab.AbProgressBar
import com.jigar.me.ui.view.ab.AbPopupLayer
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abCardEntrance
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.ab.abPt
import com.jigar.me.ui.view.ab.abScreenLeftEdge
import com.jigar.me.ui.view.home.theme.AppDimens

// Shared pieces of the restyled Level 3 and Level 4 screens (the iOS `DimensionsUtils` / `abFont` helpers, the screen header,
// the hero-panel card and the result popup). The Level 4 screens import these as well.

/** iOS `responsiveDimenstion(base)`: 1x on a phone, 1.5x on a tablet, 1.7x on a large tablet (told apart by the kit factor). */
@Composable
internal fun abResponsive(base: Float): Float {
    val scale = LocalAbScale.current
    return base * when {
        !scale.isTablet -> 1f
        scale.factor >= 1.5f -> 1.7f
        else -> 1.5f
    }
}

/** A size that iOS picks per device class (`isDeviceIpad() ? isDeviceIpad13() ? large : tablet : phone`). */
@Composable
internal fun abTabletSize(phone: Float, tablet: Float, largeTablet: Float): Float {
    val scale = LocalAbScale.current
    return when {
        !scale.isTablet -> phone
        scale.factor >= 1.5f -> largeTablet
        else -> tablet
    }
}

/** iOS `abFont(style, weight)`: Baloo 2 at the responsive size of [base], independent of the system font size. */
@Composable
internal fun abFont(base: Float, weight: FontWeight = FontWeight.SemiBold): TextStyle =
    abFixedStyle(abResponsive(base), weight)

/** White text sits on a bright coloured panel: a soft dark shadow keeps it readable on the lightest accents (orange, green). */
internal fun TextStyle.abOnPanel(): TextStyle = copy(shadow = Shadow(Color.Black.copy(alpha = 0.30f), Offset(0f, 2f), 4f))

/** The system emoji font at [points], centred in a tight line box. */
@Composable
internal fun abEmojiStyle(points: Float): TextStyle {
    val size = abPt(points)
    return TextStyle(
        fontSize = size,
        lineHeight = size * 1.15f,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both),
    )
}

/** iOS `DimensionsUtils.mainTitle`. */
@Composable
internal fun abMainTitleSize(): Float = abTabletSize(16f, 24f, 32f)

/** SwiftUI `Color.primary` under the kit theme: black in Light, white in Dark. */
internal val AbColors.systemPrimary: Color get() = if (isDark) Color.White else Color.Black

/** SwiftUI `Color.secondary` under the kit theme. */
internal val AbColors.systemSecondary: Color
    get() = (if (isDark) Color(0xFFEBEBF5) else Color(0xFF3C3C43)).copy(alpha = 0.6f)

/**
 * iOS `.abScreenLeftEdge()`: pulls the screen's foreground onto this app's standard left edge. The restyled header pads
 * itself by [AppDimens.Dimens16], the old screens by [DeviceInfo.screenHorizontalPadding], so the pull is the difference.
 */
internal fun Modifier.screenLeftEdge(): Modifier =
    abScreenLeftEdge((AppDimens.Dimens16 - DeviceInfo.screenHorizontalPadding()).coerceAtLeast(0.dp))

/** The kit's top bar with the screen padding every restyled iOS screen puts around it. */
@Composable
internal fun ScreenTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    AbTopBar(
        title = title,
        onBack = onBack,
        modifier = modifier.padding(horizontal = AppDimens.Dimens16).padding(top = DeviceInfo.screenTopPadding()),
        trailing = trailing,
    )
}

/**
 * iOS `.abFitOnPhone()` on content that has flexible children (`maxHeight: .infinity`). On a phone the content is laid out
 * at its natural height and shrunk to fit; on a tablet it fills the space and its flexible children grow ([content] gets
 * `flexible = true` and may use `weight` / `fillMaxHeight`).
 */
@Composable
internal fun FitOnPhone(modifier: Modifier = Modifier, content: @Composable (flexible: Boolean) -> Unit) {
    if (LocalAbScale.current.isTablet) {
        Box(modifier, contentAlignment = Alignment.Center) { content(true) }
    } else {
        AbFitToSpace(modifier) { content(false) }
    }
}

/**
 * The card of the Level 3 play screens: a hero panel in the mode's colour, a sticky header (emoji, title, subtitle), a
 * hairline and the phase [content] centred in the room below it.
 */
@Composable
internal fun L3ModeCard(
    accent: Color,
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier) {
        AbHeroPanel(accent, Modifier.matchParentSize(), padding = 0.dp)
        Column(Modifier.fillMaxSize()) {
            // the title sits on a dark plate: white on the light top of a bright panel is hard to read
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = AppDimens.Dimens6, end = AppDimens.Dimens16)
                    .padding(top = AppDimens.Dimens6, bottom = AppDimens.Dimens2),
            ) {
                Row(
                    Modifier
                        .background(Color.Black.copy(alpha = 0.40f), RoundedCornerShape(AppDimens.Dimens12))
                        .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens6),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(emoji, style = abEmojiStyle(abResponsive(22f)))
                    Column(verticalArrangement = Arrangement.spacedBy((-7).dp)) {
                        Text(title, style = abFont(13f, FontWeight.Black), color = Color.White)
                        Text(subtitle, style = abFont(11f), color = Color.White.copy(alpha = 0.92f))
                    }
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.25f)))
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens16)
                    .padding(bottom = AppDimens.Dimens12),
                contentAlignment = Alignment.Center,
                content = content,
            )
        }
    }
}

/** "3", "2", "1" and "Get ready!" of a Level 3 countdown. */
@Composable
internal fun L3Countdown(n: Int, digitSize: Float) {
    // a tick for every number of the countdown
    LaunchedEffect(n) { AudioPlayerManager.playSoundCountTick() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
    ) {
        Text(
            "$n",
            style = abFixedStyle(abResponsive(digitSize), FontWeight.Black).abOnPanel(),
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Text("Get ready!", style = abFont(20f, FontWeight.Medium).abOnPanel(), color = Color.White)
    }
}

/** One flashed term: the "2 / 5" counter, the number on a frosted plate (it fades while [numberVisible] is false) and the progress bar. */
@Composable
internal fun L3FlashDisplay(display: String, index: Int, total: Int, numberVisible: Boolean) {
    val numAlpha by animateFloatAsState(
        targetValue = if (numberVisible) 1f else 0f,
        animationSpec = tween(150),
        label = "l3-flash-alpha",
    )
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
    ) {
        Text(
            "${index + 1} / $total",
            style = abFont(13f, FontWeight.Bold),
            color = Color.White,
            // on a dark pill: small white text is hard to read on the lightest panels
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.30f), RoundedCornerShape(50))
                .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens2),
        )
        // fixed-height plate: only the number fades, so the layout never shifts
        Box(
            Modifier
                .fillMaxWidth()
                .height(abResponsive(100f).dp)
                .alpha(numAlpha)
                .background(Color.Black.copy(alpha = 0.22f), RoundedCornerShape(AppDimens.Dimens16)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                display,
                style = abFixedStyle(abResponsive(64f), FontWeight.Black),
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
        AbProgressBar(
            filled = index + 1,
            total = total,
            modifier = Modifier.widthIn(max = abResponsive(230f).dp).fillMaxWidth(),
            onColor = true,
        )
    }
}

/** The answer phase of the Level 3 cards: a question over the number pad, which takes the room that is left. */
@Composable
internal fun L3AnswerPhase(
    title: String,
    typedValue: String,
    onDigit: (Int) -> Unit,
    onDelete: () -> Unit,
    onConfirm: () -> Unit,
) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
            style = abFont(20f, FontWeight.Black).abOnPanel(),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = AppDimens.Dimens12),
        )
        Spacer(Modifier.height(AppDimens.Dimens12))
        L3Numpad(
            typedValue = typedValue,
            onDigit = onDigit,
            onDelete = onDelete,
            onConfirm = onConfirm,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
    }
}

/**
 * The result popup of every Level 3 mode: the kit's popup layer (the scrim fades, the card pops in) holding a hero panel in the
 * mode's colour with an emoji, the [title] block, a frosted [message] and the Try Again / Done buttons. The content shrinks
 * to the panel when the screen is short.
 */
@Composable
internal fun L3ResultPopup(
    accent: Color,
    correct: Boolean,
    emoji: String,
    message: String,
    retryLabel: String,
    onRetry: () -> Unit,
    onDone: () -> Unit,
    title: @Composable ColumnScope.() -> Unit,
) {
    // the sound of the answer, once, as the popup opens
    LaunchedEffect(Unit) {
        if (correct) AudioPlayerManager.playSoundCorrectAns() else AudioPlayerManager.playSoundOptionWrong()
    }
    // iOS gives the panel 60% of the screen height; the content of a phone is taller than that, so a phone gets more
    val heightFraction = if (LocalAbScale.current.isTablet) 0.60f else 0.90f
    AbPopupLayer(onScrimTap = null, scrim = Color.Black.copy(alpha = 0.45f)) { layerWidth, layerHeight, shown ->
        Box(Modifier.size(layerWidth * 0.50f, layerHeight * heightFraction).abCardEntrance(shown)) {
            AbHeroPanel(accent, Modifier.matchParentSize(), padding = 0.dp)
            AbFitToSpace(Modifier.matchParentSize().padding(AppDimens.Dimens28)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens14),
                ) {
                    Text(emoji, style = abEmojiStyle(abResponsive(52f)))
                    title()
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(AppDimens.Dimens16))
                            .padding(AppDimens.Dimens12),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            message,
                            style = abFont(15f, FontWeight.Medium),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(AppDimens.Dimens4))
                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                        L3ResultBtn(retryLabel, filled = false, onClick = onRetry)
                        L3ResultBtn("Done ✓", filled = true, onClick = onDone)
                    }
                }
            }
        }
    }
}

/** The title block of an answered Level 3 popup: "Correct! 12", or the typed answer beside the right one. */
@Composable
internal fun L3AnswerTitle(correct: Boolean, expected: Int, userAnswer: Int, correctText: String) {
    if (correct) {
        Text(
            correctText,
            style = abFixedStyle(abMainTitleSize(), FontWeight.Black),
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
            L3AnswerBox("Your Answer", "$userAnswer", Color(0xFFFFCDD2))
            Text("→", style = abFont(24f, FontWeight.Black).abOnPanel(), color = Color.White)
            L3AnswerBox("Correct Answer", "$expected", Color(0xFFB9F6CA))
        }
    }
}

/** One of the two answers of a wrong answer popup: a dark glass plate, a clear white label and the number in a pale tint. */
@Composable
private fun L3AnswerBox(label: String, value: String, valueColor: Color) {
    Column(
        Modifier
            .background(Color.Black.copy(alpha = 0.28f), RoundedCornerShape(AppDimens.Dimens14))
            .padding(horizontal = AppDimens.Dimens14, vertical = AppDimens.Dimens8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-3).dp),
    ) {
        Text(label, style = abFont(12f, FontWeight.Bold), color = Color.White)
        Text(value, style = abFixedStyle(abResponsive(44f), FontWeight.Black), color = valueColor)
    }
}
