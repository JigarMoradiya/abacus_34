package com.jigar.me.ui.view.home.screens.levels.level3.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbPanelButton
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSound
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlin.math.min

/** Number pad for the Level 3 answer phases: an answer display and chunky keys. */
@Composable
fun L3Numpad(
    typedValue:     String,
    onDigit:        (Int) -> Unit,
    onDelete:       () -> Unit,
    onConfirm:      () -> Unit,
    modifier:       Modifier = Modifier,
    confirmEnabled: Boolean  = typedValue.isNotEmpty(),
) {
    val colors = LocalAbColors.current
    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)
    ) {
        // Answer display. The text is sized from the room it gets, so the pad never outgrows its panel.
        AbGlassPanel(Modifier.fillMaxWidth().weight(1f)) {
            BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AbShrinkText(
                    text      = typedValue.ifEmpty { "?" },
                    style     = abFixedStyle(maxOf(1f, min(abResponsive(44f), maxHeight.value * 0.6f)), FontWeight.ExtraBold),
                    color     = if (typedValue.isEmpty()) colors.textSecondary.copy(alpha = 0.5f) else colors.textPrimary,
                    minScale  = 0.5f,
                    maxLines  = 1,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Digit rows
        listOf(listOf(7, 8, 9), listOf(4, 5, 6), listOf(1, 2, 3)).forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)
            ) {
                row.forEach { digit ->
                    L3Key("$digit", L3KeyKind.Digit, Modifier.weight(1f).fillMaxHeight()) { onDigit(digit) }
                }
            }
        }

        // Bottom row: delete, 0, confirm
        Row(
            modifier              = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)
        ) {
            L3Key("⌫", L3KeyKind.Delete, Modifier.weight(1f).fillMaxHeight()) { onDelete() }
            L3Key("0", L3KeyKind.Digit, Modifier.weight(1f).fillMaxHeight()) { onDigit(0) }
            L3Key("✓", L3KeyKind.Confirm, Modifier.weight(1f).fillMaxHeight(), enabled = confirmEnabled) { onConfirm() }
        }
    }
}

private enum class L3KeyKind { Digit, Delete, Confirm }

@Composable
private fun L3Key(
    label:    String,
    kind:     L3KeyKind,
    modifier: Modifier = Modifier,
    enabled:  Boolean  = true,
    onClick:  () -> Unit,
) {
    val colors = LocalAbColors.current
    val s      = LocalAbScale.current.factor
    val face = when (kind) {
        L3KeyKind.Digit   -> Color.White
        L3KeyKind.Delete  -> colors.danger
        L3KeyKind.Confirm -> if (enabled) colors.accentGreen else colors.surfaceTint
    }
    val edge = when (kind) {
        L3KeyKind.Digit   -> Color(0xFFC9CEE6)
        L3KeyKind.Delete  -> colors.danger.abDarkened(0.3f)
        L3KeyKind.Confirm -> if (enabled) colors.accentGreen.abDarkened(0.3f) else colors.outline
    }
    val ink = if (kind == L3KeyKind.Digit) Color(0xFF1E2470) else if (enabled) Color.White else colors.textSecondary

    BoxWithConstraints(
        modifier = modifier.abSquash(
            face = face, edge = edge, stroke = null,
            cornerRadius = (14f * s).dp, edgeHeight = (4f * s).dp,
            enabled = enabled, sound = AbSound.Button, onClick = onClick,
        ),
        contentAlignment = Alignment.Center,
    ) {
        val keyHeight = maxHeight.value
        when (kind) {
            // Baloo has no erase / check glyphs, so they are drawn as icons that scale with the key.
            L3KeyKind.Delete -> Icon(
                Icons.AutoMirrored.Rounded.Backspace, null,
                Modifier.size((min(abResponsive(28f), keyHeight * 0.5f) * AB_SF_TO_VECTOR).dp), tint = ink,
            )
            L3KeyKind.Confirm -> Icon(
                Icons.Rounded.Check, null,
                Modifier.size((min(abResponsive(28f), keyHeight * 0.5f) * AB_SF_TO_VECTOR).dp), tint = ink,
            )
            L3KeyKind.Digit -> Text(
                text     = label,
                style    = abFixedStyle(min(abResponsive(26f), keyHeight * 0.55f), FontWeight.ExtraBold),
                color    = ink,
                maxLines = 1,
            )
        }
    }
}

/** The Try Again / Done buttons of every Level 3 result popup. */
@Composable
fun L3ResultBtn(label: String, filled: Boolean, onClick: () -> Unit) {
    AbPanelButton(label = label, accent = Color(0xFF303F9F), filled = filled, onClick = onClick)
}
