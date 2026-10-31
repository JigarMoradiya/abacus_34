package com.jigar.me.ui.view.ab

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.view.base.abacus_base.components.HandLeftRightIndicator
import com.jigar.me.utils.AppConstants
import kotlin.math.max

/**
 * The answer bar of the toy abacus frame (mirrors `AbToyAnswerBar.swift`). It sits where the classic bar does (a capsule
 * straddling the top rail: Reset on the left, the current input in the middle, Next / Submit on the right) and follows the
 * same enable rules; only the look is new: a tray in the frame's colour, chunky 3D buttons and a glowing input window.
 */
object AbToyAnswerBarMetrics {
    /** The bar's layout height (tray plus its top padding), so the abacus can put the frame's top rail under its lower edge. */
    fun layoutHeight(s: Float): Dp = ((9 * 2 + 48 + 4) * s + 2).dp
}

@Composable
fun AbToyAnswerBar(
    answer: String,
    abacusType: String?,
    screenType: String,
    isNextButtonEnable: Boolean,
    isDisplayAbacusNumber: Boolean,
    /** The frame's top / base colour (from the chosen bead theme), so the bar reads as part of the same object. */
    frameTop: Color,
    frameBase: Color,
    onReset: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val isFormal = abacusType == AppConstants.apiParams.answerFormalExam
    val isCCM = screenType == AppConstants.AbacusScreen.screenTypeCCM
    val isFreeMode = screenType == AppConstants.AbacusScreen.screenTypeFreeMode
    // The same rules as the classic bar (the app logic is untouched; only the look is new).
    val resetEnabled = answer != "0"
    val nextEnabled = !isFreeMode && (isFormal || isCCM || isNextButtonEnable)

    val buttonWidth = (72 * s).dp
    val buttonHeight = (48 * s).dp
    val cream = if (colors.isDark) toyHex(0xFFEFC4) else AbacusToyPalette.panel(frameBase, false).first
    // An idle button: the cream pulled toward the frame colour, so it still reads but clearly is not tappable.
    val recessed = cream.abBlended(frameBase, 0.55f)
    val idleInk = if (LocalAbColors.current.isDark) frameBase.abDarkened(0.75f) else frameBase.abDarkened(0.5f).copy(alpha = 0.78f)

    Row(
        modifier = modifier
            .drawBehind {
                val corner = CornerRadius(22.dp.toPx() * s)
                val rect = Rect(0f, 0f, size.width, size.height)
                softShadow(rect, corner.x, blur = 5.dp.toPx(), dy = 3.dp.toPx(), alpha = 0.2f)
                drawRoundRect(frameBase.abDarkened(0.45f), topLeft = Offset(0f, 4.dp.toPx() * s), size = size, cornerRadius = corner)
                drawRoundRect(
                    Brush.verticalGradient(listOf(frameTop.abBlended(Color.White, 0.25f), frameBase, frameBase.abDarkened(0.15f)), startY = 0f, endY = size.height),
                    size = size, cornerRadius = corner,
                )
                val sw = max(2.dp.toPx(), 2.5.dp.toPx() * s)
                drawRoundRect(
                    frameBase.abDarkened(0.5f),
                    topLeft = Offset(sw / 2, sw / 2), size = Size(size.width - sw, size.height - sw),
                    cornerRadius = CornerRadius(max(corner.x - sw / 2, 0f)), style = Stroke(width = sw),
                )
                // sheen along the top edge, like the frame's top bar
                val tray = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(rect, corner)) }
                clipPath(tray) {
                    drawOval(
                        Color.White.copy(alpha = 0.2f),
                        topLeft = Offset(14.dp.toPx() * s, -6.dp.toPx() * s),
                        size = Size(size.width - 28.dp.toPx() * s, 16.dp.toPx() * s),
                    )
                }
            }
            .padding(horizontal = (11 * s).dp, vertical = (9 * s).dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((10 * s).dp),
    ) {
        // ── Reset ──
        val resetFace = if (resetEnabled) cream else recessed
        Box(
            Modifier
                .abSquash(resetFace, resetFace.abDarkened(0.3f), null, (16 * s).dp, (4 * s).dp, resetEnabled, sound = null, onClick = onReset)
                .size(buttonWidth, buttonHeight),
            contentAlignment = Alignment.Center,
        ) {
            BarLabel(Icons.Filled.Replay, stringResource(R.string.reset), if (resetEnabled) frameBase.abDarkened(0.45f) else idleInk, s)
        }

        // ── Current input ──
        InputWindow(answer, isDisplayAbacusNumber, frameBase, buttonHeight + (4 * s).dp, s)

        // ── Next / Submit / Check (gold, like the star on the beam, when it is time to move on) ──
        val nextFace = if (nextEnabled) toyHex(0xFFC53D) else recessed
        val title = when {
            isFormal -> stringResource(R.string.submit_answer)
            isCCM -> stringResource(R.string.check_answer_)
            else -> stringResource(R.string.next)
        }
        val icon = if (isFormal || isCCM) Icons.Filled.CheckCircle else Icons.Filled.PlayArrow
        val pulse = rememberToyPulse(nextEnabled)
        Box {
            Box(
                Modifier
                    .graphicsLayer { scaleX = pulse; scaleY = pulse }
                    .abSquash(nextFace, nextFace.abDarkened(0.3f), null, (16 * s).dp, (4 * s).dp, nextEnabled, sound = null, onClick = onNext)
                    .size(buttonWidth, buttonHeight),
                contentAlignment = Alignment.Center,
            ) {
                BarLabel(icon, title, if (nextEnabled) toyHex(0x5A3A00) else idleInk, s)
            }
            // the pointing hand is a Practice-only hint, as it was on the classic bar
            if (nextEnabled && screenType == AppConstants.AbacusScreen.screenTypeAbacusPractice) {
                HandLeftRightIndicator(isVisible = true, modifier = Modifier.align(Alignment.CenterEnd).offset(x = (60 * s).dp))
            }
        }
    }
}

