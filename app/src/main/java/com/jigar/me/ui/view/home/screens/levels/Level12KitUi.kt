package com.jigar.me.ui.view.home.screens.levels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbFitToSpace
import com.jigar.me.ui.view.ab.AbHeroPanel
import com.jigar.me.ui.view.ab.AbOptionButton
import com.jigar.me.ui.view.ab.AbOptionState
import com.jigar.me.ui.view.ab.AbPopupLayer
import com.jigar.me.ui.view.ab.AbProgressBar
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abCardEntrance
import com.jigar.me.ui.view.ab.abScreenLeftEdge
import com.jigar.me.ui.view.home.theme.AppDimens

/*
 * Pieces shared by the restyled Level 1 and Level 2 screens (home, lesson, learn, practice, quiz, formulas). Each one
 * is the Android counterpart of a modifier or wrapper the iOS views repeat in every screen.
 */

/** Root of a level screen: the kit theme with the level's painted world behind the foreground. */
@Composable
internal fun LevelScreenRoot(world: AbWorld, content: @Composable BoxScope.() -> Unit) {
    AbTheme {
        Box(Modifier.fillMaxSize()) {
            AbBackground(world, Modifier.fillMaxSize())
            content()
        }
    }
}

/**
 * The foreground area of a level screen: inside the safe area and pulled onto the app's standard left edge (iOS
 * `.abScreenLeftEdge()`). The header pads by [AppDimens.Dimens16], the old Android header by
 * `DeviceInfo.screenHorizontalPadding()`, so the pull is the difference. It never exceeds the left safe inset, so on a
 * device with no cutout on that side nothing is pulled off the screen.
 */
@Composable
internal fun Modifier.levelForeground(): Modifier {
    val density = LocalDensity.current
    val leftInset = with(density) { WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current).toDp() }
    val pull = minOf(AppDimens.Dimens16 - DeviceInfo.screenHorizontalPadding(), leftInset)
    return this
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .abScreenLeftEdge(pull)
}

/** iOS `AbTopBar(...).padding(.horizontal, padding16).padding(.top, deviceSpacingTop)`; [end] = 0 where the bar ends at a panel. */
internal fun Modifier.levelTopBarPadding(end: Dp = AppDimens.Dimens16): Modifier = this
    .padding(start = AppDimens.Dimens16, end = end)
    .padding(top = DeviceInfo.screenTopPadding())

/** The header of a practice or quiz screen: back + title, and the question progress as a bar at the trailing end. */
@Composable
internal fun LevelProgressTopBar(title: String, filled: Int, total: Int, onBack: () -> Unit) {
    AbTopBar(title, onBack, Modifier.levelTopBarPadding()) {
        AbProgressBar(filled, maxOf(1, total), Modifier.widthIn(max = levelDp(260f)))
    }
}

/** A coloured hero panel that is as big as [content] makes this box (iOS `ZStack { AbHeroPanel(...) { Color.clear }; content }`). */
@Composable
internal fun LevelHeroPanel(
    accent: Color,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier, contentAlignment = contentAlignment) {
        AbHeroPanel(accent, Modifier.matchParentSize(), padding = 0.dp)
        content()
    }
}

/**
 * The content of a coloured panel that has a fixed height on a phone: shrinks (never grows) when it is taller than the
 * panel, so nothing is cut on a short screen. A tablet's panel is roomy (or wraps its content), so it is left as it is.
 */
@Composable
internal fun LevelPanelFit(content: @Composable () -> Unit) {
    if (LocalAbScale.current.isTablet) content() else AbFitToSpace(Modifier.fillMaxSize(), stretchWidth = true, content = content)
}

/** The result popup of a practice or quiz: a hero panel at 55% of the screen width (it holds little) over the kit scrim, with the kit's pop-in. */
@Composable
internal fun LevelResultPopup(accent: Color, content: @Composable ColumnScope.() -> Unit) {
    AbPopupLayer(onScrimTap = null, scrim = Color.Black.copy(alpha = 0.45f)) { layerWidth, _, shown ->
        AbHeroPanel(accent, Modifier.width(layerWidth * 0.55f).abCardEntrance(shown), padding = 0.dp) {
            Column(
                Modifier.fillMaxWidth().padding(AppDimens.Dimens28),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
                content = content,
            )
        }
    }
}