/** A gentle grow-and-shrink that invites a tap; unlike the classic pulse it keeps the button fully opaque. */
@Composable
private fun rememberToyPulse(active: Boolean): Float {
    if (!active) return 1f
    val transition = rememberInfiniteTransition(label = "toyPulse")
    val scale by transition.animateFloat(
        initialValue = 1f, targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "toyPulseScale",
    )
    return scale
}

/**
 * Icon over a caption, centred in the button both ways. Every line has its own tight box so the font's tall line box cannot
 * push the pair off-centre ("Submit\nAnswer" arrives with a line break).
 */
@Composable
private fun BarLabel(icon: ImageVector, title: String, tint: Color, s: Float) {
    val parts = title.split("\n")
    val compact = parts.size > 1
    val size = (if (compact) 11f else 13.5f) * s
    Column(
        Modifier.padding(horizontal = (4 * s).dp).offset(y = (if (compact) 0f else 2.3f * s).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(((if (compact) 1 else 2) * s).dp, Alignment.CenterVertically),
    ) {
        Box(Modifier.height(((if (compact) 17 else 19) * s).dp), contentAlignment = Alignment.Center) {
            Icon(icon, null, Modifier.size(((if (compact) 15 else 17) * s).dp), tint = tint)
        }
        for (line in parts) {
            Box(Modifier.height((size * (if (compact) 0.78f else 1.27f)).dp), contentAlignment = Alignment.Center) {
                Text(
                    line, color = tint, style = AbTypography.fixed(size, FontWeight.ExtraBold), maxLines = 1, softWrap = false,
                    textAlign = TextAlign.Center, modifier = Modifier.wrapContentHeight(unbounded = true),
                )
            }
        }
    }
}

/** A window cut into the plate, finished like the abacus panel (pale by day, midnight by night). */
@Composable
private fun InputWindow(answer: String, isDisplayAbacusNumber: Boolean, frameBase: Color, height: Dp, s: Float) {
    val colors = LocalAbColors.current
    val tint = AbacusToyPalette.panel(frameBase, colors.isDark)
    val ink = if (colors.isDark) toyHex(0xFFE680) else frameBase.abDarkened(0.55f)
    val caption = if (colors.isDark) toyHex(0xFFD76B).copy(alpha = 0.85f) else frameBase.abDarkened(0.2f)
    Column(
        Modifier
            .widthIn(min = (120 * s).dp)
            .height(height)
            .drawBehind {
                val corner = CornerRadius(14.dp.toPx() * s)
                drawRoundRect(Brush.verticalGradient(listOf(tint.first, tint.second)), size = size, cornerRadius = corner)
                // inner shadow: dark along the top, like a window cut into the plate
                val sw3 = max(2.dp.toPx(), 3.dp.toPx() * s)
                drawRoundRect(
                    Brush.verticalGradient(listOf(frameBase.abDarkened(0.5f).copy(alpha = 0.55f), Color.Transparent), startY = 0f, endY = size.height / 2f),
                    topLeft = Offset(sw3 / 2, sw3 / 2), size = Size(size.width - sw3, size.height - sw3),
                    cornerRadius = CornerRadius(max(corner.x - sw3 / 2, 0f)), style = Stroke(width = sw3),
                )
                val sw2 = max(1.5.dp.toPx(), 2.dp.toPx() * s)
                drawRoundRect(
                    frameBase.abDarkened(0.5f),
                    topLeft = Offset(sw2 / 2, sw2 / 2), size = Size(size.width - sw2, size.height - sw2),
                    cornerRadius = CornerRadius(max(corner.x - sw2 / 2, 0f)), style = Stroke(width = sw2),
                )
            }
            .padding(horizontal = (12 * s).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.height((14 * s).dp), contentAlignment = Alignment.Center) {
            Text(
                stringResource(R.string.current_input).uppercase(), color = caption,
                style = AbTypography.fixed(11.5f * s, FontWeight.ExtraBold).copy(letterSpacing = 0.8.sp),
                maxLines = 1, softWrap = false, modifier = Modifier.wrapContentHeight(unbounded = true),
            )
        }
        if (isDisplayAbacusNumber) {
            Box(Modifier.height((26 * s).dp), contentAlignment = Alignment.Center) {
                Text(
                    answer, color = ink, style = AbTypography.fixed(24f * s, FontWeight.ExtraBold, tabular = true),
                    maxLines = 1, softWrap = false, modifier = Modifier.wrapContentHeight(unbounded = true),
                )
            }
        } else {
            Box(Modifier.height((16 * s).dp), contentAlignment = Alignment.Center) {
                Text("-", color = ink.copy(alpha = 0.7f), style = AbTypography.fixed(20f * s, FontWeight.ExtraBold),
                    modifier = Modifier.wrapContentHeight(unbounded = true))
            }
            Box(Modifier.height((12 * s).dp), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.hide_from_setting), color = caption, style = AbTypography.fixed(11f * s, FontWeight.SemiBold),
                    maxLines = 1, softWrap = false, modifier = Modifier.wrapContentHeight(unbounded = true))
            }
        }
    }
}