/** iOS `responsiveDimenstion`: 1x phone, 1.5x tablet, 1.7x large tablet. */
internal fun levelScale(): Float = when {
    DeviceInfo.isLargeTablet -> 1.7f
    DeviceInfo.isTablet -> 1.5f
    else -> 1f
}

/** iOS `responsiveDimenstion(base)` as a font size / a length. */
internal fun levelSp(base: Float): TextUnit = (base * levelScale()).sp
internal fun levelDp(base: Float): Dp = (base * levelScale()).dp

/** iOS `AbTypography.fixed(responsiveDimenstion(base), weight)` / `.abFont(style, weight)`: Baloo 2 at the app's responsive size. */
internal fun levelText(base: Float, weight: FontWeight): TextStyle = AbTypography.fixed(base * levelScale(), weight)

/** [levelText] for a paragraph: Baloo's tight single-line box would pack wrapped lines together, so give them some air. */
internal fun levelParagraph(base: Float, weight: FontWeight): TextStyle =
    levelText(base, weight).copy(lineHeight = (base * levelScale() * 1.3f).sp)

/** iOS `DimensionsUtils.mainTitle`: 16 on a phone, 24 on a tablet, 32 on a large tablet. */
internal val levelMainTitleSize: Float get() = when {
    DeviceInfo.isLargeTablet -> 32f
    DeviceInfo.isTablet -> 24f
    else -> 16f
}

/**
 * Lays the content out at its natural size, then shrinks it (never grows it) to fit the box it is given, centred. For the
 * abacus, which has a fixed size of its own (from the screen) that a panel on a level screen can be smaller than: the kit's
 * `AbFitToSpace` offers its content the box width, and the abacus would be squeezed narrower instead of scaled.
 */
@Composable
internal fun LevelFitToBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val placeables = measurables.map { it.measure(Constraints()) }
        val naturalW = placeables.maxOfOrNull { it.width } ?: 0
        val naturalH = placeables.maxOfOrNull { it.height } ?: 0
        val boxW = if (constraints.hasBoundedWidth) constraints.maxWidth else naturalW
        val boxH = if (constraints.hasBoundedHeight) constraints.maxHeight else naturalH
        val scale = if (naturalW > 0 && naturalH > 0) minOf(1f, boxW.toFloat() / naturalW, boxH.toFloat() / naturalH) else 1f
        layout(boxW, boxH) {
            placeables.forEach {
                it.placeRelativeWithLayer((boxW - it.width) / 2, (boxH - it.height) / 2) { scaleX = scale; scaleY = scale }
            }
        }
    }
}

/** The look of one answer tile of a quiz: neutral until an answer is picked, then the right one is green and a wrong pick red. */
internal fun levelOptionState(choice: Int, correct: Int, selected: Int?): AbOptionState = when {
    selected == null -> AbOptionState.Idle
    choice == correct -> AbOptionState.Correct
    choice == selected -> AbOptionState.Wrong
    else -> AbOptionState.Idle
}

/** The 2 x 2 answer tiles of a quiz question (iOS: two `HStack`s of `AbOptionButton`, each tile `examOptionWidth` wide at most). */
@Composable
internal fun LevelQuizOptions(choices: List<Int>, correct: Int, selected: Int?, onPick: (Int) -> Unit) {
    @Composable
    fun RowScope.option(choice: Int) {
        AbOptionButton(
            text     = "$choice",
            modifier = Modifier.weight(1f, fill = false).widthIn(max = AppDimens.examOptionWidth),
            state    = levelOptionState(choice, correct, selected),
            height   = AppDimens.examOptionHeight,
            onClick  = { if (selected == null) onPick(choice) },
        )
    }
    Column(
        modifier            = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
    ) {
        if (choices.size >= 2) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(AppDimens.Dimens10, Alignment.CenterHorizontally)) {
                option(choices[0]); option(choices[1])
            }
        }
        if (choices.size >= 4) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(AppDimens.Dimens10, Alignment.CenterHorizontally)) {
                option(choices[2]); option(choices[3])
            }
        }
    }
}
